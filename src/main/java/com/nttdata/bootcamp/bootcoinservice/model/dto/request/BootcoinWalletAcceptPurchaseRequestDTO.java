package com.nttdata.bootcamp.bootcoinservice.model.dto.request;

import com.nttdata.bootcamp.bootcoinservice.model.Customer;
import com.nttdata.bootcamp.bootcoinservice.model.Operation;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class BootcoinWalletAcceptPurchaseRequestDTO {
    private String id;
    private String operationId;
}
