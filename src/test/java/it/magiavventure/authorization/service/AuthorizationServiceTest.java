package it.magiavventure.authorization.service;

import it.magiavventure.authorization.mapper.UserMapper;
import it.magiavventure.authorization.model.LoginResponse;
import it.magiavventure.common.error.MagiavventureException;
import it.magiavventure.jwt.service.JwtService;
import it.magiavventure.mongo.entity.EUser;
import it.magiavventure.mongo.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
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
    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @Test
    @DisplayName("Login by id successfull")
    void loginById_ok() {
        UUID id = UUID.randomUUID();
        EUser eUser = EUser
                .builder()
                .id(id)
                .name("name")
                .build();

        Mockito.when(userService.findEntityById(id))
                .thenReturn(eUser);
        Mockito.when(jwtService.buildJwt(userArgumentCaptor.capture()))
                .thenReturn("token");

        LoginResponse loginResponse = authorizationService.loginById(id);

        Mockito.verify(userService).findEntityById(id);
        Mockito.verify(jwtService).buildJwt(userArgumentCaptor.capture());

        Assertions.assertNotNull(loginResponse);
        Assertions.assertEquals("token", loginResponse.getToken());
        User user = userArgumentCaptor.getValue();
        Assertions.assertEquals(user, loginResponse.getUser());
    }

    @Test
    @DisplayName("Login by id with expired ban")
    void loginById_withExpiredBan_ok() {
        UUID id = UUID.randomUUID();
        EUser eUser = EUser
                .builder()
                .id(id)
                .name("name")
                .banExpiration(LocalDateTime.now().minusMinutes(30))
                .build();

        Mockito.when(userService.findEntityById(id))
                .thenReturn(eUser);
        Mockito.when(jwtService.buildJwt(userArgumentCaptor.capture()))
                .thenReturn("token");

        LoginResponse loginResponse = authorizationService.loginById(id);

        Mockito.verify(userService).findEntityById(id);
        Mockito.verify(jwtService).buildJwt(userArgumentCaptor.capture());

        Assertions.assertNotNull(loginResponse);
        Assertions.assertEquals("token", loginResponse.getToken());
        User user = userArgumentCaptor.getValue();
        Assertions.assertEquals(user, loginResponse.getUser());
    }

    @Test
    @DisplayName("Login by id but user is banned")
    void loginById_userBanned() {
        UUID id = UUID.randomUUID();
        EUser eUser = EUser
                .builder()
                .id(id)
                .name("name")
                .banExpiration(LocalDateTime.now().plusMinutes(30))
                .build();

        Mockito.when(userService.findEntityById(id))
                .thenReturn(eUser);
        Mockito.doNothing().when(userService).evictUserCache(eUser);

        MagiavventureException exception = Assertions.assertThrows(MagiavventureException.class,
                () -> authorizationService.loginById(id));

        Mockito.verify(userService).evictUserCache(eUser);
        Mockito.verify(userService).findEntityById(id);

        Assertions.assertNotNull(exception);
        Assertions.assertEquals("user-blocked", exception.getError().getKey());
    }

}
