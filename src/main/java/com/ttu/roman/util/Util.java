package com.ttu.roman.util;

import com.ttu.roman.auth.User;
import org.springframework.security.core.context.SecurityContextHolder;

public class Util {

    public static User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
