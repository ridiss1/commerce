package com.ecommerce.microcommerce.web.controller;

import com.ecommerce.microcommerce.dao.ProductDao;
import com.ecommerce.microcommerce.model.Product;
import com.ecommerce.microcommerce.web.exceptions.ProduitGratuitException;
import com.ecommerce.microcommerce.web.exceptions.ProduitIntrouvableException;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.*;


@Api("Api pour les opérations CRUD sur les produits")
@RestController
public class ProductController {
    @Autowired
    private ProductDao productDao;

    // Récupérer la liste des produits
    @RequestMapping(value = "/Produits", method = RequestMethod.GET)
    public MappingJacksonValue listeProduits() {

        Iterable<Product> produits = productDao.findAll();

        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("prixAchat");
        FilterProvider listDeNosFiltres = new SimpleFilterProvider().addFilter("monFiltreDynamique", monFiltre);
        MappingJacksonValue produitsFiltres = new MappingJacksonValue(produits);
        produitsFiltres.setFilters(listDeNosFiltres);

        return produitsFiltres;

    }

    //Récupérer un produit par son Id
    @ApiOperation(value = "Récupère un produit grâce à son ID à condition que celui-ci soit en stock!")
    @GetMapping(value = "/Produits/{id}")
    public Product afficherUnProduit(@PathVariable int id) {
        Product product = productDao.findById(id);

        if (product == null){
            throw new ProduitIntrouvableException("Le produit avec l'id " + id + " est INTROUVABLE. Écran Bleu si je pouvais");
        }
        return product;
    }
/*
    @GetMapping(value = "test/produits/{prixLimit}")
    public List<Product> testeDeRequetes(@PathVariable int prixLimit) {
        return productDao.findByPrixGreaterThan(400);
    }
*/
    @GetMapping(value = "test/produits/{recherche}")
    public List<Product> testeDeRequetes(@PathVariable String recherche) {
        return productDao.findByNomLike("%" + recherche + "%");
    }

    //ajouter un produit
    @PostMapping(value = "/Produits")
    public ResponseEntity<Void> ajouterProduit(@Valid @RequestBody Product product) {
        Product productAdded = productDao.save(product);
        if (productAdded == null)
            return ResponseEntity.noContent().build();

        if (productAdded.getPrix() == 0){
            throw new ProduitGratuitException("Le produit, avec un prix de 0€ est interdit");
        }
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(productAdded.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping(value = "/Produits/{id}")
    public void supprimerProduit(@PathVariable int id) {
        productDao.deleteById(id);
    }

    @PutMapping(value = "/Produits")
    public void updateProduit(@RequestBody Product product) {
        productDao.save(product);
    }

    @GetMapping(value = "AdminProduits")
    public Map<Product, Integer> calculerMargeProduit(){
        List<Product> produits = productDao.findAll();

        Map<Product, Integer> map = new HashMap<Product, Integer>();

        for (Product produit:produits) {
            map.put(produit,(produit.getPrix()-produit.getPrixAchat()));
        }

        return map;
    }

    @GetMapping(value = "AdminProduits/ASC")
    public List<Product> trierProduitsParOrdreAlphabetique(){
        return productDao.findByOrderByNomAsc();
    }


}
