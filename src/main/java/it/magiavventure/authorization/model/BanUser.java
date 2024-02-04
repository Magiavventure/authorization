package it.magiavventure.authorization.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BanUser {
    @NotNull
    private int duration;
    @NotNull
    private Unit unit;

    @Getter
    @RequiredArgsConstructor
    public enum Unit {
        MS ("ms"),
        S ("s"),
        M ("m"),
        H ("h"),
        D ("d");

        private final String value;
    }
}
