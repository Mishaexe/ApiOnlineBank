package org.example.apionlinebanking.services;

import org.example.apionlinebanking.OperationsType;
import org.example.apionlinebanking.entity.Accounts;
import org.example.apionlinebanking.entity.Operations;
import org.example.apionlinebanking.repositories.OperationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OperationsService {

   private final OperationsRepository operationsRepository;

   @Autowired
   public OperationsService(OperationsRepository operationsRepository){
       this.operationsRepository = operationsRepository;
   }
    public void save(Accounts accounts, OperationsType type, BigDecimal amount){
       Operations operations = new Operations();
       operations.setAccounts(accounts);
       operations.setType(type);
       operations.setAmount(amount);

       operationsRepository.save(operations);
   }
}
