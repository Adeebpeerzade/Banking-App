package com.bank.Banking.Application.utils;

import java.time.Year;

public class AccountUtils {

    public static final String ACCOUNT_EXISTS_CODE = "001";

    public static final String ACCOUNT_EXISTS_MESSAGE = "This user already has an account Created";

    public static final String ACCOUNT_CREATION_MESSAGE = "Account has been successfully created";

    public static final String ACCOUNT_CREATION_CODE = "002";

    public static final String ACCOUNT_NOT_EXISTS_CODE = "003";

    public static final String ACCOUNT_NOT_EXISTS_MESSAGE = "User with provided account number does not exist";

    public static final String ACCOUNT_CREDITED_CODE = "004";

    public static final String ACCOUNT_CREDITED_MESSAGE = "User account credited successfully";

    public static final String ACCOUNT_DEBITED_CODE = "005";

    public static final String ACCOUNT_DEBITED_MESSAGE = "User account debited successfully";

    public static final String ACCOUNT_LOW_BALANCE_CODE = "006";

    public static final String ACCOUNT_LOW_BALANCE_MESSAGE = "Insufficient balance!";

    public static final String ACCOUNT_TRANSFER_CODE = "007";

    public static final String ACCOUNT_TRANSFER_MESSAGE = "Transfer is successfully done!";



    public static String generatedAccountNumber() {




        Year currentYear = Year.now();

        int min = 100000;

        int max = 999999;

        int randomNumber = (int) Math.floor(Math.random() * (max - min + 1) + min);

        String year = String.valueOf(currentYear);

        String randomAccNumber = String.valueOf(randomNumber);

        StringBuilder accountNumber = new StringBuilder();

        return accountNumber.append(year).append(randomAccNumber).toString();
    }
}
