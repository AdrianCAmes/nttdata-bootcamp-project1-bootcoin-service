package com.nttdata.bootcamp.bootcoinservice.repository;

import com.nttdata.bootcamp.bootcoinservice.model.BootcoinWallet;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BootcoinWalletRepository extends ReactiveMongoRepository<BootcoinWallet, String> {
}
