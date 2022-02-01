package com.nttdata.bootcamp.bootcoinservice.utils.impl;

import com.nttdata.bootcamp.bootcoinservice.model.Operation;
import com.nttdata.bootcamp.bootcoinservice.model.dto.request.OperationGeneratePurchaseRequestDTO;
import com.nttdata.bootcamp.bootcoinservice.utils.OperationUtils;
import org.springframework.stereotype.Component;

@Component
public class OperationUtilsImpl implements OperationUtils {
    @Override
    public Operation operationPurchaseRequestDTOToOperation(OperationGeneratePurchaseRequestDTO operationDTO) {
        return Operation.builder()
                .paymentType(operationDTO.getPaymentType())
                .amount(operationDTO.getAmount())
                .build();
    }
}
