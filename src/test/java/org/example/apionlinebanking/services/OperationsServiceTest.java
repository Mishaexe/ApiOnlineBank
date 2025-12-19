package org.example.apionlinebanking.services;

import org.example.apionlinebanking.OperationsType;
import org.example.apionlinebanking.entity.Accounts;
import org.example.apionlinebanking.repositories.OperationsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OperationsServiceTest {

    @Mock
    private OperationsRepository operationsRepository;

    @InjectMocks
    private OperationsService operationsService;

    @Test
    void shouldSaveOperationWithCorrectData() {

        Accounts accounts = new Accounts();
        OperationsType type = OperationsType.DEPOSIT;
        BigDecimal amount = new BigDecimal("100.50");


        operationsService.save(accounts, type, amount);


        verify(operationsRepository, times(1)).save(argThat(operation -> {
            return operation.getAccounts().equals(accounts) &&
                    operation.getType() == type &&
                    operation.getAmount().compareTo(amount) == 0;
        }));

    }
}
