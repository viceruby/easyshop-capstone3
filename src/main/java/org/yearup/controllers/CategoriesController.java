package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.CategoryDao;
import org.yearup.data.ProductDao;
import org.yearup.models.Category;
import org.yearup.models.Product;
import java.util.List;

@RestController
@RequestMapping("/categories")
@CrossOrigin
public class CategoriesController
{
    private CategoryDao categoryDao;
    private ProductDao productDao;

    @Autowired
    public CategoriesController(CategoryDao categoryDao, ProductDao productDoa)
    {
        this.categoryDao = categoryDao;
        this.productDao = productDoa;
    }

    @GetMapping
    public List<Category> getAll()
    {
        return categoryDao.getAllCategories();
    }

    @GetMapping("{id}")
    public Category getById(@PathVariable int id)
    {
        Category category = categoryDao.getById(id);

        if (category == null)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found.");
        }

        return category;
    }

    @GetMapping("{categoryId}/products")
    public List<Product> getProductsById(@PathVariable int categoryId)
    {
        return productDao.listByCategoryId(categoryId);
    }

    /* REMOVE COMMENT WHEN AUTH IS SETUP */
    @PostMapping
    // @PreAuthorize("hasRole('ADMIN')")
    public Category addCategory(@RequestBody Category category)
    {
        return categoryDao.create(category);
    }

    /* REMOVE COMMENT WHEN AUTH IS SETUP */
    @PutMapping("{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    public void updateCategory(@PathVariable int id, @RequestBody Category category)
    {
        Category existing = categoryDao.getById(id);

        if (existing == null)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found.");
        }

        categoryDao.update(id, category);
    }

    /* REMOVE COMMENT WHEN AUTH IS SETUP */
    @DeleteMapping("{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    public void deleteCategory(@PathVariable int id)
    {
        Category existing = categoryDao.getById(id);

        if(existing == null)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found.");
        }

        categoryDao.delete(id);
    }
}
