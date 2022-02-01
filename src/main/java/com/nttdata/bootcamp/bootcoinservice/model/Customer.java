package com.nttdata.bootcamp.bootcoinservice.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Customer {
    private String id;
    private String identityNumber;
    private String email;
    private String mobileNumber;
}