package com.epam.finaltask.voucher;

import com.epam.finaltask.user.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VoucherMapperImplTest {

    private final VoucherMapperImpl mapper = new VoucherMapperImpl();

    @Test
    void toVoucher_shouldReturnNullForNull() {
        assertNull(mapper.toVoucher(null));
    }

    @Test
    void toVoucher_shouldMapAllFieldsAndEnums() {
        LocalDate arrival = LocalDate.now().plusDays(1);
        LocalDate eviction = arrival.plusDays(5);

        CreateVoucherRequestDTO dto = CreateVoucherRequestDTO.builder()
                .title("Sea trip")
                .description("Trip")
                .price(500.0)
                .tourType("adventure")
                .transferType("plane")
                .hotelType("five_stars")
                .arrivalDate(arrival)
                .evictionDate(eviction)
                .build();

        Voucher result = mapper.toVoucher(dto);

        assertNotNull(result);
        assertEquals("Sea trip", result.getTitle());
        assertEquals("Trip", result.getDescription());
        assertEquals(500.0, result.getPrice());
        assertEquals(TourType.ADVENTURE, result.getTourType());
        assertEquals(TransferType.PLANE, result.getTransferType());
        assertEquals(HotelType.FIVE_STARS, result.getHotelType());
        assertEquals(arrival, result.getArrivalDate());
        assertEquals(eviction, result.getEvictionDate());
    }

    @Test
    void toVoucher_shouldMapEnumValuesCaseInsensitively() {
        CreateVoucherRequestDTO dto = CreateVoucherRequestDTO.builder()
                .title("Trip")
                .description("Description")
                .price(100.0)
                .tourType("AdVeNtUrE")
                .transferType("PlAnE")
                .hotelType("FiVe_StArS")
                .arrivalDate(LocalDate.now())
                .evictionDate(LocalDate.now().plusDays(1))
                .build();

        Voucher result = mapper.toVoucher(dto);

        assertEquals(TourType.ADVENTURE, result.getTourType());
        assertEquals(TransferType.PLANE, result.getTransferType());
        assertEquals(HotelType.FIVE_STARS, result.getHotelType());
    }

    @Test
    void toVoucher_shouldLeaveEnumsNullForNullAndBlankValues() {
        CreateVoucherRequestDTO dto = CreateVoucherRequestDTO.builder()
                .title("T")
                .description("D")
                .price(1.0)
                .tourType(" ")
                .transferType(null)
                .hotelType("")
                .arrivalDate(LocalDate.now())
                .evictionDate(LocalDate.now())
                .build();

        Voucher result = mapper.toVoucher(dto);

        assertNull(result.getTourType());
        assertNull(result.getTransferType());
        assertNull(result.getHotelType());
    }

    @Test
    void toVoucher_shouldHandleNullTourType() {
        CreateVoucherRequestDTO dto = CreateVoucherRequestDTO.builder()
                .title("T")
                .description("D")
                .price(1.0)
                .tourType(null)
                .transferType("bus")
                .hotelType("three_stars")
                .arrivalDate(LocalDate.now())
                .evictionDate(LocalDate.now().plusDays(1))
                .build();

        Voucher result = mapper.toVoucher(dto);

        assertNull(result.getTourType());
        assertEquals(TransferType.BUS, result.getTransferType());
        assertEquals(HotelType.THREE_STARS, result.getHotelType());
    }

    @Test
    void toVoucher_shouldHandleNullTransferType() {
        CreateVoucherRequestDTO dto = CreateVoucherRequestDTO.builder()
                .title("T")
                .description("D")
                .price(1.0)
                .tourType("adventure")
                .transferType(null)
                .hotelType("three_stars")
                .arrivalDate(LocalDate.now())
                .evictionDate(LocalDate.now().plusDays(1))
                .build();

        Voucher result = mapper.toVoucher(dto);

        assertEquals(TourType.ADVENTURE, result.getTourType());
        assertNull(result.getTransferType());
        assertEquals(HotelType.THREE_STARS, result.getHotelType());
    }

    @Test
    void toVoucher_shouldHandleNullHotelType() {
        CreateVoucherRequestDTO dto = CreateVoucherRequestDTO.builder()
                .title("T")
                .description("D")
                .price(1.0)
                .tourType("adventure")
                .transferType("plane")
                .hotelType(null)
                .arrivalDate(LocalDate.now())
                .evictionDate(LocalDate.now().plusDays(1))
                .build();

        Voucher result = mapper.toVoucher(dto);

        assertEquals(TourType.ADVENTURE, result.getTourType());
        assertEquals(TransferType.PLANE, result.getTransferType());
        assertNull(result.getHotelType());
    }

    @Test
    void toVoucher_shouldHandleBlankTourType() {
        CreateVoucherRequestDTO dto = CreateVoucherRequestDTO.builder()
                .title("T")
                .description("D")
                .price(1.0)
                .tourType(" ")
                .transferType("plane")
                .hotelType("three_stars")
                .arrivalDate(LocalDate.now())
                .evictionDate(LocalDate.now().plusDays(1))
                .build();

        Voucher result = mapper.toVoucher(dto);

        assertNull(result.getTourType());
        assertEquals(TransferType.PLANE, result.getTransferType());
        assertEquals(HotelType.THREE_STARS, result.getHotelType());
    }

    @Test
    void toVoucher_shouldHandleBlankTransferType() {
        CreateVoucherRequestDTO dto = CreateVoucherRequestDTO.builder()
                .title("T")
                .description("D")
                .price(1.0)
                .tourType("adventure")
                .transferType(" ")
                .hotelType("three_stars")
                .arrivalDate(LocalDate.now())
                .evictionDate(LocalDate.now().plusDays(1))
                .build();

        Voucher result = mapper.toVoucher(dto);

        assertEquals(TourType.ADVENTURE, result.getTourType());
        assertNull(result.getTransferType());
        assertEquals(HotelType.THREE_STARS, result.getHotelType());
    }

    @Test
    void toVoucher_shouldHandleBlankHotelType() {
        CreateVoucherRequestDTO dto = CreateVoucherRequestDTO.builder()
                .title("T")
                .description("D")
                .price(1.0)
                .tourType("adventure")
                .transferType("plane")
                .hotelType(" ")
                .arrivalDate(LocalDate.now())
                .evictionDate(LocalDate.now().plusDays(1))
                .build();

        Voucher result = mapper.toVoucher(dto);

        assertEquals(TourType.ADVENTURE, result.getTourType());
        assertEquals(TransferType.PLANE, result.getTransferType());
        assertNull(result.getHotelType());
    }

    @Test
    void toVoucherDTO_shouldReturnNullForNull() {
        assertNull(mapper.toVoucherDTO(null));
    }

    @Test
    void toVoucherDTO_shouldMapAllFieldsWithIdAndUser() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User user = User.builder()
                .id(userId)
                .build();

        LocalDate arrival = LocalDate.now().plusDays(1);
        LocalDate eviction = arrival.plusDays(3);

        Voucher voucher = Voucher.builder()
                .id(id)
                .title("T")
                .description("D")
                .price(20.0)
                .tourType(TourType.CULTURAL)
                .transferType(TransferType.BUS)
                .hotelType(HotelType.THREE_STARS)
                .status(VoucherStatus.PAID)
                .arrivalDate(arrival)
                .evictionDate(eviction)
                .user(user)
                .isHot(true)
                .build();

        VoucherDTO result = mapper.toVoucherDTO(voucher);

        assertNotNull(result);
        assertEquals(id.toString(), result.id());
        assertEquals("T", result.title());
        assertEquals("D", result.description());
        assertEquals(20.0, result.price());
        assertEquals("CULTURAL", result.tourType());
        assertEquals("BUS", result.transferType());
        assertEquals("THREE_STARS", result.hotelType());
        assertEquals(VoucherStatus.PAID, result.status());
        assertEquals(arrival, result.arrivalDate());
        assertEquals(eviction, result.evictionDate());
        assertEquals(userId, result.userId());
        assertTrue(result.isHot());
    }

    @Test
    void toVoucherDTO_shouldHandleNullId() {
        Voucher voucher = Voucher.builder()
                .title("T")
                .description("D")
                .price(20.0)
                .status(VoucherStatus.REGISTERED)
                .isHot(false)
                .build();

        VoucherDTO result = mapper.toVoucherDTO(voucher);

        assertNotNull(result);
        assertNull(result.id());
        assertEquals("T", result.title());
        assertEquals("D", result.description());
        assertEquals(20.0, result.price());
        assertEquals(VoucherStatus.REGISTERED, result.status());
        assertFalse(result.isHot());
    }

    @Test
    void toVoucherDTO_shouldHandleNullEnumFields() {
        Voucher voucher = new Voucher();

        voucher.setTitle("T");
        voucher.setDescription("D");
        voucher.setPrice(10.0);
        voucher.setStatus(VoucherStatus.REGISTERED);
        voucher.setHot(false);

        VoucherDTO result = mapper.toVoucherDTO(voucher);

        assertNotNull(result);
        assertNull(result.tourType());
        assertNull(result.transferType());
        assertNull(result.hotelType());
    }

    @Test
    void toVoucherDTO_shouldHandleNullUser() {
        Voucher voucher = Voucher.builder()
                .title("T")
                .description("D")
                .price(10.0)
                .status(VoucherStatus.REGISTERED)
                .isHot(false)
                .user(null)
                .build();

        VoucherDTO result = mapper.toVoucherDTO(voucher);

        assertNotNull(result);
        assertNull(result.userId());
    }

    @Test
    void toVoucherDTO_shouldHandleFalseHotStatus() {
        Voucher voucher = Voucher.builder()
                .title("T")
                .description("D")
                .price(10.0)
                .status(VoucherStatus.REGISTERED)
                .isHot(false)
                .build();

        VoucherDTO result = mapper.toVoucherDTO(voucher);

        assertFalse(result.isHot());
    }
}

