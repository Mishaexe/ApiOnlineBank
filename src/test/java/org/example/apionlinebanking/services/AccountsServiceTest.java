package org.example.apionlinebanking.services;

import java.lang.IllegalArgumentException;

import org.example.apionlinebanking.OperationsType;
import org.example.apionlinebanking.entity.Accounts;
import org.example.apionlinebanking.entity.Operations;
import org.example.apionlinebanking.repositories.AccountsRepository;
import org.example.apionlinebanking.repositories.OperationsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.naming.InsufficientResourcesException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountsServiceTest {

    @Mock
    private AccountsRepository repo;

    @Mock
    private OperationsService operationsService;

    @Mock
    private OperationsRepository operationsRepository;

    @InjectMocks
    private AccountsService accountsService;

    @Test
    void shouldReturnCorrectBalanceWhenAccountExists() {

        //GIVEN(ДАНО)

        Long accountId = 123L;
        BigDecimal expectedBalance = new BigDecimal("500.00");

        Accounts account = new Accounts();
        account.setIdUsers(accountId);
        account.setMoney(expectedBalance);

        when(repo.findById(accountId)).thenReturn(Optional.of(account));

        //WHEN(КОГДА)

        BigDecimal actualBalance = accountsService.getBalance(accountId);

        //THEN(ТОГДА)

        assertEquals(expectedBalance, actualBalance);
    }


    @Test
    public void testTakeMoneyWithNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> accountsService.takeMoney(1L, BigDecimal.valueOf(-10)));
    }


    @Test
    public void testTakeMoneyWithZeroAmount(){
        assertThrows(IllegalArgumentException.class, () -> accountsService.takeMoney(1L, BigDecimal.ZERO));
    }

    @Test
    public void testTakeAccountNotFound(){
        assertThrows(NoSuchElementException.class, () -> accountsService.takeMoney(999L, BigDecimal.TEN));
    }

    @Test
    public void testTakeMoneyInsufficientFunds() {
        Accounts accounts = new Accounts();
        accounts.setIdUsers(1L);
        accounts.setMoney(BigDecimal.valueOf(100));

        when(repo.findById(1L)).thenReturn(Optional.of(accounts));

        assertThrows(InsufficientResourcesException.class, () -> accountsService.takeMoney(1L, BigDecimal.valueOf(200L)));

        assertEquals(BigDecimal.valueOf(100), accounts.getMoney());
    }

    @Test
    public void testSuccessfulWithdrawal() throws InsufficientResourcesException {
        Accounts accounts = new Accounts();
        accounts.setIdUsers(1L);
        accounts.setMoney(BigDecimal.valueOf(100));

        when(repo.findById(1L)).thenReturn(Optional.of(accounts));

        BigDecimal newBalance = BigDecimal.valueOf(80);
        BigDecimal result = accountsService.takeMoney(1L, BigDecimal.valueOf(20));

        assertEquals(newBalance, result);
        verify(repo, times(1)).save(accounts);
    }

    @Test
    public void testOperationServiceIntegration() throws InsufficientResourcesException {
        Accounts account = new Accounts();
        account.setIdUsers(1L);
        account.setMoney(BigDecimal.valueOf(100));

        when(repo.findById(anyLong())).thenReturn(Optional.of(account));

        accountsService.takeMoney(1L, BigDecimal.valueOf(20));

        verify(operationsService, times(1)).save(eq(account), eq(OperationsType.WITHDRAW), any());
    }

    @Test
    public void testSuccessfulOperationsWithoutTimePeriod(){

        Long userId = 1L;
        LocalDateTime from = null;
        LocalDateTime to = null;

        List<Operations> expectedOperations  = Arrays.asList(
                new Operations(),
                new Operations(),
                new Operations()
        );

        when(operationsRepository.findAllByAccountsIdUsersOrderByCreatedAtAsc(userId)).thenReturn(expectedOperations);

        List<Operations> result = accountsService.getOperationList(userId, from, to);

        assertEquals(expectedOperations, result);

    }

    @Test
    public void testGetOperationsListWithDateRange(){

        Long userId = 1L;
        LocalDateTime from = LocalDateTime.of(2025, 12, 17, 22, 11);
        LocalDateTime to = LocalDateTime.of(2025, 12, 25, 22, 11);

        List<Operations> expectedOperations = Arrays.asList(
                new Operations(),
                new Operations(),
                new Operations()
        );

        when(operationsRepository.findByAccountsIdUsersAndCreatedAtBetweenOrderByCreatedAtAsc(userId, from, to)).thenReturn(expectedOperations);

        List<Operations> result = accountsService.getOperationList(userId, from, to);

        assertEquals(expectedOperations, result);
    }

    @Test
    public void testGetOperationListWithInvalidDateRange(){

        Long userId = 1L;
        LocalDateTime from = LocalDateTime.of(2025, 12, 17, 22, 11);
        LocalDateTime to = LocalDateTime.of(2025, 12, 25, 22, 11);


        assertThrows(IllegalArgumentException.class, () -> accountsService.getOperationList(userId, from ,to));


    }

    @Test
    public void testGetOperationListWithEqualDates(){

        Long userId = 1L;
        LocalDateTime from = LocalDateTime.of(2025, 12, 17, 22, 11);
        LocalDateTime to = LocalDateTime.of(2025, 12, 17, 22, 11);

        List<Operations> expectedOperations = Arrays.asList(
                new Operations(),
                new Operations(),
                new Operations()
        );

        when(operationsRepository.findByAccountsIdUsersAndCreatedAtBetweenOrderByCreatedAtAsc(userId, from, to)).thenReturn(expectedOperations);

        List<Operations> result = accountsService.getOperationList(userId, from, to);

        assertEquals(expectedOperations, result);

    }

    @Test
    public void testGetOperationListWithFirstDateNull(){

        Long userId = 1L;
        LocalDateTime from = null;
        LocalDateTime to = LocalDateTime.of(2025, 12, 17, 22, 11);

        List<Operations> expectedOperations = Arrays.asList(
                new Operations(),
                new Operations(),
                new Operations()
        );

        when(operationsRepository.findAllByAccountsIdUsersOrderByCreatedAtAsc(userId)).thenReturn(expectedOperations);

        List<Operations> result = accountsService.getOperationList(userId, from, to);

        assertEquals(expectedOperations, result);
    }

    @Test
    public void testGetOperationListWithSecondDateNull(){

        Long userId = 1L;
        LocalDateTime from = LocalDateTime.of(2025, 12, 17, 22, 11);
        LocalDateTime to = null;

        List<Operations> expectedOperations = Arrays.asList(
                new Operations(),
                new Operations(),
                new Operations()
        );

        when(operationsRepository.findAllByAccountsIdUsersOrderByCreatedAtAsc(userId)).thenReturn(expectedOperations);

        List<Operations> result = accountsService.getOperationList(userId, from, to);

        assertEquals(expectedOperations, result);
    }

    @Test
    public void testGetOperationListWithBothDateNull(){
        Long userId = 1L;
        LocalDateTime from = null;
        LocalDateTime to = null;

        List<Operations> expectedOperations = Arrays.asList(
                new Operations(),
                new Operations(),
                new Operations()
        );

        when(operationsRepository.findAllByAccountsIdUsersOrderByCreatedAtAsc(userId)).thenReturn(expectedOperations);

        List<Operations> result = accountsService.getOperationList(userId, from, to);

        assertEquals(expectedOperations, result);
    }

    @Test
    public void testSuccessfulTransfer(){

        Long fromUserId = 1L;
        Long toUserId = 2L;

        BigDecimal amount = BigDecimal.valueOf(100);

        Accounts from = new Accounts();
        from.setIdUsers(fromUserId);
        from.setMoney(BigDecimal.valueOf(200));

        Accounts to = new Accounts();
        to.setIdUsers(toUserId);
        to.setMoney(BigDecimal.valueOf(100));


        when(repo.findById(fromUserId)).thenReturn(Optional.of(from));
        when(repo.findById(toUserId)).thenReturn(Optional.of(to));

        accountsService.transferMoney(fromUserId, toUserId, amount);

        assertEquals(BigDecimal.valueOf(100), from.getMoney());
        assertEquals(BigDecimal.valueOf(200), to.getMoney());
    }

    @Test
    public void testTransferToSelf(){

        Long fromUserId = 1L;
        Long toUserId = 1L;
        BigDecimal amount = BigDecimal.valueOf(300);

        assertThrows(IllegalArgumentException.class, () -> accountsService.transferMoney(fromUserId, toUserId, amount));

    }

    @Test
    public void testInvalidUserIds(){
        Long fromUserId = -1L;
        Long toUserId = 0L;
        BigDecimal amount = BigDecimal.valueOf(200);

        assertThrows(IllegalArgumentException.class, () -> accountsService.transferMoney(fromUserId, toUserId, amount));


    }
    @Test
    public void testSenderNotFound(){
        Long fromUserId = 3L;
        Long toUserId = 2L;
        BigDecimal amount = BigDecimal.valueOf(300);

        when(repo.findById(fromUserId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> accountsService.transferMoney(fromUserId, toUserId, amount));
    }

    @Test
    public void testRecipientNotFount(){

        Long fromUserId = 3L;
        Long toUserId = 2L;
        BigDecimal amount = BigDecimal.valueOf(300);

        when(repo.findById(toUserId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> accountsService.transferMoney(fromUserId, toUserId, amount));

    }

    @Test
    public void testDisadvantageAmount(){
        Long fromUserId = 1L;
        Long toUserId = 2L;
        BigDecimal amount = BigDecimal.valueOf(300);

        Accounts from = new Accounts();
        from.setIdUsers(fromUserId);
        from.setMoney(BigDecimal.valueOf(200));

        Accounts to = new Accounts();
        to.setIdUsers(toUserId);
        to.setMoney(BigDecimal.valueOf(200));

        when(repo.findById(fromUserId)).thenReturn(Optional.of(from));
        when(repo.findById(toUserId)).thenReturn(Optional.of(to));

        assertThrows(RuntimeException.class, () -> accountsService.transferMoney(fromUserId, toUserId, amount));

    }

    @Test
    public void testSuccessfulBalanceUpdate(){
        Long fromUserId = 1L;
        Long toUserId = 2L;
        BigDecimal amount = BigDecimal.valueOf(100);

        Accounts from = new Accounts();
        from.setIdUsers(fromUserId);
        from.setMoney(BigDecimal.valueOf(200));

        Accounts to = new Accounts();
        to.setIdUsers(toUserId);
        to.setMoney(BigDecimal.valueOf(100));

        when(repo.findById(fromUserId)).thenReturn(Optional.of(from));
        when(repo.findById(toUserId)).thenReturn(Optional.of(to));

        accountsService.transferMoney(fromUserId, toUserId, amount);

        assertEquals(BigDecimal.valueOf(100), from.getMoney());
        assertEquals(BigDecimal.valueOf(200), to.getMoney());
    }

    @Test
    public void testOperationRegistration(){
        Long fromUserId = 1L;
        Long toUserId = 2L;
        BigDecimal amount = BigDecimal.valueOf(100);


        Accounts from = new Accounts();
        from.setIdUsers(fromUserId);
        from.setMoney(BigDecimal.valueOf(200));

        Accounts to = new Accounts();
        to.setIdUsers(toUserId);
        to.setMoney(BigDecimal.valueOf(100));

        when(repo.findById(fromUserId)).thenReturn(Optional.of(from));
        when(repo.findById(toUserId)).thenReturn(Optional.of(to));

        accountsService.transferMoney(fromUserId, toUserId, amount);


        verify(operationsService, times(1)).save(eq(from), eq(OperationsType.TRANSFER_OUT), eq(amount));
        verify(operationsService, times(1)).save(eq(to), eq(OperationsType.TRANSFER_IN), eq(amount));
    }
}



