package com.parmida98.Spring_Security.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/*
Denna DTO hjälper till att:
✅ Förebygga data leakage
✅ Skydda känslig användarinformation
✅ Tvinga backend att vara tydlig med vad som exponeras
 */
/** CustomUserResponseDTO
 *   Exposes username field only.
 *   Use CustomUserResponseDTO to hide sensitive information.
 *   TODO - Repeating Code, breaks DRY principle. Check CustomUserCreationDTO for duplicate code
 * */

public record CustomUserResponseDTO(
        @Size(min = 2, max = 25, message = "Username length should be between 2-25")
        @NotBlank(message = "Username may not contain whitespace characters only")
        String username
) {
}

/* DRY principen:
Det renaste sättet att lösa detta i Spring/Bean Validation är att skapa en egen sammansatt annotation, t.ex. @ValidUsername,
och använda den överallt.

@Documented
@Constraint(validatedBy = {}) // ingen egen validator behövs när vi bara komponerar
@Target({ FIELD, PARAMETER })
@Retention(RUNTIME)
@Size(min = 2, max = 25, message = "Username length should be between 2-25")
@NotBlank(message = "Username may not contain whitespace characters only")
public @interface ValidUsername {

    String message() default "Invalid username";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

Använd @ValidUsername i dina DTO:er
före:
public record CustomUserCreationDTO(

        @Size(min = 2, max = 25, message = "Username length should be between 2-25")
        @NotBlank(message = "Username may not contain whitespace characters only")
        String username,
        ...
) {}

efter:
public record CustomUserCreationDTO(

        @ValidUsername
        String username,
        ...
) {}

Samma med lösenord:
Skapa t.ex. @ValidPassword
Flytta regex + @Size dit
Använd @ValidPassword i stället för att kopiera regex överallt
 */