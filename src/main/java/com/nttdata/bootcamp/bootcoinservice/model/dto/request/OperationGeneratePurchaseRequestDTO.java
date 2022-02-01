package com.nttdata.bootcamp.bootcoinservice.model.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class OperationGeneratePurchaseRequestDTO {
    private String paymentType;
    private Double amount;
}