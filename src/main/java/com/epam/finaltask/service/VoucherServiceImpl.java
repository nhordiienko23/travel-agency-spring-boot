package com.epam.finaltask.service;

import java.util.List;
import java.util.UUID;

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

        existingVoucher.setTitle(voucherDTO.getTitle());
        existingVoucher.setDescription(voucherDTO.getDescription());
        existingVoucher.setPrice(voucherDTO.getPrice());

        if (voucherDTO.getTourType() != null) {
            existingVoucher.setTourType(TourType.valueOf(voucherDTO.getTourType().toUpperCase()));
        }
        if (voucherDTO.getTransferType() != null) {
            existingVoucher.setTransferType(TransferType.valueOf(voucherDTO.getTransferType().toUpperCase()));
        }
        if (voucherDTO.getHotelType() != null) {
            existingVoucher.setHotelType(HotelType.valueOf(voucherDTO.getHotelType().toUpperCase()));
        }
        if (voucherDTO.getStatus() != null) {
            existingVoucher.setStatus(VoucherStatus.valueOf(voucherDTO.getStatus().toUpperCase()));
        }

        existingVoucher.setArrivalDate(voucherDTO.getArrivalDate());
        existingVoucher.setEvictionDate(voucherDTO.getEvictionDate());

        Voucher saved = voucherRepository.save(existingErrorHandling(existingVoucher));
        return voucherMapper.toVoucherDTO(saved);
    }

    private Voucher existingErrorHandling(Voucher voucher) {
        return voucher;
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
        return voucherRepository.findAllByOrderByIsHotDesc().stream()
                .map(voucherMapper::toVoucherDTO)
                .toList();
    }
}