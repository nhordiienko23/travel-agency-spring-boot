package com.epam.finaltask.voucher;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"rawtypes", "unchecked"})
class VoucherSpecificationTest {

    // ========================================================================
    // FULL CRITERIA
    // ========================================================================

    @Test
    void searchVouchers_shouldApplyAllCriteria() {

        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Root<Voucher> root = mock(Root.class);
        CriteriaQuery<Voucher> query = mock(CriteriaQuery.class);

        Path titlePath = mock(Path.class);
        Path descriptionPath = mock(Path.class);
        Path tourTypePath = mock(Path.class);
        Path transferTypePath = mock(Path.class);
        Path hotelTypePath = mock(Path.class);
        Path pricePath = mock(Path.class);
        Path hotPath = mock(Path.class);
        Path statusPath = mock(Path.class);
        Path arrivalDatePath = mock(Path.class);
        Path userPath = mock(Path.class);

        Predicate titlePredicate = mock(Predicate.class);
        Predicate descriptionPredicate = mock(Predicate.class);
        Predicate keywordPredicate = mock(Predicate.class);
        Predicate tourTypePredicate = mock(Predicate.class);
        Predicate transferTypePredicate = mock(Predicate.class);
        Predicate hotelTypePredicate = mock(Predicate.class);
        Predicate pricePredicate = mock(Predicate.class);
        Predicate hotPredicate = mock(Predicate.class);
        Predicate statusPredicate = mock(Predicate.class);
        Predicate dateFromPredicate = mock(Predicate.class);
        Predicate dateToPredicate = mock(Predicate.class);
        Predicate userPredicate = mock(Predicate.class);
        Predicate availableStatusPredicate = mock(Predicate.class);
        Predicate availableDatePredicate = mock(Predicate.class);
        Predicate finalPredicate = mock(Predicate.class);

        when(root.get("title")).thenReturn(titlePath);
        when(root.get("description")).thenReturn(descriptionPath);
        when(root.get("tourType")).thenReturn(tourTypePath);
        when(root.get("transferType")).thenReturn(transferTypePath);
        when(root.get("hotelType")).thenReturn(hotelTypePath);
        when(root.get("price")).thenReturn(pricePath);
        when(root.get("isHot")).thenReturn(hotPath);
        when(root.get("status")).thenReturn(statusPath);
        when(root.get("arrivalDate")).thenReturn(arrivalDatePath);
        when(root.get("user")).thenReturn(userPath);

        when(titlePath.as(String.class)).thenReturn(titlePath);
        when(descriptionPath.as(String.class)).thenReturn(descriptionPath);
        when(tourTypePath.as(String.class)).thenReturn(tourTypePath);
        when(transferTypePath.as(String.class)).thenReturn(transferTypePath);
        when(hotelTypePath.as(String.class)).thenReturn(hotelTypePath);
        when(statusPath.as(String.class)).thenReturn(statusPath);

        when(cb.lower(titlePath)).thenReturn(titlePath);
        when(cb.lower(descriptionPath)).thenReturn(descriptionPath);

        when(cb.like(titlePath, "%sea%"))
                .thenReturn(titlePredicate);

        when(cb.like(descriptionPath, "%sea%"))
                .thenReturn(descriptionPredicate);

        when(cb.or(
                titlePredicate,
                descriptionPredicate
        )).thenReturn(keywordPredicate);

        when(cb.equal(
                tourTypePath,
                "ADVENTURE"
        )).thenReturn(tourTypePredicate);

        when(cb.equal(
                transferTypePath,
                "PLANE"
        )).thenReturn(transferTypePredicate);

        when(cb.equal(
                hotelTypePath,
                "FIVE_STARS"
        )).thenReturn(hotelTypePredicate);

        when(cb.lessThanOrEqualTo(
                pricePath,
                1000.0
        )).thenReturn(pricePredicate);

        when(cb.equal(
                hotPath,
                true
        )).thenReturn(hotPredicate);

        when(cb.equal(
                statusPath,
                "REGISTERED"
        )).thenReturn(
                statusPredicate,
                availableStatusPredicate
        );

        LocalDate today = LocalDate.now();

        when(cb.greaterThanOrEqualTo(
                arrivalDatePath,
                today
        )).thenReturn(
                dateFromPredicate,
                availableDatePredicate
        );

        when(cb.lessThanOrEqualTo(
                arrivalDatePath,
                today.plusDays(10)
        )).thenReturn(dateToPredicate);

        when(cb.isNull(userPath))
                .thenReturn(userPredicate);

        when(cb.and(any(Predicate[].class)))
                .thenReturn(finalPredicate);

        VoucherSearchRequestDTO criteria =
                new VoucherSearchRequestDTO(
                        "sea",
                        "adventure",
                        "plane",
                        "five_stars",
                        1000.0,
                        true,
                        "registered",
                        today,
                        today.plusDays(10)
                );

        Predicate result =
                VoucherSpecification
                        .searchVouchers(criteria, true)
                        .toPredicate(root, query, cb);

        assertNotNull(result);

        verify(root).get("title");
        verify(root).get("description");
        verify(root).get("tourType");
        verify(root).get("transferType");
        verify(root).get("hotelType");
        verify(root).get("price");
        verify(root).get("isHot");

        verify(root, times(2))
                .get("status");

        verify(root, times(3))
                .get("arrivalDate");

        verify(root).get("user");

        verify(cb).lower(titlePath);
        verify(cb).lower(descriptionPath);

        verify(cb).like(
                titlePath,
                "%sea%"
        );

        verify(cb).like(
                descriptionPath,
                "%sea%"
        );

        verify(cb).or(
                titlePredicate,
                descriptionPredicate
        );

        verify(cb).equal(
                tourTypePath,
                "ADVENTURE"
        );

        verify(cb).equal(
                transferTypePath,
                "PLANE"
        );

        verify(cb).equal(
                hotelTypePath,
                "FIVE_STARS"
        );

        verify(cb).lessThanOrEqualTo(
                pricePath,
                1000.0
        );

        verify(cb).equal(
                hotPath,
                true
        );

        verify(cb, times(2))
                .equal(
                        statusPath,
                        "REGISTERED"
                );

        verify(cb, times(2))
                .greaterThanOrEqualTo(
                        eq(arrivalDatePath),
                        eq(today)
                );

        verify(cb).lessThanOrEqualTo(
                arrivalDatePath,
                today.plusDays(10)
        );

        verify(cb).isNull(userPath);

        verify(cb).and(any(Predicate[].class));
    }

