package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RestController
@RequestMapping("api/cart")
public class CartControllerTest {

    private CartController cartController;

    @Autowired
    private UserRepository userRepository = mock(UserRepository.class);

    @Autowired
    private CartRepository cartRepository = mock(CartRepository.class);

    @Autowired
    private ItemRepository itemRepository = mock(ItemRepository.class);


    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
    }

    @Test
    public void add_to_cart() {
        User user = new User();
        user.setUsername("test");

        Cart cart = new Cart();
        cart.setUser(user);

        Item item = new Item();
        item.setId(1L);
        item.setDescription("First Item");
        item.setName("Item");
        item.setPrice(BigDecimal.valueOf(4.99));

        cart.addItem(item);
        user.setCart(cart);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("test");
        request.setItemId(1L);
        request.setQuantity(1);

        when(userRepository.findByUsername("test")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        
        final ResponseEntity<Cart> response = cartController.addTocart(request);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Cart testCart = response.getBody();
        assertNotNull(testCart);
        assertTrue(testCart.getItems().contains(item));
        assertEquals(2, testCart.getItems().size());
        assertEquals(user.getId(), testCart.getUser().getId());
    }

    @Test
    public void add_to_cart_error_username() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("test");
        request.setItemId(1L);
        request.setQuantity(1);

        request.setUsername("error");

        final ResponseEntity<Cart> response = cartController.addTocart(request);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void add_to_cart_error_id() {
        User user = new User();
        user.setUsername("test");

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("test");
        request.setQuantity(1);
        request.setItemId(2L);

        when(userRepository.findByUsername("test")).thenReturn(user);

        final ResponseEntity<Cart> response = cartController.addTocart(request);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void remove_from_cart() {
        User user = new User();
        user.setUsername("test");

        Cart cart = new Cart();
        cart.setUser(user);

        Item item = new Item();
        item.setId(1L);
        item.setDescription("First Item");
        item.setName("Item");
        item.setPrice(BigDecimal.valueOf(4.99));

        cart.addItem(item);
        user.setCart(cart);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("test");
        request.setItemId(1L);
        request.setQuantity(1);

        when(userRepository.findByUsername("test")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        final ResponseEntity<Cart> response = cartController.removeFromcart(request);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Cart testCart = response.getBody();
        assertNotNull(testCart);
        assertFalse(testCart.getItems().contains(item));
        assertEquals(0, testCart.getItems().size());
        assertEquals(user.getId(), testCart.getUser().getId());
    }

    @Test
    public void remove_from_cart_error_username() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("error");

        final ResponseEntity<Cart> response = cartController.removeFromcart(request);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void remove_from_cart_error_id() {
        User user = new User();
        user.setUsername("test");

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("test");
        request.setQuantity(1);
        request.setItemId(404L);

        when(userRepository.findByUsername("test")).thenReturn(user);

        final ResponseEntity<Cart> response = cartController.removeFromcart(request);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}