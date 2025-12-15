package org.example.apionlinebanking.dto;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TakeMoneyRequest {


    @PositiveOrZero(message = "Сумма должна быть больше или равна нулю.")
    private BigDecimal amount;
}
