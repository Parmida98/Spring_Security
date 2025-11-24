package com.parmida98.Spring_Security.user.authority;

/* Handles the ROLE_ concatenation
 *   This ENUM is essentially just a value holder
 * */

/*
Denna enum:
✔ Centraliserar rollnamn
✔ Säkerställer korrekt ROLE_-prefix
✔ Undviker magiska strängar
✔ Gör kod mer konsekvent och lättare att ändra
 */

public enum UserRoleName {

    GUEST("ROLE_GUEST"),
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private final String roleName;

    UserRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}
