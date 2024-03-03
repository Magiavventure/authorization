package it.magiavventure.authorization.model;

import it.magiavventure.mongo.model.Category;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUser {
    @NotNull
    private String name;
    private List<Category> preferredCategories;
    @NotNull
    private String avatar;
}
