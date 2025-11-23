package com.ecom.EcomSB.repositories;

import com.ecom.EcomSB.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String username);

    Boolean existsByUserName(@NotBlank @Size(min = 3, max = 18, message = "UserName required minimum 3 character and maximum 18.") String username);

    Boolean existsByEmail(@NotBlank @Size(max = 50) String email);
}
