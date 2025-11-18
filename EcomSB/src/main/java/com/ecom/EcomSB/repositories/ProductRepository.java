package com.ecom.EcomSB.repositories;

import com.ecom.EcomSB.model.Category;
import com.ecom.EcomSB.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByCategoryOrderByPriceAsc(Category category, Pageable pageDetail); // According to this method name JPA knows what need to do if you divide this method name in chunks you will get a SQL Query

    Page<Product> findByProductNameLikeIgnoreCase(String keyword, Pageable pageDetail);
}
