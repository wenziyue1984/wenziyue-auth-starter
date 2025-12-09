package com.wenziyue.auth.starter.helper;

import com.wenziyue.auth.core.model.LoginUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author wenziyue
 */
public final class AuthHelper {

    private AuthHelper() {}

    public static LoginUser currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return null;
        }
        Object principal = auth.getPrincipal();
        return principal instanceof LoginUser ? (LoginUser) principal : null;
    }

    public static String currentUserId() {
        LoginUser u = currentUser();
        return u == null ? null : u.getUserId();
    }

}
