package com.parmida98.Spring_Security.config;

import com.parmida98.Spring_Security.security.jwt.JwtAuthenticationFilter;
import com.parmida98.Spring_Security.user.authority.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity // säger att den här klassen innehåller säkerhetskonfiguration.
public class AppSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    public AppSecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) { // lagrar det injicerade filtret i fältet så du kan använda det senare i securityFilterChain.
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager(); // Handles: Password Encoder, UserDetailsService, Authentication
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception { // definierar hela säkerhetskedjan: vilka filter som används, vilka endpoints som är skyddade, sessionpolicy osv.

        /* TODO - If we try to move to a resource that isn't available, we have to login to get a 404
         *   This is unclear and can be made better
         *   Why login to see a 404? Is this secure?
         * */

        // TODO Memory Storage Attack - https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/erasure.html

        httpSecurity // Startar en konfiguration kedja på httpSecurity-objektet. Du bygger nu upp alla regler med "fluent API" (kedjade anrop).
                .csrf(csrfConfigurer -> csrfConfigurer.disable())   // TODO - JWT, best practice? // en lambda som säger "stäng av CSRF".
                .authorizeHttpRequests( auth -> auth                      // Startar konfigurationen för autorisering – dvs vilka requests som får göras av vem.
                        // .requestMatchers() // TODO - check against specific HTTP METHOD
                        .requestMatchers("/", "/register", "/static/**", "/login").permitAll()  // Allow localhost:8080/ / ska vara öppna för alla (ingen inloggning krävs). .permitAll() → betyder att dessa endpoints inte kräver autentisering.
                        .requestMatchers("/debug/**").permitAll()                                     // RestController for Debugging / Tillåter alla requests som matchar /debug/** utan inloggning.
                        .requestMatchers("/admin", "/tools").hasRole("ADMIN")                       // kräver att användaren har rollen ROLE_ADMIN.
                        .requestMatchers("/user").hasRole(UserRole.USER.name())                       // endpointen /user kräver en roll som motsvarar UserRole.USER.name().
                        .anyRequest().authenticated() // MUST exist AFTER matchers, TODO - Is this true by DEFAULT? // Alla andra requests som inte matchade något av ovan ska kräva autentisering.
                )

                // TODO - If you want (optional), insert configure logic here for CORS

                .sessionManagement(session -> session           //konfigurerar hur Spring ska skapa och hantera HTTP-sessioner.
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

}