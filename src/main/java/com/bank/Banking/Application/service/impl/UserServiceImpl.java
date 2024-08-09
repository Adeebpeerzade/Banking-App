package com.bank.Banking.Application.service.impl;

import com.bank.Banking.Application.dto.*;
import com.bank.Banking.Application.entity.User;
import com.bank.Banking.Application.repository.UserRepository;
import com.bank.Banking.Application.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailService emailService;


    @Override
    public BankResponse createAccount(UserRequest userRequest) {

        if (userRepository.existsByEmail(userRequest.getEmail())) {

            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();


        }
        User newUser = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .otherName(userRequest.getOtherName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtils.generatedAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .email(userRequest.getEmail())
                .phoneNumber(userRequest.getPhoneNumber())
                .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                .status("ACTIVE")
                .build();

        User savedUser = userRepository.save(newUser);
        //Send Email Alert.
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("Account Creation")
                .messageBody("Congrats! Account has been created.\nYour Account Details: \n" +
                        "Account Name: " + savedUser.getFirstName() + " " + savedUser.getLastName() + "\nAccount No: " + savedUser.getAccountNumber())
                .build();

        emailService.sendEmailAlert(emailDetails);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_CODE)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(savedUser.getAccountBalance())
                        .accountNumber(savedUser.getAccountNumber())
                        .accountName(savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName())
                        .build())
                .build();
    }

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest enquiryRequest) {
        //check if the provided account number exists in db
        boolean isAccountExists = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());
        if (!isAccountExists) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User foundUser = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(foundUser.getAccountBalance())
                        .accountNumber(enquiryRequest.getAccountNumber())
                        .accountName(foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName())
                        .build())
                .build();

    }

    @Override
    public String nameEnquiry(EnquiryRequest enquiryRequest) {
        boolean isAccountExists = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());
        if (!isAccountExists) {
            return AccountUtils.ACCOUNT_EXISTS_MESSAGE;
        }
        User foundUser = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
        return foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName();


    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest request) {
        boolean isAccountExists = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExists) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User userToCredit = userRepository.findByAccountNumber(request.getAccountNumber());
        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmount()));
        userRepository.save(userToCredit);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDITED_CODE)
                .responseMessage(AccountUtils.ACCOUNT_CREDITED_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(userToCredit.getAccountBalance())
                        .accountNumber(userToCredit.getAccountNumber())
                        .accountName(userToCredit.getFirstName() + " " + userToCredit.getLastName() + " " + userToCredit.getOtherName())
                        .build())
                .build();

    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest request) {
        boolean isAccountExists = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExists) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User userToDebit = userRepository.findByAccountNumber(request.getAccountNumber());
        BigDecimal accountBalance = userToDebit.getAccountBalance();
        BigDecimal amountToDebit = request.getAmount();

        if (amountToDebit.compareTo(accountBalance) > 0) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_LOW_BALANCE_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_LOW_BALANCE_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountBalance(userToDebit.getAccountBalance())
                            .accountNumber(userToDebit.getAccountNumber())
                            .accountName(userToDebit.getFirstName() + " " + userToDebit.getLastName() + " " + userToDebit.getOtherName())
                            .build())
                    .build();
        } else {
            userToDebit.setAccountBalance(accountBalance.subtract(amountToDebit));
            userRepository.save(userToDebit);
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DEBITED_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_DEBITED_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountBalance(userToDebit.getAccountBalance())
                            .accountNumber(userToDebit.getAccountNumber())
                            .accountName(userToDebit.getFirstName() + " " + userToDebit.getLastName() + " " + userToDebit.getOtherName())
                            .build())
                    .build();
        }
    }

    @Override
    public BankResponse transfer(TransferAmount request) {
        boolean isDestinationAccountExists = userRepository.existsByAccountNumber(request.getDestinationAccount());

        if (!isDestinationAccountExists) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User sourceAccountUser = userRepository.findByAccountNumber(request.getSourceAccount());


        if (request.getAmount().compareTo(sourceAccountUser.getAccountBalance()) > 0) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_LOW_BALANCE_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_LOW_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        sourceAccountUser.setAccountBalance(sourceAccountUser.getAccountBalance().subtract(request.getAmount()));
        userRepository.save(sourceAccountUser);
        EmailDetails debitAlert = EmailDetails.builder()
                .subject("DEBIT ALERT")
                .recipient(sourceAccountUser.getEmail())
                .messageBody("The sum of " + request.getAmount() + "has been debited. Your account balance is " + sourceAccountUser.getAccountBalance())
                .build();

        emailService.sendEmailAlert(debitAlert);

        User destinationAccountUser = userRepository.findByAccountNumber(request.getDestinationAccount());
        destinationAccountUser.setAccountBalance(destinationAccountUser.getAccountBalance().add(request.getAmount()));
        userRepository.save(destinationAccountUser);

        EmailDetails creditAlert = EmailDetails.builder()
                .subject("CREDIT ALERT")
                .recipient(destinationAccountUser.getEmail())
                .messageBody("The sum of " + request.getAmount() + "has been credited by" + sourceAccountUser.getFirstName() + "Your account balance is " + destinationAccountUser.getAccountBalance())
                .build();

        emailService.sendEmailAlert(creditAlert);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_TRANSFER_CODE)
                .responseMessage(AccountUtils.ACCOUNT_TRANSFER_MESSAGE)
                .accountInfo(null)
                .build();


    }
}
