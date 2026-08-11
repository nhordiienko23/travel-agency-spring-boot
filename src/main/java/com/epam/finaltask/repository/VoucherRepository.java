package com.epam.finaltask.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epam.finaltask.model.HotelType;
import com.epam.finaltask.model.TourType;
import com.epam.finaltask.model.TransferType;
import com.epam.finaltask.model.Voucher;

public interface VoucherRepository extends JpaRepository<Voucher, UUID> {

    List<Voucher> findAllByOrderByIsHotDesc();

    List<Voucher> findAllByUserId(UUID userId);

    List<Voucher> findAllByTourType(TourType tourType);

    List<Voucher> findAllByTransferType(TransferType transferType);

    List<Voucher> findAllByHotelType(HotelType hotelType);

    List<Voucher> findAllByPriceLessThanEqual(Double price);

    @Query("SELECT v FROM Voucher v WHERE " +
            "(:tourType IS NULL OR v.tourType = :tourType) AND " +
            "(:transferType IS NULL OR v.transferType = :transferType) AND " +
            "(:hotelType IS NULL OR v.hotelType = :hotelType) AND " +
            "(:maxPrice IS NULL OR v.price <= :maxPrice) " +
            "ORDER BY v.isHot DESC")
    List<Voucher> filterVouchers(TourType tourType, TransferType transferType, HotelType hotelType, Double maxPrice);
}