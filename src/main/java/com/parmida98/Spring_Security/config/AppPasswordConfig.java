package com.parmida98.Spring_Security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// Alla lösenord kommer hashas med BCrypt (styrka 12)
//Spring Security kommer använda exakt denna bean när en användare:
//registreras
//loggar in
//autentiseras via JWT


@Configuration
public class AppPasswordConfig {

    /* Password Hashing: Encoder / Encoder = processen att hasha lösenord
     *   The Encoder == password hashing (Generic Term)
     *   Class Abstraction to be used == PasswordEncoder
     * */

    @Bean
    public PasswordEncoder defaultPasswordEncoder() { // Spring kommer automatiskt att använda denna bean för att: hasha lösenord vid registrering verifiera lösenord vid inloggning

        /* Implementation of PasswordEncoder that uses the BCrypt strong hashing function.
        Clients can optionally supply a "version" ($2a, $2b, $2y) and
        a "strength" (a. k. a. log rounds in BCrypt) and a SecureRandom instance.

        The larger the strength parameter the more work will have to be done (exponentially)
        to hash the passwords. The default value is 10. */

        /* Iterations / Strength
         *   More iterations == More Secure
         *   Higher Value == Higher CPU Cost
         *   NOTE: Can lead to potential Bottleneck (Performance Issues)
         * */

        return new BCryptPasswordEncoder(12); // Default Strength 10 (Iterations)
    }

}
