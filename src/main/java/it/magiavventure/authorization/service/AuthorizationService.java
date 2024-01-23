package it.magiavventure.authorization.service;

import it.magiavventure.authorization.model.LoginResponse;
import it.magiavventure.jwt.service.JwtService;
import it.magiavventure.mongo.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final JwtService jwtService;
    private final UserService userService;

    public LoginResponse loginById(UUID id) {
        User user = userService.findById(id);
        String token = jwtService.buildJwt(user);
        return LoginResponse
                .builder()
                .user(user)
                .token(token)
                .build();
    }
}
