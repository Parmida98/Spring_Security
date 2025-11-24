package com.parmida98.Spring_Security.user.mapper;

import com.parmida98.Spring_Security.user.CustomUser;
import com.parmida98.Spring_Security.user.dto.CustomUserCreationDTO;
import com.parmida98.Spring_Security.user.dto.CustomUserResponseDTO;
import org.springframework.stereotype.Component;

/** CustomUserMapper:
 *   Converts CustomUser to Entity.
 *   Converts Entity to UsernameDTO
 * */

@Component
public class CustomUserMapper {

    /*
    Metod som tar emot en CustomUserCreationDTO och returnerar en CustomUser (entitet).
Syfte:
Omvandla data från frontend (DTO) till en databas-färdig entitet.
     */
    public CustomUser toEntity(CustomUserCreationDTO customUserCreationDTO) {

        return new CustomUser(
                customUserCreationDTO.username(),                 // Hämtar användarnamnet från DTO:n och skickar in det till entitetens konstruktor.
                customUserCreationDTO.password(),                 // Hämtar lösenordet från DTO:n.
                customUserCreationDTO.isAccountNonExpired(),      // Hämtar alla konto-statusflaggor från DTO:n och skickar dem vidare till entiteten
                customUserCreationDTO.isAccountNonLocked(),       // --*--
                customUserCreationDTO.isCredentialsNonExpired(),  // --*--
                customUserCreationDTO.isEnabled(),                // --*--
                customUserCreationDTO.roles()                     // Hämtar rollerna från DTO:n och ger dem till entiteten.
        );
    }

    // Syfte:
    //Säker exponering av användardata till frontend.
    public CustomUserResponseDTO toUsernameDTO(CustomUser customUser) {

        // All känslig info (lösenord, roller, statusflaggor) filtreras bort här.
        return new CustomUserResponseDTO(customUser.getUsername());
    }

}
