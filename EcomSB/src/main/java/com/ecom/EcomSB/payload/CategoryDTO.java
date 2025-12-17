package com.ecom.EcomSB.payload;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    @Schema(description = "Category ID")
    private Long categoryId;
    @Schema(description = "Category name for category you wish to create")
    private String categoryName;



}
