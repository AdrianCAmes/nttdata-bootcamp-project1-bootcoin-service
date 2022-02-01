package com.nttdata.bootcamp.bootcoinservice.model.dto.request;

import com.nttdata.bootcamp.bootcoinservice.model.Customer;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class BootcoinWalletCreateRequestDTO {
    private Customer customer;
}
