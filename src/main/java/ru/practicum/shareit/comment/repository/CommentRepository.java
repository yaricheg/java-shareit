package ru.practicum.shareit.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.comment.model.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
