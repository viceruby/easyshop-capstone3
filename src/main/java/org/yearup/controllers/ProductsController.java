package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.models.Product;
import org.yearup.data.ProductDao;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("products")
@CrossOrigin
public class ProductsController
{
    private ProductDao productDao;

    @Autowired
    public ProductsController(ProductDao productDao)
    {
        this.productDao = productDao;
    }

    @GetMapping("")
    @PreAuthorize("permitAll()")
    public List<Product> search(@RequestParam(name="cat", required = false) Integer categoryId,
                                @RequestParam(name="minPrice", required = false) BigDecimal minPrice,
                                @RequestParam(name="maxPrice", required = false) BigDecimal maxPrice,
                                @RequestParam(name="color", required = false) String color
                                )
    {
        return productDao.search(categoryId, minPrice, maxPrice, color);
    }

    @GetMapping("{id}")
    @PreAuthorize("permitAll()")
    public Product getById(@PathVariable int id )
    {
        var product = productDao.getById(id);

        if (product == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");

        return product;
    }

    @PostMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Product addProduct(@RequestBody Product product)
    {
        return productDao.create(product);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void updateProduct(@PathVariable int id, @RequestBody Product product)
    {
        var existing = productDao.getById(id);

        if(existing == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");

        productDao.update(id, product);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteProduct(@PathVariable int id)
    {
        var existing = productDao.getById(id);

        if (existing == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");

        productDao.delete(id);
    }
}
