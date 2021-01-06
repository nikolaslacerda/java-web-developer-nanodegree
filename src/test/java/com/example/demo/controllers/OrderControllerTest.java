package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RestController
@RequestMapping("api/order")
public class OrderControllerTest {

    private OrderController orderController;

    @Autowired
    private UserRepository userRepository = mock(UserRepository.class);

    @Autowired
    private CartRepository cartRepository = mock(CartRepository.class);

    @Autowired
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Autowired
    private OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
    }

    @Test
    public void submit_success() {
        User user = new User();
        user.setUsername("test");

        Cart cart = new Cart();
        cart.setUser(user);

        Item item1 = new Item();
        item1.setId(1L);
        item1.setDescription("First Item");
        item1.setName("Item1");
        item1.setPrice(BigDecimal.valueOf(4.99));
        cart.addItem(item1);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setDescription("Second Item");
        item2.setName("Item2");
        item2.setPrice(BigDecimal.valueOf(8.99));
        cart.addItem(item2);

        user.setCart(cart);

        when(userRepository.findByUsername("test")).thenReturn(user);

        final ResponseEntity<UserOrder> response = orderController.submit("test");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        verify(userRepository, times(1)).findByUsername("test");
        verify(orderRepository, times(1)).save(any(UserOrder.class));

        UserOrder testUserOrder = response.getBody();
        assertNotNull(testUserOrder);
        assertEquals(2, testUserOrder.getItems().size());
        assertEquals(user, testUserOrder.getUser());
    }

    @Test
    public void submit_username_error() {
        final ResponseEntity<UserOrder> response = orderController.submit("error");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void get_orders_for_user() {
        User user = new User();
        user.setUsername("test");

        Cart cart = new Cart();
        cart.setUser(user);

        Item item1 = new Item();
        item1.setId(1L);
        item1.setDescription("First Item");
        item1.setName("Item1");
        item1.setPrice(BigDecimal.valueOf(4.99));
        cart.addItem(item1);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setDescription("Second Item");
        item2.setName("Item2");
        item2.setPrice(BigDecimal.valueOf(8.99));
        cart.addItem(item2);

        user.setCart(cart);

        when(userRepository.findByUsername("test")).thenReturn(user);

        UserOrder order1 = new UserOrder();
        order1.setId(1L);
        order1.setUser(user);
        order1.setTotal(BigDecimal.valueOf(4.99 + 8.99));

        UserOrder order2 = new UserOrder();
        order2.setId(2L);
        order2.setUser(user);
        order2.setTotal(BigDecimal.valueOf(16.50 + 27.50));

        List<UserOrder> userOrders = new ArrayList<>();
        userOrders.add(order1);
        userOrders.add(order2);

        when(orderRepository.findByUser(user)).thenReturn(userOrders);

        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("test");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<UserOrder> testUserOrders = response.getBody();
        assertNotNull(testUserOrders);
        assertArrayEquals(userOrders.toArray(), testUserOrders.toArray());
    }

    @Test
    public void get_orders_for_username_error() {
        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("error");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}