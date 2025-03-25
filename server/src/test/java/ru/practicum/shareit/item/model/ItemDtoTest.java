package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.comment.model.CommentDto;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoTest {

    @Autowired
    private JacksonTester<ItemDto> jsonTester;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    @Test
    void testSerializeWithCustomDateFormat() throws Exception {
        LocalDateTime commentCreated = LocalDateTime.of(2025, 2, 2, 15, 15, 0);
        LocalDateTime lastBooking = LocalDateTime.of(2025, 2, 2, 13, 30, 0);
        LocalDateTime nextBooking = LocalDateTime.of(2025, 2, 5, 10, 15, 0);

        CommentDto comment = CommentDto.builder()
                .id(1L)
                .authorName("Pavel")
                .created(commentCreated)
                .text("comment")
                .build();

        ItemDto dto = ItemDto.builder()
                .id(10L)
                .name("Pavel")
                .description("Bicycle")
                .available(true)
                .comments(List.of(comment))
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .build();

        JsonContent<ItemDto> jsonContent = jsonTester.write(dto);

        assertThat(jsonContent).extractingJsonPathStringValue("$.lastBooking")
                .isEqualTo("2025-02-02T13:30:00");
        assertThat(jsonContent).extractingJsonPathStringValue("$.nextBooking")
                .isEqualTo("2025-02-05T10:15:00");
        assertThat(jsonContent).extractingJsonPathStringValue("$.comments[0].created")
                .isEqualTo("2025-02-02T15:15:00");
    }

}