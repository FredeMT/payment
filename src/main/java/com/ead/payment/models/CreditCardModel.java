package com.ead.payment.models;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "TB_CREDIT_CARDS")
public class CreditCardModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID cardId;
    @Column(nullable = false, length = 150)
    private String cardHolderFullName;
    @Column(nullable = false, length = 20)
    private String cardHolderCpf;
    @Column(nullable = false, length = 20)
    private String creditCardNumber;
    @Column(nullable = false, length = 10)
    private String expirationDate;
    @Column(nullable = false, length = 3)
    private String cvvCode;
    @OneToOne
    @JoinColumn(name = "user_id", unique=true, nullable = false)
    private UserModel user;

    public UUID getCardId() {
        return cardId;
    }

    public void setCardId(UUID cardId) {
        this.cardId = cardId;
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

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }
}
