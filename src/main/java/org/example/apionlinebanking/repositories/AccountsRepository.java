package org.example.apionlinebanking.repositories;

import org.example.apionlinebanking.entity.Accounts;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AccountsRepository extends JpaRepository <Accounts, Long> {
}
