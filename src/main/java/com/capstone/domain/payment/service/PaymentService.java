package com.capstone.domain.payment.service;

import com.capstone.domain.payment.entity.PaymentEntity;
import com.capstone.domain.payment.repository.PaymentRepository;
import com.capstone.domain.user.entity.MembershipType;
import com.capstone.domain.user.entity.User;
import com.capstone.domain.user.exception.UserNotFoundException;
import com.capstone.domain.user.repository.UserRepository;
import com.capstone.global.jwt.JwtUtil;
import com.capstone.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.capstone.domain.user.message.UserMessages.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public PaymentEntity processPayment(CustomUserDetails userDetails, String impUid)
    {
        String email =userDetails.getEmail();
        Optional<User> user = userRepository.findUserByEmail(email);
        if(user.isEmpty())
        {
            throw new UserNotFoundException();
        }
        log.info("email={}",email);

        User userExist = user.get();
        //유저 멤버쉽 프리미엄으로 변경
        userExist.setMembership(MembershipType.PREMIUM_USER);
        userRepository.save(userExist);

        //결제 엔티티 생성ㅇ
        PaymentEntity paymentEntity = PaymentEntity.builder()
                .userEmail(email)
                .totalPrice(BigDecimal.valueOf(1)) // 일단 1원 고정 추후에 변경
                .paidAt(LocalDateTime.now())
                .impUid(impUid)
                .status(true)
                .build();

        return paymentRepository.save(paymentEntity);
    }
}
