package com.bank.Banking.Application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class UserRequest {
    private String firstName;

    private String secondName;

    private String lastName;

    private String otherName;

    private String address;

    private String gender;

    private String stateOfOrigin;

    private String email;

    private String phoneNumber;

    private String alternativePhoneNumber;


}
