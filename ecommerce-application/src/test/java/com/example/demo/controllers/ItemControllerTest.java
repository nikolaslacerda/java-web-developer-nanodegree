package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RestController
@RequestMapping("api/item")
public class ItemControllerTest {

    private ItemController itemController;

    @Autowired
    private UserRepository userRepository = mock(UserRepository.class);

    @Autowired
    private CartRepository cartRepository = mock(CartRepository.class);

    @Autowired
    private ItemRepository itemRepository = mock(ItemRepository.class);


    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void get_items() {
        Item item1 = new Item();
        item1.setId(1L);
        item1.setDescription("First");
        item1.setName("Item 1");
        item1.setPrice(BigDecimal.valueOf(4.99));

        Item item2 = new Item();
        item2.setId(2L);
        item2.setDescription("Second");
        item2.setName("Item 2");
        item2.setPrice(BigDecimal.valueOf(8.99));

        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        when(itemRepository.findAll()).thenReturn(items);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        when(itemRepository.findByName("Item")).thenReturn(items);

        final ResponseEntity<List<Item>> response = itemController.getItems();
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<Item> testList = response.getBody();
        assertNotNull(testList);
        assertArrayEquals(items.toArray(), testList.toArray());
    }

    @Test
    public void get_item_by_id() {
        Item item1 = new Item();
        item1.setId(1L);
        item1.setDescription("First");
        item1.setName("Item 1");
        item1.setPrice(BigDecimal.valueOf(4.99));

        Item item2 = new Item();
        item2.setId(2L);
        item2.setDescription("Second");
        item2.setName("Item 2");
        item2.setPrice(BigDecimal.valueOf(8.99));

        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        when(itemRepository.findAll()).thenReturn(items);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        when(itemRepository.findByName("Item")).thenReturn(items);

        final ResponseEntity<Item> response = itemController.getItemById(1L);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Item testItem = response.getBody();
        assertNotNull(testItem);
        assertEquals("First", testItem.getDescription());
        assertEquals("Item 1", testItem.getName());
    }

    @Test
    public void get_item_by_id_error() {
        final ResponseEntity<Item> response = itemController.getItemById(3L);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void get_items_by_name() {
        Item item1 = new Item();
        item1.setId(1L);
        item1.setDescription("First");
        item1.setName("Item 1");
        item1.setPrice(BigDecimal.valueOf(4.99));

        Item item2 = new Item();
        item2.setId(2L);
        item2.setDescription("Second");
        item2.setName("Item 2");
        item2.setPrice(BigDecimal.valueOf(8.99));

        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        when(itemRepository.findAll()).thenReturn(items);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        when(itemRepository.findByName("Item")).thenReturn(items);

        final ResponseEntity<List<Item>> response = itemController.getItemsByName("Item");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<Item> testList = response.getBody();
        assertNotNull(testList);
        assertArrayEquals(items.toArray(), testList.toArray());
    }

    @Test
    public void get_items_by_name_error() {
        final ResponseEntity<List<Item>> response = itemController.getItemsByName("Third");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}