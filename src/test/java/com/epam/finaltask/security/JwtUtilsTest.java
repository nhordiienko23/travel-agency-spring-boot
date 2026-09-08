package com.epam.finaltask.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private static final String SECRET_KEY =
            "404E635263336A586E3272357538782F413F4428472B4B6250645367566B5970";

    private static final String ANOTHER_SECRET_KEY =
            "404E635263336A586E3272357538782F413F4428472B4B6250645367566B5971";

    private static final long EXPIRATION_MS = 86_400_000L;

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils(
                SECRET_KEY,
                EXPIRATION_MS
        );
    }

    // ========================================================================
    // GENERATE TOKEN
    // ========================================================================

    @Test
    void generateToken_ShouldReturnNonEmptyToken() {

        String token = jwtUtils.generateToken("john");

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void generateToken_ShouldStoreUsernameAsSubject() {

        String token = jwtUtils.generateToken("john");

        assertEquals(
                "john",
                jwtUtils.getUsernameFromToken(token)
        );
    }

    @Test
    void generateToken_ShouldStoreIssuedAtAndExpiration() {

        long before = System.currentTimeMillis();

        String token = jwtUtils.generateToken("john");

        long after = System.currentTimeMillis();

        SecretKey key = Keys.hmacShaKeyFor(
                SECRET_KEY.getBytes(StandardCharsets.UTF_8)
        );

        var claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Date issuedAt = claims.getIssuedAt();
        Date expiration = claims.getExpiration();

        assertNotNull(issuedAt);
        assertNotNull(expiration);

        assertTrue(
                issuedAt.getTime() >= before - 1000
        );

        assertTrue(
                issuedAt.getTime() <= after
        );

        assertEquals(
                EXPIRATION_MS,
                expiration.getTime() - issuedAt.getTime()
        );
    }

    @Test
    void generateToken_ShouldSupportDifferentUsers() {

        String firstToken = jwtUtils.generateToken("john");
        String secondToken = jwtUtils.generateToken("admin");

        assertEquals(
                "john",
                jwtUtils.getUsernameFromToken(firstToken)
        );

        assertEquals(
                "admin",
                jwtUtils.getUsernameFromToken(secondToken)
        );
    }

    // ========================================================================
    // VALIDATE TOKEN
    // ========================================================================

    @Test
    void validateToken_ShouldReturnTrue_ForValidToken() {

        String token = jwtUtils.generateToken("john");

        assertTrue(
                jwtUtils.validateToken(token)
        );
    }

    @Test
    void validateToken_ShouldReturnFalse_ForMalformedToken() {

        assertFalse(
                jwtUtils.validateToken("invalid.token")
        );
    }

    @Test
    void validateToken_ShouldReturnFalse_ForNullToken() {

        assertFalse(
                jwtUtils.validateToken(null)
        );
    }

    @Test
    void validateToken_ShouldReturnFalse_ForEmptyToken() {

        assertFalse(
                jwtUtils.validateToken("")
        );
    }

    @Test
    void validateToken_ShouldReturnFalse_ForDifferentSigningKey() {

        SecretKey anotherKey = Keys.hmacShaKeyFor(
                ANOTHER_SECRET_KEY.getBytes(StandardCharsets.UTF_8)
        );

        String token = Jwts.builder()
                .setSubject("john")
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(
                                System.currentTimeMillis()
                                        + EXPIRATION_MS
                        )
                )
                .signWith(anotherKey)
                .compact();

        assertFalse(
                jwtUtils.validateToken(token)
        );
    }

    @Test
    void validateToken_ShouldReturnFalse_ForExpiredToken() {

        SecretKey key = Keys.hmacShaKeyFor(
                SECRET_KEY.getBytes(StandardCharsets.UTF_8)
        );

        String token = Jwts.builder()
                .setSubject("john")
                .setIssuedAt(
                        new Date(
                                System.currentTimeMillis() - 20_000
                        )
                )
                .setExpiration(
                        new Date(
                                System.currentTimeMillis() - 10_000
                        )
                )
                .signWith(key)
                .compact();

        assertFalse(
                jwtUtils.validateToken(token)
        );
    }

    // ========================================================================
    // EXTRACT USERNAME
    // ========================================================================

    @Test
    void getUsernameFromToken_ShouldReturnUsername() {

        String token = jwtUtils.generateToken("john");

        assertEquals(
                "john",
                jwtUtils.getUsernameFromToken(token)
        );
    }

    @Test
    void getUsernameFromToken_ShouldThrowException_ForMalformedToken() {

        assertThrows(
                RuntimeException.class,
                () -> jwtUtils.getUsernameFromToken(
                        "invalid.token"
                )
        );
    }

    @Test
    void getUsernameFromToken_ShouldThrowException_ForExpiredToken() {

        SecretKey key = Keys.hmacShaKeyFor(
                SECRET_KEY.getBytes(StandardCharsets.UTF_8)
        );

        String token = Jwts.builder()
                .setSubject("john")
                .setIssuedAt(
                        new Date(
                                System.currentTimeMillis() - 20_000
                        )
                )
                .setExpiration(
                        new Date(
                                System.currentTimeMillis() - 10_000
                        )
                )
                .signWith(key)
                .compact();

        assertThrows(
                RuntimeException.class,
                () -> jwtUtils.getUsernameFromToken(token)
        );
    }
}