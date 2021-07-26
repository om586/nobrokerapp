package com.nobroker.userlogin.security;

import com.nobroker.userlogin.entities.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

@Component
public class JWTAuthenticationFilter extends AuthenticationFilter {
    @Autowired
    JWTAuthManager jwtAuthManager;

    public static class JWTAuthentication implements Authentication {
        private final String jwtString;
        private UserEntity userEntity;

        public void setUserEntity(UserEntity userEntity) {
            this.userEntity = userEntity;
        }

        public JWTAuthentication(String jwtString) {
            this.jwtString = jwtString;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return null;
        }

        @Override
        public String getCredentials() {
            return jwtString;
        }

        @Override
        public Object getDetails() {
            return null;
        }

        @Override
        public UserEntity getPrincipal() {
            return userEntity;
        }

        @Override
        public boolean isAuthenticated() {
            return userEntity != null;
        }

        @Override
        public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

        }

        @Override
        public String getName() {
            return null;
        }
    }

    static class Converter implements AuthenticationConverter {


        @Override
        public Authentication convert(HttpServletRequest request) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null) {

                return null;
            }

            if (!authHeader.startsWith("Token ")) {

                return null;
            }

            String jwts = authHeader.replace("Token ", "");

            return new JWTAuthentication(jwts);
        }
    }

    public JWTAuthenticationFilter(JWTAuthManager jwtAuthManager) {
        super(jwtAuthManager, new Converter());
        this.setSuccessHandler((request, response, authentication) -> {
            SecurityContextHolder.getContext().setAuthentication(authentication);
        });
    }


}