package com.everis.mstransactioncurrentaccount.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@Document("TransactionCurrentAccount")
@AllArgsConstructor
@NoArgsConstructor
public class TransactionCurrentAccount {
    @Id
    private String id;

    @NotNull
    private CurrentAccount currentAccount;

    @NotBlank
    private String transactionCode;

    @Valid
    private TypeTransaction typeTransaction;

    @NotNull
    private Double transactionAmount;

    private Double commissionAmount;

    private LocalDateTime transactionDateTime;
}
