package com.nttdata.bootcamp.bootcoinservice.model.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CustomerCustomerServiceResponseDTO {
    private String id;
    private CustomerTypeCustomerServiceResponseDTO customerType;
    private String status;
    private PersonDetailsCustomerServiceResponseDTO personDetails;
    private BusinessDetailsCustomerServiceResponseDTO businessDetails;
}