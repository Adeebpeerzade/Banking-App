package com.bank.Banking.Application.service.impl;

import com.bank.Banking.Application.dto.EmailDetails;

public interface EmailService {

    void sendEmailAlert(EmailDetails emailDetails);
}
