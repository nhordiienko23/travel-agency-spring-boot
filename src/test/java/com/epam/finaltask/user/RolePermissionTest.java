package com.epam.finaltask.user;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RolePermissionTest {

    // ========================================================================
    // ADMIN
    // ========================================================================

    @Test
    void admin_shouldHaveAllExpectedAuthorities() {

        List<SimpleGrantedAuthority> authorities =
                Role.ADMIN.getAuthorities();

        assertEquals(
                10,
                authorities.size()
        );

        assertTrue(
                authorities.contains(
                        new SimpleGrantedAuthority("ROLE_ADMIN")
                )
        );

        assertTrue(
                authorities.contains(
                        new SimpleGrantedAuthority("admin:read")
                )
        );

        assertTrue(
                authorities.contains(
                        new SimpleGrantedAuthority("admin:update")
                )
        );

        assertTrue(
                authorities.contains(
                        new SimpleGrantedAuthority("admin:create")
                )
        );

        assertTrue(
                authorities.contains(
                        new SimpleGrantedAuthority("admin:delete")
                )
        );

        assertTrue(
                authorities.contains(
                        new SimpleGrantedAuthority("manager:update")
                )
        );

        assertTrue(
                authorities.contains(
                        new SimpleGrantedAuthority("user:read")
                )
        );

        assertTrue(
                authorities.contains(
                        new SimpleGrantedAuthority("user:update")
                )
        );

        assertTrue(
                authorities.contains(
                        new SimpleGrantedAuthority("user:create")
                )
        );

        assertTrue(
                authorities.contains(
                        new SimpleGrantedAuthority("user:delete")
                )
        );
    }

    // ========================================================================
    // MANAGER
    // ========================================================================

    @Test
    void manager_shouldHaveExpectedAuthorities() {

        List<SimpleGrantedAuthority> authorities =
                Role.MANAGER.getAuthorities();

        assertEquals(
                4,
                authorities.size()
        );

        assertTrue(
                authorities.contains(
                        new SimpleGrantedAuthority("ROLE_MANAGER")
                )
        );

        assertTrue(
                authorities.contains(
                        new SimpleGrantedAuthority("manager:update")
                )
        );

        assertTrue(
                authorities.contains(
                        new SimpleGrantedAuthority("user:read")
                )
        );

        assertTrue(
                authorities.contains(
                        new SimpleGrantedAuthority("user:update")
                )
        );

        assertFalse(
                authorities.contains(
                        new SimpleGrantedAuthority("ROLE_ADMIN")
                )
        );

        assertFalse(
                authorities.contains(
                        new SimpleGrantedAuthority("admin:delete")
                )
        );
    }

    // ========================================================================
    // USER
    // ========================================================================

    @Test
    void user_shouldHaveExpectedAuthorities() {

        List<SimpleGrantedAuthority> authorities =
                Role.USER.getAuthorities();

        assertEquals(
                4,
                authorities.size()
        );

        assertTrue(
                authorities.contains(
                        new SimpleGrantedAuthority("ROLE_USER")
                )
        );

        assertTrue(
                authorities.contains(
                        new SimpleGrantedAuthority("user:read")
                )
        );

        assertTrue(
                authorities.contains(
                        new SimpleGrantedAuthority("user:update")
                )
        );

        assertTrue(
                authorities.contains(
                        new SimpleGrantedAuthority("user:create")
                )
        );

        assertFalse(
                authorities.contains(
                        new SimpleGrantedAuthority("ROLE_ADMIN")
                )
        );

        assertFalse(
                authorities.contains(
                        new SimpleGrantedAuthority("manager:update")
                )
        );
    }

    // ========================================================================
    // PERMISSIONS
    // ========================================================================

    @Test
    void permissions_shouldReturnCorrectPermissionValues() {

        assertEquals(
                "admin:read",
                Permission.ADMIN_READ.getPermission()
        );

        assertEquals(
                "admin:update",
                Permission.ADMIN_UPDATE.getPermission()
        );

        assertEquals(
                "admin:create",
                Permission.ADMIN_CREATE.getPermission()
        );

        assertEquals(
                "admin:delete",
                Permission.ADMIN_DELETE.getPermission()
        );

        assertEquals(
                "manager:update",
                Permission.MANAGER_UPDATE.getPermission()
        );

        assertEquals(
                "user:read",
                Permission.USER_READ.getPermission()
        );

        assertEquals(
                "user:update",
                Permission.USER_UPDATE.getPermission()
        );

        assertEquals(
                "user:create",
                Permission.USER_CREATE.getPermission()
        );

        assertEquals(
                "user:delete",
                Permission.USER_DELETE.getPermission()
        );
    }

    @Test
    void permissions_shouldContainExactlyNinePermissions() {

        assertEquals(
                9,
                Permission.values().length
        );
    }

    @Test
    void permissions_shouldContainUniqueValues() {

        String[] permissions = {
                Permission.ADMIN_READ.getPermission(),
                Permission.ADMIN_UPDATE.getPermission(),
                Permission.ADMIN_CREATE.getPermission(),
                Permission.ADMIN_DELETE.getPermission(),
                Permission.MANAGER_UPDATE.getPermission(),
                Permission.USER_READ.getPermission(),
                Permission.USER_UPDATE.getPermission(),
                Permission.USER_CREATE.getPermission(),
                Permission.USER_DELETE.getPermission()
        };

        assertEquals(
                9,
                java.util.Arrays.stream(permissions)
                        .distinct()
                        .count()
        );
    }
}

