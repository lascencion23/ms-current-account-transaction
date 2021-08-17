package com.everis.mstransactioncurrentaccount.repository;

import com.everis.mstransactioncurrentaccount.entity.TransactionCurrentAccount;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface TransactionCurrentAccountRepository extends ReactiveMongoRepository<TransactionCurrentAccount, String> {

    Flux<TransactionCurrentAccount> findByCurrentAccountId(String id);
}
