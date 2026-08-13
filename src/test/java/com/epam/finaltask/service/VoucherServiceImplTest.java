package com.epam.finaltask.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.exception.ResourceNotFoundException;
import com.epam.finaltask.mapper.VoucherMapper;
import com.epam.finaltask.model.HotelType;
import com.epam.finaltask.model.TourType;
import com.epam.finaltask.model.TransferType;
import com.epam.finaltask.model.User;
import com.epam.finaltask.model.Voucher;
import com.epam.finaltask.model.VoucherStatus;
import com.epam.finaltask.repository.UserRepository;
import com.epam.finaltask.repository.VoucherRepository;

@ExtendWith(MockitoExtension.class)
public class VoucherServiceImplTest {

    @Mock
    private VoucherRepository voucherRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VoucherMapper voucherMapper;

    @InjectMocks
    private VoucherServiceImpl voucherService;

    // --- EPAM TESTS (DO NOT TOUCH) ---

    @Test
    void create_ValidVoucher_ReturnsCreatedVoucherDTO() {
        // Given
        VoucherDTO requestDto = new VoucherDTO();
        Voucher voucher = new Voucher();
        Voucher savedVoucher = new Voucher();
        savedVoucher.setStatus(VoucherStatus.REGISTERED);

        VoucherDTO responseDto = new VoucherDTO();
        responseDto.setStatus("REGISTERED");

        when(voucherMapper.toVoucher(requestDto)).thenReturn(voucher);
        when(voucherRepository.save(voucher)).thenReturn(savedVoucher);
        when(voucherMapper.toVoucherDTO(savedVoucher)).thenReturn(responseDto);

        // When
        VoucherDTO result = voucherService.create(requestDto);

        // Then
        assertNotNull(result);
        assertEquals("REGISTERED", result.getStatus());
        verify(voucherRepository, times(1)).save(voucher);
    }

    @Test
    void delete_ExistingVoucher_DeletesSuccessfully() {
        // Given
        String id = UUID.randomUUID().toString();
        when(voucherRepository.existsById(UUID.fromString(id))).thenReturn(true);

        // When
        assertDoesNotThrow(() -> voucherService.delete(id));

        // Then
        verify(voucherRepository, times(1)).deleteById(UUID.fromString(id));
    }

