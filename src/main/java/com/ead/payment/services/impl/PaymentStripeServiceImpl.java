package com.ead.payment.services.impl;

import com.ead.payment.models.CreditCardModel;
import com.ead.payment.models.PaymentModel;
import com.ead.payment.services.PaymentStripeService;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.param.PaymentIntentConfirmParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentMethodCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PaymentStripeServiceImpl implements PaymentStripeService {

    @Value(value = "${ead.stripe.secretKey}")
    private String secretKeyStripe;
    String paymentIntentId = null;

    @Override
    public PaymentModel processStripePayment(PaymentModel paymentModel, CreditCardModel creditCardModel) {
                Stripe.apiKey = secretKeyStripe;
        try {
//Step 1
            PaymentIntentCreateParams paramsPaymentIntent =
                    PaymentIntentCreateParams.builder()
                            .setAmount(paymentModel.getValuePaid().multiply(new BigDecimal("100")).longValue())
                            .setCurrency("brl")
                            .setAutomaticPaymentMethods(
                                    PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                            .setEnabled(true)
                                            .build()
                            )
                            .build();

            PaymentIntent paymentIntent = PaymentIntent.create(paramsPaymentIntent);

//Step 2
            PaymentMethodCreateParams paramsPaymentMethod =
                    PaymentMethodCreateParams.builder()
                            .setType(PaymentMethodCreateParams.Type.CARD)
                            .setCard(
                                    PaymentMethodCreateParams.CardDetails.builder()
                                            .setNumber(creditCardModel.getCreditCardNumber().replaceAll(" ", ""))
                                            .setExpMonth(Long.valueOf(creditCardModel.getExpirationDate().split("/")[0]))
                                            .setExpYear(Long.valueOf(creditCardModel.getExpirationDate().split("/")[1]))
                                            .setCvc(creditCardModel.getCvvCode())
                                            .build()
                            )
                            .build();

//Step 3
            PaymentIntent resource = PaymentIntent.retrieve(paymentIntent.getId());

            PaymentIntentConfirmParams paramsPaymentConfirm =
                    PaymentIntentConfirmParams.builder()
                            .setPaymentMethod("pm_card_visa")
                            .setReturnUrl("https://www.example.com")
                            .build();

            PaymentIntent confirmPaymentIntent = resource.confirm(paramsPaymentConfirm);

        } catch(Exception exception) {

        }
        return paymentModel;
    }
}
