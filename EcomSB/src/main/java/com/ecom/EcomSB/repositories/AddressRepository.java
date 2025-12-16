package com.ecom.EcomSB.repositories;

import com.ecom.EcomSB.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
