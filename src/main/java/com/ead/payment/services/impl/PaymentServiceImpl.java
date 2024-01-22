package com.ead.payment.services.impl;

import com.ead.payment.dtos.PaymentCommandDto;
import com.ead.payment.dtos.PaymentRequestDto;
import com.ead.payment.enums.PaymentControl;
import com.ead.payment.enums.PaymentStatus;
import com.ead.payment.models.CreditCardModel;
import com.ead.payment.models.PaymentModel;
import com.ead.payment.models.UserModel;
import com.ead.payment.publishers.PaymentCommandPublisher;
import com.ead.payment.publishers.PaymentEventPublisher;
import com.ead.payment.repositories.CreditCardRepository;
import com.ead.payment.repositories.PaymentRepository;
import com.ead.payment.repositories.UserRepository;
import com.ead.payment.services.PaymentService;
import com.ead.payment.services.PaymentStripeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger logger = LogManager.getLogger(PaymentServiceImpl.class);

    @Autowired
    CreditCardRepository creditCardRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    PaymentCommandPublisher paymentCommandPublisher;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PaymentStripeService paymentStripeService;

    @Autowired
    PaymentEventPublisher paymentEventPublisher;

    public PaymentModel requestPayment(PaymentRequestDto paymentRequestDto, UserModel userModel) {
        var creditCardModel = new CreditCardModel();
        var creditCardModelOptional = creditCardRepository.findByUser(userModel);
        if(creditCardModelOptional.isPresent()){
            creditCardModel = creditCardModelOptional.get();
        }
        //Save CreditCardModel.
        BeanUtils.copyProperties(paymentRequestDto, creditCardModel);
        creditCardModel.setUser(userModel);
        creditCardRepository.save(creditCardModel);

        //save PaymentModel.
        var paymentModel = new PaymentModel();
        paymentModel.setPaymentControl(PaymentControl.REQUESTED);
        paymentModel.setPaymentRequestDate(LocalDateTime.now(ZoneId.of("UTC")));
        paymentModel.setPaymentExpirationDate(LocalDateTime.now(ZoneId.of("UTC")).plusDays(30));
        paymentModel.setLastDigitsCreditCard(paymentRequestDto.getCreditCardNumber().substring(paymentRequestDto.getCreditCardNumber().length()-4));
        paymentModel.setValuePaid(paymentRequestDto.getValuePaid());
        paymentModel.setUser(userModel);
        paymentRepository.save(paymentModel);

        //send request to queue.
        try{
            var paymentCommandDto = new PaymentCommandDto();
            paymentCommandDto.setUserId(userModel.getUserId());
            paymentCommandDto.setPaymentId(paymentModel.getPaymentId());
            paymentCommandDto.setCardId(creditCardModel.getCardId());
            paymentCommandPublisher.publishPaymentCommand(paymentCommandDto);
        }   catch (Exception e) {
            logger.warn("Error sending payment command!");
        }
        return paymentModel;
    }

    @Transactional
    @Override
    public Optional<PaymentModel> findLastPaymentByUser(UserModel userModel) {
        return paymentRepository.findTopByUserOrderByPaymentRequestDateDesc(userModel);
    }

    @Override
    public Page<PaymentModel> findAllByUser(Specification<PaymentModel> spec, Pageable pageable) {
        return paymentRepository.findAll(spec, pageable);
    }

    @Override
    public Optional<PaymentModel> findPaymentByUser(UUID userId, UUID paymentId) {
        return  paymentRepository.findPaymentByUser(userId, paymentId);
    }

    @Transactional
    @Override
    public void makePayment(PaymentCommandDto paymentCommandDto) {
        var paymentModel = paymentRepository.findById(paymentCommandDto.getPaymentId()).get();
        var userModel = userRepository.findById(paymentCommandDto.getUserId()).get();
        var creditCardModel = creditCardRepository.findById(paymentCommandDto.getCardId()).get();

        paymentModel = paymentStripeService.processStripePayment(paymentModel,creditCardModel);
        paymentRepository.save(paymentModel);

        if(paymentModel.getPaymentControl().equals(PaymentControl.EFFECTED)){
            userModel.setPaymentStatus(PaymentStatus.PAYING);
            userModel.setLastPaymentDate(LocalDateTime.now(ZoneId.of("UTC")));
            userModel.setPaymentExpirationDate(LocalDateTime.now(ZoneId.of("UTC")).plusDays(30));
            if(userModel.getFirstPaymentDate() == null){
                userModel.setFirstPaymentDate(LocalDateTime.now(ZoneId.of("UTC")));
            }
        } else {
            userModel.setPaymentStatus(PaymentStatus.DEBTOR);
        }
        userRepository.save(userModel);

        if(paymentModel.getPaymentControl().equals(PaymentControl.EFFECTED) ||
                paymentModel.getPaymentControl().equals(PaymentControl.REFUSED)){
            paymentEventPublisher.publishPaymentEvent(paymentModel.convertToPaymentEventDto());
        } else if(paymentModel.getPaymentControl().equals(PaymentControl.ERROR)) {
            //retry process and limits retry.
        }
    }
    }

