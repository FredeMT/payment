package com.ead.payment.dtos;

import org.hibernate.validator.constraints.br.CPF;

import javax.validation.constraints.*;
import java.math.BigDecimal;

public class PaymentRequestDto {

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer = 5, fraction = 2)
    private BigDecimal valuePaid;
    @NotBlank
    private String cardHolderFullName;
    @NotBlank
    @CPF
    private String cardHolderCpf;
    @NotBlank
    @Size(min = 16, max = 20)
    private String creditCardNumber;
    @NotBlank
    @Size(min = 4, max = 10)
    private String expirationDate;
    @NotBlank
    @Size(min = 3, max = 3)
    private String cvvCode;

    public BigDecimal getValuePaid() {
        return valuePaid;
    }

    public void setValuePaid(BigDecimal valuePaid) {
        this.valuePaid = valuePaid;
    }

    public String getCardHolderFullName() {
        return cardHolderFullName;
    }

    public void setCardHolderFullName(String cardHolderFullName) {
        this.cardHolderFullName = cardHolderFullName;
    }

    public String getCardHolderCpf() {
        return cardHolderCpf;
    }

    public void setCardHolderCpf(String cardHolderCpf) {
        this.cardHolderCpf = cardHolderCpf;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getCvvCode() {
        return cvvCode;
    }

    public void setCvvCode(String cvvCode) {
        this.cvvCode = cvvCode;
    }
}