package com.epam.finaltask.mapper;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.model.HotelType;
import com.epam.finaltask.model.TourType;
import com.epam.finaltask.model.Voucher;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class VoucherMapperImplTest {

    private final VoucherMapperImpl voucherMapper = new VoucherMapperImpl();

    @Test
    void toVoucher_ValidDTO_ReturnsVoucher() {
        VoucherDTO dto = new VoucherDTO();
        dto.setId(UUID.randomUUID().toString());
        dto.setTitle("Test Title");
        dto.setTourType("LEISURE");
        dto.setHotelType("FIVE_STARS");
        dto.setIsHot(true);

        Voucher voucher = voucherMapper.toVoucher(dto);

        assertNotNull(voucher);
        assertEquals("Test Title", voucher.getTitle());
        assertEquals(TourType.LEISURE, voucher.getTourType());
        assertEquals(HotelType.FIVE_STARS, voucher.getHotelType());
        assertTrue(voucher.isHot());
    }

    @Test
    void nullChecks() {
        assertNull(voucherMapper.toVoucher(null));
        assertNull(voucherMapper.toVoucherDTO(null));
    }
}