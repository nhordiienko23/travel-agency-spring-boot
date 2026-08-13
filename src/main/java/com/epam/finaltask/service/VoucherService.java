package com.epam.finaltask.service;

import java.util.List;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.model.HotelType;
import com.epam.finaltask.model.TourType;
import org.springframework.data.domain.Page;

public interface VoucherService {
    VoucherDTO create(VoucherDTO voucherDTO);
    VoucherDTO order(String id, String userId);
    VoucherDTO update(String id, VoucherDTO voucherDTO);
    void delete(String voucherId);
    VoucherDTO changeHotStatus(String id, VoucherDTO voucherDTO);
    List<VoucherDTO> findAllByUserId(String userId);

    List<VoucherDTO> findAllByTourType(TourType tourType);
    List<VoucherDTO> findAllByTransferType(String transferType);
    List<VoucherDTO> findAllByPrice(Double price);
    List<VoucherDTO> findAllByHotelType(HotelType hotelType);

    List<VoucherDTO> findAll();
    Page<VoucherDTO> findAvailableVouchers(String keyword, String tourType, String hotelType, Double maxPrice, Boolean isHot, int page, int size, String sortField, String sortDir);

    // --- НОВЫЙ МЕТОД ---
    Page<VoucherDTO> findAllVouchersForManager(String keyword, String status, int page, int size, String sortField, String sortDir);
}