    @Test
    void delete_NonExistingVoucher_ThrowsException() {
        // Given
        String id = UUID.randomUUID().toString();
        when(voucherRepository.existsById(UUID.fromString(id))).thenReturn(false);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> voucherService.delete(id));
        verify(voucherRepository, never()).deleteById(any());
    }

    @Test
    void order_ValidDataAndSufficientBalance_Success() {
        // Given
        String voucherId = UUID.randomUUID().toString();
        String userId = UUID.randomUUID().toString();

        Voucher voucher = new Voucher();
        voucher.setPrice(100.0);

        User user = new User();
        user.setBalance(150.0);

        when(voucherRepository.findById(UUID.fromString(voucherId))).thenReturn(Optional.of(voucher));
        when(userRepository.findById(UUID.fromString(userId))).thenReturn(Optional.of(user));
        when(voucherRepository.save(any(Voucher.class))).thenReturn(voucher);
        when(voucherMapper.toVoucherDTO(any(Voucher.class))).thenReturn(new VoucherDTO());

        // When
        VoucherDTO result = voucherService.order(voucherId, userId);

        // Then
        assertNotNull(result);
        assertEquals(50.0, user.getBalance()); // Проверяем списание средств
        assertEquals(VoucherStatus.REGISTERED, voucher.getStatus()); // Проверяем статус
        verify(userRepository, times(1)).save(user);
        verify(voucherRepository, times(1)).save(voucher);
    }

    @Test
    void order_InsufficientBalance_ThrowsException() {
        // Given
        String voucherId = UUID.randomUUID().toString();
        String userId = UUID.randomUUID().toString();

        Voucher voucher = new Voucher();
        voucher.setPrice(200.0); // Цена выше баланса

        User user = new User();
        user.setBalance(150.0);

        when(voucherRepository.findById(UUID.fromString(voucherId))).thenReturn(Optional.of(voucher));
        when(userRepository.findById(UUID.fromString(userId))).thenReturn(Optional.of(user));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> voucherService.order(voucherId, userId));

        assertEquals("Insufficient balance to order this voucher", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void changeHotStatus_ExistingVoucher_Success() {
        // Given
        String voucherId = UUID.randomUUID().toString();
        VoucherDTO requestDto = new VoucherDTO();
        requestDto.setIsHot(true);

        Voucher voucher = new Voucher();
        voucher.setHot(false);

        Voucher savedVoucher = new Voucher();
        savedVoucher.setHot(true);

        when(voucherRepository.findById(UUID.fromString(voucherId))).thenReturn(Optional.of(voucher));
        when(voucherRepository.save(voucher)).thenReturn(savedVoucher);
        when(voucherMapper.toVoucherDTO(savedVoucher)).thenReturn(new VoucherDTO());

        // When
        VoucherDTO result = voucherService.changeHotStatus(voucherId, requestDto);

        // Then
        assertNotNull(result);
        verify(voucherRepository, times(1)).save(voucher);
    }

    @Test
    void findAll_ReturnsVoucherList() {
        // Given
        Voucher voucher = new Voucher();
        VoucherDTO dto = new VoucherDTO();

        when(voucherRepository.findAllByOrderByIsHotDesc()).thenReturn(List.of(voucher));
        when(voucherMapper.toVoucherDTO(voucher)).thenReturn(dto);

        // When
        List<VoucherDTO> result = voucherService.findAll();

        // Then
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(voucherRepository, times(1)).findAllByOrderByIsHotDesc();
    }

    // --- NEW TESTS TO COVER REMAINING LOGIC ---

    @Test
    void update_ExistingVoucher_Success() {
        String voucherId = UUID.randomUUID().toString();
        Voucher existingVoucher = new Voucher();
        existingVoucher.setTitle("Old Title");

        VoucherDTO updateDto = new VoucherDTO();
        updateDto.setTitle("New Title");
        updateDto.setPrice(1500.0);

        when(voucherRepository.findById(UUID.fromString(voucherId))).thenReturn(Optional.of(existingVoucher));
        when(voucherRepository.save(any(Voucher.class))).thenReturn(existingVoucher);
        when(voucherMapper.toVoucherDTO(any(Voucher.class))).thenReturn(new VoucherDTO());

        VoucherDTO result = voucherService.update(voucherId, updateDto);

        assertNotNull(result);
        assertEquals("New Title", existingVoucher.getTitle());
        assertEquals(1500.0, existingVoucher.getPrice());
        verify(voucherRepository, times(1)).save(existingVoucher);
    }

    @Test
    void findAllByUserId_ReturnsList() {
        String userId = UUID.randomUUID().toString();
        Voucher voucher = new Voucher();
        when(voucherRepository.findAllByUserId(UUID.fromString(userId))).thenReturn(List.of(voucher));
        when(voucherMapper.toVoucherDTO(voucher)).thenReturn(new VoucherDTO());

        List<VoucherDTO> result = voucherService.findAllByUserId(userId);

        assertEquals(1, result.size());
        verify(voucherRepository, times(1)).findAllByUserId(UUID.fromString(userId));
    }

    @Test
    void findAllByTourType_ReturnsList() {
        Voucher voucher = new Voucher();
        when(voucherRepository.findAllByTourType(TourType.LEISURE)).thenReturn(List.of(voucher));
        when(voucherMapper.toVoucherDTO(voucher)).thenReturn(new VoucherDTO());

        List<VoucherDTO> result = voucherService.findAllByTourType(TourType.LEISURE);

        assertEquals(1, result.size());
        verify(voucherRepository, times(1)).findAllByTourType(TourType.LEISURE);
    }

    @Test
    void findAllByTransferType_ReturnsList() {
        Voucher voucher = new Voucher();
        when(voucherRepository.findAllByTransferType(TransferType.PLANE)).thenReturn(List.of(voucher));
        when(voucherMapper.toVoucherDTO(voucher)).thenReturn(new VoucherDTO());

        List<VoucherDTO> result = voucherService.findAllByTransferType("PLANE");

        assertEquals(1, result.size());
        verify(voucherRepository, times(1)).findAllByTransferType(TransferType.PLANE);
    }

    @Test
    void findAllByPrice_ReturnsList() {
        Voucher voucher = new Voucher();
        when(voucherRepository.findAllByPriceLessThanEqual(500.0)).thenReturn(List.of(voucher));
        when(voucherMapper.toVoucherDTO(voucher)).thenReturn(new VoucherDTO());

        List<VoucherDTO> result = voucherService.findAllByPrice(500.0);

        assertEquals(1, result.size());
        verify(voucherRepository, times(1)).findAllByPriceLessThanEqual(500.0);
    }

    @Test
    void findAllByHotelType_ReturnsList() {
        Voucher voucher = new Voucher();
        when(voucherRepository.findAllByHotelType(HotelType.FIVE_STARS)).thenReturn(List.of(voucher));
        when(voucherMapper.toVoucherDTO(voucher)).thenReturn(new VoucherDTO());

        List<VoucherDTO> result = voucherService.findAllByHotelType(HotelType.FIVE_STARS);

        assertEquals(1, result.size());
        verify(voucherRepository, times(1)).findAllByHotelType(HotelType.FIVE_STARS);
    }

    @Test
    void findAvailableVouchers_ReturnsPage() {
        Page<Voucher> voucherPage = new PageImpl<>(List.of(new Voucher()));
        when(voucherRepository.findAvailableVouchers(
                any(), any(), any(), any(), any(), any(Pageable.class)
        )).thenReturn(voucherPage);

        Page<VoucherDTO> result = voucherService.findAvailableVouchers(
                "keyword", "LEISURE", "FIVE_STARS", 1000.0, true, 0, 5, "price", "asc"
        );

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(voucherRepository, times(1)).findAvailableVouchers(
                any(), any(), any(), any(), any(), any(Pageable.class)
        );
    }

    @Test
    void findAllVouchersForManager_ReturnsPage() {
        Page<Voucher> voucherPage = new PageImpl<>(List.of(new Voucher()));
        when(voucherRepository.findAllVouchersForManager(
                any(), any(), any(Pageable.class)
        )).thenReturn(voucherPage);

        Page<VoucherDTO> result = voucherService.findAllVouchersForManager(
                "keyword", "REGISTERED", 0, 5, "title", "desc"
        );

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(voucherRepository, times(1)).findAllVouchersForManager(
                any(), any(), any(Pageable.class)
        );
    }
}