package com.epam.finaltask.repository;

import com.epam.finaltask.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, UUID> {

    @Query("SELECT v FROM Voucher v WHERE " +
            "v.user IS NULL AND " +
            "(:keyword IS NULL OR :keyword = '' OR LOWER(CAST(v.title AS text)) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:tourType IS NULL OR v.tourType = :tourType) AND " +
            "(:hotelType IS NULL OR v.hotelType = :hotelType) AND " +
            "(:maxPrice IS NULL OR v.price <= :maxPrice) AND " +
            "(:isHot IS NULL OR v.isHot = :isHot)")
    Page<Voucher> findAvailableVouchers(
            @Param("keyword") String keyword,
            @Param("tourType") TourType tourType,
            @Param("hotelType") HotelType hotelType,
            @Param("maxPrice") Double maxPrice,
            @Param("isHot") Boolean isHot,
            Pageable pageable);

    // --- НОВЫЙ МЕТОД ДЛЯ ПАНЕЛИ МЕНЕДЖЕРА ---
    @Query("SELECT v FROM Voucher v WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR LOWER(CAST(v.title AS text)) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:status IS NULL OR v.status = :status)")
    Page<Voucher> findAllVouchersForManager(
            @Param("keyword") String keyword,
            @Param("status") VoucherStatus status,
            Pageable pageable);

    java.util.List<Voucher> findAllByUserId(UUID userId);
    java.util.List<Voucher> findAllByTourType(TourType tourType);
    java.util.List<Voucher> findAllByTransferType(TransferType transferType);
    java.util.List<Voucher> findAllByPriceLessThanEqual(Double price);
    java.util.List<Voucher> findAllByHotelType(HotelType hotelType);
    java.util.List<Voucher> findAllByOrderByIsHotDesc();
}