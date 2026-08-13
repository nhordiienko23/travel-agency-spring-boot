package com.epam.finaltask.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.exception.ResourceNotFoundException;
import com.epam.finaltask.mapper.VoucherMapper;
import com.epam.finaltask.model.*;
import com.epam.finaltask.repository.UserRepository;
import com.epam.finaltask.repository.VoucherRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository;
    private final UserRepository userRepository;
    private final VoucherMapper voucherMapper;

    @Override
    @Transactional
    public VoucherDTO create(VoucherDTO voucherDTO) {
        Voucher voucher = voucherMapper.toVoucher(voucherDTO);
        if (voucher.getStatus() == null) {
            voucher.setStatus(VoucherStatus.REGISTERED);
        }
        Voucher savedVoucher = voucherRepository.save(voucher);
        return voucherMapper.toVoucherDTO(savedVoucher);
    }

    @Override
    @Transactional
    public VoucherDTO order(String id, String userId) {
        Voucher voucher = voucherRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found with id: " + id));

        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (user.getBalance() < voucher.getPrice()) {
            throw new IllegalArgumentException("Insufficient balance to order this voucher");
        }

        user.setBalance(user.getBalance() - voucher.getPrice());
        userRepository.save(user);

        voucher.setUser(user);
        voucher.setStatus(VoucherStatus.REGISTERED);

        Voucher updatedVoucher = voucherRepository.save(voucher);
        return voucherMapper.toVoucherDTO(updatedVoucher);
    }

    @Override
    @Transactional
    public VoucherDTO update(String id, VoucherDTO voucherDTO) {
        Voucher existingVoucher = voucherRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found with id: " + id));

        if (voucherDTO.getTitle() != null && !voucherDTO.getTitle().isEmpty()) {
            existingVoucher.setTitle(voucherDTO.getTitle());
        }
        if (voucherDTO.getDescription() != null && !voucherDTO.getDescription().isEmpty()) {
            existingVoucher.setDescription(voucherDTO.getDescription());
        }
        if (voucherDTO.getPrice() != null) {
            existingVoucher.setPrice(voucherDTO.getPrice());
        }

        // Если в DTO прилетает строка, парсим её напрямую через valueOf
        if (voucherDTO.getTourType() != null) {
            existingVoucher.setTourType(TourType.valueOf(voucherDTO.getTourType().toString().toUpperCase()));
        }
        if (voucherDTO.getTransferType() != null) {
            existingVoucher.setTransferType(TransferType.valueOf(voucherDTO.getTransferType().toString().toUpperCase()));
        }
        if (voucherDTO.getHotelType() != null) {
            existingVoucher.setHotelType(HotelType.valueOf(voucherDTO.getHotelType().toString().toUpperCase()));
        }
        if (voucherDTO.getStatus() != null) {
            existingVoucher.setStatus(VoucherStatus.valueOf(voucherDTO.getStatus().toString().toUpperCase()));
        }

        if (voucherDTO.getArrivalDate() != null) {
            existingVoucher.setArrivalDate(voucherDTO.getArrivalDate());
        }
        if (voucherDTO.getEvictionDate() != null) {
            existingVoucher.setEvictionDate(voucherDTO.getEvictionDate());
        }

        Voucher saved = voucherRepository.save(existingVoucher);
        return voucherMapper.toVoucherDTO(saved);
    }

    @Override
    @Transactional
    public void delete(String voucherId) {
        UUID id = UUID.fromString(voucherId);
        if (!voucherRepository.existsById(id)) {
            throw new ResourceNotFoundException("Voucher not found with id: " + voucherId);
        }
        voucherRepository.deleteById(id);
    }

    @Override
    @Transactional
    public VoucherDTO changeHotStatus(String id, VoucherDTO voucherDTO) {
        Voucher voucher = voucherRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found with id: " + id));

        voucher.setHot(Boolean.TRUE.equals(voucherDTO.getIsHot()));
        Voucher saved = voucherRepository.save(voucher);
        return voucherMapper.toVoucherDTO(saved);
    }

    @Override
    public List<VoucherDTO> findAllByUserId(String userId) {
        return voucherRepository.findAllByUserId(UUID.fromString(userId)).stream()
                .map(voucherMapper::toVoucherDTO)
                .toList();
    }

    @Override
    public List<VoucherDTO> findAllByTourType(TourType tourType) {
        return voucherRepository.findAllByTourType(tourType).stream()
                .map(voucherMapper::toVoucherDTO)
                .toList();
    }

    @Override
    public List<VoucherDTO> findAllByTransferType(String transferType) {
        TransferType type = TransferType.valueOf(transferType.toUpperCase());
        return voucherRepository.findAllByTransferType(type).stream()
                .map(voucherMapper::toVoucherDTO)
                .toList();
    }

    @Override
    public List<VoucherDTO> findAllByPrice(Double price) {
        return voucherRepository.findAllByPriceLessThanEqual(price).stream()
                .map(voucherMapper::toVoucherDTO)
                .toList();
    }

    @Override
    public List<VoucherDTO> findAllByHotelType(HotelType hotelType) {
        return voucherRepository.findAllByHotelType(hotelType).stream()
                .map(voucherMapper::toVoucherDTO)
                .toList();
    }

    @Override
    public List<VoucherDTO> findAll() {
        // Сортируем: сначала горящие (isHot = true), затем обычные
        return voucherRepository.findAllByOrderByIsHotDesc().stream()
                .map(voucherMapper::toVoucherDTO)
                .toList();
    }

    @Override
    public Page<VoucherDTO> findAvailableVouchers(String keyword, String tourType, String hotelType, Double maxPrice, Boolean isHot, int page, int size, String sortField, String sortDir) {

        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;

        String field = (sortField != null && !sortField.isEmpty()) ? sortField : "isHot";


        Sort sorting = Sort.by(Sort.Direction.DESC, "isHot").and(Sort.by(direction, field));

        Pageable pageable = PageRequest.of(page, size, sorting);

        TourType tType = (tourType != null && !tourType.isEmpty()) ? TourType.valueOf(tourType) : null;
        HotelType hType = (hotelType != null && !hotelType.isEmpty()) ? HotelType.valueOf(hotelType) : null;

        return voucherRepository.findAvailableVouchers(keyword, tType, hType, maxPrice, isHot, pageable)
                .map(voucherMapper::toVoucherDTO);
    }
    @Override
    public Page<VoucherDTO> findAllVouchersForManager(String keyword, String status, int page, int size, String sortField, String sortDir) {
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String field = (sortField != null && !sortField.isEmpty()) ? sortField : "isHot";

        Sort sorting = Sort.by(Sort.Direction.DESC, "isHot").and(Sort.by(direction, field));
        Pageable pageable = PageRequest.of(page, size, sorting);

        VoucherStatus vStatus = (status != null && !status.isEmpty()) ? VoucherStatus.valueOf(status.toUpperCase()) : null;

        return voucherRepository.findAllVouchersForManager(keyword, vStatus, pageable)
                .map(voucherMapper::toVoucherDTO);
    }
}