package com.parmida98.Spring_Security;

import com.parmida98.Spring_Security.user.CustomUser;
import com.parmida98.Spring_Security.user.authority.UserRole;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;

import java.util.Set;

@EnableRabbit
@SpringBootApplication
public class SpringSecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityApplication.class, args);

		System.out.println(
				UserRole.GUEST.getRoleName()	// ROLE_GUEST
		);

		System.out.println(
				UserRole.USER.getUserPermissions() // PERMISSIONS
		);

		System.out.println(
				UserRole.ADMIN.getUserPermissions()
		);

		System.out.println(
				UserRole.GUEST.getUserAuthorities() + " \n " + // GUEST - AUTHORITY
						UserRole.USER.getUserAuthorities() + " \n " + // USER - AUTHORITY incl. perms.
						UserRole.ADMIN.getUserAuthorities()			 // ADMIN - AUTHORITY incl. perms.
		);

		CustomUser benny = new CustomUser(
				"",
				"",
				true,
				true,
				true,
				true,
				Set.of(UserRole.USER, UserRole.ADMIN)
		);
		/*
			CustomUserDetails customUserDetails = new CustomUserDetails(benny);
			System.out.println("getAuthorities: " + customUserDetails.getAuthorities());

			JwtUtils jwtUtils = new JwtUtils();

			// Generate the token
			String token = jwtUtils.generateJwtToken(benny);
			System.out.println("Generated JWT:\n" + token);

			// Extract the roles
			Set<UserRole> extractedRoles = jwtUtils.getRolesFromJwtToken(token);
			System.out.println("Extracted roles: " + extractedRoles);

		 */
	}

}
