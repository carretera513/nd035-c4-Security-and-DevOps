package com.example.demo.controllers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
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
public class ItemControllerTest {
    @Mock
    private ItemRepository itemRepo;

    @InjectMocks
    private ItemController itemController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() {
        Mockito.reset(itemRepo);
    }

    @Test
    public void checkItemByName() throws Exception {
        Long itemId = 2L;
        String itemName = "Item 2";

        // Get second item in the lsit
        when(itemRepo.findByName(itemName)).thenReturn(getItems().subList(1, 2));
        List<Item> items = itemController.getItemsByName(itemName).getBody();

        assertEquals(itemId, items.get(0).getId());
    }

    @Test
    public void checkItemById() throws Exception {
        Long itemId = 2L;
        String itemName = "Item 2";

        when(itemRepo.findById(itemId)).thenReturn(Optional.of(getItems().get((1))));
        Item item = itemController.getItemById(itemId).getBody();

        assertEquals(itemName, item.getName());
    }

    @Test
    public void getAllItems() throws Exception {
        when(itemRepo.findAll()).thenReturn(getItems());

        List<Item> allItems = itemController.getItems().getBody();

        assertEquals(2, allItems.size());
    }

    private List<Item> getItems() {
        ArrayList<Item> list = new ArrayList<>();
        Item item1= new Item();
        item1.setId(1L);
        item1.setName("Item 1");
        item1.setDescription("This is Item 1");
        item1.setPrice(new BigDecimal("1.00"));
        list.add(item1);
        Item item2= new Item();
        item2.setId(2L);
        item2.setName("Item 2");
        item2.setDescription("This is Item 2");
        item2.setPrice(new BigDecimal("2.00"));
        list.add(item2);
        return list;
    }
}
