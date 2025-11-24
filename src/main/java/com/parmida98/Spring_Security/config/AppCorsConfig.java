package com.parmida98.Spring_Security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

// Vilka klienter som får anropa API:t
// Vilka metoder och headers som är tillåtna
// Om cookies får skickas
// Utan denna konfiguration hade browsers blockerat anrop från t.ex. React-frontend på annan port.

@Configuration
public class AppCorsConfig {

    @Bean //Säger till Spring att metoden under ska skapa ett objekt som ska hanteras av Spring som en bean i Application Context
    public CorsConfigurationSource corsConfigurationSource() { // metod för att veta vilka CORS-regler som ska gälla

        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // Whitelist
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:3000", "http://172.0.0.1:3000")); // VERCEL ADDRESS / DOMAIN / Anger vilka domäner som får göra requests till ditt backend-API.
        corsConfiguration.setAllowedMethods(List.of("GET", "POST"));                                    // HTTP METHODS / Bestämmer vilka HTTP-metoder som är tillåtna från dessa origins.
        corsConfiguration.setAllowedHeaders(List.of("Content-Type", "Authorization", "X-Requested-With")); // Session based? Unnecessary? // Specificerar vilka headers frontend får skicka med sina requests / Content-Type → används för JSON/form-data / Authorization → används för JWT eller Basic Auth / X-Requested-With → ofta använd av AJAX
        corsConfiguration.setAllowCredentials(true); // Send Cookies / Tillåter att cookies och autentiseringsuppgifter skickas mellan frontend och backend.

        // Backend related endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // source.registerCorsConfiguration("/api/v1/register", corsConfiguration); // Om de vore aktiva skulle CORS-reglerna
        // source.registerCorsConfiguration("/api/v1/who-am-i", corsConfiguration); // endast gälla för dessa två endpoints.
        source.registerCorsConfiguration("/**", corsConfiguration); // ENABLE EVERYTHING

        return source;
    }
}
