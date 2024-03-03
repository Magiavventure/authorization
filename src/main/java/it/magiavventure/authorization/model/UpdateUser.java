package it.magiavventure.authorization.model;

import it.magiavventure.mongo.model.Category;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUser {
    @NotNull
    private UUID id;
    @NotNull
    private String name;
    @NotNull
    private String avatar;
    private List<Category> preferredCategories;
}
