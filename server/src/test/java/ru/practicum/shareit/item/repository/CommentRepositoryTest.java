package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class CommentRepositoryTest {
    // Just for Fun

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    void findAll_WithEmptyDB_ShouldNothingFind() {
        assertTrue(commentRepository.findAll().isEmpty(),
                "Архив комментариев оборудования должен быть пустым");
    }

    @Test
    void findAllByItemId_WithExistingComment_ShouldReturnComment() {
        // Участники обсуждения
        User technician = createUser("Иван Слесарев", "ivan@repair.com");
        User engineer = createUser("Ольга Инженерова", "olga@tech.com");
        User supervisor = createUser("Сергей Контролов", "sergey@qa.com");

        // Оборудование цеха
        Item weldingMachine = createItem("Сварочный аппарат", "Аппарат аргонной сварки", technician);
        Item cncMachine = createItem("ЧПУ станок", "5-осевой фрезерный станок", engineer);
        Item compressor = createItem("Воздушный компрессор", "Промышленный 100-литровый", technician);

        // Диалоги по оборудованию
        createComment("Проблема с подачей газа", weldingMachine, engineer);
        createComment("Замена электродов", weldingMachine, supervisor);
        createComment("Калибровка осей", cncMachine, technician);

        // Проверка обсуждений по ЧПУ станку
        List<Comment> machineDiscussions = commentRepository.findAllByItemId(cncMachine.getId());
        assertEquals(1, machineDiscussions.size(),
                "Должно найтись 1 обсуждение по ЧПУ станку");
    }

    @Test
    void findDiscussionsForNewEquipment_ShouldReturnEmpty() {
        // Участник обсуждений
        User mechanic = createUser("Алексей Механиков", "alex@garage.com");

        // Новое оборудование
        Item newDrill = createItem("Сверлильный станок", "Настольный 1500W", mechanic);

        // Попытка найти обсуждения
        List<Comment> discussions = commentRepository.findAllByItemId(newDrill.getId());
        assertTrue(discussions.isEmpty(),
                "Для нового оборудования не должно быть записей в обсуждениях");
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return userRepository.save(user);
    }

    private Item createItem(String name, String specs, User owner) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(specs);
        item.setOwner(owner);
        item.setAvailable(true);
        return itemRepository.save(item);
    }

    private Comment createComment(String message, Item equipment, User author) {
        Comment comment = new Comment();
        comment.setText(message);
        comment.setItem(equipment);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        return commentRepository.save(comment);
    }
}