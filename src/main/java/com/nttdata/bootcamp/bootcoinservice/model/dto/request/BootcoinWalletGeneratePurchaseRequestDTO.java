package com.nttdata.bootcamp.bootcoinservice.model.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class BootcoinWalletGeneratePurchaseRequestDTO {
    private String id;
    private OperationGeneratePurchaseRequestDTO operation;
}
