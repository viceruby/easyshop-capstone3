package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.User;

import java.security.Principal;

// IGNORE, FOR C&P USE ONLY: /* A */
// Submission commit

@RestController
@RequestMapping("/cart")
@CrossOrigin
@PreAuthorize("isAuthenticated()")
public class ShoppingCartController
{
    // DAOs
    private ShoppingCartDao shoppingCartDao;
    private UserDao userDao;
    private ProductDao productDao;

    // DEPENDENCY INJECTION
    @Autowired
    public ShoppingCartController(ShoppingCartDao shoppingCartDao,
                                  UserDao userDao,
                                  ProductDao productDao)
    {
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
        this.productDao = productDao;
    }

    /* GET /cart */
    @GetMapping("")
    public ShoppingCart getCart(Principal principal)                   // (7)
    {
        try
        {
            String userName = principal.getName();                     // (1)
            User user = userDao.getByUserName(userName);               // (2)
            int userId = user.getId();                                 // (3)
            ShoppingCart cart = shoppingCartDao.getByUserId(userId);   // (4)

            return cart;                                               // (5), (6)
        }
        catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    /* POST /cart/products/{productId} */
    @PostMapping("/products/{productId}")
    public ShoppingCart addProductToCart(@PathVariable int productId,
                                         Principal principal)
    {
        try
            {
            String userName = principal.getName();                               // (8)
            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            var product = productDao.getById(productId);
            if (product == null)
                {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product was not found.");
                }

            ShoppingCart cart = shoppingCartDao.addProduct(userId, productId);   // (9)
            shoppingCartDao.saveCart(userId, cart);                              // (10)

            return cart;
        }

        catch (ResponseStatusException ex)
            {
            throw ex;                                                            // (11)
            }

        catch (Exception e)
            {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... Our bad.");
            }
    }


    /* PUT /cart/products/{qty} */
    @PutMapping("/products/{productId}")
    public ShoppingCart updateProductQuantity(@PathVariable int productId,
                                              @RequestBody ShoppingCartItem item,
                                              Principal principal)
    {

        try
        {
            String userName = principal.getName();   // (12)
            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            int quantity = item.getQuantity();       // (13)
            if (quantity <= 0)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be at least 1.");

            ShoppingCart cart = shoppingCartDao.updateProductQuantity(userId, productId, quantity);

            if (cart == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not in cart.");

            shoppingCartDao.saveCart(userId, cart);
            return cart;
        }

        catch(ResponseStatusException ex)
        {
            throw ex;
        }

        catch (Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... Our bad.");
        }
    }

    /* DELETE /cart */
    @DeleteMapping("")
    public void clearCart(Principal principal)
    {
        try
        {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            shoppingCartDao.clearCart(userId);
        }

        catch (Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... Our bad.");
        }

    }

}

/* NOTES */

/* GET /cart */
// (1) Gets username of the logged-in user. Spring gives you the authenticated username.
// (2) Looks up full user record in DB. Returns a `User` object.
// (3) Extracts userId (the row's primary key). Carts belong to userIds, not usernames.
// (4) Use ShoppingCartDao to get user's cart. Returns a cart with or without any items.
// (5) Return the cart to Postman
// (6) Removed `null` and changed to `cart.` `return null;` means "send nothing." `return cart;` means "send the actual data."
// (7) SUMMARY of GET /cart
// ShoppingCart lives in memory, keyed by userId.
// Each user has their own cart stored in a HashMap inside MemoryShoppingCartDao.
// 7A. Identify the currently logged-in user (via JWT → Principal)
// 7B. Looks up user in database
// 7C. Get the user's shopping cart from MemoryShoppingCartDao
// 7D. Return the cart as JSON

/* POST /cart/products/{productId} */
// (8) Find the logged-in user
// (9) Ensure the product exists
// (10) Add the product to cart (in memory)
// (11) 404

/* PUT Update Qty */
// (12) Who is this?
// (13) Quantity validity check