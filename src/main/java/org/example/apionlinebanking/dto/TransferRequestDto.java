package org.example.apionlinebanking.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequestDto {
    private Long fromUserId;
    private Long toUserId;
    private BigDecimal amount;
}
