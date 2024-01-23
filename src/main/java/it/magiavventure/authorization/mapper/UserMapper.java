package it.magiavventure.authorization.mapper;

import it.magiavventure.mongo.entity.EUser;
import it.magiavventure.mongo.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    User map(EUser user);
}
