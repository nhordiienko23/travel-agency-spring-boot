package com.epam.finaltask.mapper;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.model.*;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class VoucherMapperImpl implements VoucherMapper {

    @Override
    public Voucher toVoucher(VoucherDTO dto) {
        if (dto == null) {
            return null;
        }
        Voucher voucher = new Voucher();

        if (dto.getId() != null && !dto.getId().isBlank()) {
            voucher.setId(UUID.fromString(dto.getId()));
        }

        voucher.setTitle(dto.getTitle());
        voucher.setDescription(dto.getDescription());
        voucher.setPrice(dto.getPrice());

        if (dto.getTourType() != null && !dto.getTourType().isBlank()) {
            voucher.setTourType(TourType.valueOf(dto.getTourType().toUpperCase()));
        }
        if (dto.getTransferType() != null && !dto.getTransferType().isBlank()) {
            voucher.setTransferType(TransferType.valueOf(dto.getTransferType().toUpperCase()));
        }
        if (dto.getHotelType() != null && !dto.getHotelType().isBlank()) {
            voucher.setHotelType(HotelType.valueOf(dto.getHotelType().toUpperCase()));
        }
        if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
            voucher.setStatus(VoucherStatus.valueOf(dto.getStatus().toUpperCase()));
        }

        voucher.setArrivalDate(dto.getArrivalDate());
        voucher.setEvictionDate(dto.getEvictionDate());

        if (dto.getIsHot() != null) {
            voucher.setHot(dto.getIsHot());
        }

        return voucher;
    }

    @Override
    public VoucherDTO toVoucherDTO(Voucher voucher) {
        if (voucher == null) {
            return null;
        }
        VoucherDTO dto = new VoucherDTO();

        if (voucher.getId() != null) {
            dto.setId(voucher.getId().toString());
        }

        dto.setTitle(voucher.getTitle());
        dto.setDescription(voucher.getDescription());
        dto.setPrice(voucher.getPrice());

        if (voucher.getTourType() != null) {
            dto.setTourType(voucher.getTourType().name());
        }
        if (voucher.getTransferType() != null) {
            dto.setTransferType(voucher.getTransferType().name());
        }
        if (voucher.getHotelType() != null) {
            dto.setHotelType(voucher.getHotelType().name());
        }
        if (voucher.getStatus() != null) {
            dto.setStatus(voucher.getStatus().name());
        }

        dto.setArrivalDate(voucher.getArrivalDate());
        dto.setEvictionDate(voucher.getEvictionDate());

        if (voucher.getUser() != null) {
            dto.setUserId(voucher.getUser().getId());
        }

        dto.setIsHot(voucher.isHot());

        return dto;
    }
}