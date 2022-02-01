package com.nttdata.bootcamp.bootcoinservice.utils;

import com.nttdata.bootcamp.bootcoinservice.model.Operation;
import com.nttdata.bootcamp.bootcoinservice.model.dto.request.OperationGeneratePurchaseRequestDTO;

public interface OperationUtils {
    Operation operationPurchaseRequestDTOToOperation(OperationGeneratePurchaseRequestDTO operationDTO);
}
