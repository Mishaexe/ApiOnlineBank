package org.example.apionlinebanking.services;

import org.example.apionlinebanking.entity.Accounts;
import org.example.apionlinebanking.repositories.AccountsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.InsufficientResourcesException;
import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class AccountsService {

    private final AccountsRepository repo;

    @Autowired
    public AccountsService(AccountsRepository repo){
        this.repo = repo;
    }




    @Transactional(readOnly = true)
    public BigDecimal getBalance(Long id){
        Optional<Accounts> accounts = repo.findById(id);
        return accounts.map(Accounts::getMoney).orElseThrow(() -> new RuntimeException("Account not found with id:" + id));
    }

    @Transactional
    public Accounts putMoney(Long id, BigDecimal amount){
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Сумма должна быть положительной");
        }

        Accounts accounts = repo.findById(id).orElseThrow(()-> new NoSuchElementException("Счет не найден " + id));

        BigDecimal current = accounts.getMoney() != null ? accounts.getMoney() : BigDecimal.ZERO;
        accounts.setMoney(current.add(amount));

        return repo.save(accounts);
    }

    public BigDecimal takeMoney(Long id, BigDecimal decimal) throws InsufficientResourcesException {
        if (decimal == null || decimal.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Сумма должна быть положительной");
        }

        Accounts accounts = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Счет не найден " + id));

        BigDecimal currentBalance = accounts.getMoney();

        if (currentBalance.compareTo(decimal) < 0){
            throw new InsufficientResourcesException("Недостаточно средств на счете");
        }

        BigDecimal newBalance = currentBalance.subtract(decimal);

        accounts.setMoney(newBalance);

        repo.save(accounts);
        
        return newBalance;

    }
}
