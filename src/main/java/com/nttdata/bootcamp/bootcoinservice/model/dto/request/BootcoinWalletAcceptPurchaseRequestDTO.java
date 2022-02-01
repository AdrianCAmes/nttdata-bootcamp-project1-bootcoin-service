package com.nttdata.bootcamp.bootcoinservice.model.dto.request;
import lombok.*;

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
