package ru.practicum.shareit.comment.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;


@Data
@Validated
@Entity
@Table(name = "COMMENTS")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  //— уникальный идентификатор комментария;

    @Column(name = "text_comment")
    private String text;  //— содержимое комментария;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item itemId; // — вещь, к которой относится комментарий;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;  //— автор комментария;

    @Column(name = "created_date")
    private LocalDateTime created;  // — дата создания комментария
}
