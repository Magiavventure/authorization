package it.magiavventure.authorization.error;

import it.magiavventure.common.error.MagiavventureException;
import it.magiavventure.common.model.Error;
import lombok.Getter;

@Getter
public class AuthorizationException extends MagiavventureException {
    public static final String USER_EXISTS = "user-exists";
    public static final String USER_NOT_FOUND = "user-not-found";
    public static final String USER_BLOCKED= "user-blocked";
    public AuthorizationException(Error error) {
        super(error);
    }
}
