package com.mercus.mercus_backend.repository;

import com.mercus.mercus_backend.model.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CategoryRepository extends JpaRepository<Category,Long>
{

    Category findByCategoryName( String categoryName);
}
