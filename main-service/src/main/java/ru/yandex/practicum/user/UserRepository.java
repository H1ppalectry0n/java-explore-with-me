package ru.yandex.practicum.user;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface UserRepository extends JpaRepository<UserModel, Long> {

    List<UserModel> findByIdIn(List<Long> ids);

    List<UserModel> findByIdGreaterThan(long from, Pageable pageable);
}
