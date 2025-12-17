package com.ecom.EcomSB.controller;


import com.ecom.EcomSB.config.AppConstants;

import com.ecom.EcomSB.payload.CategoryDTO;
import com.ecom.EcomSB.payload.CategoryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ecom.EcomSB.service.CategoryService;


@RestController
@RequestMapping("/api") // now we can remove the /api from every Request
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

//todo :: This is for Learning Purpose (How Query Parameters Work (@RequestParam)
//    @GetMapping("/echo")
////    public ResponseEntity<String> echoMessage(@RequestParam(name = "message", defaultValue = "!!! This is the Default Valud of QueryParameter.!!!") String message){
//    public ResponseEntity<String> echoMessage(@RequestParam(name = "message", required = false) String message){ // default value of "require" = true // so now if you not pass any value in query parameter than you will get the null value
//        return new ResponseEntity<>("Echoed Message : "+message, HttpStatus.OK);
//    }

    @Tag(name = "Category APIs", description = "APIs for Managing Category")
    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse> getAllCategories(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_CATEGORIES_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder
    ) {
        CategoryResponse categoryResponse = categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(categoryResponse, HttpStatus.OK);
    }

    @Tag(name = "Category APIs", description = "APIs for Managing Category")
    @Operation(summary = "Create Category", description = "API to create a new Category")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Category is Created Successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    @PostMapping("/public/categories")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) { // @Valid is use because if you pass an empty string in request than you will get an internal server error(500) but now you will get the 400 (that is client side error)
//        System.out.println("Received categoryName = " + category.getCategoryName());
        CategoryDTO savedCategoryDTO = categoryService.createCategory(categoryDTO);
        return new ResponseEntity<>(savedCategoryDTO, HttpStatus.CREATED);
    }

    @Tag(name = "Category APIs", description = "APIs for Managing Category")
    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(@Parameter(description = "Category that you wish to create") @PathVariable Long categoryId) {
        CategoryDTO deletedCategory = categoryService.deleteCategory(categoryId);
         return new ResponseEntity<>(deletedCategory, HttpStatus.OK);
        // return ResponseEntity.ok(status);
//        return ResponseEntity.status(HttpStatus.OK).body(status);

        //todo : Here in Delete API no need to handle Exceptions using TryCatch Blocks Because we use here
    }

    @Tag(name = "Category APIs", description = "APIs for Managing Category")
    @PutMapping("/public/categories/{categoryId}")
    // todo : We can also write this
    //      @RequestMapping(value = "/api/public/categories/{categoryId}", method = RequestMethod.PUT)
    public ResponseEntity<CategoryDTO> updateCategory(@Valid @RequestBody CategoryDTO categoryDTO,
                                                      @PathVariable Long categoryId) {
        CategoryDTO savedCategoryDTO = categoryService.updateCategory(categoryDTO, categoryId);
        return new ResponseEntity<>(savedCategoryDTO, HttpStatus.OK);
    }
}


/*
 Annnotation they are use for Swagger Documentation http://localhost:5000/swagger-ui/index.html
 @Tag, @Parameter, @ApiResponses, @Operation
 and we will add in every controller class these : @Tag, @Operation
* */