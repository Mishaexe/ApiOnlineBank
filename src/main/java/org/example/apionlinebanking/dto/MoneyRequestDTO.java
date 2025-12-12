package org.example.apionlinebanking.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


import java.math.BigDecimal;

@Getter
@Setter
public class MoneyRequestDTO {

    @NotNull
    @DecimalMin(value = "0.01", message = "Сумма должна быть положительной")
    private BigDecimal amount;
}
