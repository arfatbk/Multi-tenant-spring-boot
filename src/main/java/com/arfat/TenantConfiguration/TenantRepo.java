package com.arfat.TenantConfiguration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenantRepo extends JpaRepository<Tenant, Long> {
    Optional<Tenant> findByName(String tenant);
}
