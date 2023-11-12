package com.example.demo.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CartControllerTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private CartRepository cartRepo;

    @Mock
    private ItemRepository itemRepo;

    @InjectMocks
    private CartController cartController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() {
        Mockito.reset(userRepo);
        Mockito.reset(cartRepo);
        Mockito.reset(itemRepo);
    }

    @Test
    public void checkAddToCart() throws Exception {
        String userName = "test_user";
        Long itemId = 1L;
        int quantity = 3;

        User user = getUser();
        Cart inCart = getCart(user);
        user = updateUser(user, inCart);
        Item item = getItem();

        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setItemId(item.getId());
        cartRequest.setQuantity(quantity);
        cartRequest.setUsername(user.getUsername());

        when(userRepo.findByUsername(userName)).thenReturn(user);
        when(cartRepo.findByUser(user)).thenReturn(user.getCart());
        when(itemRepo.findById(itemId)).thenReturn(Optional.of(item));

        Cart cart = cartController.addTocart(cartRequest).getBody();
        assertNotNull(cart);
        assertEquals(cart.getItems().get(0).getPrice().multiply(BigDecimal.valueOf(quantity)),
                cart.getTotal());
        assertEquals(userName, cart.getUser().getUsername());
        assertEquals(quantity, cart.getItems().size());
    }

    @Test
    public void checkRemoveFromCart() throws Exception {
        String userName = "test_user";
        Long itemId = 1L;
        int addQuantity = 3;
        int removeQuantity = 1;
        // Should have 2 when done

        assertTrue(addQuantity >= removeQuantity);

        User user = getUser();
        Cart inCart = getCart(user);
        user = updateUser(user, inCart);
        Item item = getItem();

        // First add to cart
        ModifyCartRequest addRequest = new ModifyCartRequest();
        addRequest.setItemId(item.getId());
        addRequest.setQuantity(addQuantity);
        addRequest.setUsername(user.getUsername());

        when(userRepo.findByUsername(userName)).thenReturn(user);
        when(cartRepo.findByUser(user)).thenReturn(user.getCart());
        when(itemRepo.findById(itemId)).thenReturn(Optional.of(item));

        Cart cart = cartController.addTocart(addRequest).getBody();
        assertNotNull(cart);

        // Now remove from cart
        ModifyCartRequest removeRequest = new ModifyCartRequest();
        removeRequest.setItemId(item.getId());
        removeRequest.setQuantity(removeQuantity);
        removeRequest.setUsername(user.getUsername());

        cart = cartController.removeFromCart(removeRequest).getBody();
        assertNotNull(cart);

        assertEquals(cart.getItems().get(0).getPrice()
                .multiply(BigDecimal.valueOf(addQuantity - removeQuantity)), cart.getTotal());
        assertEquals(userName, cart.getUser().getUsername());
        assertEquals(addQuantity - removeQuantity, cart.getItems().size());
    }


    private User getUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("test_user");
        user.setPassword("P@ssword");
        return user;
    }

    private User updateUser(User user, Cart cart) {
        user.setCart(cart);
        return user;
    }

    private Cart getCart(User user) {
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        return cart;
    }

    private Item getItem() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item 1");
        item.setDescription("This is Item 1");
        item.setPrice(new BigDecimal("1.00"));
        return item;
    }
}
