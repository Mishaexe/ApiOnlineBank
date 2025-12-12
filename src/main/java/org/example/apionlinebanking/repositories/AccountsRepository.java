package org.example.apionlinebanking.repositories;

import org.example.apionlinebanking.entity.Accounts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountsRepository extends JpaRepository <Accounts, Long> {
}
