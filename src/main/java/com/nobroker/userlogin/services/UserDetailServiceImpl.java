
package com.nobroker.userlogin.services;

import com.nobroker.userlogin.entities.UserEntity;
import com.nobroker.userlogin.model.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailServiceImpl implements UserDetailsService {
    @Autowired
    UserService userService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userService.findUserByName(username);
        if(user == null)
            throw new UsernameNotFoundException("User Not Found");

        return new UserDetailsImpl(user);

    }
}

