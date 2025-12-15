package org.example.apionlinebanking.controllers;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import org.example.apionlinebanking.dto.MoneyRequestDTO;
import org.example.apionlinebanking.dto.TakeMoneyRequest;
import org.example.apionlinebanking.dto.TransferRequestDto;
import org.example.apionlinebanking.entity.Accounts;
import org.example.apionlinebanking.entity.Operations;
import org.example.apionlinebanking.services.AccountsService;
import org.example.apionlinebanking.services.OperationsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.InsufficientResourcesException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/accounts")
public class AccountsController {

    private final AccountsService accountsService;
    private final OperationsService operationsService;


    public AccountsController(AccountsService accountsService, OperationsService operationsService){
        this.accountsService = accountsService;
        this.operationsService = operationsService;
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
    @GetMapping("/{id}/getOperationList")
    public List<Operations> getOperationList(@PathVariable Long id, @RequestParam(required = false)@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime from, @RequestParam(required = false)@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime to){
        return accountsService.getOperationList(id, from, to);
    }

    @PostMapping("/transfer")
    public void transferMoney(@RequestBody TransferRequestDto dto){
        accountsService.transferMoney(dto.getFromUserId(), dto.getToUserId(), dto.getAmount());
    }
}
