package com.epam.finaltask.voucher;

import com.epam.finaltask.core.exception.notFound.ResourceNotFoundException;
import com.epam.finaltask.log.AuditContext;
import com.epam.finaltask.user.User;
import com.epam.finaltask.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoucherServiceImplTest {

    @Mock
    private VoucherRepository voucherRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VoucherMapper voucherMapper;

    @InjectMocks
    private VoucherServiceImpl service;

    private UUID voucherId;
    private UUID userId;

    private Voucher voucher;
    private User user;

    @BeforeEach
    void setUp() {

        voucherId = UUID.randomUUID();
        userId = UUID.randomUUID();

        user = User.builder()
                .id(userId)
                .username("john")
                .balance(100.0)
                .build();

        voucher = Voucher.builder()
                .id(voucherId)
                .title("Trip")
                .description("Description")
                .price(60.0)
                .tourType(TourType.LEISURE)
                .transferType(TransferType.BUS)
                .hotelType(HotelType.THREE_STARS)
                .status(VoucherStatus.REGISTERED)
                .arrivalDate(LocalDate.now().plusDays(5))
                .evictionDate(LocalDate.now().plusDays(7))
                .isHot(false)
                .build();
    }

    @AfterEach
    void tearDown() {
        AuditContext.clear();
    }

    // ========================================================================
    // findById
    // ========================================================================

    @Test
    void findById_shouldReturnDto() {

        VoucherDTO dto = VoucherDTO.builder()
                .id(voucherId.toString())
                .title("Trip")
                .build();

        when(voucherRepository.findById(voucherId))
                .thenReturn(Optional.of(voucher));

        when(voucherMapper.toVoucherDTO(voucher))
                .thenReturn(dto);

        VoucherDTO result =
                service.findById(voucherId.toString());

        assertEquals(dto, result);

        verify(voucherRepository)
                .findById(voucherId);

        verify(voucherMapper)
                .toVoucherDTO(voucher);
    }

    @Test
    void findById_shouldThrowWhenVoucherNotFound() {

        when(voucherRepository.findById(voucherId))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> service.findById(
                                voucherId.toString()
                        )
                );

        assertEquals(
                "err.tour.notFound",
                exception.getMessage()
        );

        verify(voucherRepository)
                .findById(voucherId);

        verifyNoInteractions(
                voucherMapper
        );
    }

    @Test
    void findById_shouldThrowWhenIdIsInvalid() {

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> service.findById("invalid-id")
                );

        assertEquals(
                "err.tour.notFound",
                exception.getMessage()
        );

        verifyNoInteractions(
                voucherRepository,
                voucherMapper
        );
    }

    // ========================================================================
    // create
    // ========================================================================

    @Test
    void create_shouldInitializeRegisteredNotHotAndWithoutUser() {

        CreateVoucherRequestDTO request =
                CreateVoucherRequestDTO.builder()
                        .title("Trip")
                        .description("Description")
                        .price(60.0)
                        .tourType("leisure")
                        .transferType("bus")
                        .hotelType("three_stars")
                        .arrivalDate(
                                voucher.getArrivalDate()
                        )
                        .evictionDate(
                                voucher.getEvictionDate()
                        )
                        .build();

        Voucher mappedVoucher =
                Voucher.builder()
                        .title("Trip")
                        .description("Description")
                        .price(60.0)
                        .build();

        VoucherDTO dto =
                VoucherDTO.builder()
                        .title("Trip")
                        .build();

        when(voucherMapper.toVoucher(request))
                .thenReturn(mappedVoucher);

        when(voucherRepository.save(mappedVoucher))
                .thenReturn(mappedVoucher);

        when(voucherMapper.toVoucherDTO(mappedVoucher))
                .thenReturn(dto);

        VoucherDTO result =
                service.create(request);

        assertEquals(dto, result);
        assertNull(mappedVoucher.getUser());
        assertEquals(
                VoucherStatus.REGISTERED,
                mappedVoucher.getStatus()
        );
        assertFalse(mappedVoucher.isHot());

        verify(voucherMapper)
                .toVoucher(request);

        verify(voucherRepository)
                .save(mappedVoucher);

        verify(voucherMapper)
                .toVoucherDTO(mappedVoucher);
    }

    // ========================================================================
    // order
    // ========================================================================

    @Test
    void order_shouldBuyVoucherAndReduceBalance() {

        when(voucherRepository.findById(voucherId))
                .thenReturn(Optional.of(voucher));

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(userRepository.save(user))
                .thenReturn(user);

        when(voucherRepository.save(voucher))
                .thenReturn(voucher);

        VoucherDTO dto =
                VoucherDTO.builder()
                        .id(voucherId.toString())
                        .build();

        when(voucherMapper.toVoucherDTO(voucher))
                .thenReturn(dto);

        VoucherDTO result =
                service.order(
                        voucherId.toString(),
                        userId.toString()
                );

        assertEquals(dto, result);
        assertEquals(40.0, user.getBalance());
        assertSame(user, voucher.getUser());
        assertEquals(
                VoucherStatus.PAID,
                voucher.getStatus()
        );

        verify(userRepository)
                .save(user);

        verify(voucherRepository)
                .save(voucher);

        verify(voucherMapper)
                .toVoucherDTO(voucher);
    }

    @Test
    void order_shouldThrowWhenVoucherNotFound() {

        when(voucherRepository.findById(voucherId))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> service.order(
                                voucherId.toString(),
                                userId.toString()
                        )
                );

        assertEquals(
                "err.tour.notFound",
                exception.getMessage()
        );

        verifyNoInteractions(
                userRepository,
                voucherMapper
        );

        verify(
                voucherRepository,
                never()
        ).save(any(Voucher.class));
    }

    @Test
    void order_shouldThrowWhenUserDoesNotExist() {

        when(voucherRepository.findById(voucherId))
                .thenReturn(Optional.of(voucher));

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> service.order(
                                voucherId.toString(),
                                userId.toString()
                        )
                );

        assertEquals(
                "err.user.notFound",
                exception.getMessage()
        );

        verify(userRepository)
                .findById(userId);

        verify(
                userRepository,
                never()
        ).save(any(User.class));

        verify(
                voucherRepository,
                never()
        ).save(any(Voucher.class));

        verifyNoInteractions(
                voucherMapper
        );
    }

    @Test
    void order_shouldThrowWhenUserIdIsInvalid() {

        when(voucherRepository.findById(voucherId))
                .thenReturn(Optional.of(voucher));

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> service.order(
                                voucherId.toString(),
                                "invalid-id"
                        )
                );

        assertEquals(
                "err.user.notFound",
                exception.getMessage()
        );

        verify(
                userRepository,
                never()
        ).save(any(User.class));

        verify(
                voucherRepository,
                never()
        ).save(any(Voucher.class));

        verifyNoInteractions(
                voucherMapper
        );
    }

    @Test
    void order_shouldRejectAlreadyOwnedVoucher() {

        voucher.setUser(user);

        when(voucherRepository.findById(voucherId))
                .thenReturn(Optional.of(voucher));

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.order(
                                voucherId.toString(),
                                userId.toString()
                        )
                );

        assertEquals(
                "err.tour.ordered",
                exception.getMessage()
        );

        verify(
                userRepository,
                never()
        ).save(any(User.class));

        verify(
                voucherRepository,
                never()
        ).save(any(Voucher.class));
    }

    @Test
    void order_shouldRejectPaidVoucher() {

        voucher.setStatus(
                VoucherStatus.PAID
        );

        when(voucherRepository.findById(voucherId))
                .thenReturn(Optional.of(voucher));

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.order(
                                voucherId.toString(),
                                userId.toString()
                        )
                );

        assertEquals(
                "err.tour.ordered",
                exception.getMessage()
        );

        verify(
                userRepository,
                never()
        ).save(any(User.class));

        verify(
                voucherRepository,
                never()
        ).save(any(Voucher.class));
    }

    @Test
    void order_shouldRejectInsufficientFunds() {

        user.setBalance(10.0);

        when(voucherRepository.findById(voucherId))
                .thenReturn(Optional.of(voucher));

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.order(
                                voucherId.toString(),
                                userId.toString()
                        )
                );

        assertEquals(
                "err.tour.funds",
                exception.getMessage()
        );

        verify(
                userRepository,
                never()
        ).save(any(User.class));

        verify(
                voucherRepository,
                never()
        ).save(any(Voucher.class));
    }

    @Test
    void order_shouldHandleNullUserBalance() {

        user.setBalance(null);

        when(voucherRepository.findById(voucherId))
                .thenReturn(Optional.of(voucher));

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.order(
                                voucherId.toString(),
                                userId.toString()
                        )
                );

        assertEquals(
                "err.tour.funds",
                exception.getMessage()
        );

        verify(
                userRepository,
                never()
        ).save(any(User.class));

        verify(
                voucherRepository,
                never()
        ).save(any(Voucher.class));
    }

    // ========================================================================
    // update
    // ========================================================================

    @Test
    void update_shouldApplyAllFields() {

        when(voucherRepository.findById(voucherId))
                .thenReturn(Optional.of(voucher));

        when(voucherRepository.save(voucher))
                .thenReturn(voucher);

        VoucherDTO dto =
                VoucherDTO.builder()
                        .id(voucherId.toString())
                        .build();

        when(voucherMapper.toVoucherDTO(voucher))
                .thenReturn(dto);

        LocalDate arrivalDate =
                LocalDate.now().plusDays(10);

        LocalDate evictionDate =
                LocalDate.now().plusDays(12);

        UpdateVoucherRequestDTO request =
                UpdateVoucherRequestDTO.builder()
                        .title("New")
                        .description("New Description")
                        .price(70.0)
                        .tourType("adventure")
                        .transferType("plane")
                        .hotelType("five_stars")
                        .arrivalDate(arrivalDate)
                        .evictionDate(evictionDate)
                        .isHot(true)
                        .status(VoucherStatus.REGISTERED)
                        .build();

        VoucherDTO result =
                service.update(
                        voucherId.toString(),
                        request
                );

        assertEquals(dto, result);
        assertEquals(
                "New",
                voucher.getTitle()
        );
        assertEquals(
                "New Description",
                voucher.getDescription()
        );
        assertEquals(
                70.0,
                voucher.getPrice()
        );
        assertEquals(
                TourType.ADVENTURE,
                voucher.getTourType()
        );
        assertEquals(
                TransferType.PLANE,
                voucher.getTransferType()
        );
        assertEquals(
                HotelType.FIVE_STARS,
                voucher.getHotelType()
        );
        assertEquals(
                arrivalDate,
                voucher.getArrivalDate()
        );
        assertEquals(
                evictionDate,
                voucher.getEvictionDate()
        );
        assertTrue(voucher.isHot());
        assertEquals(
                VoucherStatus.REGISTERED,
                voucher.getStatus()
        );
        assertNotNull(
                AuditContext.getDetails()
        );

        verify(voucherRepository)
                .save(voucher);
    }

    @Test
    void update_shouldSetPaidWithoutRefund() {

        voucher.setUser(user);
        voucher.setStatus(VoucherStatus.PAID);

        when(voucherRepository.findById(voucherId))
                .thenReturn(Optional.of(voucher));

        when(voucherRepository.save(voucher))
                .thenReturn(voucher);

        when(voucherMapper.toVoucherDTO(voucher))
                .thenReturn(
                        VoucherDTO.builder().build()
                );

        UpdateVoucherRequestDTO request =
                createUpdateRequestWithStatus(
                        VoucherStatus.PAID
                );

        service.update(
                voucherId.toString(),
                request
        );

        assertEquals(
                VoucherStatus.PAID,
                voucher.getStatus()
        );

        assertSame(
                user,
                voucher.getUser()
        );

        assertEquals(
                100.0,
                user.getBalance()
        );

        verify(
                userRepository,
                never()
        ).save(any(User.class));

        verify(voucherRepository)
                .save(voucher);
    }

    @Test
    void update_shouldRefundWhenChangingPaidToCanceled() {

        voucher.setUser(user);
        voucher.setStatus(VoucherStatus.PAID);

        when(voucherRepository.findById(voucherId))
                .thenReturn(Optional.of(voucher));

        when(voucherRepository.save(voucher))
                .thenReturn(voucher);

        when(userRepository.save(user))
                .thenReturn(user);

        when(voucherMapper.toVoucherDTO(voucher))
                .thenReturn(
                        VoucherDTO.builder().build()
                );

        UpdateVoucherRequestDTO request =
                createUpdateRequestWithStatus(
                        VoucherStatus.CANCELED
                );

        service.update(
                voucherId.toString(),
                request
        );

        assertEquals(
                160.0,
                user.getBalance()
        );

        assertNull(
                voucher.getUser()
        );

        assertEquals(
                VoucherStatus.CANCELED,
                voucher.getStatus()
        );

        verify(userRepository)
                .save(user);

        verify(voucherRepository)
                .save(voucher);
    }

    @Test
    void update_shouldDetachUserWhenVoucherIsNotPaid() {

        voucher.setUser(user);
        voucher.setStatus(
                VoucherStatus.REGISTERED
        );

        when(voucherRepository.findById(voucherId))
                .thenReturn(Optional.of(voucher));

        when(voucherRepository.save(voucher))
                .thenReturn(voucher);

        when(voucherMapper.toVoucherDTO(voucher))
                .thenReturn(
                        VoucherDTO.builder().build()
                );

        UpdateVoucherRequestDTO request =
                createUpdateRequestWithStatus(
                        VoucherStatus.CANCELED
                );

        service.update(
                voucherId.toString(),
                request
        );

        assertEquals(
                100.0,
                user.getBalance()
        );

        assertNull(
                voucher.getUser()
        );

        assertEquals(
                VoucherStatus.CANCELED,
                voucher.getStatus()
        );

        verify(
                userRepository,
                never()
        ).save(any(User.class));

        verify(voucherRepository)
                .save(voucher);
    }

    @Test
    void update_shouldNotRefundWhenVoucherHasNoUser() {

        voucher.setUser(null);
        voucher.setStatus(VoucherStatus.PAID);

        when(voucherRepository.findById(voucherId))
                .thenReturn(Optional.of(voucher));

        when(voucherRepository.save(voucher))
                .thenReturn(voucher);

        when(voucherMapper.toVoucherDTO(voucher))
                .thenReturn(
                        VoucherDTO.builder().build()
                );

        UpdateVoucherRequestDTO request =
                createUpdateRequestWithStatus(
                        VoucherStatus.CANCELED
                );

        service.update(
                voucherId.toString(),
                request
        );

        assertNull(
                voucher.getUser()
        );

        assertEquals(
                VoucherStatus.CANCELED,
                voucher.getStatus()
        );

        verify(
                userRepository,
                never()
        ).save(any(User.class));

        verify(voucherRepository)
                .save(voucher);
    }

    @Test
    void update_shouldThrowWhenVoucherNotFound() {

        when(voucherRepository.findById(voucherId))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> service.update(
                                voucherId.toString(),
                                createUpdateRequest()
                        )
                );

        assertEquals(
                "err.tour.notFound",
                exception.getMessage()
        );

        verify(
                voucherRepository,
                never()
        ).save(any(Voucher.class));
    }

    // ========================================================================
    // INVALID ENUM VALUES
    // ========================================================================

    @Test
    void update_shouldThrowForInvalidTourType() {

        when(voucherRepository.findById(voucherId))
                .thenReturn(Optional.of(voucher));

        UpdateVoucherRequestDTO request =
                UpdateVoucherRequestDTO.builder()
                        .title("New")
                        .description("Description")
                        .price(70.0)
                        .tourType("invalid-tour")
                        .transferType("bus")
                        .hotelType("three_stars")
                        .arrivalDate(
                                LocalDate.now().plusDays(10)
                        )
                        .evictionDate(
                                LocalDate.now().plusDays(12)
                        )
                        .build();

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.update(
                                voucherId.toString(),
                                request
                        )
                );

        assertEquals(
                "err.tourType.invalid",
                exception.getMessage()
        );

        verify(
                voucherRepository,
                never()
        ).save(any(Voucher.class));
    }

    @Test
    void update_shouldThrowForInvalidTransferType() {

        when(voucherRepository.findById(voucherId))
                .thenReturn(Optional.of(voucher));

        UpdateVoucherRequestDTO request =
                UpdateVoucherRequestDTO.builder()
                        .title("New")
                        .description("Description")
                        .price(70.0)
                        .tourType("leisure")
                        .transferType("invalid-transfer")
                        .hotelType("three_stars")
                        .arrivalDate(
                                LocalDate.now().plusDays(10)
                        )
                        .evictionDate(
                                LocalDate.now().plusDays(12)
                        )
                        .build();

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.update(
                                voucherId.toString(),
                                request
                        )
                );

        assertEquals(
                "err.transferType.invalid",
                exception.getMessage()
        );

        verify(
                voucherRepository,
                never()
        ).save(any(Voucher.class));
    }

    @Test
    void update_shouldThrowForInvalidHotelType() {

        when(voucherRepository.findById(voucherId))
                .thenReturn(Optional.of(voucher));

        UpdateVoucherRequestDTO request =
                UpdateVoucherRequestDTO.builder()
                        .title("New")
                        .description("Description")
                        .price(70.0)
                        .tourType("leisure")
                        .transferType("bus")
                        .hotelType("invalid-hotel")
                        .arrivalDate(
                                LocalDate.now().plusDays(10)
                        )
                        .evictionDate(
                                LocalDate.now().plusDays(12)
                        )
                        .build();

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.update(
                                voucherId.toString(),
                                request
                        )
                );

        assertEquals(
                "err.hotelType.invalid",
                exception.getMessage()
        );

        verify(
                voucherRepository,
                never()
        ).save(any(Voucher.class));
    }

    // ========================================================================
    // buildVoucherChanges
    // ========================================================================

    @Test
    void update_shouldReportNoChangesWhenValuesAreUnchanged() {

        UpdateVoucherRequestDTO request =
                createUnchangedRequestBuilder()
                        .isHot(null)
                        .status(null)
                        .build();

        when(voucherRepository.findById(voucherId))
                .thenReturn(Optional.of(voucher));

        when(voucherRepository.save(voucher))
                .thenReturn(voucher);

        when(voucherMapper.toVoucherDTO(voucher))
                .thenReturn(
                        VoucherDTO.builder().build()
                );

        service.update(
                voucherId.toString(),
                request
        );

        assertEquals(
                "voucherId="
                        + voucherId
                        + ", no changes",
                AuditContext.getDetails()
        );
    }

    @Test
    void update_shouldReportHotChange() {

        UpdateVoucherRequestDTO request =
                createUnchangedRequestBuilder()
                        .isHot(true)
                        .status(null)
                        .build();

        when(voucherRepository.findById(voucherId))
                .thenReturn(Optional.of(voucher));

        when(voucherRepository.save(voucher))
                .thenReturn(voucher);

        when(voucherMapper.toVoucherDTO(voucher))
                .thenReturn(
                        VoucherDTO.builder().build()
                );

        service.update(
                voucherId.toString(),
                request
        );

        assertTrue(
                AuditContext.getDetails()
                        .contains(
                                "isHot: false -> true"
                        )
        );
    }

    @Test
    void update_shouldReportStatusChange() {

        UpdateVoucherRequestDTO request =
                createUnchangedRequestBuilder()
                        .isHot(null)
                        .status(VoucherStatus.CANCELED)
                        .build();

        when(voucherRepository.findById(voucherId))
                .thenReturn(Optional.of(voucher));

        when(voucherRepository.save(voucher))
                .thenReturn(voucher);

        when(voucherMapper.toVoucherDTO(voucher))
                .thenReturn(
                        VoucherDTO.builder().build()
                );

        service.update(
                voucherId.toString(),
                request
        );

        assertTrue(
                AuditContext.getDetails()
                        .contains(
                                "status: REGISTERED -> CANCELED"
                        )
        );
    }

    @Test
    void buildVoucherChanges_shouldHandleNullEnumValues() {

        UpdateVoucherRequestDTO request =
                UpdateVoucherRequestDTO.builder()
                        .title(voucher.getTitle())
                        .description(voucher.getDescription())
                        .price(voucher.getPrice())
                        .tourType(null)
                        .transferType(null)
                        .hotelType(null)
                        .arrivalDate(voucher.getArrivalDate())
                        .evictionDate(voucher.getEvictionDate())
                        .isHot(null)
                        .status(null)
                        .build();

        String result =
                ReflectionTestUtils.invokeMethod(
                        service,
                        "buildVoucherChanges",
                        voucherId.toString(),
                        voucher,
                        request
                );

        assertNotNull(result);

        assertTrue(
                result.contains(
                        "tourType: LEISURE -> null"
                )
        );

        assertTrue(
                result.contains(
                        "transferType: BUS -> null"
                )
        );

        assertTrue(
                result.contains(
                        "hotelType: THREE_STARS -> null"
                )
        );
    }

    @Test
    void buildVoucherChanges_shouldFormatNullOldValue() {

        Voucher voucherWithNullTitle =
                Voucher.builder()
                        .id(voucherId)
                        .title(null)
                        .description("Description")
                        .price(60.0)
                        .tourType(TourType.LEISURE)
                        .transferType(TransferType.BUS)
                        .hotelType(HotelType.THREE_STARS)
                        .status(VoucherStatus.REGISTERED)
                        .arrivalDate(
                                voucher.getArrivalDate()
                        )
                        .evictionDate(
                                voucher.getEvictionDate()
                        )
                        .isHot(false)
                        .build();

        UpdateVoucherRequestDTO request =
                createUnchangedRequestBuilder()
                        .title("New Trip")
                        .build();

        String result =
                ReflectionTestUtils.invokeMethod(
                        service,
                        "buildVoucherChanges",
                        voucherId.toString(),
                        voucherWithNullTitle,
                        request
                );

        assertNotNull(result);

        assertTrue(
                result.contains(
                        "title: null -> New Trip"
                )
        );
    }

    // ========================================================================
    // DELETE
    // ========================================================================

    @Test
    void delete_shouldRefundPaidVoucher() {

        voucher.setUser(user);
        voucher.setStatus(
                VoucherStatus.PAID
        );

        when(voucherRepository.findById(voucherId))
                .thenReturn(Optional.of(voucher));

        when(userRepository.save(user))
                .thenReturn(user);

        service.delete(
                voucherId.toString()
        );

        assertEquals(
                160.0,
                user.getBalance()
        );

        assertNull(
                voucher.getUser()
        );

        verify(userRepository)
                .save(user);

        verify(voucherRepository)
                .delete(voucher);
    }

    @Test
    void delete_shouldRefundPaidVoucherWithNullBalance() {

        user.setBalance(null);

        voucher.setUser(user);
        voucher.setStatus(
                VoucherStatus.PAID
        );

        when(voucherRepository.findById(voucherId))
                .thenReturn(Optional.of(voucher));

        when(userRepository.save(user))
                .thenReturn(user);

        service.delete(
                voucherId.toString()
        );

        assertEquals(
                60.0,
                user.getBalance()
        );

        assertNull(
                voucher.getUser()
        );

        verify(userRepository)
                .save(user);

        verify(voucherRepository)
                .delete(voucher);
    }

    @Test
    void delete_shouldDetachRegisteredVoucherWithoutRefund() {

        voucher.setUser(user);
        voucher.setStatus(
                VoucherStatus.REGISTERED
        );

        when(voucherRepository.findById(voucherId))
                .thenReturn(Optional.of(voucher));

        service.delete(
                voucherId.toString()
        );

        assertNull(
                voucher.getUser()
        );

        assertEquals(
                100.0,
                user.getBalance()
        );

        verify(
                userRepository,
                never()
        ).save(any(User.class));

        verify(voucherRepository)
                .delete(voucher);
    }

    @Test
    void delete_shouldNotRefundPaidVoucherWithoutUser() {

        voucher.setUser(null);
        voucher.setStatus(
                VoucherStatus.PAID
        );

        when(voucherRepository.findById(voucherId))
                .thenReturn(Optional.of(voucher));

        service.delete(
                voucherId.toString()
        );

        verify(
                userRepository,
                never()
        ).save(any(User.class));

        verify(voucherRepository)
                .delete(voucher);
    }

    @Test
    void delete_shouldThrowWhenVoucherNotFound() {

        when(voucherRepository.findById(voucherId))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> service.delete(
                                voucherId.toString()
                        )
                );

        assertEquals(
                "err.tour.notFound",
                exception.getMessage()
        );

        verify(
                voucherRepository,
                never()
        ).delete(any(Voucher.class));

        verifyNoInteractions(
                userRepository
        );
    }

    @Test
    void delete_shouldThrowWhenVoucherIdIsInvalid() {

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> service.delete(
                                "invalid-id"
                        )
                );

        assertEquals(
                "err.tour.notFound",
                exception.getMessage()
        );

        verifyNoInteractions(
                voucherRepository,
                userRepository,
                voucherMapper
        );
    }

    // ========================================================================
    // changeHotStatus
    // ========================================================================

    @Test
    void changeHotStatus_shouldSetTrue() {

        voucher.setHot(false);

        when(voucherRepository.findById(voucherId))
                .thenReturn(Optional.of(voucher));

        when(voucherRepository.save(voucher))
                .thenReturn(voucher);

        when(voucherMapper.toVoucherDTO(voucher))
                .thenReturn(
                        VoucherDTO.builder().build()
                );

        service.changeHotStatus(
                voucherId.toString(),
                VoucherDTO.builder()
                        .isHot(true)
                        .build()
        );

        assertTrue(
                voucher.isHot()
        );

        verify(voucherRepository)
                .save(voucher);
    }

    @Test
    void changeHotStatus_shouldSetFalseForNull() {

        voucher.setHot(true);

        when(voucherRepository.findById(voucherId))
                .thenReturn(Optional.of(voucher));

        when(voucherRepository.save(voucher))
                .thenReturn(voucher);

        when(voucherMapper.toVoucherDTO(voucher))
                .thenReturn(
                        VoucherDTO.builder().build()
                );

        service.changeHotStatus(
                voucherId.toString(),
                VoucherDTO.builder()
                        .isHot(null)
                        .build()
        );

        assertFalse(
                voucher.isHot()
        );

        verify(voucherRepository)
                .save(voucher);
    }

    @Test
    void changeHotStatus_shouldThrowWhenVoucherNotFound() {

        when(voucherRepository.findById(voucherId))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> service.changeHotStatus(
                                voucherId.toString(),
                                VoucherDTO.builder()
                                        .isHot(true)
                                        .build()
                        )
                );

        assertEquals(
                "err.tour.notFound",
                exception.getMessage()
        );

        verify(
                voucherRepository,
                never()
        ).save(any(Voucher.class));
    }

    // ========================================================================
    // changeStatus
    // ========================================================================

    @Test
    void changeStatus_shouldRefundPaidWhenChangingToCanceled() {

        voucher.setUser(user);
        voucher.setStatus(
                VoucherStatus.PAID
        );

        when(voucherRepository.findById(voucherId))
                .thenReturn(Optional.of(voucher));

        when(voucherRepository.save(voucher))
                .thenReturn(voucher);

        when(userRepository.save(user))
                .thenReturn(user);

        when(voucherMapper.toVoucherDTO(voucher))
                .thenReturn(
                        VoucherDTO.builder().build()
                );

        service.changeStatus(
                voucherId.toString(),
                new ChangeVoucherStatusRequestDTO(
                        VoucherStatus.CANCELED
                )
        );

        assertEquals(
                160.0,
                user.getBalance()
        );

        assertNull(
                voucher.getUser()
        );

        assertEquals(
                VoucherStatus.CANCELED,
                voucher.getStatus()
        );

        verify(userRepository)
                .save(user);

        verify(voucherRepository)
                .save(voucher);
    }

    @Test
    void changeStatus_shouldDetachUserWhenChangingRegisteredToCanceled() {

        voucher.setUser(user);
        voucher.setStatus(
                VoucherStatus.REGISTERED
        );

        when(voucherRepository.findById(voucherId))
                .thenReturn(Optional.of(voucher));

        when(voucherRepository.save(voucher))
                .thenReturn(voucher);

        when(voucherMapper.toVoucherDTO(voucher))
                .thenReturn(
                        VoucherDTO.builder().build()
                );

        service.changeStatus(
                voucherId.toString(),
                new ChangeVoucherStatusRequestDTO(
                        VoucherStatus.CANCELED
                )
        );

        assertEquals(
                100.0,
                user.getBalance()
        );

        assertNull(
                voucher.getUser()
        );

        assertEquals(
                VoucherStatus.CANCELED,
                voucher.getStatus()
        );

        verify(
                userRepository,
                never()
        ).save(any(User.class));

        verify(voucherRepository)
                .save(voucher);
    }

    @Test
    void changeStatus_shouldNotRefundWhenSettingPaid() {

        voucher.setUser(user);
        voucher.setStatus(
                VoucherStatus.PAID
        );

        when(voucherRepository.findById(voucherId))
                .thenReturn(Optional.of(voucher));

        when(voucherRepository.save(voucher))
                .thenReturn(voucher);

        when(voucherMapper.toVoucherDTO(voucher))
                .thenReturn(
                        VoucherDTO.builder().build()
                );

        service.changeStatus(
                voucherId.toString(),
                new ChangeVoucherStatusRequestDTO(
                        VoucherStatus.PAID
                )
        );

        assertEquals(
                100.0,
                user.getBalance()
        );

        assertSame(
                user,
                voucher.getUser()
        );

        verify(
                userRepository,
                never()
        ).save(any(User.class));

        verify(voucherRepository)
                .save(voucher);
    }

    @Test
    void changeStatus_shouldNotRefundWhenVoucherHasNoUser() {

        voucher.setUser(null);
        voucher.setStatus(
                VoucherStatus.PAID
        );

        when(voucherRepository.findById(voucherId))
                .thenReturn(Optional.of(voucher));

        when(voucherRepository.save(voucher))
                .thenReturn(voucher);

        when(voucherMapper.toVoucherDTO(voucher))
                .thenReturn(
                        VoucherDTO.builder().build()
                );

        service.changeStatus(
                voucherId.toString(),
                new ChangeVoucherStatusRequestDTO(
                        VoucherStatus.CANCELED
                )
        );

        assertNull(
                voucher.getUser()
        );

        assertEquals(
                VoucherStatus.CANCELED,
                voucher.getStatus()
        );

        verify(
                userRepository,
                never()
        ).save(any(User.class));

        verify(voucherRepository)
                .save(voucher);
    }

    @Test
    void changeStatus_shouldThrowWhenVoucherNotFound() {

        when(voucherRepository.findById(voucherId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.changeStatus(
                        voucherId.toString(),
                        new ChangeVoucherStatusRequestDTO(
                                VoucherStatus.CANCELED
                        )
                )
        );

        verify(
                voucherRepository,
                never()
        ).save(any(Voucher.class));
    }

    // ========================================================================
    // cancelOrder
    // ========================================================================

    @Test
    void cancelOrder_shouldRefundOwnPaidVoucher() {

        voucher.setUser(user);
        voucher.setStatus(
                VoucherStatus.PAID
        );

        when(voucherRepository.findById(voucherId))
                .thenReturn(Optional.of(voucher));

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.of(user));

        when(userRepository.save(user))
                .thenReturn(user);

        service.cancelOrder(
                voucherId.toString(),
                "john"
        );

        assertEquals(
                160.0,
                user.getBalance()
        );

        assertNull(
                voucher.getUser()
        );

        assertEquals(
                VoucherStatus.REGISTERED,
                voucher.getStatus()
        );

        verify(userRepository)
                .save(user);

        verify(voucherRepository)
                .save(voucher);
    }

    @Test
    void cancelOrder_shouldRejectNoOwner() {

        voucher.setUser(null);

        when(voucherRepository.findById(voucherId))
                .thenReturn(Optional.of(voucher));

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.of(user));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.cancelOrder(
                                voucherId.toString(),
                                "john"
                        )
                );

        assertEquals(
                "err.tour.cancel.own",
                exception.getMessage()
        );

        verify(
                voucherRepository,
                never()
        ).save(any(Voucher.class));

        verify(
                userRepository,
                never()
        ).save(any(User.class));
    }

    @Test
    void cancelOrder_shouldRejectWrongOwner() {

        User anotherUser =
                User.builder()
                        .id(UUID.randomUUID())
                        .username("other")
                        .balance(100.0)
                        .build();

        voucher.setUser(anotherUser);

        when(voucherRepository.findById(voucherId))
                .thenReturn(Optional.of(voucher));

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.of(user));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.cancelOrder(
                                voucherId.toString(),
                                "john"
                        )
                );

        assertEquals(
                "err.tour.cancel.own",
                exception.getMessage()
        );

        verify(
                voucherRepository,
                never()
        ).save(any(Voucher.class));

        verify(
                userRepository,
                never()
        ).save(any(User.class));
    }

    @Test
    void cancelOrder_shouldThrowWhenUserNotFound() {

        voucher.setUser(user);

        when(voucherRepository.findById(voucherId))
                .thenReturn(Optional.of(voucher));

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> service.cancelOrder(
                                voucherId.toString(),
                                "john"
                        )
                );

        assertEquals(
                "err.user.notFound",
                exception.getMessage()
        );

        verify(
                voucherRepository,
                never()
        ).save(any(Voucher.class));
    }

    @Test
    void cancelOrder_shouldDetachRegisteredVoucherWithoutRefund() {

        voucher.setUser(user);
        voucher.setStatus(
                VoucherStatus.REGISTERED
        );

        when(voucherRepository.findById(voucherId))
                .thenReturn(Optional.of(voucher));

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.of(user));

        service.cancelOrder(
                voucherId.toString(),
                "john"
        );

        assertEquals(
                100.0,
                user.getBalance()
        );

        assertNull(
                voucher.getUser()
        );

        assertEquals(
                VoucherStatus.REGISTERED,
                voucher.getStatus()
        );

        verify(
                userRepository,
                never()
        ).save(any(User.class));

        verify(voucherRepository)
                .save(voucher);
    }

    // ========================================================================
    // findAllByUserIdPaged
    // ========================================================================

    @Test
    void findAllByUserIdPaged_shouldUseRepositoryAndMap() {

        VoucherDTO dto =
                VoucherDTO.builder()
                        .title("Trip")
                        .build();

        Page<Voucher> page =
                new PageImpl<>(
                        List.of(voucher)
                );

        when(
                voucherRepository
                        .findAllByUserIdAndArrivalDateGreaterThanEqual(
                                eq(userId),
                                any(LocalDate.class),
                                any(Pageable.class)
                        )
        ).thenReturn(page);

        when(
                voucherMapper.toVoucherDTO(voucher)
        ).thenReturn(dto);

        Page<VoucherDTO> result =
                service.findAllByUserIdPaged(
                        userId.toString(),
                        0,
                        10
                );

        assertEquals(
                1,
                result.getContent().size()
        );

        assertEquals(
                dto,
                result.getContent().get(0)
        );

        verify(
                voucherRepository
        ).findAllByUserIdAndArrivalDateGreaterThanEqual(
                eq(userId),
                any(LocalDate.class),
                any(Pageable.class)
        );

        verify(
                voucherMapper
        ).toVoucherDTO(voucher);
    }

    @Test
    void findAllByUserIdPaged_shouldThrowForInvalidUserId() {

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> service.findAllByUserIdPaged(
                                "invalid",
                                0,
                                10
                        )
                );

        assertEquals(
                "err.user.notFound",
                exception.getMessage()
        );

        verifyNoInteractions(
                voucherRepository,
                voucherMapper
        );
    }

    // ========================================================================
    // findAvailableVouchers
    // ========================================================================

    @Test
    void findAvailableVouchers_shouldUseAsc() {

        mockFindAllPage();

        Page<VoucherDTO> result =
                service.findAvailableVouchers(
                        emptySearchRequest(),
                        0,
                        8,
                        "price",
                        "asc"
                );

        assertEquals(
                1,
                result.getContent().size()
        );

        Pageable pageable =
                capturePageable();

        assertTrue(
                pageable.getSort()
                        .getOrderFor("price")
                        .isAscending()
        );

        assertTrue(
                pageable.getSort()
                        .getOrderFor("isHot")
                        .isDescending()
        );
    }

    @Test
    void findAvailableVouchers_shouldUseDescForNonAscDirection() {

        mockFindAllPage();

        Page<VoucherDTO> result =
                service.findAvailableVouchers(
                        emptySearchRequest(),
                        0,
                        8,
                        "price",
                        "desc"
                );

        assertEquals(
                1,
                result.getContent().size()
        );

        Pageable pageable =
                capturePageable();

        assertTrue(
                pageable.getSort()
                        .getOrderFor("price")
                        .isDescending()
        );
    }

    @Test
    void findAvailableVouchers_shouldUseDefaultFieldWhenSortFieldNull() {

        mockFindAllPage();

        service.findAvailableVouchers(
                emptySearchRequest(),
                0,
                8,
                null,
                "asc"
        );

        Pageable pageable =
                capturePageable();

        assertNotNull(
                pageable.getSort()
                        .getOrderFor("isHot")
        );
    }

    @Test
    void findAvailableVouchers_shouldUseDefaultFieldWhenSortFieldBlank() {

        mockFindAllPage();

        service.findAvailableVouchers(
                emptySearchRequest(),
                0,
                8,
                "   ",
                "asc"
        );

        Pageable pageable =
                capturePageable();

        assertNotNull(
                pageable.getSort()
                        .getOrderFor("isHot")
        );
    }

    // ========================================================================
    // findAllVouchersPaged
    // ========================================================================

    @Test
    void findAllVouchersPaged_shouldUseAsc() {

        mockFindAllPage();

        Page<VoucherDTO> result =
                service.findAllVouchersPaged(
                        emptySearchRequest(),
                        0,
                        5,
                        "price",
                        "asc"
                );

        assertEquals(
                1,
                result.getContent().size()
        );

        Pageable pageable =
                capturePageable();

        assertTrue(
                pageable.getSort()
                        .getOrderFor("price")
                        .isAscending()
        );

        assertTrue(
                pageable.getSort()
                        .getOrderFor("isHot")
                        .isDescending()
        );
    }

    @Test
    void findAllVouchersPaged_shouldUseDesc() {

        mockFindAllPage();

        Page<VoucherDTO> result =
                service.findAllVouchersPaged(
                        emptySearchRequest(),
                        0,
                        5,
                        "price",
                        "desc"
                );

        assertEquals(
                1,
                result.getContent().size()
        );

        Pageable pageable =
                capturePageable();

        assertTrue(
                pageable.getSort()
                        .getOrderFor("price")
                        .isDescending()
        );
    }

    @Test
    void findAllVouchersPaged_shouldUseDefaultFieldWhenSortFieldNull() {

        mockFindAllPage();

        service.findAllVouchersPaged(
                emptySearchRequest(),
                0,
                5,
                null,
                "desc"
        );

        Pageable pageable =
                capturePageable();

        assertNotNull(
                pageable.getSort()
                        .getOrderFor("isHot")
        );
    }

    @Test
    void findAllVouchersPaged_shouldUseDefaultFieldWhenSortFieldBlank() {

        mockFindAllPage();

        service.findAllVouchersPaged(
                emptySearchRequest(),
                0,
                5,
                "   ",
                "desc"
        );

        Pageable pageable =
                capturePageable();

        assertNotNull(
                pageable.getSort()
                        .getOrderFor("isHot")
        );
    }

    // ========================================================================
    // findAllByUserId
    // ========================================================================

    @Test
    void findAllByUserId_shouldMapList() {

        VoucherDTO dto =
                VoucherDTO.builder()
                        .title("Trip")
                        .build();

        when(
                voucherRepository.findAllByUserId(
                        userId
                )
        ).thenReturn(
                List.of(voucher)
        );

        when(
                voucherMapper.toVoucherDTO(voucher)
        ).thenReturn(dto);

        List<VoucherDTO> result =
                service.findAllByUserId(
                        userId.toString()
                );

        assertEquals(
                1,
                result.size()
        );

        assertEquals(
                dto,
                result.get(0)
        );

        verify(
                voucherRepository
        ).findAllByUserId(userId);

        verify(
                voucherMapper
        ).toVoucherDTO(voucher);
    }

    @Test
    void findAllByUserId_shouldReturnEmptyList() {

        when(
                voucherRepository.findAllByUserId(userId)
        ).thenReturn(
                Collections.emptyList()
        );

        List<VoucherDTO> result =
                service.findAllByUserId(
                        userId.toString()
                );

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(
                voucherRepository
        ).findAllByUserId(userId);

        verifyNoInteractions(
                voucherMapper
        );
    }

    @Test
    void findAllByUserId_shouldThrowForInvalidId() {

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> service.findAllByUserId(
                                "invalid"
                        )
                );

        assertEquals(
                "err.user.notFound",
                exception.getMessage()
        );

        verifyNoInteractions(
                voucherRepository,
                voucherMapper
        );
    }

    // ========================================================================
    // findAll
    // ========================================================================

    @Test
    void findAll_shouldMapList() {

        VoucherDTO dto =
                VoucherDTO.builder()
                        .title("Trip")
                        .build();

        when(
                voucherRepository
                        .findAllByOrderByIsHotDesc()
        ).thenReturn(
                List.of(voucher)
        );

        when(
                voucherMapper.toVoucherDTO(voucher)
        ).thenReturn(dto);

        List<VoucherDTO> result =
                service.findAll();

        assertEquals(
                1,
                result.size()
        );

        assertEquals(
                dto,
                result.get(0)
        );

        verify(
                voucherRepository
        ).findAllByOrderByIsHotDesc();

        verify(
                voucherMapper
        ).toVoucherDTO(voucher);
    }

    @Test
    void findAll_shouldReturnEmptyList() {

        when(
                voucherRepository
                        .findAllByOrderByIsHotDesc()
        ).thenReturn(
                Collections.emptyList()
        );

        List<VoucherDTO> result =
                service.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(
                voucherRepository
        ).findAllByOrderByIsHotDesc();

        verifyNoInteractions(
                voucherMapper
        );
    }

    // ========================================================================
    // HELPERS
    // ========================================================================

    private UpdateVoucherRequestDTO createUpdateRequest() {

        return UpdateVoucherRequestDTO.builder()
                .title("New")
                .description("Description")
                .price(70.0)
                .tourType("leisure")
                .transferType("bus")
                .hotelType("three_stars")
                .arrivalDate(
                        LocalDate.now().plusDays(10)
                )
                .evictionDate(
                        LocalDate.now().plusDays(12)
                )
                .isHot(null)
                .status(null)
                .build();
    }

    private UpdateVoucherRequestDTO createUpdateRequestWithStatus(
            VoucherStatus status
    ) {

        return createUnchangedRequestBuilder()
                .isHot(null)
                .status(status)
                .build();
    }

    private UpdateVoucherRequestDTO.UpdateVoucherRequestDTOBuilder
    createUnchangedRequestBuilder() {

        return UpdateVoucherRequestDTO.builder()
                .title(
                        voucher.getTitle()
                )
                .description(
                        voucher.getDescription()
                )
                .price(
                        voucher.getPrice()
                )
                .tourType(
                        voucher.getTourType()
                                .name()
                                .toLowerCase()
                )
                .transferType(
                        voucher.getTransferType()
                                .name()
                                .toLowerCase()
                )
                .hotelType(
                        voucher.getHotelType()
                                .name()
                                .toLowerCase()
                )
                .arrivalDate(
                        voucher.getArrivalDate()
                )
                .evictionDate(
                        voucher.getEvictionDate()
                );
    }

    private void mockFindAllPage() {

        Page<Voucher> page =
                new PageImpl<>(
                        List.of(voucher)
                );

        when(
                voucherRepository.findAll(
                        any(Specification.class),
                        any(Pageable.class)
                )
        ).thenReturn(page);

        when(
                voucherMapper.toVoucherDTO(voucher)
        ).thenReturn(
                VoucherDTO.builder()
                        .title("Trip")
                        .build()
        );
    }

    private Pageable capturePageable() {

        ArgumentCaptor<Pageable> pageableCaptor =
                ArgumentCaptor.forClass(
                        Pageable.class
                );

        verify(
                voucherRepository
        ).findAll(
                any(Specification.class),
                pageableCaptor.capture()
        );

        return pageableCaptor.getValue();
    }

    private VoucherSearchRequestDTO emptySearchRequest() {

        return new VoucherSearchRequestDTO(
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
    }
}

