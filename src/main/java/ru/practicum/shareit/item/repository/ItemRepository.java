package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(value = "SELECT * FROM items i WHERE i.owner_id = ?1",
            nativeQuery = true)
    List<Item> getByOwnerItem(Long userId);

    List<Item> findByNameContainingIgnoreCaseAndAvailable(String text, Boolean available);

    List<Item> findByDescriptionContainingIgnoreCaseAndAvailable(String text, Boolean available);
}
