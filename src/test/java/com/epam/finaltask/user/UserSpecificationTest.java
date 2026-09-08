package com.epam.finaltask.user;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SuppressWarnings({"rawtypes", "unchecked"})
class UserSpecificationTest {

    @Test
    void searchByCriteria_shouldBuildPredicatesForAllCriteria() {

        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Root<User> root = mock(Root.class);
        CriteriaQuery<User> query = mock(CriteriaQuery.class);

        Path path = mock(Path.class);
        Predicate predicate = mock(Predicate.class);

        when(root.get(anyString())).thenReturn(path);

        when(path.as(any(Class.class)))
                .thenReturn(path);

        when(cb.lower(any(Expression.class)))
                .thenReturn(path);

        when(cb.like(any(Expression.class), anyString()))
                .thenReturn(predicate);

        when(cb.equal(path, "ADMIN"))
                .thenReturn(predicate);

        when(cb.equal(path, true))
                .thenReturn(predicate);

        when(cb.and(any(Predicate[].class)))
                .thenReturn(predicate);

        UserSearchRequestDTO criteria =
                new UserSearchRequestDTO(
                        "John",
                        "+48",
                        "admin",
                        true
                );

        Predicate result =
                UserSpecification.searchByCriteria(criteria)
                        .toPredicate(root, query, cb);

        assertNotNull(result);

        verify(cb).lower(path);

        verify(cb, times(2))
                .like(any(Expression.class), anyString());

        verify(cb).equal(path, "ADMIN");

        verify(cb).equal(path, true);

        verify(cb).and(any(Predicate[].class));
    }

    @Test
    void searchByCriteria_shouldIgnoreBlankAndNullCriteria() {

        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Root<User> root = mock(Root.class);
        CriteriaQuery<User> query = mock(CriteriaQuery.class);

        Predicate predicate = mock(Predicate.class);

        when(cb.and(any(Predicate[].class)))
                .thenReturn(predicate);

        UserSearchRequestDTO criteria =
                new UserSearchRequestDTO(
                        null,
                        "",
                        " ",
                        null
                );

        Predicate result =
                UserSpecification.searchByCriteria(criteria)
                        .toPredicate(root, query, cb);

        assertNotNull(result);

        verify(cb).and(any(Predicate[].class));

        verifyNoMoreInteractions(cb);
    }

    @Test
    void searchByCriteria_shouldUseOnlyUsername() {

        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Root<User> root = mock(Root.class);
        CriteriaQuery<User> query = mock(CriteriaQuery.class);

        Path path = mock(Path.class);
        Predicate predicate = mock(Predicate.class);

        when(root.get("username"))
                .thenReturn(path);

        when(cb.lower(path))
                .thenReturn(path);

        when(cb.like(path, "%john%"))
                .thenReturn(predicate);

        when(cb.and(any(Predicate[].class)))
                .thenReturn(predicate);

        UserSearchRequestDTO criteria =
                new UserSearchRequestDTO(
                        "John",
                        null,
                        null,
                        null
                );

        Predicate result =
                UserSpecification.searchByCriteria(criteria)
                        .toPredicate(root, query, cb);

        assertNotNull(result);

        verify(cb).lower(path);
        verify(cb).like(path, "%john%");
        verify(cb).and(any(Predicate[].class));
    }

    @Test
    void searchByCriteria_shouldUseOnlyPhone() {

        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Root<User> root = mock(Root.class);
        CriteriaQuery<User> query = mock(CriteriaQuery.class);

        Path path = mock(Path.class);
        Predicate predicate = mock(Predicate.class);

        when(root.get("phoneNumber"))
                .thenReturn(path);

        when(cb.like(path, "%+48%"))
                .thenReturn(predicate);

        when(cb.and(any(Predicate[].class)))
                .thenReturn(predicate);

        UserSearchRequestDTO criteria =
                new UserSearchRequestDTO(
                        null,
                        "+48",
                        null,
                        null
                );

        Predicate result =
                UserSpecification.searchByCriteria(criteria)
                        .toPredicate(root, query, cb);

        assertNotNull(result);

        verify(cb).like(path, "%+48%");
        verify(cb).and(any(Predicate[].class));
    }

    @Test
    void searchByCriteria_shouldUseOnlyRole() {

        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Root<User> root = mock(Root.class);
        CriteriaQuery<User> query = mock(CriteriaQuery.class);

        Path path = mock(Path.class);
        Predicate predicate = mock(Predicate.class);

        when(root.get("role"))
                .thenReturn(path);

        when(path.as(String.class))
                .thenReturn(path);

        when(cb.equal(path, "MANAGER"))
                .thenReturn(predicate);

        when(cb.and(any(Predicate[].class)))
                .thenReturn(predicate);

        UserSearchRequestDTO criteria =
                new UserSearchRequestDTO(
                        null,
                        null,
                        "manager",
                        null
                );

        Predicate result =
                UserSpecification.searchByCriteria(criteria)
                        .toPredicate(root, query, cb);

        assertNotNull(result);

        verify(path).as(String.class);
        verify(cb).equal(path, "MANAGER");
        verify(cb).and(any(Predicate[].class));
    }

    @Test
    void searchByCriteria_shouldUseOnlyActive() {

        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Root<User> root = mock(Root.class);
        CriteriaQuery<User> query = mock(CriteriaQuery.class);

        Path path = mock(Path.class);
        Predicate predicate = mock(Predicate.class);

        when(root.get("active"))
                .thenReturn(path);

        when(cb.equal(path, false))
                .thenReturn(predicate);

        when(cb.and(any(Predicate[].class)))
                .thenReturn(predicate);

        UserSearchRequestDTO criteria =
                new UserSearchRequestDTO(
                        null,
                        null,
                        null,
                        false
                );

        Predicate result =
                UserSpecification.searchByCriteria(criteria)
                        .toPredicate(root, query, cb);

        assertNotNull(result);

        verify(cb).equal(path, false);
        verify(cb).and(any(Predicate[].class));
    }


    @Test
    void constructor_shouldBeCovered() {
        UserSpecification specification = new UserSpecification();

        assertNotNull(specification);
    }

    @Test
    void searchByCriteria_shouldIgnoreBlankUsername() {

        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Root<User> root = mock(Root.class);
        CriteriaQuery<User> query = mock(CriteriaQuery.class);

        Predicate predicate = mock(Predicate.class);

        when(cb.and(any(Predicate[].class)))
                .thenReturn(predicate);

        UserSearchRequestDTO criteria =
                new UserSearchRequestDTO(
                        "   ",
                        null,
                        null,
                        null
                );

        Predicate result =
                UserSpecification.searchByCriteria(criteria)
                        .toPredicate(root, query, cb);

        assertNotNull(result);

        verify(cb).and(any(Predicate[].class));

        verify(cb, never())
                .lower(any(Expression.class));

        verify(cb, never())
                .like(any(Expression.class), anyString());
    }


}

