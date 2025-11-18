package com.ecom.EcomSB.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity(name = "categories")  // this Annotation is make the table in our DB (H2DB) this is inMemory DB jha per hum sabhi data ko ek saath dekh sakte hai ye ek MySQL Workbench jaisa surface de deta hai hume runtime per hi go to this path ::: http://localhost:8080/h2-console
@Data // when you define this one than no need to define constructors
@NoArgsConstructor // this is also part of @Data
@AllArgsConstructor // this is also part of @Data
public class Category {
    @Id // now categoryId is mark as primary key in DB
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // categoryId ka jo type hai ab vo Unique ho gaya hai DB me hume kisi bhi id ya kisi bhi column ko unique bnane ka ye hi tarika hai and in GenerationType we have more options -> UUID, Identity, Auto, Sequence
    private Long categoryId;

    @NotBlank   // ab ise khali nahi rakh sakte CategoryName wali field ko (if you pass "" empty string it will give you internal server error)  ::: |||        // this is coming from jakarta.validation.constraints and this is part of Hibernate (using hibernate we can provide validation in our code Like : NotNull, NotBlank, Size etc.)
    @Size(min = 4, message = "Category Name must contain atleast 4 or 4+ words ... !!! ")  // Extra Validation
    private String categoryName;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Product> products;

    // todo :: NOW There is no need to define the constructors and getter setters because here we use Lombok and we can use these simple annotations they are providing us these all things

/*
    //todo : It is a good practice where we define a default constructor


    public Category() {
    }

    public Category(Long categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public String toString() {
        return "Category{" +
                "categoryId = " + categoryId +
                ", categoryName = '" + categoryName + '\'' +
                '}';
    }
    */
}
