package com.parmida98.Spring_Security.view;

import com.parmida98.Spring_Security.user.CustomUser;
import com.parmida98.Spring_Security.user.CustomUserRepository;
import com.parmida98.Spring_Security.user.authority.UserRole;
import com.parmida98.Spring_Security.user.dto.CustomUserCreationDTO;
import com.parmida98.Spring_Security.user.mapper.CustomUserMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Set;

/*
Denna controller:
✔ Hanterar server-renderade vyer
✔ Skapar nya användare
✔ Validerar inmatning
✔ Använder mapper för ren kod
✔ Implementerar grundläggande säkerhetslogik
✔ Är bryggan mellan frontend-formulär och databasen
 */
@Controller
public class CustomViewController {

    // TODO - Replace with Service in the future
    private final CustomUserRepository customUserRepository;    // sparar användare i databasen
    private final PasswordEncoder passwordEncoder;              // hashning av lösenord
    private final CustomUserMapper customUserMapper;            //konverterar DTO → Entity

    @Autowired
    public CustomViewController(CustomUserRepository customUserRepository, PasswordEncoder passwordEncoder, CustomUserMapper customUserMapper) {
        this.customUserRepository = customUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.customUserMapper = customUserMapper;
    }

    /*
    När användaren går till /path:
    Returneras view-namnet "pathen"
    Spring letar efter pathen.html i templates.
     */
    @GetMapping("/login")
    public String loginPage() {

        return "login";
    }

    @GetMapping("/logout")
    public String logoutPage() {

        return "logout";
    }

    // Åtkomst styrs av Spring Security-konfigurationen (t.ex. endast ADMIN).
    @GetMapping("/admin")
    public String adminPage() {

        return "adminpage"; // Must Reflect .html document name
    }

    @GetMapping("/user")
    public String userPage() {

        return "userpage";
    }

    // Responsible for Inserting CustomUser Entity (otherwise DTO)
    // När /register öppnas via GET visas registreringsformuläret.
    @GetMapping("/register")
    public String registerPage(Model model) {

        // Best practice: id aka AttributeName should be the same as object name
        // Lägger till ett objekt i model med nyckeln "customUser".
        // Detta används av t.ex. Thymeleaf för att binda formuläret till objektet.
        model.addAttribute("customUser", new CustomUser());

        return "registerpage";
    }

    // Handles Business Logic - coming from SUBMIT FORM
    // Hanterar formulär-submission från registreringssidan.
    @PostMapping("/register")
    public String registerUser(
            @Valid CustomUserCreationDTO customUserCreationDTO, BindingResult bindingResult
    ) {

        if (bindingResult.hasErrors()) {
            return "registerpage";
        }

        // Konverterar DTO → Entity med hjälp av din mapper.
        /*
        Detta behövs för att:
        🔐 Skydda databasen
        🔧 Ha full kontroll över vad som sparas
        📦 Isolera API från interna modeller
        ✅ Följa god arkitektur
        🚫 Stoppa användare från att manipulera känsliga fält

        DTO = Vad världen får se
        Entity = Hur systemet faktiskt fungerar
         */
        CustomUser customUser = customUserMapper.toEntity(customUserCreationDTO);

        customUser.setPassword(
                customUser.getPassword(),
                passwordEncoder
        );

        // TODO - Verification Process STATUS: Nice To Have
        // Sätter säkerhetsflaggor för användarkontot:
        customUser.setAccountNonExpired(true);
        customUser.setAccountNonLocked(true);
        customUser.setCredentialsNonExpired(true);
        customUser.setEnabled(true);

        // TODO - Handle Roles Graciously
        // Tilldelar rollen USER till den nya användaren.
        customUser.setUserRoles(
                Set.of(UserRole.USER)
        );

        System.out.println("Saving user... ");
        customUserRepository.save(customUser);

        return "redirect:/login";
    }

}