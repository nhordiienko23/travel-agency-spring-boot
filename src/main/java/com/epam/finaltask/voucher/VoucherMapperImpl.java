package com.epam.finaltask.voucher;

import org.springframework.stereotype.Component;

@Component
public class VoucherMapperImpl implements VoucherMapper {

    @Override
    public Voucher toVoucher(CreateVoucherRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Voucher voucher = new Voucher();
        voucher.setTitle(dto.title());
        voucher.setDescription(dto.description());
        voucher.setPrice(dto.price());

        if (dto.tourType() != null && !dto.tourType().isBlank()) {
            voucher.setTourType(TourType.valueOf(dto.tourType().toUpperCase()));
        }
        if (dto.transferType() != null && !dto.transferType().isBlank()) {
            voucher.setTransferType(TransferType.valueOf(dto.transferType().toUpperCase()));
        }
        if (dto.hotelType() != null && !dto.hotelType().isBlank()) {
            voucher.setHotelType(HotelType.valueOf(dto.hotelType().toUpperCase()));
        }

        voucher.setArrivalDate(dto.arrivalDate());
        voucher.setEvictionDate(dto.evictionDate());

        return voucher;
    }

    @Override
    public VoucherDTO toVoucherDTO(Voucher voucher) {
        if (voucher == null) {
            return null;
        }

        VoucherDTO.VoucherDTOBuilder dtoBuilder = VoucherDTO.builder();

        if (voucher.getId() != null) {
            dtoBuilder.id(voucher.getId().toString());
        }

        dtoBuilder.title(voucher.getTitle());
        dtoBuilder.description(voucher.getDescription());
        dtoBuilder.price(voucher.getPrice());

        if (voucher.getTourType() != null) {
            dtoBuilder.tourType(voucher.getTourType().name());
        }
        if (voucher.getTransferType() != null) {
            dtoBuilder.transferType(voucher.getTransferType().name());
        }
        if (voucher.getHotelType() != null) {
            dtoBuilder.hotelType(voucher.getHotelType().name());
        }

        dtoBuilder.status(voucher.getStatus());
        dtoBuilder.arrivalDate(voucher.getArrivalDate());
        dtoBuilder.evictionDate(voucher.getEvictionDate());

        if (voucher.getUser() != null) {
            dtoBuilder.userId(voucher.getUser().getId());
        }

        dtoBuilder.isHot(voucher.isHot());

        return dtoBuilder.build();
    }
}