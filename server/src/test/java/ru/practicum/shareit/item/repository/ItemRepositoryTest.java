package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class ItemRepositoryTest {
    // Just for Fun
    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RequestRepository itemRequestRepository;

    @Test
    public void testEmpty() {
        List<Item> items = itemRepository.findAll();
        assertTrue(items.isEmpty(), "База космического оборудования должна быть пустой");
    }

    @Test
    public void findAllByOwnerId_WithCapitan_SHouldReturnCapitanItem() {
        // Подготовка экипажа
        User captain = new User();
        captain.setName("Иван Космический");
        captain.setEmail("starcommand@galaxy.ru");
        userRepository.save(captain);

        // Добавление космического ровера
        Item marsRover = new Item();
        marsRover.setName("Марсоход-Эксплорер");
        marsRover.setDescription("Ровер для марсианских миссий");
        marsRover.setOwner(captain);
        marsRover.setAvailable(true);
        itemRepository.save(marsRover);

        // Поиск оборудования капитана
        List<Item> result = itemRepository.findAllByOwnerId(captain.getId());

        // Проверка
        assertEquals(1, result.size(), "Должен найтись один ровер");
        assertEquals("Иван Космический", result.get(0).getOwner().getName());
        assertEquals("Марсоход-Эксплорер", result.get(0).getName());
    }

    @Test
    public void testSearch_ThereAreItems_ShouldReturnItemForMission() {
        // Создание экипажа
        User engineer = createUser("Елена Гравитационная", "gravity@space.com");
        User pilot = createUser("Алексей Орбитальный", "orbital@space.com");

        // Добавление оборудования
        createItem("Лунный вездеход", "Транспорт для лунных миссий", engineer);
        createItem("Марсианский спутник", "Коммуникационный аппарат вЕздЕхОд для орбитАльных исследований", pilot);
        createItem("Солнечный зонд", "Исследовательский зонд для изучения Солнца", engineer);
        createItem("Орбитальный телескоп", "Телескоп для глубокого космоса", pilot);

        // Поиск по ключевым словам
        List<Item> rovers = itemRepository.search("вЕздЕхОд");
        List<Item> solarItems = itemRepository.search("сОлнечный");
        List<Item> orbitItems = itemRepository.search("орбита");

        // Проверка результатов
        assertEquals(2, rovers.size(), "Должны найтись лунный и марсианский роверы");
        assertEquals(1, solarItems.size(), "Должен найтись солнечный зонд");
        assertEquals(2, orbitItems.size(), "Должны найтись спутник и телескоп");
    }

    @Test
    public void testSearch_findAllByRequest_ShouldReturnItemForMission() {
        // Организация миссии
        User missionCommander = createUser("Сергей Звездный", "commander@nasa.gov");
        User technician = createUser("Ольга Инженерная", "engineer@nasa.gov");

        // Создание запроса на оборудование
        Request missionRequest = new Request();
        missionRequest.setDescription("Оборудование для лунной базы");
        missionRequest.setRequestor(technician);
        missionRequest.setCreated(LocalDateTime.now());
        itemRequestRepository.save(missionRequest);

        // Привязка оборудования к миссии
        createItem("Лунный модуль", "Посадочный модуль", missionCommander).setRequest(missionRequest);
        createItem("Скафандр", "Для выхода в открытый космос", technician);
        createItem("Солнечные батареи", "Энергоснабжение базы", technician).setRequest(missionRequest);

        // Поиск оборудования для миссии
        List<Item> missionEquipment = itemRepository.findAllByRequest(missionRequest);

        assertEquals(2, missionEquipment.size(), "Должны найтись модуль и батареи");
        assertTrue(missionEquipment.stream()
                .anyMatch(item -> item.getName().equals("Лунный модуль")));
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return userRepository.save(user);
    }

    private Item createItem(String name, String description, User owner) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setOwner(owner);
        item.setAvailable(true);
        return itemRepository.save(item);
    }
}