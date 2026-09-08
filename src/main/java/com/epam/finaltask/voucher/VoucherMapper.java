package com.epam.finaltask.voucher;

public interface VoucherMapper {
    Voucher toVoucher(CreateVoucherRequestDTO request);
    VoucherDTO toVoucherDTO(Voucher voucher);
}