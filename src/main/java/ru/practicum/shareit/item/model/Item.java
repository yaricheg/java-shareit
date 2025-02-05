package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Data
@Validated
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @Column(name = "is_available")
    private boolean available;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @Column(name = "request_id")
    private Long request;

    @Column(name = "last_booking")
    private LocalDateTime lastBooking;

    @Column(name = "next_booking")
    private LocalDateTime nextBooking;

    @ElementCollection
    @CollectionTable(name = "comments", joinColumns = @JoinColumn(name = "item_id"))
    @Column(name = "text_comment")
    private List<String> comments = new ArrayList<>();
}
