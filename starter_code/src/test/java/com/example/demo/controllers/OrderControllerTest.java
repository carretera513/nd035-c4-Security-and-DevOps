package com.example.demo.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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
public class OrderControllerTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private OrderRepository orderRepo;

    @InjectMocks
    private OrderController orderController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() {
        Mockito.reset(userRepo);
        Mockito.reset(orderRepo);
    }

    @Test
    public void CheckSubmitOrder() {
        String userName = "test_user";
        Long itemId = 1L;

        // Get user
        User user = getUser();
        // Get item
        Item item = getItem();
        List<Item> items = new ArrayList<>();
        items.add(item);
        // Get cart
        Cart cart = getCart(user);
        cart.setItems(items);
        cart.setTotal(item.getPrice());
        // Update user
        user = updateUser(user, cart);

        when(userRepo.findByUsername(userName)).thenReturn(user);

        UserOrder order = orderController.submit(userName).getBody();

        assertNotNull(order);
        assertEquals(1, order.getItems().size());
        assertEquals(new BigDecimal("1.00"), order.getTotal());
        assertEquals(userName, order.getUser().getUsername());
    }

    @Test
    public void CheckOrderHistory() {
        String userName = "test_user";
        Long itemId = 1L;

        // Get user
        User user = getUser();
        // Get item
        Item item = getItem();
        List<Item> items = new ArrayList<>();
        items.add(item);
        // Get cart
        Cart cart = getCart(user);
        cart.setItems(items);
        cart.setTotal(item.getPrice());
        // Update user
        user = updateUser(user, cart);

        when(userRepo.findByUsername(userName)).thenReturn(user);

        UserOrder order = orderController.submit(userName).getBody();
        List<UserOrder> newOrders = new ArrayList<>();
        newOrders.add(order);
        when(orderRepo.findByUser(user)).thenReturn(newOrders);

        List<UserOrder> orders = orderController.getOrdersForUser(userName).getBody();

        assertNotNull(orders);
        assertEquals(1, orders.size());
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
