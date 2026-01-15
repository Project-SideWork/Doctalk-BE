package com.capstone.domain.payment.controller;

import com.capstone.docs.PaymentControllerDocs;
import com.capstone.domain.payment.dto.PaymentRequestDto;
import com.capstone.domain.payment.exception.InvaildPaymentException;
import com.capstone.domain.payment.service.PaymentService;
import com.capstone.global.ratelimit.annotation.RateLimit;
import com.capstone.global.ratelimit.enums.RateLimitKeyType;
import com.capstone.global.response.ApiResponse;
import com.capstone.global.security.CustomUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static com.capstone.domain.payment.message.PaymentMessages.IAMPORT_FAILDED;
import static com.capstone.domain.payment.message.PaymentMessages.PAYMENT_NOT_FOUND;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController implements PaymentControllerDocs
{

    private final PaymentService paymentService;

    @Value("${IMP_API_KEY}")
    private String apiKey;

    @Value("${IMP_API_SECRET}")
    private String secretKey;

    private IamportClient iamportClient;

    @PostConstruct
    public void init()
    {
        this.iamportClient = new IamportClient(apiKey, secretKey);
    }


    @PostMapping("/verify")
    @RateLimit(limit = 10, duration = 3600, keyType = RateLimitKeyType.USER)
    public ResponseEntity<ApiResponse<?>> validateIamport(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody PaymentRequestDto request) {
        try {
            IamportResponse<Payment> payment = iamportClient.paymentByImpUid(request.getImpUid());

            if (payment == null || payment.getResponse() == null) {
                throw new InvaildPaymentException(PAYMENT_NOT_FOUND);
            }

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(payment.getResponse());
            log.info("아임포트 응답: {}", jsonResponse);

            paymentService.processPayment(userDetails,request.getImpUid());

            return ResponseEntity.ok(ApiResponse.onSuccess(payment));

        } catch (IamportResponseException e) {
            log.error("아임포트 결제 검증 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.onFailure("PAYMENT_FAILED", "아임포트 결제 검증 실패: " + e.getMessage(), null));
        } catch (IOException e) {
            log.error("JSON 변환 오류: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.onFailure("INTERNAL_SERVER_ERROR", "서버 오류 발생: " + e.getMessage(),null));
        }
    }
}
