package com.ecom.EcomSB.repositories;

import com.ecom.EcomSB.model.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByCategoryName(@NotBlank @Size(min = 4, message = "Category Name must contain atleast 4 or 4+ words ... !!! ") String categoryName);
    // now we can use this repository in any file if you go inside this repository ctrl+click on JpaRepository where you can see all the methods of interact with db

    // This file provide us ALL the SQL queries like : save, remove, removeById, getAll, getALlById etc...
}
