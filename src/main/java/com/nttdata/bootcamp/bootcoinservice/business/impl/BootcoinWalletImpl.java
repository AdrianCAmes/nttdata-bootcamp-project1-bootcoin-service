package com.nttdata.bootcamp.bootcoinservice.business.impl;

import com.nttdata.bootcamp.bootcoinservice.business.BootcoinWalletService;
import com.nttdata.bootcamp.bootcoinservice.config.Constants;
import com.nttdata.bootcamp.bootcoinservice.model.BootcoinWallet;
import com.nttdata.bootcamp.bootcoinservice.model.Customer;
import com.nttdata.bootcamp.bootcoinservice.model.Operation;
import com.nttdata.bootcamp.bootcoinservice.model.dto.request.BootcoinWalletAcceptPurchaseRequestDTO;
import com.nttdata.bootcamp.bootcoinservice.model.dto.request.BootcoinWalletCreateRequestDTO;
import com.nttdata.bootcamp.bootcoinservice.model.dto.request.BootcoinWalletGeneratePurchaseRequestDTO;
import com.nttdata.bootcamp.bootcoinservice.model.dto.response.CustomerCustomerServiceResponseDTO;
import com.nttdata.bootcamp.bootcoinservice.repository.BootcoinWalletRepository;
import com.nttdata.bootcamp.bootcoinservice.utils.BootcoinWalletUtils;
import com.nttdata.bootcamp.bootcoinservice.utils.CustomerUtils;
import com.nttdata.bootcamp.bootcoinservice.utils.OperationUtils;
import com.nttdata.bootcamp.bootcoinservice.utils.errorhandling.CircuitBreakerException;
import com.nttdata.bootcamp.bootcoinservice.utils.errorhandling.ElementBlockedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BootcoinWalletImpl implements BootcoinWalletService {

    private final BootcoinWalletRepository walletRepository;
    private final WebClient.Builder webClientBuilder;
    private final Constants constants;
    private final BootcoinWalletUtils walletUtils;
    private final CustomerUtils customerUtils;
    private final OperationUtils operationUtils;
    private final ReactiveCircuitBreaker customersServiceReactiveCircuitBreaker;
    private final ReactiveCircuitBreaker activesServiceReactiveCircuitBreaker;

    @Override
    public Mono<BootcoinWallet> create(BootcoinWalletCreateRequestDTO walletDTO) {
        log.info("Start of operation to create a credit");

        Mono<BootcoinWallet> createdWallet = findByIdCustomerService(walletDTO.getCustomer().getId())
                .switchIfEmpty(Mono.just(customerUtils.customerToPersonalCustomerCustomerServiceResponseDTO(walletDTO.getCustomer())))
                .flatMap(retrievedCustomer -> {
                    log.info("Validating customer");
                    return walletToCreateCustomerValidation(retrievedCustomer);
                })
                .flatMap(validatedCustomer -> {
                    BootcoinWallet walletToCreate = walletUtils.bootcoinWalletCreateRequestDTOToBootcoinWallet(walletDTO);
                    Customer customer = customerUtils.customerCustomerServiceDTOToCustomer(validatedCustomer);

                    walletToCreate.setCustomer(customer);
                    walletToCreate.setStatus(constants.getStatusActive());
                    walletToCreate.setBootcoinAmount(50.0);

                    log.info("Creating new wallet: [{}]", walletToCreate.toString());
                    return walletRepository.insert(walletToCreate);
                })
                .switchIfEmpty(Mono.error(new NoSuchElementException("Customer does not exist")));

        log.info("End of operation to create a credit");
        return createdWallet;
    }

    @Override
    public Mono<BootcoinWallet> findById(String id) {
        log.info("Start of operation to find a wallet by id");

        log.info("Retrieving wallet with id: [{}]", id);
        Mono<BootcoinWallet> retrievedWallet = walletRepository.findById(id);
        log.info("Wallet with id: [{}] was retrieved successfully", id);

        log.info("End of operation to find a wallet by id");
        return retrievedWallet;
    }

    @Override
    public Flux<BootcoinWallet> findAll() {

        log.info("Start of operation to retrieve all wallets");

        log.info("Retrieving all credits");
        Flux<BootcoinWallet> retrievedWallet = walletRepository.findAll();
        log.info("All wallets retrieved successfully");

        log.info("End of operation to retrieve all wallets");
        return retrievedWallet;
    }

    @Override
    public Mono<BootcoinWallet> update(BootcoinWallet walletDTO) {
        return null;
    }

    @Override
    public Mono<BootcoinWallet> removeById(String id) {
        log.info("Start of operation to remove a wallet");

        log.info("Deleting wallet with id: [{}]", id);
        Mono<BootcoinWallet> removedWallet = findById(id)
                .flatMap(retrievedWallet -> walletRepository.deleteById(retrievedWallet.getId()).thenReturn(retrievedWallet));
        log.info("Wallet with id: [{}] was successfully deleted", id);

        log.info("End of operation to remove a wallet");
        return removedWallet;
    }

    @Override
    public Mono<CustomerCustomerServiceResponseDTO> findByIdCustomerService(String id) {
        if (id == null || id.isBlank()) {
            log.info("Customer Id is null or empty");
            return Mono.empty();
        }

        log.info("Start of operation to retrieve customer with id [{}] from customer-info-service", id);

        log.info("Retrieving customer");
        String url = constants.getUrlPrefix() + constants.getGatewayServiceUrl() + "/" + constants.getCustomerInfoServiceUrl() + "/api/v1/customers/" + id;
        Mono<CustomerCustomerServiceResponseDTO> retrievedCustomer = customersServiceReactiveCircuitBreaker.run(
                webClientBuilder.build().get()
                        .uri(url)
                        .retrieve()
                        .onStatus(httpStatus -> httpStatus == HttpStatus.NOT_FOUND, clientResponse -> Mono.empty())
                        .bodyToMono(CustomerCustomerServiceResponseDTO.class),
                throwable -> {
                    log.warn("Error in circuit breaker call");
                    log.warn(throwable.getMessage());
                    return Mono.error(new CircuitBreakerException("Error in circuit breaker"));
                });
        log.info("Customer retrieved successfully");

        log.info("End of operation to retrieve customer with id: [{}]", id);
        return retrievedCustomer;
    }

    @Override
    public Mono<BootcoinWallet> generatePurchaseRequest(BootcoinWalletGeneratePurchaseRequestDTO walletDTO) {
        log.info("Start to save a new purchase request for the wallet with id: [{}]", walletDTO.getId());

        Mono<BootcoinWallet> updatedWallet = walletRepository.findById(walletDTO.getId())
                .flatMap(retrievedWallet -> storeOperationIntoWallet(walletDTO, retrievedWallet))
                .flatMap(transformedWallet -> {
                    log.info("Saving operation into wallet: [{}]", transformedWallet.toString());
                    return walletRepository.save(transformedWallet);
                })
                .switchIfEmpty(Mono.error(new NoSuchElementException("Wallet does not exist")));

        log.info("End to save a new purchase request for the wallet with id: [{}]", walletDTO.getId());
        return updatedWallet;
    }

    @Override
    public Mono<BootcoinWallet> acceptPurchaseRequest(BootcoinWalletAcceptPurchaseRequestDTO walletDTO) {
        log.info("Start of operation to accept purchase request");

        log.info("Looking for purchase request operation");
        Mono<BootcoinWallet> updatedWallet = findAll()
                .filter(retrievedWallet -> {
                    if (retrievedWallet.getOperations() != null) {
                        return retrievedWallet.getOperations()
                                .stream()
                                .anyMatch(operation -> operation.getId() != null &&
                                        operation.getId().contentEquals(walletDTO.getOperationId()));
                    }
                    return false;
                })
                .single()
                .flatMap(buyerWallet -> {
                    log.info("Validating consumption operation");
                    return acceptPurchaseValidation(walletDTO, buyerWallet);
                })
                .flatMap(buyerValidatedWallet -> {
                    Operation sellerOperation = Operation.builder()
                            .id(UUID.randomUUID().toString())
                            .status(constants.getOperationAcceptedStatus())
                            .time(new Date())
                            .operationType(constants.getOperationSellType())
                            .build();
                    ArrayList<Operation> buyerOperations = buyerValidatedWallet.getOperations();
                    ArrayList<Operation> buyerMappedOperations = new ArrayList<>(buyerOperations.stream()
                            .map(operation -> {
                                if (operation.getId() != null && operation.getId().contentEquals(walletDTO.getOperationId())) {
                                    Double bootcoinAmount = buyerValidatedWallet.getBootcoinAmount() + operation.getAmount();
                                    buyerValidatedWallet.setBootcoinAmount(bootcoinAmount);

                                    log.info("Updating buyer purchase operation");
                                    String operationNumber = UUID.randomUUID().toString();
                                    operation.setOperationNumber(operationNumber);
                                    operation.setStatus(constants.getOperationAcceptedStatus());

                                    log.info("Updating seller purchase operation");
                                    sellerOperation.setOperationNumber(operationNumber);
                                    sellerOperation.setPaymentType(operation.getPaymentType());
                                    sellerOperation.setAmount(operation.getAmount());
                                }
                                return operation;
                            })
                            .collect(Collectors.toList()));

                    buyerValidatedWallet.setOperations(buyerMappedOperations);
                    log.info("Updating purchase request into wallet: [{}]", buyerValidatedWallet.toString());
                    walletRepository.save(buyerValidatedWallet).subscribe();

                    Mono<BootcoinWallet> nestedUpdatedSellerWallet = findById(walletDTO.getId())
                            .flatMap(sellerWallet -> {
                                ArrayList<Operation> operations = sellerWallet.getOperations() == null ? new ArrayList<>() : sellerWallet.getOperations();
                                operations.add(sellerOperation);
                                sellerWallet.setOperations(operations);
                                sellerWallet.setBootcoinAmount(sellerWallet.getBootcoinAmount() - sellerOperation.getAmount());
                                return walletRepository.save(sellerWallet);
                            });

                    log.info("Creating accepting operation into seller wallet");
                    return nestedUpdatedSellerWallet;
                })
                .switchIfEmpty(Mono.error(new NoSuchElementException("Operation does not exist")));

        log.info("End of operation to accept purchase request");
        return updatedWallet;
    }

    //region Private Helper Functions
    private Mono<CustomerCustomerServiceResponseDTO> walletToCreateCustomerValidation(CustomerCustomerServiceResponseDTO customerFromMicroservice) {
        log.info("Customer exists");

        if (customerFromMicroservice.getStatus() != null &&
            customerFromMicroservice.getStatus().contentEquals(constants.getStatusBlocked())) {
            log.warn("Customer have blocked status");
            log.warn("Proceeding to abort create wallet");
            return Mono.error(new ElementBlockedException("The customer have blocked status"));
        }

        if (customerFromMicroservice.getCustomerType() != null &&
            customerFromMicroservice.getCustomerType().getGroup().contentEquals(constants.getCustomerPersonalGroup()) &&
            (customerFromMicroservice.getPersonDetails().getIdentityNumber() == null ||
             customerFromMicroservice.getPersonDetails().getMobileNumber() == null ||
             customerFromMicroservice.getPersonDetails().getEmail() == null)) {
            log.warn("Customer does not contain identity number, mobile number or email");
            log.warn("Proceeding to abort create wallet");
            return Mono.error(new IllegalArgumentException("Customer does not contain identity number, mobile number or email"));
        }

        if (customerFromMicroservice.getCustomerType() != null &&
            customerFromMicroservice.getCustomerType().getGroup().contentEquals(constants.getCustomerBusinessGroup()) &&
            (customerFromMicroservice.getBusinessDetails().getRuc() == null ||
             customerFromMicroservice.getBusinessDetails().getRepresentatives().get(0).getMobileNumber() == null ||
             customerFromMicroservice.getBusinessDetails().getRepresentatives().get(0).getEmail() == null)) {
            log.warn("Customer does not contain ruc, mobile number or email");
            log.warn("Proceeding to abort create wallet");
            return Mono.error(new IllegalArgumentException("Customer does not contain ruc, mobile number or email"));
        }

        log.info("Customer successfully validated");
        return Mono.just(customerFromMicroservice);
    }

    private Mono<BootcoinWallet> storeOperationIntoWallet(BootcoinWalletGeneratePurchaseRequestDTO walletDTO, BootcoinWallet walletInDatabase) {
        Operation operation = operationUtils.operationPurchaseRequestDTOToOperation(walletDTO.getOperation());
        operation.setId(UUID.randomUUID().toString());
        operation.setStatus(constants.getOperationPendingStatus());
        operation.setTime(new Date());
        operation.setOperationType(constants.getOperationBuyType());

        ArrayList<Operation> operations = walletInDatabase.getOperations() == null ? new ArrayList<>() : walletInDatabase.getOperations();
        operations.add(operation);

        walletInDatabase.setOperations(operations);

        return Mono.just(walletInDatabase);
    }

    private Mono<BootcoinWallet> acceptPurchaseValidation(BootcoinWalletAcceptPurchaseRequestDTO walletDTO, BootcoinWallet buyerWallet) {
        ArrayList<Operation> operations = buyerWallet.getOperations();
        Optional<Operation> validatedOperation = operations.stream()
                .filter(operation -> operation.getId() != null && operation.getId().contentEquals(walletDTO.getOperationId()))
                .findFirst();

        if(validatedOperation.isPresent()) {
            return findById(walletDTO.getId())
                    .flatMap(sellerWallet -> {
                        if (sellerWallet.getBootcoinAmount() - validatedOperation.get().getAmount() < 0) {
                            log.info("Account has insufficient funds");
                            log.warn("Proceeding to abort do operation");
                            return Mono.error(new IllegalArgumentException("The account has insufficient funds"));
                        } else {
                            log.info("Operation successfully validated");
                            return Mono.just(buyerWallet);
                        }
                    });
        } else {
            log.info("Could not retrieve operation");
            log.warn("Proceeding to abort do operation");
            return Mono.error(new IllegalArgumentException("Could not retrieve operation"));
        }
    }
    //endregion
}