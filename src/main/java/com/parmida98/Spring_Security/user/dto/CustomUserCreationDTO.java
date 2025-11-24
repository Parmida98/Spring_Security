package com.parmida98.Spring_Security.user.dto;

import com.parmida98.Spring_Security.user.authority.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.Set;
/*
Denna record:
✔ Tar emot användardata vid registrering
✔ Validerar användarnamn och lösenord
✔ Säkerställer korrekt kontostatus
✔ Kräver minst en roll
✔ Skyddar systemet från ogiltig input
 */
/*
Records är immutable dataobjekt.
Används typiskt för att ta emot data från frontend (request body).
Spring mappar JSON automatiskt till denna record.
 */
public record CustomUserCreationDTO(

        @Size(min = 2, max = 25, message = "Username length should be between 2-25")
        @NotBlank(message = "Username may not contain whitespace characters only")
        String username,

        @Pattern( // Start på regex-validering för lösenordet.
                regexp = "^" +
                        "(?=.*[a-z])" +        // at least one lowercase letter
                        "(?=.*[A-Z])" +        // at least one uppercase letter
                        "(?=.*[0-9])" +        // at least one digit
                        "(?=.*[ @$!%*?&])" +   // at least one special character
                        ".+$",                 // one or more characters, until end
                message = "Password must contain at least one uppercase, one lowercase, one digit, and one special character"
        )
        @Size(max = 80, message = "Maximum length of password exceeded")
        String password,

        @NotNull boolean isAccountNonExpired,
        @NotNull boolean isAccountNonLocked,
        @NotNull boolean isCredentialsNonExpired,
        @NotNull boolean isEnabled,
        // @NotNull @AssertTrue boolean acceptAppTerms, // Expect the result NOT to be null, NOT to be False

        @NotEmpty // Map, Collections, Array
        @Pattern( // Försöker validera att rollen matchar något av dessa värden:
                regexp = "^(GUEST|USER|ADMIN)$",        // TODO - Util function, loops through each ENUM: secure type-safety
                message = "Must be a Valid Role"
        )
        Set<UserRole> roles

) {
}
