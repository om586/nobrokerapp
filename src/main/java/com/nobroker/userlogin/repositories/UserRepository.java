package com.nobroker.userlogin.repositories;

import com.nobroker.userlogin.entities.UserEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface UserRepository extends MongoRepository<UserEntity, ObjectId> {

            @Query(value = "{ username : ?0 }")
            UserEntity findUserEntityByUsername(String username);




}
