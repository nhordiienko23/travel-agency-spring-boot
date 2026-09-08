package com.epam.finaltask.voucher;

import org.springframework.data.domain.Page;
import java.util.List;

public interface VoucherService {
    VoucherDTO findById(String id);
    VoucherDTO create(CreateVoucherRequestDTO request);
    VoucherDTO order(String id, String userId);
    VoucherDTO update(String id, UpdateVoucherRequestDTO request);
    void delete(String voucherId);
    VoucherDTO changeHotStatus(String id, VoucherDTO voucherDTO);
    VoucherDTO changeStatus(String id, ChangeVoucherStatusRequestDTO request);
    void cancelOrder(String voucherId, String username);

    Page<VoucherDTO> findAllByUserIdPaged(String userId, int page, int size);
    List<VoucherDTO> findAllByUserId(String userId);

    Page<VoucherDTO> findAvailableVouchers(VoucherSearchRequestDTO searchRequest, int page, int size, String sortField, String sortDir);
    Page<VoucherDTO> findAllVouchersPaged(VoucherSearchRequestDTO searchRequest, int page, int size, String sortField, String sortDir);

    List<VoucherDTO> findAll();
}