    // ========================================================================
    // NO OPTIONAL CRITERIA + NON AVAILABLE
    // ========================================================================

    @Test
    void searchVouchers_shouldReturnPredicateWithoutOptionalCriteria() {

        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Root<Voucher> root = mock(Root.class);
        CriteriaQuery<Voucher> query = mock(CriteriaQuery.class);
        Predicate predicate = mock(Predicate.class);

        when(cb.and(any(Predicate[].class)))
                .thenReturn(predicate);

        VoucherSearchRequestDTO criteria =
                new VoucherSearchRequestDTO(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                );

        Predicate result =
                VoucherSpecification
                        .searchVouchers(criteria, false)
                        .toPredicate(root, query, cb);

        assertNotNull(result);

        verify(cb).and(any(Predicate[].class));
        verifyNoInteractions(root);
    }

    // ========================================================================
    // ONLY AVAILABLE
    // ========================================================================

    @Test
    void searchVouchers_shouldApplyOnlyAvailableConditions() {

        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Root<Voucher> root = mock(Root.class);
        CriteriaQuery<Voucher> query = mock(CriteriaQuery.class);

        Path userPath = mock(Path.class);
        Path statusPath = mock(Path.class);
        Path arrivalDatePath = mock(Path.class);

        Predicate userPredicate = mock(Predicate.class);
        Predicate statusPredicate = mock(Predicate.class);
        Predicate datePredicate = mock(Predicate.class);
        Predicate resultPredicate = mock(Predicate.class);

        LocalDate today = LocalDate.now();

        when(root.get("user"))
                .thenReturn(userPath);

        when(root.get("status"))
                .thenReturn(statusPath);

        when(root.get("arrivalDate"))
                .thenReturn(arrivalDatePath);

        when(statusPath.as(String.class))
                .thenReturn(statusPath);

        when(cb.isNull(userPath))
                .thenReturn(userPredicate);

        when(cb.equal(
                statusPath,
                VoucherStatus.REGISTERED.name()
        )).thenReturn(statusPredicate);

        when(cb.greaterThanOrEqualTo(
                arrivalDatePath,
                today
        )).thenReturn(datePredicate);

        when(cb.and(any(Predicate[].class)))
                .thenReturn(resultPredicate);

        VoucherSearchRequestDTO criteria =
                new VoucherSearchRequestDTO(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                );

        Predicate result =
                VoucherSpecification
                        .searchVouchers(criteria, true)
                        .toPredicate(root, query, cb);

        assertNotNull(result);

        verify(root).get("user");
        verify(root).get("status");
        verify(root).get("arrivalDate");

        verify(cb).isNull(userPath);

        verify(cb).equal(
                statusPath,
                VoucherStatus.REGISTERED.name()
        );

        verify(cb).greaterThanOrEqualTo(
                arrivalDatePath,
                today
        );

        verify(cb).and(any(Predicate[].class));
    }

