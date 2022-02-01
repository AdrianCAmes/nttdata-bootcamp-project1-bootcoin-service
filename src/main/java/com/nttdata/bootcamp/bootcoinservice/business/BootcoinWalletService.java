package com.nttdata.bootcamp.bootcoinservice.business;

import com.nttdata.bootcamp.bootcoinservice.model.BootcoinWallet;
import com.nttdata.bootcamp.bootcoinservice.model.dto.request.BootcoinWalletAcceptPurchaseRequestDTO;
import com.nttdata.bootcamp.bootcoinservice.model.dto.request.BootcoinWalletCreateRequestDTO;
import com.nttdata.bootcamp.bootcoinservice.model.dto.request.BootcoinWalletGeneratePurchaseRequestDTO;
import com.nttdata.bootcamp.bootcoinservice.model.dto.response.CustomerCustomerServiceResponseDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BootcoinWalletService {
    Mono<BootcoinWallet> create(BootcoinWalletCreateRequestDTO walletDTO);
    Mono<BootcoinWallet> findById(String id);
    Flux<BootcoinWallet> findAll();
    Mono<BootcoinWallet> update(BootcoinWallet walletDTO);
    Mono<BootcoinWallet> removeById(String id);
    Mono<CustomerCustomerServiceResponseDTO> findByIdCustomerService(String id);
    Mono<BootcoinWallet> generatePurchaseRequest(BootcoinWalletGeneratePurchaseRequestDTO walletDTO);
    Mono<BootcoinWallet> acceptPurchaseRequest(BootcoinWalletAcceptPurchaseRequestDTO walletDTO);
}
