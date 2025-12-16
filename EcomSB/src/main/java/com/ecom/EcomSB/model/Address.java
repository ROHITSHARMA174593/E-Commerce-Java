package com.ecom.EcomSB.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @NotBlank
    @Size(min = 5, message = "Street name must be atleast 5 character")
    private String street;

    @NotBlank
    @Size(min = 5, message = "BuildingName must be atleast 5 character")
    private String buildingName;


    @NotBlank
    @Size(min = 3, message = "City name must be atleast 3 character")
    private String city;

    @NotBlank
    @Size(min = 2, message = "State name must be atleast 2 character")
    private String state;

    @NotBlank
    @Size(min = 3, message = "Country name must be atleast 3 character")
    private String country;

    @NotBlank
    @Size(min = 6, message = "PinCode must be atleast 6 character")
    private String pincode;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Address(String street, String buildingName, String city, String state, String country, String pincode) {
        this.street = street;
        this.buildingName = buildingName;
        this.city = city;
        this.state = state;
        this.country = country;
        this.pincode = pincode;
    }
}
