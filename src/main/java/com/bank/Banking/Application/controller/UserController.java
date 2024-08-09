package com.bank.Banking.Application.controller;


import com.bank.Banking.Application.dto.*;
import com.bank.Banking.Application.service.impl.UserService;
import com.bank.Banking.Application.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping
    public BankResponse createAccount(@RequestBody UserRequest userRequest){
        return userService.createAccount(userRequest);
    }

    @GetMapping("balanceEnquiry")
    public BankResponse balanceEnquiry(@RequestBody EnquiryRequest enquiryRequest) {
        return userService.balanceEnquiry(enquiryRequest);
    }

    @GetMapping("nameEnquiry")
    public String nameEnquiry(@RequestBody EnquiryRequest enquiryRequest) {
        return userService.nameEnquiry(enquiryRequest);
    }

    @PostMapping("credit")
    public BankResponse createAccount(@RequestBody CreditDebitRequest userRequest){
        return userService.creditAccount(userRequest);
    }

    @PostMapping("debit")
    public BankResponse debitAmount(@RequestBody CreditDebitRequest userRequest){
        return userService.debitAccount(userRequest);
    }

    @PostMapping("transfer")
    public BankResponse transferAmount(@RequestBody TransferAmount userRequest){
        return userService.transfer(userRequest);
    }
}
