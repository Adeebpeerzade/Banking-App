package com.bank.Banking.Application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferAmount {

    private String sourceAccount;

    private String destinationAccount;

    private BigDecimal amount;
}
