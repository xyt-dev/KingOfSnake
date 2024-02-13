package com.kos.backend.service.impl.utils;

import com.kos.backend.pojo.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

public class GetUserData {
    public static User getUserData() {
        UsernamePasswordAuthenticationToken autyhenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) autyhenticationToken.getPrincipal();
        return loginUser.getUser();
    }
}
