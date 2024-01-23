package it.magiavventure.authorization.service;

import it.magiavventure.authorization.error.AuthorizationException;
import it.magiavventure.authorization.mapper.UserMapper;
import it.magiavventure.authorization.model.CreateUser;
import it.magiavventure.authorization.model.UpdateUser;
import it.magiavventure.authorization.repository.UserRepository;
import it.magiavventure.common.error.MagiavventureException;
import it.magiavventure.mongo.entity.EUser;
import it.magiavventure.mongo.model.User;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public User createUser(CreateUser createUser) {
        checkIfUserExists(createUser.getName());
        EUser userToSave = EUser
                .builder()
                .id(UUID.randomUUID())
                .name(createUser.getName())
                .preferredCategories(createUser.getPreferredCategories())
                .active(true)
                .build();

        EUser savedUser = userRepository.save(userToSave);
        return userMapper.map(savedUser);
    }

    public User updateUser(UpdateUser updateUser) {
        EUser userToUpdate = findEntityById(updateUser.getId());

        if(!userToUpdate.getName().equalsIgnoreCase(updateUser.getName()))
            checkIfUserExists(updateUser.getName());

        userToUpdate.setName(updateUser.getName());
        userToUpdate.setPreferredCategories(updateUser.getPreferredCategories());

        EUser updatedUser = userRepository.save(userToUpdate);
        return userMapper.map(updatedUser);
    }

    public List<User> findAll() {
        var sort = Sort.by(Sort.Direction.ASC, "name");
        return userRepository.findAll(sort)
                .stream()
                .map(userMapper::map)
                .toList();
    }

    public User findById(UUID id) {
        return userMapper.map(findEntityById(id));
    }

    public void deleteById(UUID id) {
        findEntityById(id);
        userRepository.deleteById(id);
    }

    private EUser findEntityById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> MagiavventureException.of(AuthorizationException.USER_NOT_FOUND, id.toString()));
    }

    public void checkIfUserExists(String name) {
        Example<EUser> example = Example.of(EUser
                .builder()
                .name(name)
                .build(), ExampleMatcher.matchingAny());

        if(userRepository.exists(example))
            throw MagiavventureException.of(AuthorizationException.USER_EXISTS, name);

    }


}
