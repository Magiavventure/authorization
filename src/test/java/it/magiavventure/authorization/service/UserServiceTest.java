package it.magiavventure.authorization.service;

import it.magiavventure.authorization.mapper.UserMapper;
import it.magiavventure.authorization.model.CreateUser;
import it.magiavventure.authorization.model.UpdateUser;
import it.magiavventure.authorization.repository.UserRepository;
import it.magiavventure.common.error.MagiavventureException;
import it.magiavventure.mongo.entity.EUser;
import it.magiavventure.mongo.model.Category;
import it.magiavventure.mongo.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@DisplayName("User service tests")
class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    @Captor
    ArgumentCaptor<EUser> eUserArgumentCaptor;
    @Captor
    ArgumentCaptor<Example<EUser>> exampleArgumentCaptor;
    @Captor
    ArgumentCaptor<Sort> sortArgumentCaptor;

    @Test
    @DisplayName("Create user with name that not exists")
    void createUser_ok_nameNotExists() {
        var categories = List.of(Category
                .builder()
                .id(UUID.randomUUID())
                .name("category")
                .background("background")
                .build());
        var createUser = CreateUser
                .builder()
                .name("test")
                .preferredCategories(categories)
                .build();
        var eUser = EUser
                .builder()
                .id(UUID.randomUUID())
                .name("test")
                .preferredCategories(categories)
                .active(true)
                .build();

        Mockito.when(userRepository.save(eUserArgumentCaptor.capture()))
                .thenReturn(eUser);
        Mockito.when(userRepository.exists(exampleArgumentCaptor.capture()))
                .thenReturn(false);

        User user = userService.createUser(createUser);

        Mockito.verify(userRepository).save(eUserArgumentCaptor.capture());
        Mockito.verify(userRepository).exists(exampleArgumentCaptor.capture());

        EUser userCapt = eUserArgumentCaptor.getValue();
        Example<EUser> example = exampleArgumentCaptor.getValue();

        Assertions.assertNotNull(user);
        Assertions.assertEquals(createUser.getName(), user.getName());
        Assertions.assertIterableEquals(createUser.getPreferredCategories(), user.getPreferredCategories());
        Assertions.assertEquals(userCapt.getName(), user.getName());
        Assertions.assertIterableEquals(userCapt.getPreferredCategories(), user.getPreferredCategories());
        Assertions.assertNotNull(userCapt.getId());
        Assertions.assertTrue(userCapt.isActive());
        Assertions.assertEquals(createUser.getName(), example.getProbe().getName());
    }

    @Test
    @DisplayName("Create user with name that already exists")
     void createUser_ko_nameAlreadyExists() {
        var categories = List.of(Category
                .builder()
                .id(UUID.randomUUID())
                .name("category")
                .background("background")
                .build());
        var createUser = CreateUser
                .builder()
                .name("test")
                .preferredCategories(categories)
                .build();

        Mockito.when(userRepository.exists(exampleArgumentCaptor.capture()))
                .thenReturn(true);

        MagiavventureException exception = Assertions.assertThrows(MagiavventureException.class,
                () -> userService.createUser(createUser));

        Mockito.verify(userRepository).exists(exampleArgumentCaptor.capture());
        Example<EUser> example = exampleArgumentCaptor.getValue();

        Assertions.assertEquals(createUser.getName(), example.getProbe().getName());
        Assertions.assertEquals("user-exists", exception.getError().getKey());
        Assertions.assertEquals(1, exception.getError().getArgs().length);
    }

    @Test
    @DisplayName("Update user with name that not exists")
    void updateUser_ok_nameNotExists() {
        var id = UUID.randomUUID();
        var categories = List.of(Category
                .builder()
                .id(UUID.randomUUID())
                .name("category")
                .background("background")
                .build());
        var updateUser = UpdateUser
                .builder()
                .id(id)
                .name("test 2")
                .preferredCategories(categories)
                .build();
        var eUser = EUser
                .builder()
                .id(id)
                .name("test")
                .preferredCategories(categories)
                .active(true)
                .build();
        var eUserUpdated = EUser
                .builder()
                .id(id)
                .name("test 2")
                .preferredCategories(categories)
                .active(false)
                .build();

        Mockito.when(userRepository.findById(id))
                .thenReturn(Optional.of(eUser));
        Mockito.when(userRepository.save(eUserArgumentCaptor.capture()))
                .thenReturn(eUserUpdated);
        Mockito.when(userRepository.exists(exampleArgumentCaptor.capture()))
                .thenReturn(false);

        User user = userService.updateUser(updateUser);

        Mockito.verify(userRepository).findById(id);
        Mockito.verify(userRepository).save(eUserArgumentCaptor.capture());
        Mockito.verify(userRepository).exists(exampleArgumentCaptor.capture());
        EUser userCapt = eUserArgumentCaptor.getValue();
        Example<EUser> example = exampleArgumentCaptor.getValue();

        Assertions.assertNotNull(user);
        Assertions.assertEquals(updateUser.getName(), user.getName());
        Assertions.assertIterableEquals(updateUser.getPreferredCategories(), user.getPreferredCategories());
        Assertions.assertEquals(updateUser.getName(), userCapt.getName());
        Assertions.assertIterableEquals(updateUser.getPreferredCategories(), userCapt.getPreferredCategories());
        Assertions.assertNotNull(userCapt.getId());
        Assertions.assertTrue(userCapt.isActive());
        Assertions.assertEquals(updateUser.getName(), example.getProbe().getName());
    }

    @Test
    @DisplayName("Update user with same name")
    void updateUser_ok_withSameName() {
        var id = UUID.randomUUID();
        var categories = List.of(Category
                .builder()
                .id(UUID.randomUUID())
                .name("category")
                .background("background")
                .build());
        var updateUser = UpdateUser
                .builder()
                .id(id)
                .name("test")
                .preferredCategories(categories)
                .build();
        var eUser = EUser
                .builder()
                .id(id)
                .name("test")
                .preferredCategories(categories)
                .active(true)
                .build();
        var eUserUpdated = EUser
                .builder()
                .id(id)
                .name("test")
                .preferredCategories(categories)
                .active(false)
                .build();

        Mockito.when(userRepository.findById(id))
                .thenReturn(Optional.of(eUser));
        Mockito.when(userRepository.save(eUserArgumentCaptor.capture()))
                .thenReturn(eUserUpdated);

        User user = userService.updateUser(updateUser);

        Mockito.verify(userRepository).findById(id);
        Mockito.verify(userRepository).save(eUserArgumentCaptor.capture());
        EUser userCapt = eUserArgumentCaptor.getValue();

        Assertions.assertNotNull(user);
        Assertions.assertEquals(updateUser.getName(), user.getName());
        Assertions.assertIterableEquals(updateUser.getPreferredCategories(), user.getPreferredCategories());
        Assertions.assertEquals(updateUser.getName(), userCapt.getName());
        Assertions.assertIterableEquals(updateUser.getPreferredCategories(), userCapt.getPreferredCategories());
        Assertions.assertNotNull(userCapt.getId());
        Assertions.assertTrue(userCapt.isActive());
    }

    @Test
    @DisplayName("Update user but user not found")
    void updateUser_ko_userNotExists() {
        var categories = List.of(Category
                .builder()
                .id(UUID.randomUUID())
                .name("category")
                .background("background")
                .build());
        var updateUser = UpdateUser
                .builder()
                .id(UUID.randomUUID())
                .name("test")
                .preferredCategories(categories)
                .build();

        Mockito.when(userRepository.findById(updateUser.getId()))
                .thenReturn(Optional.empty());

        MagiavventureException exception = Assertions.assertThrows(MagiavventureException.class,
                () -> userService.updateUser(updateUser));

        Mockito.verify(userRepository).findById(updateUser.getId());

        Assertions.assertEquals("user-not-found", exception.getError().getKey());
        Assertions.assertEquals(1, exception.getError().getArgs().length);
    }

    @Test
    @DisplayName("Update user with name that already exists")
    void updateUser_ko_userNameAlreadyExists() {
        var id = UUID.randomUUID();
        var categories = List.of(Category
                .builder()
                .id(UUID.randomUUID())
                .name("category")
                .background("background")
                .build());
        var updateUser = UpdateUser
                .builder()
                .id(id)
                .name("test 2")
                .preferredCategories(categories)
                .build();
        var eUser = EUser
                .builder()
                .id(id)
                .name("test")
                .preferredCategories(categories)
                .active(true)
                .build();

        Mockito.when(userRepository.findById(id))
                .thenReturn(Optional.of(eUser));
        Mockito.when(userRepository.exists(exampleArgumentCaptor.capture()))
                .thenReturn(true);

        MagiavventureException exception = Assertions.assertThrows(MagiavventureException.class,
                () -> userService.updateUser(updateUser));

        Mockito.verify(userRepository).findById(id);
        Mockito.verify(userRepository).exists(exampleArgumentCaptor.capture());
        Example<EUser> example = exampleArgumentCaptor.getValue();

        Assertions.assertEquals("user-exists", exception.getError().getKey());
        Assertions.assertEquals(1, exception.getError().getArgs().length);
        Assertions.assertEquals(updateUser.getName(), example.getProbe().getName());
    }

    @Test
    @DisplayName("Find user by id")
    void findUserById_ok() {
        var id = UUID.randomUUID();
        var categories = List.of(Category
                .builder()
                .id(UUID.randomUUID())
                .name("category")
                .background("background")
                .build());
        var eUser = EUser
                .builder()
                .id(id)
                .name("test")
                .preferredCategories(categories)
                .active(true)
                .build();

        Mockito.when(userRepository.findById(id))
                .thenReturn(Optional.of(eUser));

        User user = userService.findById(id);

        Mockito.verify(userRepository).findById(id);

        Assertions.assertNotNull(user);
        Assertions.assertEquals(eUser.getName(), user.getName());
        Assertions.assertIterableEquals(eUser.getPreferredCategories(), user.getPreferredCategories());
    }

    @Test
    @DisplayName("Find user by id but not found")
    void findUserById_ko_notFound() {
        var id = UUID.randomUUID();

        Mockito.when(userRepository.findById(id))
                .thenReturn(Optional.empty());

        MagiavventureException exception = Assertions.assertThrows(MagiavventureException.class,
                () -> userService.findById(id));

        Mockito.verify(userRepository).findById(id);

        Assertions.assertNotNull(exception);
        Assertions.assertEquals("user-not-found", exception.getError().getKey());
        Assertions.assertEquals(1, exception.getError().getArgs().length);
    }

    @Test
    @DisplayName("Find all users")
    void findAllUsers_ok() {
        var id = UUID.randomUUID();
        var categories = List.of(Category
                .builder()
                .id(UUID.randomUUID())
                .name("category")
                .background("background")
                .build());
        var eUser = EUser
                .builder()
                .id(id)
                .name("test")
                .preferredCategories(categories)
                .active(true)
                .build();
        var usersResponse = List.of(eUser);

        Mockito.when(userRepository.findAll(sortArgumentCaptor.capture()))
                .thenReturn(usersResponse);

        List<User> users = userService.findAll();

        Mockito.verify(userRepository).findAll(sortArgumentCaptor.capture());

        Sort sort = sortArgumentCaptor.getValue();

        Assertions.assertNotNull(users);
        Assertions.assertEquals(1, users.size());
        var order = sort.getOrderFor("name");
        Assertions.assertNotNull(order);
        Assertions.assertEquals(Sort.Direction.ASC, order.getDirection());
    }

    @Test
    @DisplayName("Delete user by id")
    void deleteUserById_ok() {
        var id = UUID.randomUUID();
        var categories = List.of(Category
                .builder()
                .id(UUID.randomUUID())
                .name("category")
                .background("background")
                .build());
        var eUser = EUser
                .builder()
                .id(id)
                .name("test")
                .preferredCategories(categories)
                .active(true)
                .build();

        Mockito.when(userRepository.findById(id))
                .thenReturn(Optional.of(eUser));
        Mockito.doNothing().when(userRepository).deleteById(id);

        userService.deleteById(id);

        Mockito.verify(userRepository).findById(id);
        Mockito.verify(userRepository).deleteById(id);
    }

    @Test
    @DisplayName("Delete user by id but not found")
    void deleteUserById_ko_notFound() {
        var id = UUID.randomUUID();

        Mockito.when(userRepository.findById(id))
                .thenReturn(Optional.empty());

        MagiavventureException exception = Assertions.assertThrows(MagiavventureException.class,
                () -> userService.deleteById(id));

        Mockito.verify(userRepository).findById(id);

        Assertions.assertNotNull(exception);
        Assertions.assertEquals("user-not-found", exception.getError().getKey());
        Assertions.assertEquals(1, exception.getError().getArgs().length);
    }
}