    // ========================================================================
    // BLANK KEYWORD
    // ========================================================================

    @Test
    void searchVouchers_shouldIgnoreBlankKeyword() {

        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Root<Voucher> root = mock(Root.class);
        CriteriaQuery<Voucher> query = mock(CriteriaQuery.class);

        Predicate predicate = mock(Predicate.class);

        when(cb.and(any(Predicate[].class)))
                .thenReturn(predicate);

        VoucherSearchRequestDTO criteria =
                new VoucherSearchRequestDTO(
                        "   ",
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                );

        Predicate result =
                VoucherSpecification
                        .searchVouchers(criteria, false)
                        .toPredicate(root, query, cb);

        assertNotNull(result);

        verify(cb, never())
                .lower(any(Expression.class));

        verify(cb, never())
                .like(
                        any(Expression.class),
                        anyString()
                );

        verify(cb, never())
                .or(
                        any(Predicate.class),
                        any(Predicate.class)
                );

        verify(cb).and(any(Predicate[].class));
    }

    // ========================================================================
    // BLANK ENUM / STATUS FILTERS
    // ========================================================================

    @Test
    void searchVouchers_shouldIgnoreBlankEnumAndStatusFilters() {

        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Root<Voucher> root = mock(Root.class);
        CriteriaQuery<Voucher> query = mock(CriteriaQuery.class);

        Predicate predicate = mock(Predicate.class);

        when(cb.and(any(Predicate[].class)))
                .thenReturn(predicate);

        VoucherSearchRequestDTO criteria =
                new VoucherSearchRequestDTO(
                        null,
                        "   ",
                        "   ",
                        "   ",
                        null,
                        null,
                        "   ",
                        null,
                        null
                );

        Predicate result =
                VoucherSpecification
                        .searchVouchers(criteria, false)
                        .toPredicate(root, query, cb);

        assertNotNull(result);

        verify(root, never())
                .get("tourType");

        verify(root, never())
                .get("transferType");

        verify(root, never())
                .get("hotelType");

        verify(root, never())
                .get("status");

        verify(cb, never())
                .equal(
                        any(Expression.class),
                        any()
                );

        verify(cb).and(any(Predicate[].class));
    }

    // ========================================================================
    // MAX PRICE
    // ========================================================================

    @Test
    void searchVouchers_shouldApplyMaxPrice() {

        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Root<Voucher> root = mock(Root.class);
        CriteriaQuery<Voucher> query = mock(CriteriaQuery.class);

        Path pricePath = mock(Path.class);
        Predicate pricePredicate = mock(Predicate.class);
        Predicate resultPredicate = mock(Predicate.class);

        when(root.get("price"))
                .thenReturn(pricePath);

        when(cb.lessThanOrEqualTo(
                pricePath,
                500.0
        )).thenReturn(pricePredicate);

        when(cb.and(any(Predicate[].class)))
                .thenReturn(resultPredicate);

        VoucherSearchRequestDTO criteria =
                new VoucherSearchRequestDTO(
                        null,
                        null,
                        null,
                        null,
                        500.0,
                        null,
                        null,
                        null,
                        null
                );

        Predicate result =
                VoucherSpecification
                        .searchVouchers(criteria, false)
                        .toPredicate(root, query, cb);

        assertNotNull(result);

        verify(root).get("price");

        verify(cb).lessThanOrEqualTo(
                pricePath,
                500.0
        );

        verify(cb).and(any(Predicate[].class));
    }

    // ========================================================================
    // HOT
    // ========================================================================

    @Test
    void searchVouchers_shouldApplyHotFilter() {

        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Root<Voucher> root = mock(Root.class);
        CriteriaQuery<Voucher> query = mock(CriteriaQuery.class);

        Path hotPath = mock(Path.class);
        Predicate hotPredicate = mock(Predicate.class);
        Predicate resultPredicate = mock(Predicate.class);

        when(root.get("isHot"))
                .thenReturn(hotPath);

        when(cb.equal(
                hotPath,
                true
        )).thenReturn(hotPredicate);

        when(cb.and(any(Predicate[].class)))
                .thenReturn(resultPredicate);

        VoucherSearchRequestDTO criteria =
                new VoucherSearchRequestDTO(
                        null,
                        null,
                        null,
                        null,
                        null,
                        true,
                        null,
                        null,
                        null
                );

        Predicate result =
                VoucherSpecification
                        .searchVouchers(criteria, false)
                        .toPredicate(root, query, cb);

        assertNotNull(result);

        verify(root).get("isHot");

        verify(cb).equal(
                hotPath,
                true
        );

        verify(cb).and(any(Predicate[].class));
    }

