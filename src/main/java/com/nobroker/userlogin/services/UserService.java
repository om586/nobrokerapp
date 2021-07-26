package com.nobroker.userlogin.services;

import com.nobroker.userlogin.entities.UserEntity;
import com.nobroker.userlogin.repositories.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }


    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException() {
            super("No such user found");
        }
    }

    public static class UserPasswordIncorectException extends RuntimeException {
        public UserPasswordIncorectException() {
            super("Invalid Password");
        }
    }

    public static class UserRegisteredException extends RuntimeException{
        UserRegisteredException(){
            super("user has been registered earlier");
        }
    }


    public static BCryptPasswordEncoder bcryptEncoder() {
        return new BCryptPasswordEncoder();
    }


    public UserEntity registerNewUser(String username, String password, String role){

        if(userRepository.findUserEntityByUsername(username) != null)
            throw new UserRegisteredException();
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(bcryptEncoder().encode(password));
        user.setRole(role);
        return userRepository.save(user);
    }

    public UserEntity findUserByName(String username){
        UserEntity user = userRepository.findUserEntityByUsername(username);
        if(user == null)
                throw new UserNotFoundException();
        return user;
    }

    public List<UserEntity> findAll(){
        return userRepository.findAll();
    }

    public UserEntity upgradeUserRole(String username, String role){
        UserEntity user = findUserByName(username);
        if(role.equals("user"))
            user.setRole("superuser");
        else
            user.setRole("user");
        return userRepository.save(user);
    }

    public void deleteUser(String username){
        UserEntity user = userRepository.findUserEntityByUsername(username);
        if(user == null)
            throw new UserNotFoundException();
        userRepository.delete(user);
    }

    public UserEntity verifyUser(String username, String password) {
        UserEntity user = userRepository.findUserEntityByUsername(username);
        if (user == null)
            throw new UserNotFoundException();

        if (!bcryptEncoder().matches(password, user.getPassword()))
            throw new UserPasswordIncorectException();


        return user;
    }

}
