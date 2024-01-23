package it.magiavventure.authorization.operation;

import it.magiavventure.authorization.model.CreateUser;
import it.magiavventure.authorization.model.Login;
import it.magiavventure.authorization.model.LoginResponse;
import it.magiavventure.authorization.model.UpdateUser;
import it.magiavventure.authorization.service.AuthorizationService;
import it.magiavventure.authorization.service.UserService;
import it.magiavventure.jwt.service.JwtService;
import it.magiavventure.mongo.model.User;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class AuthorizationOperation {

    private final AuthorizationService authorizationService;
    private final UserService userService;
    private final JwtService jwtService;
    
    @PostMapping("/loginById")
    public User loginById(@RequestBody @Valid Login login, HttpServletResponse response) {
        LoginResponse loginResponse = authorizationService.loginById(login.getId());
        response.setHeader(jwtService.getTokenHeader(), loginResponse.getToken());
        return loginResponse.getUser();
    }

    @PostMapping("/saveUser")
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody @Valid CreateUser createUser) {
        return userService.createUser(createUser);
    }

    @GetMapping("/retrieveUsers")
    public List<User> findAllUser() {
        return userService.findAll();
    }
    @GetMapping("/retrieveUser/{id}")
    public User findUser(@PathVariable(name = "id") UUID id) {
        return userService.findById(id);
    }

    @GetMapping("/checkUserName/{name}")
    public void checkName(@PathVariable(name = "name") String name) {
        userService.checkIfUserExists(name);
    }

    @PutMapping("/updateUser")
    public User updateUser(@RequestBody @Valid UpdateUser updateUser) {
        return userService.updateUser(updateUser);
    }

    @DeleteMapping("/deleteUser/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable(name = "id") UUID id) {
        userService.deleteById(id);
    }


}
