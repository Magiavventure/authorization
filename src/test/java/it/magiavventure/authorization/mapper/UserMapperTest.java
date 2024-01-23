package it.magiavventure.authorization.mapper;

import it.magiavventure.mongo.entity.EUser;
import it.magiavventure.mongo.model.Category;
import it.magiavventure.mongo.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@DisplayName("User mapping tests")
class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    @DisplayName("Map User entity in User dto object")
    void mapEUser_asUser_ok() {
        EUser eUser = EUser
                .builder()
                .id(UUID.randomUUID())
                .active(true)
                .name("name")
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .version(1L)
                .preferredCategories(List.of(Category
                        .builder()
                        .id(UUID.randomUUID())
                        .background("background")
                        .name("name")
                        .build()))
                .build();

        User user = userMapper.map(eUser);

        Assertions.assertNotNull(user);
        Assertions.assertEquals(eUser.getId(), user.getId());
        Assertions.assertEquals(eUser.getName(), user.getName());
        Assertions.assertEquals(eUser.getPreferredCategories(), user.getPreferredCategories());
    }

    @Test
    @DisplayName("Map User entity without preferredCategories in User dto object")
    void mapEUser_asUser_withoutPreferredCategories() {
        EUser eUser = EUser
                .builder()
                .id(UUID.randomUUID())
                .active(true)
                .name("name")
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .version(1L)
                .build();

        User user = userMapper.map(eUser);

        Assertions.assertNotNull(user);
        Assertions.assertEquals(eUser.getId(), user.getId());
        Assertions.assertEquals(eUser.getName(), user.getName());
        Assertions.assertNull(user.getPreferredCategories());
    }


    @Test
    @DisplayName("Map User entity null in User dto object")
    void mapEUser_asUser_null() {
        User user = userMapper.map(null);

        Assertions.assertNull(user);
    }
}
