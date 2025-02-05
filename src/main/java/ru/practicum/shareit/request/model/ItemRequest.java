package ru.practicum.shareit.request.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;


@Data
@Validated
@Table(name = "requests")
@Entity
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //— уникальный идентификатор запроса;

    private String description; // — текст запроса, содержащий описание требуемой вещи;

    @ManyToOne
    @JoinColumn(name = "requestor_id")
    private User requestor; //— пользователь, создавший запрос;

    @ManyToOne
    @JoinColumn(name = "requested_item_id")
    private Item requestedItem;
   // private LocalDateTime created; //— дата и время создания запроса.
}
