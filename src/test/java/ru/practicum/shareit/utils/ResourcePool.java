package ru.practicum.shareit.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Slf4j
public class ResourcePool {

    public static Resource createUserRequest =
            new ClassPathResource("json/user-controller-data/create/create-user-request-dto.json");

    public static Resource createdUserDto =
            new ClassPathResource("json/user-controller-data/create/saved-user-dto.json");

    public static Resource usersDtoList =
            new ClassPathResource("json/user-controller-data/find/found-users-dto-list.json");

    public static Resource updateNameUserRequest =
            new ClassPathResource("json/user-controller-data/update/update-name-user-request-dto.json");

    public static Resource updatedNameUsersDto =
            new ClassPathResource("json/user-controller-data/update/updated-name-user-dto.json");

    private static ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    public static final String ERROR_IO = "Ошибка при получении данных из файла-ресурса";

    public static <T> T read(Resource resource, Class<T> objectClass) {
        try {
            return mapper.readValue(resource.getInputStream(), objectClass);
        } catch (IOException e) {
            log.error(ERROR_IO, e);
            throw new RuntimeException(e);
        }
    }

    public static <T> T read(Resource resource, TypeReference<T> tr) {
        try {
            return mapper.readValue(resource.getInputStream(), tr);
        } catch (IOException e) {
            log.error(ERROR_IO, e);
            throw new RuntimeException(e);
        }
    }
}
