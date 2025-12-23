package com.wenziyue.auth.starter.filter;

import com.wenziyue.auth.core.constants.AuthConstants;
import com.wenziyue.auth.core.header.HeaderUtils;
import com.wenziyue.auth.core.model.LoginUser;
import lombok.var;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wenziyue
 */
public class HeaderAuthFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            String userInfoHeader = request.getHeader(AuthConstants.USER_INFO_HEADER);

            if (StringUtils.hasText(userInfoHeader)) {
                LoginUser loginUser = HeaderUtils.parseUserInfoFromHeader(userInfoHeader);

                if (loginUser != null && StringUtils.hasText(loginUser.getUserId())) {
                    List<SimpleGrantedAuthority> authorities = buildAuthorities(loginUser.getRoles());

                    var authentication = new UsernamePasswordAuthenticationToken(loginUser, null, authorities);

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    request.setAttribute("loginUser", loginUser);
                }
            }

            filterChain.doFilter(request, response);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    private List<SimpleGrantedAuthority> buildAuthorities(String role) {
        if (!StringUtils.hasText(role)) {
            return Collections.emptyList();
        }
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }
}
