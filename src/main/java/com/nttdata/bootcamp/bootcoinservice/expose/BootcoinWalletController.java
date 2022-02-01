package com.nttdata.bootcamp.bootcoinservice.expose;

import com.nttdata.bootcamp.bootcoinservice.business.BootcoinWalletService;
import com.nttdata.bootcamp.bootcoinservice.model.BootcoinWallet;
import com.nttdata.bootcamp.bootcoinservice.model.dto.request.BootcoinWalletAcceptPurchaseRequestDTO;
import com.nttdata.bootcamp.bootcoinservice.model.dto.request.BootcoinWalletCreateRequestDTO;
import com.nttdata.bootcamp.bootcoinservice.model.dto.request.BootcoinWalletGeneratePurchaseRequestDTO;
import com.nttdata.bootcamp.bootcoinservice.model.dto.response.CustomerCustomerServiceResponseDTO;
import com.nttdata.bootcamp.bootcoinservice.utils.errorhandling.CircuitBreakerException;
import com.nttdata.bootcamp.bootcoinservice.utils.errorhandling.ElementBlockedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1")
public class BootcoinWalletController {

    private final BootcoinWalletService walletService;

    //region CRUD Endpoints
    @GetMapping("/wallets")
    public Flux<BootcoinWallet> findAllWallets(){
        log.info("Get operation in /wallets");
        return walletService.findAll();
    }


    @GetMapping("/wallets/{id}")
    public Mono<ResponseEntity<BootcoinWallet>> findWalletById(@PathVariable("id") String id) {
        log.info("Get operation in /wallets/{}", id);
        return walletService.findById(id)
                .flatMap(retrievedWallet -> Mono.just(ResponseEntity.ok(retrievedWallet)))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @PostMapping("/wallets")
    public Mono<ResponseEntity<BootcoinWallet>> createWallet(@RequestBody BootcoinWalletCreateRequestDTO walletDTO) {
        log.info("Post operation in /wallets");
        return walletService.create(walletDTO)
                .flatMap(createdWallet -> Mono.just(ResponseEntity.status(HttpStatus.CREATED).body(createdWallet)))
                .onErrorResume(ElementBlockedException.class, error -> Mono.just(ResponseEntity.status(HttpStatus.LOCKED).build()))
                .onErrorResume(IllegalArgumentException.class, error -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build()))
                .onErrorResume(CircuitBreakerException.class, error -> Mono.just(ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).build()));
    }

    @DeleteMapping("/wallets/{id}")
    public Mono<ResponseEntity<BootcoinWallet>> deleteWallet(@PathVariable("id") String id) {
        log.info("Delete operation in /wallets/{}", id);
        return walletService.removeById(id)
                .flatMap(removedWallet -> Mono.just(ResponseEntity.ok(removedWallet)))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }
    //endregion

    //region Additional Repository Endpoints
    @GetMapping("customers-service/{id}")
    public Mono<ResponseEntity<CustomerCustomerServiceResponseDTO>> findByIdCustomerService(@PathVariable("id") String id) {
        log.info("Get operation in /customers-service/{}", id);
        return walletService.findByIdCustomerService(id)
                .flatMap(retrievedCustomer -> Mono.just(ResponseEntity.ok(retrievedCustomer)))
                .onErrorResume(CircuitBreakerException.class, error -> Mono.just(ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).build()));
    }
    //endregion

    //region UseCases
    @PostMapping("/wallets/operations/request")
    public Mono<ResponseEntity<BootcoinWallet>> generatePurchaseRequest(@RequestBody BootcoinWalletGeneratePurchaseRequestDTO walletDTO) {
        log.info("Post operation in /wallets/operations/request");
        return walletService.generatePurchaseRequest(walletDTO)
                .flatMap(updatedWallet -> Mono.just(ResponseEntity.status(HttpStatus.CREATED).body(updatedWallet)))
                .onErrorResume(NoSuchElementException.class, error -> Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build()));
    }


    @PostMapping("/wallets/operations/accept")
    public Mono<ResponseEntity<BootcoinWallet>> acceptPurchaseRequest(@RequestBody BootcoinWalletAcceptPurchaseRequestDTO walletDTO) {
        log.info("Post operation in /wallets/operations/request");
        return walletService.acceptPurchaseRequest(walletDTO)
                .flatMap(updatedWallet -> Mono.just(ResponseEntity.status(HttpStatus.CREATED).body(updatedWallet)))
                .onErrorResume(IllegalArgumentException.class, error -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build()))
                .onErrorResume(NoSuchElementException.class, error -> Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build()));
    }
    //endregion
}
