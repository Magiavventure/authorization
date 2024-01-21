package it.magiavventure.authorization.operation;

import it.magiavventure.authorization.model.CreateUser;
import it.magiavventure.authorization.model.Login;
import it.magiavventure.authorization.model.UpdateUser;
import it.magiavventure.authorization.service.AuthorizationService;
import it.magiavventure.authorization.service.UserService;
import it.magiavventure.mongo.model.Category;
import it.magiavventure.mongo.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@DisplayName("User operation tests")
class AuthorizationOperationTest {

    @InjectMocks
    private AuthorizationOperation authorizationOperation;

    @Mock
    private AuthorizationService authorizationService;

    @Mock
    private UserService userService;

    @Test
    @DisplayName("Login user by id")
    void loginUser_byId() {
        Login login = Login.builder().id(UUID.randomUUID()).build();

        Mockito.when(authorizationService.loginById(login.getId()))
                .thenReturn("token");

        String token = authorizationOperation.loginById(login);

        Mockito.verify(authorizationService).loginById(login.getId());

        Assertions.assertNotNull(token);
        Assertions.assertEquals("token", token);
    }

    @Test
    @DisplayName("Create user api test")
    void createUser_ok() {
        Category category = Category
                .builder()
                .name("name")
                .id(UUID.randomUUID())
                .background("background")
                .build();
        CreateUser createUser = CreateUser
                .builder()
                .name("name")
                .preferredCategories(List.of(category))
                .build();
        User returnedUser = User
                .builder()
                .id(UUID.randomUUID())
                .name("name")
                .preferredCategories(List.of(category))
                .build();

        Mockito.when(userService.createUser(createUser))
                .thenReturn(returnedUser);

        User user = authorizationOperation.createUser(createUser);

        Mockito.verify(userService).createUser(createUser);

        Assertions.assertNotNull(user);
        Assertions.assertEquals(createUser.getName(), user.getName());
        Assertions.assertNotNull(user.getId());
        Assertions.assertEquals(createUser.getPreferredCategories(), user.getPreferredCategories());
    }

    @Test
    @DisplayName("Find all users api test")
    void findAllUsers_ok() {
        Category category = Category
                .builder()
                .name("name")
                .id(UUID.randomUUID())
                .background("background")
                .build();
        User returnedUser = User
                .builder()
                .id(UUID.randomUUID())
                .name("name")
                .preferredCategories(List.of(category))
                .build();

        Mockito.when(userService.findAll())
                .thenReturn(List.of(returnedUser));

        List<User> users = authorizationOperation.findAllUser();

        Mockito.verify(userService).findAll();

        Assertions.assertNotNull(users);
        users.stream().findFirst()
                        .ifPresent(user -> {
                            Assertions.assertEquals(returnedUser.getName(), user.getName());
                            Assertions.assertNotNull(user.getId());
                            Assertions.assertEquals(returnedUser.getPreferredCategories(),
                                    user.getPreferredCategories());
                        });
    }

    @Test
    @DisplayName("Find user by ID api test")
    void findUserById_ok() {
        UUID id = UUID.randomUUID();
        Category category = Category
                .builder()
                .name("name")
                .id(UUID.randomUUID())
                .background("background")
                .build();
        User returnedUser = User
                .builder()
                .id(id)
                .name("name")
                .preferredCategories(List.of(category))
                .build();

        Mockito.when(userService.findById(id))
                .thenReturn(returnedUser);

        User user = authorizationOperation.findUser(id);

        Mockito.verify(userService).findById(id);

        Assertions.assertNotNull(user);
        Assertions.assertEquals(returnedUser.getName(), user.getName());
        Assertions.assertEquals(id, user.getId());
        Assertions.assertEquals(returnedUser.getPreferredCategories(), user.getPreferredCategories());
    }

    @Test
    @DisplayName("Check name user api test")
    void checkNameUser_ok() {
        Mockito.doNothing().when(userService).checkIfUserExists("name");

        authorizationOperation.checkName("name");

        Mockito.verify(userService).checkIfUserExists("name");
    }

    @Test
    @DisplayName("Update user api test")
    void updateUser_ok() {
        UUID id = UUID.randomUUID();
        Category category = Category
                .builder()
                .name("name")
                .id(UUID.randomUUID())
                .background("background")
                .build();
        UpdateUser updateUser = UpdateUser
                .builder()
                .id(id)
                .name("name")
                .preferredCategories(List.of(category))
                .build();
        User returnedUser = User
                .builder()
                .id(id)
                .name("name")
                .preferredCategories(List.of(category))
                .build();

        Mockito.when(userService.updateUser(updateUser))
                .thenReturn(returnedUser);

        User user = authorizationOperation.updateUser(updateUser);

        Mockito.verify(userService).updateUser(updateUser);

        Assertions.assertNotNull(user);
        Assertions.assertEquals(updateUser.getName(), user.getName());
        Assertions.assertEquals(updateUser.getId(), user.getId());
        Assertions.assertEquals(updateUser.getPreferredCategories(), user.getPreferredCategories());
    }

    @Test
    @DisplayName("Delete user api test")
    void deleteUser_ok() {
        UUID id = UUID.randomUUID();
        Mockito.doNothing().when(userService).deleteById(id);

        authorizationOperation.deleteUser(id);

        Mockito.verify(userService).deleteById(id);
    }
}
