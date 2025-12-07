package com.ecom.EcomSB.service;

import com.ecom.EcomSB.exception.APIException;
import com.ecom.EcomSB.exception.ResourceNotFoundException;
import com.ecom.EcomSB.model.Cart;
import com.ecom.EcomSB.model.CartItem;
import com.ecom.EcomSB.model.Product;
import com.ecom.EcomSB.payload.CartDTO;
import com.ecom.EcomSB.payload.ProductDTO;
import com.ecom.EcomSB.repositories.CartItemRepository;
import com.ecom.EcomSB.repositories.CartRepository;
import com.ecom.EcomSB.repositories.ProductRepository;
import com.ecom.EcomSB.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class CartServiceImpl implements CartService {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AuthUtil authUtil;

    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        // Find Existing cart or create new one
        Cart cart = createCart();

        // Retrieve Products Details
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product ", "productId", productId));

        // Perform Validation
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), productId);


        if(cartItem != null)
            throw new APIException("Product "+product.getProductName()+" Already exist in the cart");
        if(product.getQuantity() == 0)
            throw new APIException(product.getProductName()+" is not available");
        if(product.getQuantity() < quantity)
            throw new APIException("Please, make the order of the "+ product.getProductName()+" is Lessthan or Equal to the "+product.getQuantity()+".");

        // Create Cart Item
        CartItem newCartItem = new CartItem();
        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());

    // ADD TO CART LIST (IMPORTANT)
        cart.getCartItems().add(newCartItem);

    // Save in DB
        cartItemRepository.save(newCartItem);


        product.setQuantity(product.getQuantity());

        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));

        cartRepository.save(cart);

        // Return Updated Cart
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<CartItem> cartItems = cart.getCartItems();
        Stream<ProductDTO> productDTOStream = cartItems.stream().map(item -> {
            ProductDTO map = modelMapper.map(item.getProduct(), ProductDTO.class);
            map.setQuantity(item.getQuantity());
            return map;
        });
        cartDTO.setProducts(productDTOStream.toList());
        return cartDTO;
    }



    private Cart createCart(){
        Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if(userCart != null){
            return userCart;
        }
        Cart cart = new Cart();
        cart.setTotalPrice(0.00);
        cart.setUser(authUtil.loggedInUser());
        try {
            Cart newCart =  cartRepository.save(cart);
            return newCart;
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            return cartRepository.findCartByEmail(authUtil.loggedInEmail());
        }
    }


    //todo : Get All Carts
    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();
        if(carts.size() == 0){
            throw new APIException("No Carts Exist");
        }
        // If the validation is not runs than we need to send the data on frontend
        // and convert the List<Cart> into List<CartDTO>
        List<CartDTO> cartDTOS = carts.stream()
                .map(cart -> {
                    CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
                    List<ProductDTO> products = cart.getCartItems().stream()
                            .map(p -> modelMapper.map(p.getProduct(), ProductDTO.class))
                            .toList();
                    cartDTO.setProducts(products);
                    return cartDTO;
                }).toList();


        return cartDTOS;
    }
}








