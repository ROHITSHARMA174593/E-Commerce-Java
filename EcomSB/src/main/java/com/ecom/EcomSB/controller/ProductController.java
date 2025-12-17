package com.ecom.EcomSB.controller;


import com.ecom.EcomSB.config.AppConstants;

import com.ecom.EcomSB.payload.ProductDTO;
import com.ecom.EcomSB.payload.ProductResponse;
import com.ecom.EcomSB.service.ProductService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    ProductService productService;

//todo : --------------------------------- POST METHOD START ------------------------------------------------------------------------------------------------------------------------------------------
    @Tag(name = "Product APIs", description = "APIs for Managing Product")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@Valid @RequestBody ProductDTO productDTO, @PathVariable Long categoryId){
        ProductDTO savedProductDTO = productService.addProduct(categoryId, productDTO);
        return new ResponseEntity<>(savedProductDTO, HttpStatus.CREATED);
    }
//todo : ---------------------------------- POST METHOD END-----------------------------------------------------------------------------------------------------------------------------------------

//todo : -------------------------------- GET METHOD START -------------------------------------------------------------------------------------------------------------------------------------------

    // todo : Fetch All the Products
    @Tag(name = "Product APIs", description = "APIs for Managing Product")
    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR) String sortOrder
    ){
        ProductResponse productResponse = productService.getAllProducts(pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    //todo : Fetch Product By Category
    @Tag(name = "Product APIs", description = "APIs for Managing Product")
    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR) String sortOrder
    ){
        ProductResponse productResponse = productService.searchByCategory(categoryId, pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    //todo : Fetch Product By Keyword
    @Tag(name = "Product APIs", description = "APIs for Managing Product")
    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductByKeyword(
            @PathVariable String keyword,
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR) String sortOrder
    ){ // keyword === productName
        ProductResponse productResponse = productService.searchProductByKeyword(keyword, pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(productResponse, HttpStatus.FOUND);
    }
//todo : ----------------------------------- GET METHOD END ----------------------------------------------------------------------------------------------------------------------------------------


//todo : -------------------------------- PUT METHOD START -------------------------------------------------------------------------------------------------------------------------------------------
    //todo ::: Update the Product (Accept a id in URL)
    @Tag(name = "Product APIs", description = "APIs for Managing Product")
    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@Valid @RequestBody ProductDTO productDTO, @PathVariable Long productId){
        ProductDTO updatedProductDTO = productService.updateProduct(productId, productDTO);
        return new ResponseEntity<>(updatedProductDTO, HttpStatus.OK);
    }
//todo : --------------------------------- PUT METHOD END ------------------------------------------------------------------------------------------------------------------------------------------

//todo : ------------------------------- DELETE METHOD START --------------------------------------------------------------------------------------------------------------------------------------------

    @Tag(name = "Product APIs", description = "APIs for Managing Product")
    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId){
        ProductDTO deletedProduct = productService. deletedProduct(productId);
        return new ResponseEntity<>(deletedProduct, HttpStatus.OK);
    }
//todo : ------------------------------ DELETE METHOD END ---------------------------------------------------------------------------------------------------------------------------------------------


//todo : ------------------------------- PUT METHOD for Image (mainfolder->imagedir) --------------------------------------------------------------------------------------------------------------------------------------------

    @Tag(name = "Product APIs", description = "APIs for Managing Product")
    @PutMapping("/products/{productId}/image")
    public ResponseEntity<ProductDTO> updateProductImage(@PathVariable Long productId, @RequestParam("image")MultipartFile image) throws IOException { // Image accept karne ke liye MultipartFile Annotation kaam me aata hai
        ProductDTO updatedProduct = productService.updateProductImage(productId, image);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }


}
