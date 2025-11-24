package com.parmida98.Spring_Security.security.jwt;

import com.parmida98.Spring_Security.user.CustomUser;
import com.parmida98.Spring_Security.user.authority.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecureDigestAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

// Denna klass ansvarar för:
//✅ Skapa JWT-token
//✅ Signera dem säkert
//✅ Plocka ut användarnamn
//✅ Plocka ut roller
//✅ Validera tokens
//✅ Hämta token från cookies eller headers


// gör klassen till en Spring-bean som kan injiceras i andra klasser.
@Component
public class JwtUtils { // Klassen samlar all logik kring skapande, läsning och validering av JWT.

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    // En hårdkodad hemlig nyckel i Base64-format som används för att signera och verifiera JWT-tokens.
    private final String base64EncodedSecretKey = "U2VjdXJlQXBpX1NlY3JldEtleV9mb3JfSFMyNTYwX3NlY3JldF9wcm9qZWN0X2tleV9leGFtcGxl";

    // Avkodar Base64-strängen till råa bytes som kan användas kryptografiskt.
    private final byte[] keyBytes = Base64.getDecoder().decode(base64EncodedSecretKey);

    // Avkodar Base64-strängen till råa bytes som kan användas kryptografiskt.
    private final SecretKey key = Keys.hmacShaKeyFor(keyBytes); // HMAC algorithm

    // Skapar en SecretKey baserad på bytesen, avsedd för HMAC-signering av JWT (symmetrisk nyckel).
    private final int jwtExpirationMs = (int) TimeUnit.HOURS.toMillis(1);   // TODO - Check Expiration

    // Definierar giltighetstiden för JWT till 1 timme uttryckt i millisekunder.
    public String generateJwtToken(CustomUser customUser) { // TODO - CustomUserDetails
        logger.debug("Generating JWT for user: {} with roles: {}", customUser.getUsername(), customUser.getRoles());

        // Hämtar roller från användaren. Konverterar varje UserRole till dess strängnamn. Resultatet blir en lista av rollnamn som ska in i token.
        List<String> roles = customUser.getRoles().stream().map(
                userRole -> userRole.getRoleName()
        ).toList();

        String token = Jwts.builder()
                .subject(customUser.getUsername())      // Sätter subject (sub) i token till användarnamnet.
                .claim("authorities", roles)         // Sätter subject (sub) i token till användarnamnet.
                .issuedAt(new Date())                   // Skapar en custom claim med namnet authorities som innehåller användarens roller.
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs)) // Sätter när token ska löpa ut (exp).
                .signWith(key)                          // Signerar token med din hemliga HMAC-nyckel. TODO - signWith using a predefined Algorithm. //.signWith(key, SignatureAlgorithm)
                .compact();                             // Bygger ihop och serialiserar token till en sträng.

        logger.info("JWT generated successfully for user: {}", customUser.getUsername());
        return token;
    }

    public String getUsernameFromJwtToken(String token) {
        try {
            // Parsar token -> Verifierar signaturen med nyckeln -> Extraherar dess claims (innehåll)
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            String username = claims.getSubject(); // Subject, whom the token refers to, principal: whose currently authenticated (system)
            logger.debug("Extracted username '{}' from JWT token", username);
            return username;

        } catch (Exception e) {
            logger.warn("Failed to extract username from token: {}", e.getMessage());
            return null;
        }
    }

    public Set<UserRole> getRolesFromJwtToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        List<?> authoritiesClaim = claims.get("authorities", List.class); // Hämtar claimen authorities som en lista.

        if (authoritiesClaim == null || authoritiesClaim.isEmpty()) {
            logger.warn("No authorities found in JWT token");
            return Set.of();
        }

        // Convert each string like "ROLE_USER" -> UserRole.USER
        Set<UserRole> roles = authoritiesClaim.stream()         // Startar konverteringskedja.
                .filter(String.class::isInstance)               // keep only strings
                .map(String.class::cast)                        // Castar till String.
                .map(role -> role.replace("ROLE_", "")) // remove prefix if necessary
                .map(String::toUpperCase)
                .map(UserRole::valueOf)                         // Konverterar strängen till enum-värde UserRole.
                .collect(Collectors.toSet());                   // Samlar resultatet i en Set.

        logger.debug("Extracted roles from JWT token: {}", roles);
        return roles;
    }

    // Used to pass in JWT token for Validation
    public boolean validateJwtToken(String authToken) {
        try {
            // Försöker parsa och verifiera token. Misslyckas om:
            //token är ogiltig
            //fel signatur
            //utgången
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(authToken);

            logger.debug("JWT validation succeeded");
            return true;

        } catch (Exception e) {
            logger.error("JWT validation failed: {}", e.getMessage());
        }

        return false;
    }


    // Helper: Extract JWT from cookie
    String extractJwtFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if ("authToken".equals(cookie.getName())) {     // Cookie should be named authToken
                return cookie.getValue();
            }
        }
        return null;
    }

    // Helper: Extract JWT from Authorization header
    String extractJwtFromRequest(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);     // Tar bort "Bearer " och returnerar själva token.
        }
        return null;
    }
}
