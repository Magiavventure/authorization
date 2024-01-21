package it.magiavventure.authorization.service;

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

    public String loginById(UUID id) {
        return jwtService.buildJwt(User.builder().id(id).authorities(List.of("admin")).build());
    }
}
