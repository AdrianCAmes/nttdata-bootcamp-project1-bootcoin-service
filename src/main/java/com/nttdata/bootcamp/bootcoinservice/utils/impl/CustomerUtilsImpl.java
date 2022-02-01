package com.nttdata.bootcamp.bootcoinservice.utils.impl;

import com.nttdata.bootcamp.bootcoinservice.config.Constants;
import com.nttdata.bootcamp.bootcoinservice.model.Customer;
import com.nttdata.bootcamp.bootcoinservice.model.dto.response.CustomerCustomerServiceResponseDTO;
import com.nttdata.bootcamp.bootcoinservice.model.dto.response.CustomerTypeCustomerServiceResponseDTO;
import com.nttdata.bootcamp.bootcoinservice.model.dto.response.PersonDetailsCustomerServiceResponseDTO;
import com.nttdata.bootcamp.bootcoinservice.utils.CustomerUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerUtilsImpl implements CustomerUtils {
    private final Constants constants;

    @Override
    public Customer customerCustomerServiceDTOToCustomer(CustomerCustomerServiceResponseDTO customerDTO) {
        if (customerDTO.getBusinessDetails() == null) {
            return Customer.builder()
                    .id(customerDTO.getId())
                    .identityNumber(customerDTO.getPersonDetails().getIdentityNumber())
                    .email(customerDTO.getPersonDetails().getEmail())
                    .mobileNumber(customerDTO.getPersonDetails().getMobileNumber())
                    .build();
        } else {
            return Customer.builder()
                    .id(customerDTO.getId())
                    .identityNumber(customerDTO.getBusinessDetails().getRuc())
                    .email(customerDTO.getBusinessDetails().getRepresentatives().get(0).getEmail())
                    .mobileNumber(customerDTO.getBusinessDetails().getRepresentatives().get(0).getMobileNumber())
                    .build();
        }
    }

    @Override
    public CustomerCustomerServiceResponseDTO customerToPersonalCustomerCustomerServiceResponseDTO(Customer customer) {
        return CustomerCustomerServiceResponseDTO.builder()
                .customerType(CustomerTypeCustomerServiceResponseDTO.builder()
                        .group(constants.getCustomerPersonalGroup())
                        .build())
                .personDetails(PersonDetailsCustomerServiceResponseDTO.builder()
                        .identityNumber(customer.getIdentityNumber())
                        .email(customer.getEmail())
                        .mobileNumber(customer.getMobileNumber())
                        .build())
                .build();
    }
}
