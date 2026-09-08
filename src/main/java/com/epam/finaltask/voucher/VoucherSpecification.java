package com.epam.finaltask.voucher;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VoucherSpecification {

    public static Specification<Voucher> searchVouchers(VoucherSearchRequestDTO criteria, boolean onlyAvailable) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.keyword() != null && !criteria.keyword().isBlank()) {
                String likeKeyword = "%" + criteria.keyword().toLowerCase() + "%";
                Predicate titleLike = cb.like(cb.lower(root.get("title")), likeKeyword);
                Predicate descLike = cb.like(cb.lower(root.get("description")), likeKeyword);
                predicates.add(cb.or(titleLike, descLike));
            }

            if (criteria.tourType() != null && !criteria.tourType().isBlank()) {
                predicates.add(cb.equal(root.get("tourType").as(String.class), criteria.tourType().toUpperCase()));
            }

            if (criteria.transferType() != null && !criteria.transferType().isBlank()) {
                predicates.add(cb.equal(root.get("transferType").as(String.class), criteria.transferType().toUpperCase()));
            }

            if (criteria.hotelType() != null && !criteria.hotelType().isBlank()) {
                predicates.add(cb.equal(root.get("hotelType").as(String.class), criteria.hotelType().toUpperCase()));
            }

            if (criteria.maxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), criteria.maxPrice()));
            }

            if (criteria.isHot() != null) {
                predicates.add(cb.equal(root.get("isHot"), criteria.isHot()));
            }

            if (criteria.status() != null && !criteria.status().isBlank()) {
                predicates.add(cb.equal(root.get("status").as(String.class), criteria.status().toUpperCase()));
            }

            if (criteria.dateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("arrivalDate"), criteria.dateFrom()));
            }
            if (criteria.dateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("arrivalDate"), criteria.dateTo()));
            }

            if (onlyAvailable) {
                predicates.add(cb.isNull(root.get("user")));
                predicates.add(cb.equal(root.get("status").as(String.class), VoucherStatus.REGISTERED.name()));
                predicates.add(cb.greaterThanOrEqualTo(root.get("arrivalDate"), LocalDate.now()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}