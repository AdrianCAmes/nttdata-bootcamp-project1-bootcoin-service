package com.nttdata.bootcamp.bootcoinservice.utils;

import com.nttdata.bootcamp.bootcoinservice.model.Customer;
import com.nttdata.bootcamp.bootcoinservice.model.dto.response.CustomerCustomerServiceResponseDTO;

public interface CustomerUtils {
    Customer customerCustomerServiceDTOToCustomer(CustomerCustomerServiceResponseDTO customerDTO);
    CustomerCustomerServiceResponseDTO customerToPersonalCustomerCustomerServiceResponseDTO(Customer customer);
}
