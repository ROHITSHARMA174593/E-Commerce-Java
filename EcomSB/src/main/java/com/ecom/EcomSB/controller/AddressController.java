package com.ecom.EcomSB.controller;


import com.ecom.EcomSB.model.User;
import com.ecom.EcomSB.payload.AddressDTO;
import com.ecom.EcomSB.service.AddressService;
import com.ecom.EcomSB.util.AuthUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    AddressService addressService;

    @Tag(name = "Address APIs", description = "APIs for Managing Address")
    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> createAddress(@Valid @RequestBody AddressDTO addressDTO){
        User user = authUtil.loggedInUser();
        AddressDTO savedAddressDTO = addressService.createAddress(addressDTO, user);
        return new ResponseEntity<>(savedAddressDTO, HttpStatus.CREATED);
    }

    @Tag(name = "Address APIs", description = "APIs for Managing Address")
    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDTO>> getAddress(){
        List<AddressDTO> addressList = addressService.getAddresses();
        return new ResponseEntity<>(addressList, HttpStatus.OK);
    }

    @Tag(name = "Address APIs", description = "APIs for Managing Address")
    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable Long addressId){
        AddressDTO address = addressService.getAddressById(addressId);
        return new ResponseEntity<>(address, HttpStatus.OK);
    }

    @Tag(name = "Address APIs", description = "APIs for Managing Address")
    @GetMapping("/user/addresses")
    public ResponseEntity<List<AddressDTO>> getUserAddress(){
        User user = authUtil.loggedInUser();
        List<AddressDTO> addressList = addressService.getUserAddresses(user);
        return new ResponseEntity<>(addressList, HttpStatus.OK);
    }

    //todo : Update the Address
    @Tag(name = "Address APIs", description = "APIs for Managing Address")
    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> updateAddressById(@PathVariable Long addressId, @RequestBody AddressDTO addressDTO){
        AddressDTO updatedAddress = addressService.updateAddress(addressId, addressDTO);
        return new ResponseEntity<>(updatedAddress, HttpStatus.OK);
    }

    //todo : Delete Address by ID
    @Tag(name = "Address APIs", description = "APIs for Managing Address")
    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<String> deleteAddressById(@PathVariable Long addressId){
        String status = addressService.deleteAddressById(addressId);
        return new ResponseEntity<String>(status, HttpStatus.OK);
    }
}
