package org.yearup.data.memory;

import org.springframework.stereotype.Component;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.ProductDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import java.util.HashMap;
import java.util.Map;

// IGNORE, FOR C&P USE ONLY: /* A */

@Component
public class MemoryShoppingCartDao implements ShoppingCartDao {

    private Map<Integer, ShoppingCart> carts = new HashMap<>();
    private ProductDao productDao;

    public MemoryShoppingCartDao(ProductDao productDao)
    {
        this.productDao = productDao;
    }

    /* GET CART BY USER ID */
    @Override
    public ShoppingCart getByUserId(int userId)
    {
        // NO CART? CREATE ONE
        if (!carts.containsKey(userId))
            carts.put(userId, new ShoppingCart());

        return carts.get(userId);
    }

    /* ADD PRODUCTS TO CART */
    public ShoppingCart addProduct(int userId, int productId)
    {
        ShoppingCart cart = getByUserId(userId);

        // CONTROLLER HANDLES 404
        Product product = productDao.getById(productId);
        if (product == null)
            return null;

        // ADD QTY
        if (cart.contains(productId))
        {
            ShoppingCartItem existing = cart.get(productId);
            existing.setQuantity(existing.getQuantity() + 1);
        }
        else
        {
            ShoppingCartItem item = new ShoppingCartItem();
            item.setProduct(product);
            item.setQuantity(1);
            cart.add(item);
        }

        return cart;
    }

    /* UPDATE QTY */
    public ShoppingCart updateProductQuantity(int userId, int productId, int quantity)
    {
        ShoppingCart cart = getByUserId(userId);

        if (!cart.contains(productId))
            return null;

        ShoppingCartItem item = cart.get(productId);
        item.setQuantity(quantity);

        return cart;
    }

    /* CLEAR CART */
    public void clearCart(int userId)
    {
        carts.put(userId, new ShoppingCart());
    }

    /* SAVE CART */
    @Override
    public void saveCart(int userId, ShoppingCart cart)
    {
        carts.put(userId, cart);
    }

}
