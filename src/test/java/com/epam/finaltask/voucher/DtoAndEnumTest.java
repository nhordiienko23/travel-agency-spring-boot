package com.epam.finaltask.voucher;

import com.epam.finaltask.user.AdminDepositBalanceRequestDTO;
import com.epam.finaltask.user.ChangeAccountStatusRequestDTO;
import com.epam.finaltask.user.ChangePasswordRequestDTO;
import com.epam.finaltask.user.DepositBalanceRequestDTO;
import com.epam.finaltask.user.User;
import com.epam.finaltask.user.UserResponseDTO;
import com.epam.finaltask.user.UserSearchRequestDTO;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DtoAndEnumTest {

    // =========================================================
    // Voucher DTOs
    // =========================================================

    @Test
    void createVoucherRequestDto_shouldExposeAllValues() {
        LocalDate arrivalDate = LocalDate.now();
        LocalDate evictionDate = arrivalDate.plusDays(5);

        CreateVoucherRequestDTO dto =
                CreateVoucherRequestDTO.builder()
                        .title("Trip")
                        .description("Description")
                        .price(100.0)
                        .tourType("adventure")
                        .transferType("plane")
                        .hotelType("five_stars")
                        .arrivalDate(arrivalDate)
                        .evictionDate(evictionDate)
                        .build();

        assertEquals("Trip", dto.title());
        assertEquals("Description", dto.description());
        assertEquals(100.0, dto.price());
        assertEquals("adventure", dto.tourType());
        assertEquals("plane", dto.transferType());
        assertEquals("five_stars", dto.hotelType());
        assertEquals(arrivalDate, dto.arrivalDate());
        assertEquals(evictionDate, dto.evictionDate());
    }

    @Test
    void updateVoucherRequestDto_shouldExposeAllValues() {
        LocalDate arrivalDate = LocalDate.now();
        LocalDate evictionDate = arrivalDate.plusDays(5);

        UpdateVoucherRequestDTO dto =
                UpdateVoucherRequestDTO.builder()
                        .title("Updated Trip")
                        .description("Updated Description")
                        .price(200.0)
                        .tourType("leisure")
                        .transferType("bus")
                        .hotelType("four_stars")
                        .arrivalDate(arrivalDate)
                        .evictionDate(evictionDate)
                        .isHot(true)
                        .status(VoucherStatus.PAID)
                        .build();

        assertEquals("Updated Trip", dto.title());
        assertEquals("Updated Description", dto.description());
        assertEquals(200.0, dto.price());
        assertEquals("leisure", dto.tourType());
        assertEquals("bus", dto.transferType());
        assertEquals("four_stars", dto.hotelType());
        assertEquals(arrivalDate, dto.arrivalDate());
        assertEquals(evictionDate, dto.evictionDate());
        assertTrue(dto.isHot());
        assertEquals(VoucherStatus.PAID, dto.status());
    }

    @Test
    void updateVoucherRequestDto_shouldHandleNullOptionalFields() {
        UpdateVoucherRequestDTO dto =
                UpdateVoucherRequestDTO.builder()
                        .title("Trip")
                        .description("Description")
                        .price(100.0)
                        .tourType("adventure")
                        .transferType("plane")
                        .hotelType("five_stars")
                        .arrivalDate(LocalDate.now())
                        .evictionDate(LocalDate.now().plusDays(1))
                        .isHot(null)
                        .status(null)
                        .build();

        assertEquals(null, dto.isHot());
        assertEquals(null, dto.status());
    }

    @Test
    void changeVoucherStatusRequestDto_shouldExposeStatus() {
        ChangeVoucherStatusRequestDTO dto =
                ChangeVoucherStatusRequestDTO.builder()
                        .status(VoucherStatus.CANCELED)
                        .build();

        assertEquals(
                VoucherStatus.CANCELED,
                dto.status()
        );
    }

    @Test
    void voucherDto_shouldExposeAllValues() {
        UUID userId = UUID.randomUUID();
        LocalDate arrivalDate = LocalDate.now();
        LocalDate evictionDate = arrivalDate.plusDays(5);

        VoucherDTO dto =
                VoucherDTO.builder()
                        .id("voucher-id")
                        .title("Trip")
                        .description("Description")
                        .price(100.0)
                        .tourType("ADVENTURE")
                        .transferType("PLANE")
                        .hotelType("FIVE_STARS")
                        .status(VoucherStatus.REGISTERED)
                        .arrivalDate(arrivalDate)
                        .evictionDate(evictionDate)
                        .userId(userId)
                        .isHot(true)
                        .build();

        assertEquals("voucher-id", dto.id());
        assertEquals("Trip", dto.title());
        assertEquals("Description", dto.description());
        assertEquals(100.0, dto.price());
        assertEquals("ADVENTURE", dto.tourType());
        assertEquals("PLANE", dto.transferType());
        assertEquals("FIVE_STARS", dto.hotelType());
        assertEquals(VoucherStatus.REGISTERED, dto.status());
        assertEquals(arrivalDate, dto.arrivalDate());
        assertEquals(evictionDate, dto.evictionDate());
        assertEquals(userId, dto.userId());
        assertTrue(dto.isHot());
    }

    @Test
    void voucherSearchRequestDto_shouldExposeAllValues() {
        LocalDate dateFrom = LocalDate.now();
        LocalDate dateTo = dateFrom.plusDays(10);

        VoucherSearchRequestDTO dto =
                new VoucherSearchRequestDTO(
                        "sea",
                        "adventure",
                        "plane",
                        "five_stars",
                        1000.0,
                        true,
                        "PAID",
                        dateFrom,
                        dateTo
                );

        assertEquals("sea", dto.keyword());
        assertEquals("adventure", dto.tourType());
        assertEquals("plane", dto.transferType());
        assertEquals("five_stars", dto.hotelType());
        assertEquals(1000.0, dto.maxPrice());
        assertTrue(dto.isHot());
        assertEquals("PAID", dto.status());
        assertEquals(dateFrom, dto.dateFrom());
        assertEquals(dateTo, dto.dateTo());
    }

    @Test
    void voucherSearchRequestDto_shouldHandleNullValues() {
        VoucherSearchRequestDTO dto =
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

        assertEquals(null, dto.keyword());
        assertEquals(null, dto.tourType());
        assertEquals(null, dto.transferType());
        assertEquals(null, dto.hotelType());
        assertEquals(null, dto.maxPrice());
        assertEquals(null, dto.isHot());
        assertEquals(null, dto.status());
        assertEquals(null, dto.dateFrom());
        assertEquals(null, dto.dateTo());
    }

    // =========================================================
    // User DTOs
    // =========================================================

    @Test
    void userResponseDto_shouldExposeAllValues() {
        UUID id = UUID.randomUUID();

        UserResponseDTO dto =
                UserResponseDTO.builder()
                        .id(id.toString())
                        .username("john")
                        .email("john@gmail.com")
                        .lastName("Smith")
                        .phoneNumber("+48123456789")
                        .balance(100.0)
                        .role("USER")
                        .active(true)
                        .build();

        assertEquals(id.toString(), dto.id());
        assertEquals("john", dto.username());
        assertEquals("john@gmail.com", dto.email());
        assertEquals("Smith", dto.lastName());
        assertEquals("+48123456789", dto.phoneNumber());
        assertEquals(100.0, dto.balance());
        assertEquals("USER", dto.role());
        assertTrue(dto.active());
    }

    @Test
    void userSearchRequestDto_shouldExposeAllValues() {
        UserSearchRequestDTO dto =
                new UserSearchRequestDTO(
                        "john",
                        "+48",
                        "USER",
                        true
                );

        assertEquals("john", dto.username());
        assertEquals("+48", dto.phoneNumber());
        assertEquals("USER", dto.role());
        assertTrue(dto.active());
    }

    @Test
    void depositBalanceRequestDto_shouldExposeAmount() {
        DepositBalanceRequestDTO dto =
                DepositBalanceRequestDTO.builder()
                        .amount(50.0)
                        .build();

        assertEquals(
                50.0,
                dto.amount()
        );
    }

    @Test
    void changePasswordRequestDto_shouldExposeAllValues() {
        ChangePasswordRequestDTO dto =
                ChangePasswordRequestDTO.builder()
                        .currentPassword("old")
                        .newPassword("newPassword")
                        .build();

        assertEquals(
                "old",
                dto.currentPassword()
        );

        assertEquals(
                "newPassword",
                dto.newPassword()
        );
    }

    @Test
    void changeAccountStatusRequestDto_shouldExposeAllValues() {
        UUID id = UUID.randomUUID();

        ChangeAccountStatusRequestDTO dto =
                ChangeAccountStatusRequestDTO.builder()
                        .id(id)
                        .active(false)
                        .build();

        assertEquals(
                id,
                dto.id()
        );

        assertFalse(
                dto.active()
        );
    }

    @Test
    void adminDepositBalanceRequestDto_shouldExposeAllValues() {
        UUID userId = UUID.randomUUID();

        AdminDepositBalanceRequestDTO dto =
                AdminDepositBalanceRequestDTO.builder()
                        .userId(userId)
                        .amount(75.0)
                        .build();

        assertEquals(
                userId,
                dto.userId()
        );

        assertEquals(
                75.0,
                dto.amount()
        );
    }

    // =========================================================
    // Enums
    // =========================================================

    @Test
    void voucherEnums_shouldContainAllExpectedValues() {
        assertEquals(
                8,
                TransferType.values().length
        );

        assertEquals(
                8,
                TourType.values().length
        );

        assertEquals(
                5,
                HotelType.values().length
        );

        assertEquals(
                3,
                VoucherStatus.values().length
        );
    }

    @Test
    void transferType_shouldContainAllValues() {
        assertEquals(
                TransferType.BUS,
                TransferType.valueOf("BUS")
        );

        assertEquals(
                TransferType.TRAIN,
                TransferType.valueOf("TRAIN")
        );

        assertEquals(
                TransferType.PLANE,
                TransferType.valueOf("PLANE")
        );

        assertEquals(
                TransferType.SHIP,
                TransferType.valueOf("SHIP")
        );

        assertEquals(
                TransferType.PRIVATE_CAR,
                TransferType.valueOf("PRIVATE_CAR")
        );

        assertEquals(
                TransferType.JEEPS,
                TransferType.valueOf("JEEPS")
        );

        assertEquals(
                TransferType.MINIBUS,
                TransferType.valueOf("MINIBUS")
        );

        assertEquals(
                TransferType.ELECTRICAL_CARS,
                TransferType.valueOf("ELECTRICAL_CARS")
        );
    }

    @Test
    void tourType_shouldContainAllValues() {
        assertEquals(
                TourType.HEALTH,
                TourType.valueOf("HEALTH")
        );

        assertEquals(
                TourType.SPORTS,
                TourType.valueOf("SPORTS")
        );

        assertEquals(
                TourType.LEISURE,
                TourType.valueOf("LEISURE")
        );

        assertEquals(
                TourType.SAFARI,
                TourType.valueOf("SAFARI")
        );

        assertEquals(
                TourType.WINE,
                TourType.valueOf("WINE")
        );

        assertEquals(
                TourType.ECO,
                TourType.valueOf("ECO")
        );

        assertEquals(
                TourType.ADVENTURE,
                TourType.valueOf("ADVENTURE")
        );

        assertEquals(
                TourType.CULTURAL,
                TourType.valueOf("CULTURAL")
        );
    }

    @Test
    void hotelType_shouldContainAllValues() {
        assertEquals(
                HotelType.ONE_STAR,
                HotelType.valueOf("ONE_STAR")
        );

        assertEquals(
                HotelType.TWO_STARS,
                HotelType.valueOf("TWO_STARS")
        );

        assertEquals(
                HotelType.THREE_STARS,
                HotelType.valueOf("THREE_STARS")
        );

        assertEquals(
                HotelType.FOUR_STARS,
                HotelType.valueOf("FOUR_STARS")
        );

        assertEquals(
                HotelType.FIVE_STARS,
                HotelType.valueOf("FIVE_STARS")
        );
    }

    @Test
    void voucherStatus_shouldContainAllValues() {
        assertEquals(
                VoucherStatus.REGISTERED,
                VoucherStatus.valueOf("REGISTERED")
        );

        assertEquals(
                VoucherStatus.PAID,
                VoucherStatus.valueOf("PAID")
        );

        assertEquals(
                VoucherStatus.CANCELED,
                VoucherStatus.valueOf("CANCELED")
        );
    }

    // =========================================================
    // Lombok builders
    // =========================================================

    @Test
    void voucherBuilder_shouldReturnBuilderString() {
        Voucher.VoucherBuilder builder =
                Voucher.builder()
                        .id(UUID.randomUUID())
                        .title("Trip")
                        .description("Description")
                        .price(100.0)
                        .tourType(TourType.ADVENTURE)
                        .transferType(TransferType.PLANE)
                        .hotelType(HotelType.FIVE_STARS)
                        .status(VoucherStatus.REGISTERED)
                        .arrivalDate(LocalDate.now())
                        .evictionDate(LocalDate.now().plusDays(5))
                        .isHot(true);

        assertNotNull(builder.toString());
    }

    @Test
    void voucherDtoBuilder_shouldReturnBuilderString() {
        VoucherDTO.VoucherDTOBuilder builder =
                VoucherDTO.builder()
                        .id(UUID.randomUUID().toString())
                        .title("Trip")
                        .description("Description")
                        .price(100.0)
                        .tourType("ADVENTURE")
                        .transferType("PLANE")
                        .hotelType("FIVE_STARS")
                        .status(VoucherStatus.REGISTERED)
                        .arrivalDate(LocalDate.now())
                        .evictionDate(LocalDate.now().plusDays(5))
                        .userId(UUID.randomUUID())
                        .isHot(true);

        assertNotNull(builder.toString());
    }

    @Test
    void createVoucherRequestDtoBuilder_shouldReturnBuilderString() {
        CreateVoucherRequestDTO.CreateVoucherRequestDTOBuilder builder =
                CreateVoucherRequestDTO.builder()
                        .title("Trip")
                        .description("Description")
                        .price(100.0)
                        .tourType("adventure")
                        .transferType("plane")
                        .hotelType("five_stars")
                        .arrivalDate(LocalDate.now())
                        .evictionDate(LocalDate.now().plusDays(5));

        assertNotNull(builder.toString());
    }

    @Test
    void updateVoucherRequestDtoBuilder_shouldReturnBuilderString() {
        UpdateVoucherRequestDTO.UpdateVoucherRequestDTOBuilder builder =
                UpdateVoucherRequestDTO.builder()
                        .title("Trip")
                        .description("Description")
                        .price(100.0)
                        .tourType("adventure")
                        .transferType("plane")
                        .hotelType("five_stars")
                        .arrivalDate(LocalDate.now())
                        .evictionDate(LocalDate.now().plusDays(5))
                        .isHot(true)
                        .status(VoucherStatus.PAID);

        assertNotNull(builder.toString());
    }

    @Test
    void changeVoucherStatusRequestDtoBuilder_shouldReturnBuilderString() {
        ChangeVoucherStatusRequestDTO.ChangeVoucherStatusRequestDTOBuilder builder =
                ChangeVoucherStatusRequestDTO.builder()
                        .status(VoucherStatus.CANCELED);

        assertNotNull(builder.toString());

        ChangeVoucherStatusRequestDTO result =
                builder.build();

        assertEquals(
                VoucherStatus.CANCELED,
                result.status()
        );
    }

    // =========================================================
    // Voucher entity
    // =========================================================

    @Test
    void voucher_shouldExposeAllFields() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User user =
                User.builder()
                        .id(userId)
                        .username("john")
                        .build();

        LocalDate arrivalDate = LocalDate.now();
        LocalDate evictionDate = arrivalDate.plusDays(5);

        Voucher voucher =
                Voucher.builder()
                        .id(id)
                        .title("Trip")
                        .description("Description")
                        .price(150.0)
                        .tourType(TourType.ADVENTURE)
                        .transferType(TransferType.PLANE)
                        .hotelType(HotelType.FIVE_STARS)
                        .status(VoucherStatus.PAID)
                        .arrivalDate(arrivalDate)
                        .evictionDate(evictionDate)
                        .user(user)
                        .isHot(true)
                        .build();

        assertEquals(id, voucher.getId());
        assertEquals("Trip", voucher.getTitle());
        assertEquals("Description", voucher.getDescription());
        assertEquals(150.0, voucher.getPrice());
        assertEquals(TourType.ADVENTURE, voucher.getTourType());
        assertEquals(TransferType.PLANE, voucher.getTransferType());
        assertEquals(HotelType.FIVE_STARS, voucher.getHotelType());
        assertEquals(VoucherStatus.PAID, voucher.getStatus());
        assertEquals(arrivalDate, voucher.getArrivalDate());
        assertEquals(evictionDate, voucher.getEvictionDate());
        assertSame(user, voucher.getUser());
        assertTrue(voucher.isHot());
    }

    @Test
    void voucher_shouldAllowSetters() {
        Voucher voucher = new Voucher();

        UUID id = UUID.randomUUID();
        User user = User.builder()
                .id(UUID.randomUUID())
                .build();

        LocalDate arrivalDate = LocalDate.now();
        LocalDate evictionDate = arrivalDate.plusDays(2);

        voucher.setId(id);
        voucher.setTitle("Trip");
        voucher.setDescription("Description");
        voucher.setPrice(200.0);
        voucher.setTourType(TourType.CULTURAL);
        voucher.setTransferType(TransferType.BUS);
        voucher.setHotelType(HotelType.THREE_STARS);
        voucher.setStatus(VoucherStatus.REGISTERED);
        voucher.setArrivalDate(arrivalDate);
        voucher.setEvictionDate(evictionDate);
        voucher.setUser(user);
        voucher.setHot(false);

        assertEquals(id, voucher.getId());
        assertEquals("Trip", voucher.getTitle());
        assertEquals("Description", voucher.getDescription());
        assertEquals(200.0, voucher.getPrice());
        assertEquals(TourType.CULTURAL, voucher.getTourType());
        assertEquals(TransferType.BUS, voucher.getTransferType());
        assertEquals(HotelType.THREE_STARS, voucher.getHotelType());
        assertEquals(VoucherStatus.REGISTERED, voucher.getStatus());
        assertEquals(arrivalDate, voucher.getArrivalDate());
        assertEquals(evictionDate, voucher.getEvictionDate());
        assertSame(user, voucher.getUser());
        assertFalse(voucher.isHot());
    }
}

