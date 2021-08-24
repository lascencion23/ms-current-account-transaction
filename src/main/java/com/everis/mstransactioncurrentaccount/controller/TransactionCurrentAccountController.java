package com.everis.mstransactioncurrentaccount.controller;

import com.everis.mstransactioncurrentaccount.entity.TransactionCurrentAccount;
import com.everis.mstransactioncurrentaccount.service.TransactionCurrentAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RefreshScope
@RestController
@RequestMapping("/transactionCurrentAccount")
public class TransactionCurrentAccountController {

    @Autowired
    TransactionCurrentAccountService transactionCurrentAccountService;

    @GetMapping("list")
    public Flux<TransactionCurrentAccount> findAll(){
        return transactionCurrentAccountService.findAll();
    }

    @GetMapping("/find/{id}")
    public Mono<TransactionCurrentAccount> findById(@PathVariable String id){
        return transactionCurrentAccountService.findById(id);
    }

    @GetMapping("/findByCurrentAccountId/{id}")
    public Flux<TransactionCurrentAccount> findByCurrentAccountId(String id){
    	return transactionCurrentAccountService.findByCurrentAccountId(id);
    }
    
    @PostMapping("/create")
    public Mono<ResponseEntity<TransactionCurrentAccount>> create(@RequestBody TransactionCurrentAccount transaction){
        // VERIFICAMOS SI EXISTE EL CLIENTE
        return transactionCurrentAccountService.countMovements(transaction.getCurrentAccount().getId()) // N° Movimientos actuales
                .flatMap(cnt -> {
                    return transactionCurrentAccountService.findSavingAccountById(transaction.getCurrentAccount().getId()) // Busco la Cuenta Bancaria
                            .flatMap(ca -> {
                                switch (transaction.getTypeTransaction()){
                                    case DEPOSIT: ca.setBalance(ca.getBalance() + transaction.getTransactionAmount()); break;
                                    case DRAFT: ca.setBalance(ca.getBalance() - transaction.getTransactionAmount()); break;
                                }
                                if(cnt >= ca.getFreeTransactions() ){
                                    ca.setBalance(ca.getBalance() - ca.getCommissionTransactions());
                                    transaction.setCommissionAmount(ca.getCommissionTransactions());
                                }else{
                                    transaction.setCommissionAmount(0.0);
                                }
                                return transactionCurrentAccountService.updateSavingAccount(ca)
                                        .flatMap(saveCa -> {
                                            transaction.setCurrentAccount(saveCa);
                                            transaction.setTransactionDateTime(LocalDateTime.now());
                                            return transactionCurrentAccountService.create(transaction); // Mono<TransactionCurrentAccount>
                                        }); // Mono<CurrentAccount>
                            });
                })

                .map(tca -> new ResponseEntity<>(tca, HttpStatus.CREATED))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

    }

    @PutMapping("/update")
    public Mono<ResponseEntity<TransactionCurrentAccount>> update(@RequestBody TransactionCurrentAccount transaction) {
        return transactionCurrentAccountService.findById(transaction.getId())
                .flatMap(tcaDB -> transactionCurrentAccountService.findSavingAccountById(transaction.getCurrentAccount().getId())
                                .flatMap(ca -> {
                                    switch (transaction.getTypeTransaction()){
                                        case DEPOSIT: ca.setBalance(ca.getBalance() - tcaDB.getTransactionAmount() + transaction.getTransactionAmount());
                                            return transactionCurrentAccountService.updateSavingAccount(ca).flatMap(caUpdate -> {
                                                                                                transaction.setCurrentAccount(caUpdate);
                                                                                                transaction.setTransactionDateTime(LocalDateTime.now());
                                                                                                return transactionCurrentAccountService.create(transaction); // Mono<TransactionCurrentAccount>
                                                                                            }); // Mono<CurrentAccount>
                                        case DRAFT: ca.setBalance(ca.getBalance() + tcaDB.getTransactionAmount() - transaction.getTransactionAmount());
                                            return transactionCurrentAccountService.updateSavingAccount(ca).flatMap(caUpdate -> {
                                                                                                transaction.setCurrentAccount(caUpdate);
                                                                                                transaction.setTransactionDateTime(LocalDateTime.now());
                                                                                                return transactionCurrentAccountService.create(transaction); // Mono<TransactionCurrentAccount>
                                                                                            }); // Mono<CurrentAccount>
                                        default: return Mono.empty();
                                    }
                                })
                )
                .map(tca -> new ResponseEntity<>(tca, HttpStatus.CREATED))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

    }

    @DeleteMapping("/delete/{id}")
    public Mono<ResponseEntity<String>> delete(@PathVariable String id) {
        return transactionCurrentAccountService.delete(id)
                .filter(deleteCustomer -> deleteCustomer)
                .map(deleteCustomer -> new ResponseEntity<>("Transaction Deleted", HttpStatus.ACCEPTED))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}
