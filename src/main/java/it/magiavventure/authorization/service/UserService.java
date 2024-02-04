package it.magiavventure.authorization.service;

import it.magiavventure.authorization.error.AuthorizationException;
import it.magiavventure.authorization.mapper.UserMapper;
import it.magiavventure.authorization.model.BanUser;
import it.magiavventure.authorization.model.CreateUser;
import it.magiavventure.authorization.model.UpdateUser;
import it.magiavventure.common.error.MagiavventureException;
import it.magiavventure.jwt.service.UserJwtService;
import it.magiavventure.mongo.entity.EUser;
import it.magiavventure.mongo.model.User;
import it.magiavventure.mongo.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
public class UserService {
    private static final String USER_AUTHORITY = "user";
    private static final String ADMIN_AUTHORITIY = "admin";

    private final UserRepository userRepository;
    private final UserJwtService userJwtService;
    private final UserMapper userMapper;

    public User createUser(CreateUser createUser) {
        log.debug("Execute create user for '{}'", createUser);
        checkIfUserExists(createUser.getName());
        EUser userToSave = EUser
                .builder()
                .id(UUID.randomUUID())
                .name(createUser.getName())
                .preferredCategories(createUser.getPreferredCategories())
                .authorities(List.of(USER_AUTHORITY))
                .build();
        return saveAndMapUser(userToSave);
    }

    @CacheEvict(value = "user_entity", key = "#p0")
    public User banUser(UUID id, BanUser banUser) {
        log.debug("Execute ban user for id '{}' with duration '{}'", id, banUser);
        EUser userToBan = userJwtService.retrieveById(id);
        String duration = banUser.getDuration()+banUser.getUnit().getValue();
        LocalDateTime banExpiration = LocalDateTime.now().plus(DurationStyle.detectAndParse(duration));
        userToBan.setBanExpiration(banExpiration);
        return saveAndMapUser(userToBan);
    }

    @CacheEvict(value = "user_entity", key = "#p0")
    public User giveAdminAuthorityToUser(UUID id) {
        log.debug("Execute give admin authority to user with id '{}'", id);
        EUser eUser = userJwtService.retrieveById(id);
        eUser.setAuthorities(List.of(USER_AUTHORITY, ADMIN_AUTHORITIY));
        return saveAndMapUser(eUser);
    }

    @CacheEvict(value = "user_entity", key = "#p0.id")
    public void evictUserCache(EUser eUser) {
        log.debug("Evicted user_entity cache for key '{}'", eUser.getId());
    }

    @CacheEvict(value = "user_entity", key = "#p0.id")
    public User updateUser(UpdateUser updateUser) {
        log.debug("Execute update user for '{}'", updateUser);
        EUser userToUpdate = userJwtService.retrieveById(updateUser.getId());

        if(!userToUpdate.getName().equalsIgnoreCase(updateUser.getName()))
            checkIfUserExists(updateUser.getName());

        userToUpdate.setName(updateUser.getName());
        userToUpdate.setPreferredCategories(updateUser.getPreferredCategories());

        return saveAndMapUser(userToUpdate);
    }

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
        return userMapper.map(userJwtService.retrieveById(id));
    }

    @CacheEvict(value = "user_entity", key = "#p0")
    public void deleteById(UUID id) {
        log.debug("Execute user delete by id '{}'", id);
        userJwtService.retrieveById(id);
        userRepository.deleteById(id);
    }

    @Cacheable(value="user_entity", key = "#p0")
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
                .build(), ExampleMatcher.matchingAny());

        if(userRepository.exists(example))
            throw MagiavventureException.of(AuthorizationException.USER_EXISTS, name);

    }

    private User saveAndMapUser(EUser userToSave) {
        EUser savedUser = userRepository.save(userToSave);
        return userMapper.map(savedUser);
    }


}
