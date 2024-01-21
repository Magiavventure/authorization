package it.magiavventure.authorization.repository;

import it.magiavventure.authorization.entity.EUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends MongoRepository<EUser, UUID> {

}
