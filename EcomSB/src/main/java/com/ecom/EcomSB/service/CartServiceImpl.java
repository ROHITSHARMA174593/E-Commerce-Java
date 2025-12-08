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
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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


    //todo : Get the User Cart (By Id and Email)
    @Override
    public CartDTO getCart(String emailId, Long cartId) {
        Cart cart = cartRepository.findCartByEmailAndCartId(emailId, cartId);
        if(cart == null)
            throw new ResourceNotFoundException("Cart", "CartId", cartId);

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        cart.getCartItems().forEach(c -> c.getProduct().setQuantity(c.getQuantity()));
        List<ProductDTO> products = cart.getCartItems().stream()
                .map(p -> modelMapper.map(p.getProduct(), ProductDTO.class))
                .toList();
        cartDTO.setProducts(products);
        return cartDTO;
    }

    // Update Quantity in Cart Section
    @Transactional
    @Override
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {
        String emailId = authUtil.loggedInEmail();
        Cart userCart = cartRepository.findCartByEmail(emailId);
        Long cartId = userCart.getCartId();

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product","productId",productId));

        if(product.getQuantity() == 0)
            throw new APIException(product.getProductName()+" is not available");
        if(product.getQuantity() < quantity)
            throw new APIException("Please, make the order of the "+ product.getProductName()+" is Lessthan or Equal to the "+product.getQuantity()+".");

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);
        if(cartItem == null){
            throw new APIException("Product "+product.getProductName()+" not available in cart.");
        }

        // Validation for : Quantity ko delete kare to negative value na aaye
        int newQuantity = cartItem.getQuantity()+quantity;
        if(newQuantity < 0){
            throw new APIException("The Resulting quantity cannot be Negative.");
        }
        if(newQuantity == 0){
            deleteProductFromCart(cartId, productId);
        }else{
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setQuantity(cartItem.getQuantity()+quantity);
            cartItem.setDiscount(product.getDiscount());
            cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getProductPrice() * quantity));

            cartRepository.save(cart);
        }


        CartItem updatedItem = cartItemRepository.save(cartItem);
        if(updatedItem.getQuantity() == 0){
            cartItemRepository.deleteById(updatedItem.getCartItemId());
        }
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<CartItem> cartItems = cart.getCartItems();
        Stream<ProductDTO> productDTOStream = cartItems.stream()
                .map(item -> {
                    ProductDTO prdto = modelMapper.map(item.getProduct(), ProductDTO.class);
                    prdto.setQuantity(item.getQuantity());
                    return prdto;
                });

        cartDTO.setProducts(productDTOStream.toList());

        return cartDTO;
    }

    @Transactional
    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);
        if(cartItem == null){
            throw new ResourceNotFoundException("Product","productId", productId);
        }

        // if something is deleted than we need to subtract the price also
        cart.setTotalPrice(cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity()));

        cartItemRepository.deleteCartItemByProductIdAndCartId(cartId, productId);

        return " !!! Product "+cartItem.getProduct().getProductName()+" REMOVED from Cart !!! ";
    }

    @Override
    public void updateProductInCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);
        if(cartItem == null){
            throw new APIException("!!! Products "+product.getProductName()+" NOT Available in the Cart !!! ");
        }
        double cartPrice = cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity());
        cartItem.setProductPrice(product.getSpecialPrice());
        cart.setTotalPrice(cartPrice + (cartItem.getProductPrice() * cartItem.getQuantity()));

        cartItem = cartItemRepository.save(cartItem);
    }
}








