package it.magiavventure.authorization.operation;

import it.magiavventure.authorization.model.*;
import it.magiavventure.authorization.service.AuthorizationService;
import it.magiavventure.authorization.service.UserService;
import it.magiavventure.jwt.service.JwtService;
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
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@DisplayName("Authorization operation tests")
class AuthorizationOperationTest {

    @InjectMocks
    private AuthorizationOperation authorizationOperation;

    @Mock
    private AuthorizationService authorizationService;
    @Mock
    private JwtService jwtService;

    @Mock
    private UserService userService;

    @Test
    @DisplayName("Login user by id")
    void loginUser_byId() {
        Login login = Login.builder().id(UUID.randomUUID()).build();
        LoginResponse loginResponse = LoginResponse
                .builder()
                .user(User.builder().id(UUID.randomUUID()).build())
                .token("token")
                .build();
        MockHttpServletResponse response = new MockHttpServletResponse();

        Mockito.when(authorizationService.loginById(login.getId()))
                .thenReturn(loginResponse);
        Mockito.when(jwtService.getTokenHeader())
                .thenReturn("mg-a-token");

        User user = authorizationOperation.loginById(login, response);

        Mockito.verify(authorizationService).loginById(login.getId());
        Mockito.verify(jwtService).getTokenHeader();

        Assertions.assertNotNull(user);
        Assertions.assertEquals(user, loginResponse.getUser());
        Assertions.assertEquals("token", loginResponse.getToken());
        Assertions.assertTrue(response.containsHeader("mg-a-token"));
        Assertions.assertEquals("token", response.getHeader("mg-a-token"));
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
                .avatar("avatar")
                .preferredCategories(List.of(category))
                .build();
        User returnedUser = User
                .builder()
                .id(UUID.randomUUID())
                .name("name")
                .avatar("avatar")
                .preferredCategories(List.of(category))
                .build();

        Mockito.when(userService.createUser(createUser))
                .thenReturn(returnedUser);

        User user = authorizationOperation.createUser(createUser);

        Mockito.verify(userService).createUser(createUser);

        Assertions.assertNotNull(user);
        Assertions.assertEquals(createUser.getName(), user.getName());
        Assertions.assertEquals(createUser.getAvatar(), user.getAvatar());
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
                .avatar("avatar")
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
                            Assertions.assertEquals(returnedUser.getAvatar(), user.getAvatar());
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
                .avatar("avatar")
                .preferredCategories(List.of(category))
                .build();

        Mockito.when(userService.findById(id))
                .thenReturn(returnedUser);

        User user = authorizationOperation.findUser(id);

        Mockito.verify(userService).findById(id);

        Assertions.assertNotNull(user);
        Assertions.assertEquals(returnedUser.getName(), user.getName());
        Assertions.assertEquals(returnedUser.getAvatar(), user.getAvatar());
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
                .avatar("avatar")
                .preferredCategories(List.of(category))
                .build();
        User returnedUser = User
                .builder()
                .id(id)
                .name("name")
                .avatar("avatar")
                .preferredCategories(List.of(category))
                .build();

        Mockito.when(userService.updateUser(updateUser))
                .thenReturn(returnedUser);

        User user = authorizationOperation.updateUser(updateUser);

        Mockito.verify(userService).updateUser(updateUser);

        Assertions.assertNotNull(user);
        Assertions.assertEquals(updateUser.getName(), user.getName());
        Assertions.assertEquals(updateUser.getAvatar(), user.getAvatar());
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

    @Test
    @DisplayName("Given id and duration of ban proceed with ban user")
    void givenIdAndBanDuration_proceedWithBanUser_ok() {
        UUID id = UUID.randomUUID();
        BanUser banUser = BanUser
                .builder()
                .duration(10)
                .unit(BanUser.Unit.M)
                .build();

        Mockito.when(userService.banUser(id, banUser))
                .thenReturn(User.builder().build());

        User user = authorizationOperation.banUser(id, banUser);

        Mockito.verify(userService).banUser(id, banUser);

        Assertions.assertNotNull(user);
    }

    @Test
    @DisplayName("Given id elevate user")
    void givenId_elevateUser_ok() {
        UUID id = UUID.randomUUID();

        Mockito.when(userService.giveAdminAuthorityToUser(id))
                .thenReturn(User.builder().build());

        User user = authorizationOperation.elevateUser(id);

        Mockito.verify(userService).giveAdminAuthorityToUser(id);

        Assertions.assertNotNull(user);
    }
}
