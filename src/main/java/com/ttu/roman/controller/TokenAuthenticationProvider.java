package com.ttu.roman.controller;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.io.IOException;
import java.net.URISyntaxException;

public class TokenAuthenticationProvider implements AuthenticationProvider {

    private GoogleUserInfoProvider googleUserInfoProvider;

    public TokenAuthenticationProvider(GoogleUserInfoProvider googleUserInfoProvider) {
        this.googleUserInfoProvider = googleUserInfoProvider;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = (String) authentication.getPrincipal();
        try {
            User user = googleUserInfoProvider.getUserBy(token);
            return new UsernamePasswordAuthenticationToken(user, null, AuthorityUtils.createAuthorityList("ROLE_USER"));
        } catch (URISyntaxException | IOException e) {
            throw new InternalAuthenticationServiceException(e.getMessage());
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(PreAuthenticatedAuthenticationToken.class);
    }
}