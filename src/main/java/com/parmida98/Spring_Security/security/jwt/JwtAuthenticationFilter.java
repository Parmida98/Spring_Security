package com.parmida98.Spring_Security.security.jwt;

import com.parmida98.Spring_Security.user.CustomUserDetailsService;
import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // säger till Spring att den här klassen är en bean som ska skapas automatiskt och kunna injiceras.
public class JwtAuthenticationFilter extends OncePerRequestFilter {     // gör detta till ett Servlet-filter som körs en gång per request.

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService customUserDetailsService; // TODO - Change to UserDetailsService

    @Autowired
    public JwtAuthenticationFilter(JwtUtils jwtUtils,
                                   CustomUserDetailsService customUserDetailsService) {
        this.jwtUtils = jwtUtils;
        this.customUserDetailsService = customUserDetailsService;
    }

    // metoden som körs för varje HTTP-request.
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,    // inkommande HTTP-request (headers, cookies, body, etc.)
            @NonNull HttpServletResponse response,  // nkommande HTTP-request (headers, cookies, body, etc.)
            @NonNull FilterChain filterChain        // Decides when a filterChain stops / används för att skicka vidare request/response till nästa filter i kedjan
    ) throws ServletException, IOException {

        logger.debug("---- JwtAuthenticationFilter START ----");

        // Extract token
        String token = jwtUtils.extractJwtFromCookie(request);  // Försöker först läsa JWT-token från en cookie i requesten.
        if (token == null) {
            token = jwtUtils.extractJwtFromRequest(request); // fallback to Authorization header / försök igen genom att läsa från t.ex. Authorization-headern (Bearer <token>)
        }

        if (token == null) {
            logger.debug("No JWT token found in request");
            filterChain.doFilter(request, response);    // släpper igenom requesten vidare utan att sätta någon authentication
            return;
        }

        logger.debug("JWT token found: {}", token);

        // Kontrollerar om token är:
        // korrekt signerad
        // inte utgången (expiration)
        // har rätt struktur
        // Om validateJwtToken returnerar true går vi vidare.
        // Validate token
        if (jwtUtils.validateJwtToken(token)) {
            String username = jwtUtils.getUsernameFromJwtToken(token);

            // Det här gör att vi inte skriver över en redan autentiserad användare.
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Live DB lookup (ensures user still exists / is enabled)
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username); // ENTITY

                // Possibility to check for other userDetails booleans
                if (userDetails != null && userDetails.isEnabled()) {
                    // Detta objekt representerar en autentiserad användare i Spring Security.
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    // Update Spring with possible new change
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Sätter in authentication i SecurityContext
                    // SecurityContext hålls thread-local per request
                    // Efter denna rad betraktas användaren som inloggad/autentiserad i resten av request-flödet (controllers, services osv.).
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    logger.debug("Authenticated (DB verified) user '{}'", username);
                } else {
                    logger.warn("User '{}' not found or disabled", username);
                }
            }
        } else {
            logger.warn("Invalid JWT token");
        }

        // Continue the filter chain / skickar vidare request/response till nästa filter eller till slut själva controller-metoden.
        filterChain.doFilter(request, response);
        logger.debug("---- JwtAuthenticationFilter END ----");
    }

}
