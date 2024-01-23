package it.magiavventure.authorization.service;

import it.magiavventure.authorization.model.LoginResponse;
import it.magiavventure.jwt.service.JwtService;
import it.magiavventure.mongo.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@DisplayName("Authorization service tests")
class AuthorizationServiceTest {

    @InjectMocks
    private AuthorizationService authorizationService;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserService userService;

    @Test
    @DisplayName("Login by id successfull")
    void loginById_ok() {
        UUID id = UUID.randomUUID();
        User user = User
                .builder()
                .id(id)
                .name("name")
                .build();

        Mockito.when(userService.findById(id))
                .thenReturn(user);
        Mockito.when(jwtService.buildJwt(user))
                .thenReturn("token");

        LoginResponse loginResponse = authorizationService.loginById(id);

        Mockito.verify(userService).findById(id);
        Mockito.verify(jwtService).buildJwt(user);

        Assertions.assertEquals("token", loginResponse.getToken());
        Assertions.assertEquals(user, loginResponse.getUser());
    }

}
