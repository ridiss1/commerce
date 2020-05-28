package com.ecommerce.microcommerce.dao;

import com.ecommerce.microcommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductDao extends JpaRepository<Product, Integer> {
    List<Product> findAll();
    List<Product> deleteById(int id);
    Product findById(int id);
    List<Product> findByPrixGreaterThan(int prixLimit);
    List<Product> findByNomLike(String recherche);

    @Query("SELECT p FROM Product p WHERE p.prix > :prixLimit")
    List<Product> chercherUnProduitCher(@Param("prixLimit") int prix);

    List<Product> findByOrderByNomAsc();
}
