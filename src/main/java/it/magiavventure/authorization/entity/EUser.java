package it.magiavventure.authorization.entity;

import it.magiavventure.mongo.model.Category;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Document(collection = "user")
public class EUser {
    @Id
    private UUID id;
    @NotNull
    @Indexed(unique = true)
    private String name;
    private List<Category> preferredCategories;
    private boolean active;
    @CreatedDate
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    @Version
    private Long version;
}
