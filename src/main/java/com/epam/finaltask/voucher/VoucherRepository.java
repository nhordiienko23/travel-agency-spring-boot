package com.epam.finaltask.voucher;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, UUID>, JpaSpecificationExecutor<Voucher> {
    List<Voucher> findAllByUserId(UUID userId);
    Page<Voucher> findAllByUserIdAndArrivalDateGreaterThanEqual(UUID userId, LocalDate date, Pageable pageable);
    List<Voucher> findAllByOrderByIsHotDesc();
}