package org.example.apionlinebanking.services;

import org.example.apionlinebanking.OperationsType;
import org.example.apionlinebanking.entity.Accounts;
import org.example.apionlinebanking.entity.Operations;
import org.example.apionlinebanking.repositories.AccountsRepository;
import org.example.apionlinebanking.repositories.OperationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.InsufficientResourcesException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class AccountsService {

    private final AccountsRepository repo;
    private final OperationsService operationsService;
    private final OperationsRepository operationsRepository;

    @Autowired
    public AccountsService(AccountsRepository repo, OperationsService operationsService, OperationsRepository operationsRepository) {
        this.repo = repo;
        this.operationsService = operationsService;
        this.operationsRepository = operationsRepository;
    }


    @Transactional(readOnly = true)
    public BigDecimal getBalance(Long id) {
        Optional<Accounts> accounts = repo.findById(id);
        return accounts.map(Accounts::getMoney).orElseThrow(() -> new RuntimeException("Account not found with id:" + id));
    }

    @Transactional
    public Accounts putMoney(Long id, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма должна быть положительной");
        }

        Accounts accounts = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Счет не найден " + id));

        BigDecimal current = accounts.getMoney() != null ? accounts.getMoney() : BigDecimal.ZERO;
        accounts.setMoney(current.add(amount));

        operationsService.save(accounts, OperationsType.DEPOSIT, amount);

        return repo.save(accounts);

    }

    @Transactional
    public BigDecimal takeMoney(Long id, BigDecimal decimal) throws InsufficientResourcesException {
        if (decimal == null || decimal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма должна быть положительной");
        }

        Accounts accounts = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Счет не найден " + id));

        BigDecimal currentBalance = accounts.getMoney();

        if (currentBalance.compareTo(decimal) < 0) {
            throw new InsufficientResourcesException("Недостаточно средств на счете");
        }

        BigDecimal newBalance = currentBalance.subtract(decimal);

        accounts.setMoney(newBalance);

        operationsService.save(accounts, OperationsType.WITHDRAW, decimal);


        repo.save(accounts);

        return newBalance;

    }

    @Transactional
    public List<Operations> getOperationList(Long userId, LocalDateTime from, LocalDateTime to) {
        if (from == null || to == null) {
            return operationsRepository.findAllByAccountsIdUsersOrderByCreatedAtAsc(userId);
        }


        if (from.isAfter(to)) {
            throw new IllegalArgumentException("Начальная дата должна быть меньше или равна конечной.");
        }

        return operationsRepository.findByAccountsIdUsersAndCreatedAtBetweenOrderByCreatedAtAsc(userId, from, to);
    }

    @Transactional
    public void transferMoney(Long fromUserId, Long toUserId, BigDecimal amount){

        if (fromUserId.equals(toUserId)){
            throw new IllegalArgumentException("Нельзя переводить самому себе");
        }

        if (fromUserId <= 0 || toUserId <= 0){
            throw new IllegalArgumentException("Id получателя и отправителяне могут быть равны или меньше нуля");
        }

        Accounts from = repo.findById(fromUserId).orElseThrow(() -> new RuntimeException("Отправитель не найден"));

        Accounts to = repo.findById(toUserId).orElseThrow(() -> new RuntimeException("Приниматель не найден"));

        if (from.getMoney().compareTo(amount) < 0){
            throw new RuntimeException("Недостаточно средств");
        }

        from.setMoney(from.getMoney().subtract(amount));
        to.setMoney(to.getMoney().add(amount));

        repo.save(from);
        repo.save(to);

        operationsService.save(from, OperationsType.TRANSFER_OUT, amount);
        operationsService.save(to, OperationsType.TRANSFER_IN, amount);


    }

}