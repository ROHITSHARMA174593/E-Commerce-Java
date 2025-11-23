package com.ecom.EcomSB.repositories;

import com.ecom.EcomSB.model.AppRole;
import com.ecom.EcomSB.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(AppRole appRole);
}
