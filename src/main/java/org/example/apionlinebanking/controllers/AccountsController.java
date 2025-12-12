package org.example.apionlinebanking.controllers;

import jakarta.validation.Valid;
import org.example.apionlinebanking.dto.MoneyRequestDTO;
import org.example.apionlinebanking.dto.TakeMoneyRequest;
import org.example.apionlinebanking.entity.Accounts;
import org.example.apionlinebanking.services.AccountsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.InsufficientResourcesException;
import java.math.BigDecimal;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/accounts")
public class AccountsController {

    private final AccountsService accountsService;

    public AccountsController(AccountsService accountsService){
        this.accountsService = accountsService;
    }

   @GetMapping("/{id}/getBalance")
    public BigDecimal getUserBalance(@PathVariable Long id){
        return accountsService.getBalance(id);
   }

   @PutMapping("/{id}/putMoney")
    public Accounts putMoney(@PathVariable Long id, @RequestBody @Valid MoneyRequestDTO request){
       if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
           throw new IllegalArgumentException("Сумма должна быть положительной");
       }
       return accountsService.putMoney(id, request.getAmount());

    }


    @PostMapping("/{id}/takeMoney")
    public ResponseEntity<?> takeMoney(@PathVariable Long id, @RequestBody @Valid TakeMoneyRequest request){
        try {
            BigDecimal newBalance = accountsService.takeMoney(id, request.getAmount());
            return ResponseEntity.ok(newBalance);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (InsufficientResourcesException e){
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    //getOperationList

    //transferMoney
}
