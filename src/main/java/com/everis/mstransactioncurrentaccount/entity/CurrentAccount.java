package com.everis.mstransactioncurrentaccount.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrentAccount {
    private String id;

    private Customer customer;

    private String accountNumber;

    private List<Person> holders;

    private List<Person> signers;

    private Integer freeTransactions;

    private Double commissionTransactions;

    private Double commissionMaintenance;

    private Double balance;

    private LocalDateTime date; 
}
