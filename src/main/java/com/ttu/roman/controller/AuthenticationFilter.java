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
import java.io.IOException;
import java.util.Date;


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

        if (authentication != null) {
            User user = (User) authentication.getPrincipal();
            Date expiresIn = user.getTokenInfo().getExpiresIn();
            if (expiresIn.after(new Date())) {
                SecurityContextHolder.clearContext();
            } else {
                return;
            }
        }

        authenticate(request);
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
    private <T> T cast(ServletRequest request) {
        return (T) request;
    }
}
