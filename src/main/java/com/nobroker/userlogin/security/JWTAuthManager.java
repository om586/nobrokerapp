package com.nobroker.userlogin.security;


import com.nobroker.userlogin.entities.UserEntity;
import com.nobroker.userlogin.services.JWTService;
import com.nobroker.userlogin.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
class JWTAuthManager implements AuthenticationManager {
    @Autowired
    private JWTService jwtService;
    @Autowired
    private UserService userService;

    @Override
    public JWTAuthenticationFilter.JWTAuthentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!(authentication instanceof JWTAuthenticationFilter.JWTAuthentication)) {
            throw new IllegalStateException("This Authentication Manager only deals with JWT Authentication");
        }
        JWTAuthenticationFilter.JWTAuthentication jwtAuth = (JWTAuthenticationFilter.JWTAuthentication) authentication;
        String username = jwtService.decodeJwt(jwtAuth.getCredentials());
        UserEntity userEntity = userService.findUserByName(username);
        jwtAuth.setUserEntity(userEntity);

        return jwtAuth;
    }
}