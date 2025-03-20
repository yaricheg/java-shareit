package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.model.CommentDto;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;
    private UserDto user;
    private Long userId = 1L;
    private ItemDto item1;
    private ItemDto item2;

    @BeforeEach
    void setUp() {

        user = UserDto.builder()
                .id(userId)
                .name("Павел")
                .email("pavel@mail.ru")
                .build();

        item1 = ItemDto.builder()
                .id(1L)
                .name("item1")
                .description("Description1")
                .available(true)
                .build();

        item2 = ItemDto.builder()
                .id(2L)
                .name("item2")
                .description("Description2")
                .available(true)
                .build();
    }

    @Test
    void createItemThenReturnItemDto() throws Exception {
        when(itemService.createItem(item1, userId)).thenReturn(item1);
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .content(new ObjectMapper().writeValueAsString(item1))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value(item1.getName()));
        verify(itemService, times(1)).createItem(item1, userId);
    }

    @Test
    void updateItemThenReturnItemDto() throws Exception {
        ItemDto updatedItem = ItemDto.builder()
                .id(1L)
                .name("updateItem1")
                .description("updateItemDescription1")
                .available(true)
                .build();
        when(itemService.updateItem(updatedItem, item1.getId(), userId)).thenReturn(updatedItem);
        mockMvc.perform(patch("/items/{id}", item1.getId())
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value(updatedItem.getName()));
        verify(itemService, times(1)).updateItem(updatedItem, item1.getId(), userId);
    }

    @Test
    void getItemById() throws Exception {
        ItemDto item = ItemDto.builder()
                .id(3L)
                .name("item")
                .description("Description")
                .available(true)
                .build();

        when(itemService.getItemById(item.getId())).thenReturn(item);

        mockMvc.perform(get("/items/{id}", item.getId())
                        .header("X-Sharer-User-Id", userId))
                .andExpect(jsonPath("$.id").value(item.getId()))
                .andExpect(status().isOk());

        verify(itemService, times(1)).getItemById(item.getId());
    }


    @Test
    void getItems() throws Exception {
        List<ItemDto> items = List.of(item1);
        ItemDto item = ItemDto.builder()
                .id(3L)
                .name("item")
                .description("Description")
                .available(true)
                .build();
        itemService.createItem(item, userId);
        when(itemService.getItems(userId)).thenReturn(items);
        mockMvc.perform(get("/items/{id}", item.getId())
                        .header("X-Sharer-User-Id", userId))
                .andExpect(jsonPath("$.id").value(item.getId()))
                .andExpect(status().isOk());
        verify(itemService, times(1)).getItems(userId);
    }


    @Test
    void searchItems() throws Exception {
        List<ItemDto> items = List.of(item1, item2);
        String text = "item";
        when(itemService.searchItems(text)).thenReturn(items);

        mockMvc.perform(get("/items/search").param("text", text))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value(item1.getName()))
                .andExpect(jsonPath("$").isArray());

        verify(itemService, times(1)).searchItems(text);
    }

    @Test
    void addComment() throws Exception {
        CommentDto comment = CommentDto.builder()
                .text("comment")
                .build();

        CommentDto response = CommentDto.builder()
                .id(1L)
                .text(comment.getText())
                .authorName(user.getName())
                .created(LocalDateTime.now())
                .build();
        when(itemService.addComment(userId, item1.getId(), comment)).thenReturn(response);
        mockMvc.perform(post("/items/{itemId}/comment", item1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .content(new ObjectMapper().writeValueAsString(comment))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value(comment.getText()))
                .andExpect(jsonPath("$.authorName").value(user.getName()));
        verify(itemService, times(1)).addComment(userId, item1.getId(), comment);
    }
}