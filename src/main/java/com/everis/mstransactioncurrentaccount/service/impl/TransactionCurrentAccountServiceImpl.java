package com.everis.mstransactioncurrentaccount.service.impl;

import com.everis.mstransactioncurrentaccount.entity.CurrentAccount;
import com.everis.mstransactioncurrentaccount.entity.TransactionCurrentAccount;
import com.everis.mstransactioncurrentaccount.repository.TransactionCurrentAccountRepository;
import com.everis.mstransactioncurrentaccount.service.TransactionCurrentAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TransactionCurrentAccountServiceImpl implements TransactionCurrentAccountService {

	private final WebClient webClient;
	private final ReactiveCircuitBreaker reactiveCircuitBreaker;
	
	String uri = "http://localhost:8090/api/ms-current-account/currentAccount";
    
	public TransactionCurrentAccountServiceImpl(ReactiveResilience4JCircuitBreakerFactory circuitBreakerFactory) {
		this.webClient = WebClient.builder().baseUrl(this.uri).build();
		this.reactiveCircuitBreaker = circuitBreakerFactory.create("current");
	}
	
    @Autowired
    TransactionCurrentAccountRepository transactionCurrentAccountRepository;

    // Plan A - FindById
    @Override
    public Mono<CurrentAccount> findSavingAccountById(String id) {
		return reactiveCircuitBreaker.run(webClient.get().uri(this.uri + "/find/{id}",id).accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(CurrentAccount.class),
				throwable -> {
					return this.getDefaultCurrentAccount();
				});
    }
    
    // Plan A - Update
    @Override
    public Mono<CurrentAccount> updateSavingAccount(CurrentAccount sa) {
		return reactiveCircuitBreaker.run(webClient.put().uri(this.uri + "/update",sa.getId()).accept(MediaType.APPLICATION_JSON).syncBody(sa).retrieve().bodyToMono(CurrentAccount.class),
				throwable -> {
					return this.getDefaultCurrentAccount();
				});
    }
    
    
    // Plan B
  	public Mono<CurrentAccount> getDefaultCurrentAccount() {
  		Mono<CurrentAccount> currentAccount = Mono.just(new CurrentAccount("0", null, null,null,null,null,null,null, null, null));
  		return currentAccount;
  	}
    
	@Override
	public Flux<TransactionCurrentAccount> findByCurrentAccountId(String id) {
		return transactionCurrentAccountRepository.findByCurrentAccountId(id);
	}

  	
  	
  	
    @Override
    public Mono<TransactionCurrentAccount> create(TransactionCurrentAccount t) {
        return transactionCurrentAccountRepository.save(t);
    }

    @Override
    public Flux<TransactionCurrentAccount> findAll() {
        return transactionCurrentAccountRepository.findAll();
    }

    @Override
    public Mono<TransactionCurrentAccount> findById(String id) {
        return transactionCurrentAccountRepository.findById(id);
    }

    @Override
    public Mono<TransactionCurrentAccount> update(TransactionCurrentAccount t) {
        return transactionCurrentAccountRepository.save(t);
    }

    @Override
    public Mono<Boolean> delete(String t) {
        return transactionCurrentAccountRepository.findById(t)
                .flatMap(ca -> transactionCurrentAccountRepository.delete(ca).then(Mono.just(Boolean.TRUE)))
                .defaultIfEmpty(Boolean.FALSE);
    }

    @Override
    public Mono<Long> countMovements(String t) {
        return transactionCurrentAccountRepository.findByCurrentAccountId(t).count();
    }



}
