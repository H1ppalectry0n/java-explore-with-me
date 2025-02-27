package ru.yandex.practicum.user;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.ConstraintException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAdminService {

    private final UserRepository userRepository;

    public UserDto createNewUser(@Nonnull UserDto userDto) {
        try {
            UserModel user = UserMapper.toModel(userDto);
            return UserMapper.toDto(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            throw new ConstraintException(e.getMessage());
        }
    }

    public void deleteUser(long userId) {
        try {
            userRepository.deleteById(userId);
        } catch (DataIntegrityViolationException e) {
            throw new ConstraintException(e.getMessage());
        }
    }

    public List<UserDto> getUsers(long from, int size, List<Long> ids) {
        List<UserModel> users;

        if (ids != null && !ids.isEmpty()) {
            users = userRepository.findByIdIn(ids);
        } else {
            users = userRepository.findByIdGreaterThan(from, PageRequest.of(0, size));
        }

        return users.stream().map(UserMapper::toDto).toList();
    }
}
