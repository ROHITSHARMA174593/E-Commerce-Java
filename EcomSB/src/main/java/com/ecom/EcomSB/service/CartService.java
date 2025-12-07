package com.ecom.EcomSB.service;


import com.ecom.EcomSB.payload.CartDTO;

import java.util.List;


public interface CartService {


    CartDTO addProductToCart(Long productId, Integer quantity);

    List<CartDTO> getAllCarts();
}
