package com.ttu.roman.controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


public class AuthenticationFilter extends GenericFilterBean {
    private static final String AUTH_TOKEN_PARAM = "authToken";
    private AuthenticationManager authenticationManager;

    public AuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        if (authentication == null) {
            authenticate(request);
        }
        HttpServletResponse httpResponse = cast(response);
        HttpServletRequest httpRequest = cast(request);

        long maxInactiveIntervalInMs = httpRequest.getSession().getMaxInactiveInterval() * 1000;

        httpResponse.setHeader("expiresAt", String.valueOf(maxInactiveIntervalInMs));
        filterChain.doFilter(request, response);
    }

    private void authenticate(ServletRequest request) {
        HttpServletRequest httpRequest = cast(request);
        String authToken = httpRequest.getParameter(AUTH_TOKEN_PARAM);

        if (authToken != null) {
            Authentication resultOfAuthentication = authenticateWithToken(authToken);
            SecurityContextHolder.getContext().setAuthentication(resultOfAuthentication);
        } else {
            throw new InternalAuthenticationServiceException("tokenRequiredForAuth");
        }
    }

    private Authentication authenticateWithToken(String token) {
        PreAuthenticatedAuthenticationToken requestAuthentication = new PreAuthenticatedAuthenticationToken(token, null);
        return tryToAuthenticate(requestAuthentication);
    }

    private Authentication tryToAuthenticate(Authentication requestAuthentication) {
        Authentication responseAuthentication = authenticationManager.authenticate(requestAuthentication);
        if (responseAuthentication == null || !responseAuthentication.isAuthenticated()) {
            throw new InternalAuthenticationServiceException("Unable to authenticate User");
        }
        logger.debug("User successfully authenticated");
        return responseAuthentication;
    }

    @SuppressWarnings("unchecked")
    private static  <T> T cast(Object o) {
        return (T) o;
    }
}