    // ========================================================================
    // STATUS
    // ========================================================================

    @Test
    void searchVouchers_shouldApplyStatusFilter() {

        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Root<Voucher> root = mock(Root.class);
        CriteriaQuery<Voucher> query = mock(CriteriaQuery.class);

        Path statusPath = mock(Path.class);
        Predicate statusPredicate = mock(Predicate.class);
        Predicate resultPredicate = mock(Predicate.class);

        when(root.get("status"))
                .thenReturn(statusPath);

        when(statusPath.as(String.class))
                .thenReturn(statusPath);

        when(cb.equal(
                statusPath,
                "PAID"
        )).thenReturn(statusPredicate);

        when(cb.and(any(Predicate[].class)))
                .thenReturn(resultPredicate);

        VoucherSearchRequestDTO criteria =
                new VoucherSearchRequestDTO(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        "paid",
                        null,
                        null
                );

        Predicate result =
                VoucherSpecification
                        .searchVouchers(criteria, false)
                        .toPredicate(root, query, cb);

        assertNotNull(result);

        verify(root).get("status");

        verify(cb).equal(
                statusPath,
                "PAID"
        );

        verify(cb).and(any(Predicate[].class));
    }

    // ========================================================================
    // DATE FROM
    // ========================================================================

    @Test
    void searchVouchers_shouldApplyDateFrom() {

        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Root<Voucher> root = mock(Root.class);
        CriteriaQuery<Voucher> query = mock(CriteriaQuery.class);

        Path arrivalDatePath = mock(Path.class);
        Predicate datePredicate = mock(Predicate.class);
        Predicate resultPredicate = mock(Predicate.class);

        LocalDate dateFrom =
                LocalDate.now().plusDays(2);

        when(root.get("arrivalDate"))
                .thenReturn(arrivalDatePath);

        when(cb.greaterThanOrEqualTo(
                arrivalDatePath,
                dateFrom
        )).thenReturn(datePredicate);

        when(cb.and(any(Predicate[].class)))
                .thenReturn(resultPredicate);

        VoucherSearchRequestDTO criteria =
                new VoucherSearchRequestDTO(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        dateFrom,
                        null
                );

        Predicate result =
                VoucherSpecification
                        .searchVouchers(criteria, false)
                        .toPredicate(root, query, cb);

        assertNotNull(result);

        verify(root).get("arrivalDate");

        verify(cb).greaterThanOrEqualTo(
                arrivalDatePath,
                dateFrom
        );

        verify(cb).and(any(Predicate[].class));
    }

    // ========================================================================
    // DATE TO
    // ========================================================================

    @Test
    void searchVouchers_shouldApplyDateTo() {

        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Root<Voucher> root = mock(Root.class);
        CriteriaQuery<Voucher> query = mock(CriteriaQuery.class);

        Path arrivalDatePath = mock(Path.class);
        Predicate datePredicate = mock(Predicate.class);
        Predicate resultPredicate = mock(Predicate.class);

        LocalDate dateTo =
                LocalDate.now().plusDays(10);

        when(root.get("arrivalDate"))
                .thenReturn(arrivalDatePath);

        when(cb.lessThanOrEqualTo(
                arrivalDatePath,
                dateTo
        )).thenReturn(datePredicate);

        when(cb.and(any(Predicate[].class)))
                .thenReturn(resultPredicate);

        VoucherSearchRequestDTO criteria =
                new VoucherSearchRequestDTO(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        dateTo
                );

        Predicate result =
                VoucherSpecification
                        .searchVouchers(criteria, false)
                        .toPredicate(root, query, cb);

        assertNotNull(result);

        verify(root).get("arrivalDate");

        verify(cb).lessThanOrEqualTo(
                arrivalDatePath,
                dateTo
        );

        verify(cb).and(any(Predicate[].class));
    }

    // ========================================================================
    // CONSTRUCTOR
    // ========================================================================

    @Test
    void constructor_shouldBeCovered() {

        VoucherSpecification specification =
                new VoucherSpecification();

        assertNotNull(specification);
    }
}

