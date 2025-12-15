package org.example.apionlinebanking.repositories;

import org.example.apionlinebanking.entity.Operations;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OperationsRepository extends JpaRepository <Operations, Long> {
    List<Operations> findAllByAccountsIdUsersOrderByCreatedAtAsc(Long userId);

    List<Operations> findByAccountsIdUsersAndCreatedAtBetweenOrderByCreatedAtAsc(
            Long userId, LocalDateTime from,
            LocalDateTime to
    );
}
