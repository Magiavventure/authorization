package it.magiavventure.authorization.model;

import it.magiavventure.mongo.model.User;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    @NotNull
    User user;
    @NotNull
    String token;
}
