package it.magiavventure.authorization.service;

import it.magiavventure.authorization.error.AuthorizationException;
import it.magiavventure.authorization.mapper.UserMapper;
import it.magiavventure.authorization.model.BanUser;
import it.magiavventure.authorization.model.CreateUser;
import it.magiavventure.authorization.model.UpdateUser;
import it.magiavventure.common.error.MagiavventureException;
import it.magiavventure.jwt.service.OwnershipService;
import it.magiavventure.mongo.entity.EUser;
import it.magiavventure.mongo.model.User;
import it.magiavventure.mongo.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserService {
    private final UserService self;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final OwnershipService ownershipService;

    @CacheEvict(value = "users", key = "'all'")
    public User createUser(CreateUser createUser) {
        log.debug("Execute create user for '{}'", createUser);
        checkIfUserExists(createUser.getName());
        EUser userToSave = EUser
                .builder()
                .id(UUID.randomUUID())
                .name(createUser.getName())
                .avatar(createUser.getAvatar())
                .preferredCategories(createUser.getPreferredCategories())
                .authorities(List.of(OwnershipService.USER_AUTHORITY))
                .build();
        return saveAndMapUser(userToSave);
    }

    @Caching(
            evict = {
                    @CacheEvict(value = "user_entity", key = "#p0"),
                    @CacheEvict(value = "users", key = "'all'")
            }
    )
    public User banUser(UUID id, BanUser banUser) {
        log.debug("Execute ban user for id '{}' with duration '{}'", id, banUser);
        EUser userToBan = self.findEntityById(id);
        String duration = banUser.getDuration()+banUser.getUnit().getValue();
        LocalDateTime banExpiration = LocalDateTime.now().plus(DurationStyle.detectAndParse(duration));
        userToBan.setBanExpiration(banExpiration);
        return saveAndMapUser(userToBan);
    }

    @Caching(
            evict = {
                    @CacheEvict(value = "user", key = "#p0"),
                    @CacheEvict(value = "users", key = "'all'")
            }
    )
    public User giveAdminAuthorityToUser(UUID id) {
        log.debug("Execute give admin authority to user with id '{}'", id);
        EUser eUser = self.findEntityById(id);
        eUser.setAuthorities(List.of(OwnershipService.USER_AUTHORITY, OwnershipService.ADMIN_AUTHORITY));
        return saveAndMapUser(eUser);
    }

    @Caching(
            evict = {
                    @CacheEvict(value = "user", key = "#p0.id"),
                    @CacheEvict(value = "users", key = "'all'")
            }
    )
    public void evictUserCache(EUser eUser) {
        log.debug("Evicted user cache for key '{}'", eUser.getId());
    }

    @Caching(
            evict = {
                    @CacheEvict(value = "user", key = "#p0.id"),
                    @CacheEvict(value = "users", key = "'all'")
            }
    )
    public User updateUser(UpdateUser updateUser) {
        log.debug("Execute update user for '{}'", updateUser);
        validateUser(updateUser.getId());
        EUser userToUpdate = self.findEntityById(updateUser.getId());

        if(!userToUpdate.getName().equalsIgnoreCase(updateUser.getName()))
            checkIfUserExists(updateUser.getName());

        userToUpdate.setName(updateUser.getName());
        userToUpdate.setAvatar(updateUser.getAvatar());
        userToUpdate.setPreferredCategories(updateUser.getPreferredCategories());

        return saveAndMapUser(userToUpdate);
    }

    @Cacheable(value = "users", key = "'all'")
    public List<User> findAll() {
        log.debug("Execute find all users");
        var sort = Sort.by(Sort.Direction.ASC, "name");
        return userRepository.findAll(sort)
                .stream()
                .map(userMapper::map)
                .toList();
    }

    public User findById(UUID id) {
        log.debug("Execute user find by id '{}'", id);
        validateUser(id);
        return userMapper.map(self.findEntityById(id));
    }

    @CacheEvict(value = "user", key = "#p0")
    public void deleteById(UUID id) {
        log.debug("Execute user delete by id '{}'", id);
        validateUser(id);
        self.findEntityById(id);
        userRepository.deleteById(id);
    }

    @Cacheable(value="user", key = "#p0")
    public EUser findEntityById(UUID id) {
        log.debug("Execute user find entity by id '{}'", id);
        return userRepository.findById(id)
                .orElseThrow(() -> MagiavventureException.of(AuthorizationException.USER_NOT_FOUND, id.toString()));
    }

    public void checkIfUserExists(String name) {
        log.debug("Execute check if user name exists for name '{}'", name);
        Example<EUser> example = Example.of(EUser
                .builder()
                .name(name)
                .build(), ExampleMatcher.matchingAny().withMatcher("name",
                ExampleMatcher.GenericPropertyMatchers.ignoreCase()));

        if(userRepository.exists(example))
            throw MagiavventureException.of(AuthorizationException.USER_EXISTS, name);

    }

    private User saveAndMapUser(EUser userToSave) {
        EUser savedUser = userRepository.save(userToSave);
        return userMapper.map(savedUser);
    }

    private void validateUser(UUID id) {
        ownershipService.validateOwnership(id);
    }


}
