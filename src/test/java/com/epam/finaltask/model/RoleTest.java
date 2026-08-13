package com.epam.finaltask.model;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    void getAuthorities_Admin_ReturnsAllPermissionsAndRole() {
        List<SimpleGrantedAuthority> authorities = Role.ADMIN.getAuthorities();

        assertNotNull(authorities);
        // ADMIN имеет 9 разрешений + 1 саму роль = 10 authorities
        assertEquals(10, authorities.size());

        // Проверяем наличие ключевых разрешений
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("admin:read")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("user:delete")));
    }

    @Test
    void getAuthorities_Manager_ReturnsManagerPermissionsAndRole() {
        List<SimpleGrantedAuthority> authorities = Role.MANAGER.getAuthorities();

        assertNotNull(authorities);
        // MANAGER имеет 3 разрешения + 1 саму роль = 4 authorities
        assertEquals(4, authorities.size());

        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_MANAGER")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("manager:update")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("user:read")));
        assertFalse(authorities.contains(new SimpleGrantedAuthority("admin:delete"))); // Не должно быть прав админа
    }

    @Test
    void getAuthorities_User_ReturnsUserPermissionsAndRole() {
        List<SimpleGrantedAuthority> authorities = Role.USER.getAuthorities();

        assertNotNull(authorities);
        // USER имеет 3 разрешения + 1 саму роль = 4 authorities
        assertEquals(4, authorities.size());

        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("user:read")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("user:create")));
        assertFalse(authorities.contains(new SimpleGrantedAuthority("manager:update")));
    }
}