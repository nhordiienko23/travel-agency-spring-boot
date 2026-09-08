package com.epam.finaltask.user;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    public static Specification<User> searchByCriteria(UserSearchRequestDTO criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.username() != null && !criteria.username().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("username")), "%" + criteria.username().toLowerCase() + "%"));
            }

            if (criteria.phoneNumber() != null && !criteria.phoneNumber().isBlank()) {
                predicates.add(cb.like(root.get("phoneNumber"), "%" + criteria.phoneNumber() + "%"));
            }

            if (criteria.role() != null && !criteria.role().isBlank()) {
                predicates.add(cb.equal(root.get("role").as(String.class), criteria.role().toUpperCase()));
            }

            if (criteria.active() != null) {
                predicates.add(cb.equal(root.get("active"), criteria.active()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}