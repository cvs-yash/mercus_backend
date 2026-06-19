package com.mercus.mercus_backend.repository;

import com.mercus.mercus_backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductRepository extends JpaRepository<Product,Long>
{

}
