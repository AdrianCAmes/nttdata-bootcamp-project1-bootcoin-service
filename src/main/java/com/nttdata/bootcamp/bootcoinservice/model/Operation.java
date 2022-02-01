package com.nttdata.bootcamp.bootcoinservice.model;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Operation {
    private String id;
    private String operationNumber;
    private String status;
    private Date time;
    private String operationType;
    private String paymentType;
    private Double amount;
}