package com.epam.finaltask.voucher;

import com.epam.finaltask.core.exception.notFound.ResourceNotFoundException;
import com.epam.finaltask.log.AuditContext;
import com.epam.finaltask.log.Loggable;
import com.epam.finaltask.user.User;
import com.epam.finaltask.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository;
    private final UserRepository userRepository;
    private final VoucherMapper voucherMapper;

    // ========================================================================
    // HELPERS
    // ========================================================================

    private UUID parseUuidOrThrow(
            String value,
            String errorKey
    ) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException(
                    errorKey
            );
        }
    }

    private Voucher findVoucherByIdOrThrow(
            String id
    ) {

        UUID voucherId =
                parseUuidOrThrow(
                        id,
                        "err.tour.notFound"
                );

        return voucherRepository
                .findById(voucherId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "err.tour.notFound"
                        )
                );
    }

    private User findUserByIdOrThrow(
            String userId
    ) {

        UUID id =
                parseUuidOrThrow(
                        userId,
                        "err.user.notFound"
                );

        return userRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "err.user.notFound"
                        )
                );
    }

    private User findUserByUsernameOrThrow(
            String username
    ) {

        return userRepository
                .findUserByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "err.user.notFound"
                        )
                );
    }

    private TourType parseTourType(
            String value
    ) {

        try {
            return TourType.valueOf(
                    value.toUpperCase()
            );
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "err.tourType.invalid"
            );
        }
    }

    private TransferType parseTransferType(
            String value
    ) {

        try {
            return TransferType.valueOf(
                    value.toUpperCase()
            );
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "err.transferType.invalid"
            );
        }
    }

    private HotelType parseHotelType(
            String value
    ) {

        try {
            return HotelType.valueOf(
                    value.toUpperCase()
            );
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "err.hotelType.invalid"
            );
        }
    }

    /**
     * Refunds the voucher price when the voucher was paid
     * and removes the user from the voucher.
     */
    private void processRefundIfNecessary(
            Voucher voucher
    ) {

        User user =
                voucher.getUser();

        if (user == null) {
            return;
        }

        if (voucher.getStatus() == VoucherStatus.PAID) {

            double currentBalance =
                    user.getBalance() == null
                            ? 0.0
                            : user.getBalance();

            user.setBalance(
                    currentBalance
                            + voucher.getPrice()
            );

            userRepository.save(
                    user
            );
        }

        voucher.setUser(null);
    }

    private void addChange(
            List<String> changes,
            String field,
            Object oldValue,
            Object newValue
    ) {

        if (!Objects.equals(
                oldValue,
                newValue
        )) {

            changes.add(
                    field
                            + ": "
                            + formatValue(oldValue)
                            + " -> "
                            + formatValue(newValue)
            );
        }
    }

    private String formatValue(
            Object value
    ) {

        if (value == null) {
            return "null";
        }

        if (value instanceof Number number) {
            return String.format(
                    "%.2f",
                    number.doubleValue()
            );
        }

        return String.valueOf(value);
    }

    private String buildVoucherChanges(
            String voucherId,
            Voucher voucher,
            UpdateVoucherRequestDTO request
    ) {

        List<String> changes =
                new ArrayList<>();

        TourType newTourType =
                request.tourType() == null
                        ? null
                        : parseTourType(
                        request.tourType()
                );

        TransferType newTransferType =
                request.transferType() == null
                        ? null
                        : parseTransferType(
                        request.transferType()
                );

        HotelType newHotelType =
                request.hotelType() == null
                        ? null
                        : parseHotelType(
                        request.hotelType()
                );

        addChange(
                changes,
                "title",
                voucher.getTitle(),
                request.title()
        );

        addChange(
                changes,
                "description",
                voucher.getDescription(),
                request.description()
        );

        addChange(
                changes,
                "price",
                voucher.getPrice(),
                request.price()
        );

        addChange(
                changes,
                "tourType",
                voucher.getTourType(),
                newTourType
        );

        addChange(
                changes,
                "transferType",
                voucher.getTransferType(),
                newTransferType
        );

        addChange(
                changes,
                "hotelType",
                voucher.getHotelType(),
                newHotelType
        );

        addChange(
                changes,
                "arrivalDate",
                voucher.getArrivalDate(),
                request.arrivalDate()
        );

        addChange(
                changes,
                "evictionDate",
                voucher.getEvictionDate(),
                request.evictionDate()
        );

        if (request.isHot() != null) {

            addChange(
                    changes,
                    "isHot",
                    voucher.isHot(),
                    request.isHot()
            );
        }

        if (request.status() != null) {

            addChange(
                    changes,
                    "status",
                    voucher.getStatus(),
                    request.status()
            );
        }

        if (changes.isEmpty()) {

            return "voucherId="
                    + voucherId
                    + ", no changes";
        }

        return "voucherId="
                + voucherId
                + ", "
                + String.join(
                ", ",
                changes
        );
    }

    // ========================================================================
    // FIND
    // ========================================================================

    @Override
    public VoucherDTO findById(
            String id
    ) {

        return voucherMapper.toVoucherDTO(
                findVoucherByIdOrThrow(id)
        );
    }

    // ========================================================================
    // CREATE
    // ========================================================================

    @Override
    @Transactional
    @Loggable("CREATE_NEW_TOUR")
    public VoucherDTO create(
            CreateVoucherRequestDTO request
    ) {

        Voucher voucher =
                voucherMapper.toVoucher(
                        request
                );

        voucher.setUser(null);

        voucher.setStatus(
                VoucherStatus.REGISTERED
        );

        voucher.setHot(false);

        return voucherMapper.toVoucherDTO(
                voucherRepository.save(voucher)
        );
    }

    // ========================================================================
    // ORDER
    // ========================================================================

    @Override
    @Transactional
    @Loggable("ORDER_TOUR")
    public VoucherDTO order(
            String id,
            String userId
    ) {

        Voucher voucher =
                findVoucherByIdOrThrow(id);

        User user =
                findUserByIdOrThrow(userId);

        if (voucher.getUser() != null
                || voucher.getStatus()
                == VoucherStatus.PAID) {

            throw new IllegalArgumentException(
                    "err.tour.ordered"
            );
        }

        double balance =
                user.getBalance() == null
                        ? 0.0
                        : user.getBalance();

        if (balance < voucher.getPrice()) {

            throw new IllegalArgumentException(
                    "err.tour.funds"
            );
        }

        user.setBalance(
                balance
                        - voucher.getPrice()
        );

        userRepository.save(
                user
        );

        voucher.setUser(
                user
        );

        voucher.setStatus(
                VoucherStatus.PAID
        );

        return voucherMapper.toVoucherDTO(
                voucherRepository.save(
                        voucher
                )
        );
    }

    // ========================================================================
    // UPDATE
    // ========================================================================

    @Override
    @Transactional
    @Loggable("UPDATE_TOUR_INFO")
    public VoucherDTO update(
            String id,
            UpdateVoucherRequestDTO request
    ) {

        Voucher existingVoucher =
                findVoucherByIdOrThrow(id);

        String auditDetails =
                buildVoucherChanges(
                        id,
                        existingVoucher,
                        request
                );

        AuditContext.setDetails(
                auditDetails
        );

        existingVoucher.setTitle(
                request.title()
        );

        existingVoucher.setDescription(
                request.description()
        );

        existingVoucher.setPrice(
                request.price()
        );

        existingVoucher.setTourType(
                parseTourType(
                        request.tourType()
                )
        );

        existingVoucher.setTransferType(
                parseTransferType(
                        request.transferType()
                )
        );

        existingVoucher.setHotelType(
                parseHotelType(
                        request.hotelType()
                )
        );

        existingVoucher.setArrivalDate(
                request.arrivalDate()
        );

        existingVoucher.setEvictionDate(
                request.evictionDate()
        );

        if (request.isHot() != null) {

            existingVoucher.setHot(
                    request.isHot()
            );
        }

        if (request.status() != null) {

            if (request.status()
                    != VoucherStatus.PAID) {

                processRefundIfNecessary(
                        existingVoucher
                );
            }

            existingVoucher.setStatus(
                    request.status()
            );
        }

        return voucherMapper.toVoucherDTO(
                voucherRepository.save(
                        existingVoucher
                )
        );
    }

    // ========================================================================
    // DELETE
    // ========================================================================

    @Override
    @Transactional
    @Loggable("DELETE_TOUR")
    public void delete(
            String voucherId
    ) {

        Voucher voucher =
                findVoucherByIdOrThrow(
                        voucherId
                );

        processRefundIfNecessary(
                voucher
        );

        voucherRepository.delete(
                voucher
        );
    }

    // ========================================================================
    // HOT STATUS
    // ========================================================================

    @Override
    @Transactional
    @Loggable("TOGGLE_HOT_STATUS")
    public VoucherDTO changeHotStatus(
            String id,
            VoucherDTO voucherDTO
    ) {

        Voucher voucher =
                findVoucherByIdOrThrow(id);

        boolean oldValue =
                voucher.isHot();

        boolean newValue =
                Boolean.TRUE.equals(
                        voucherDTO.isHot()
                );

        AuditContext.setDetails(
                String.format(
                        "voucherId=%s, isHot: %s -> %s",
                        id,
                        oldValue,
                        newValue
                )
        );

        voucher.setHot(
                newValue
        );

        return voucherMapper.toVoucherDTO(
                voucherRepository.save(
                        voucher
                )
        );
    }

    // ========================================================================
    // CHANGE STATUS
    // ========================================================================

    @Override
    @Transactional
    @Loggable("CHANGE_TOUR_STATUS")
    public VoucherDTO changeStatus(
            String id,
            ChangeVoucherStatusRequestDTO request
    ) {

        Voucher voucher =
                findVoucherByIdOrThrow(id);

        VoucherStatus oldStatus =
                voucher.getStatus();

        VoucherStatus newStatus =
                request.status();

        AuditContext.setDetails(
                String.format(
                        "voucherId=%s, status: %s -> %s",
                        id,
                        oldStatus,
                        newStatus
                )
        );

        if (newStatus != VoucherStatus.PAID) {

            processRefundIfNecessary(
                    voucher
            );
        }

        voucher.setStatus(
                newStatus
        );

        return voucherMapper.toVoucherDTO(
                voucherRepository.save(
                        voucher
                )
        );
    }

    // ========================================================================
    // CANCEL ORDER
    // ========================================================================

    @Override
    @Transactional
    @Loggable("CANCEL_TOUR")
    public void cancelOrder(
            String voucherId,
            String username
    ) {

        Voucher voucher =
                findVoucherByIdOrThrow(
                        voucherId
                );

        User user =
                findUserByUsernameOrThrow(
                        username
                );

        if (voucher.getUser() == null
                || !voucher.getUser()
                .getId()
                .equals(user.getId())) {

            throw new IllegalArgumentException(
                    "err.tour.cancel.own"
            );
        }

        VoucherStatus oldStatus =
                voucher.getStatus();

        AuditContext.setDetails(
                String.format(
                        "voucherId=%s, status: %s -> %s, username=%s",
                        voucherId,
                        oldStatus,
                        VoucherStatus.REGISTERED,
                        username
                )
        );

        processRefundIfNecessary(
                voucher
        );

        voucher.setStatus(
                VoucherStatus.REGISTERED
        );

        voucherRepository.save(
                voucher
        );
    }

    // ========================================================================
    // USER VOUCHERS PAGE
    // ========================================================================

    @Override
    public Page<VoucherDTO> findAllByUserIdPaged(
            String userId,
            int page,
            int size
    ) {

        UUID id =
                parseUuidOrThrow(
                        userId,
                        "err.user.notFound"
                );

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                Sort.Direction.DESC,
                                "arrivalDate"
                        )
                );

        return voucherRepository
                .findAllByUserIdAndArrivalDateGreaterThanEqual(
                        id,
                        LocalDate.now(),
                        pageable
                )
                .map(
                        voucherMapper::toVoucherDTO
                );
    }

    // ========================================================================
    // AVAILABLE VOUCHERS
    // ========================================================================

    @Override
    public Page<VoucherDTO> findAvailableVouchers(
            VoucherSearchRequestDTO request,
            int page,
            int size,
            String sortField,
            String sortDir
    ) {

        Sort.Direction direction =
                "asc".equalsIgnoreCase(sortDir)
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC;

        String field =
                sortField != null
                        && !sortField.isBlank()
                        ? sortField
                        : "isHot";

        Sort sorting =
                Sort.by(
                        Sort.Direction.DESC,
                        "isHot"
                ).and(
                        Sort.by(
                                direction,
                                field
                        )
                );

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        sorting
                );

        return voucherRepository
                .findAll(
                        VoucherSpecification.searchVouchers(
                                request,
                                true
                        ),
                        pageable
                )
                .map(
                        voucherMapper::toVoucherDTO
                );
    }

    // ========================================================================
    // ALL VOUCHERS PAGE
    // ========================================================================

    @Override
    public Page<VoucherDTO> findAllVouchersPaged(
            VoucherSearchRequestDTO request,
            int page,
            int size,
            String sortField,
            String sortDir
    ) {

        Sort.Direction direction =
                "asc".equalsIgnoreCase(sortDir)
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC;

        String field =
                sortField != null
                        && !sortField.isBlank()
                        ? sortField
                        : "isHot";

        Sort sorting =
                Sort.by(
                        Sort.Direction.DESC,
                        "isHot"
                ).and(
                        Sort.by(
                                direction,
                                field
                        )
                );

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        sorting
                );

        return voucherRepository
                .findAll(
                        VoucherSpecification.searchVouchers(
                                request,
                                false
                        ),
                        pageable
                )
                .map(
                        voucherMapper::toVoucherDTO
                );
    }

    // ========================================================================
    // USER VOUCHERS
    // ========================================================================

    @Override
    public List<VoucherDTO> findAllByUserId(
            String userId
    ) {

        UUID id =
                parseUuidOrThrow(
                        userId,
                        "err.user.notFound"
                );

        return voucherRepository
                .findAllByUserId(id)
                .stream()
                .map(
                        voucherMapper::toVoucherDTO
                )
                .toList();
    }

    // ========================================================================
    // ALL
    // ========================================================================

    @Override
    public List<VoucherDTO> findAll() {

        return voucherRepository
                .findAllByOrderByIsHotDesc()
                .stream()
                .map(
                        voucherMapper::toVoucherDTO
                )
                .toList();
    }
}

