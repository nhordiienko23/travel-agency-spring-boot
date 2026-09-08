package com.epam.finaltask.log;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AppLogRepository extends JpaRepository<AppLog, UUID> {
}