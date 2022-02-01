package com.nttdata.bootcamp.bootcoinservice.model;

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
@Document(collection = "bootcoinwallet")
public class BootcoinWallet {
    @Id
    private String id;
    private String status;
    private Customer customer;
    private Double bootcoinAmount;
    private ArrayList<Operation> operations;
}
