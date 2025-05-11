package com.example.shoseshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class StripePaymentService {

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.stripe.com")
            .build();

    @Value("${stripe.secret-key}")
    private String secretKey;

    // ✅ 결제 준비 (PaymentIntent 생성)
    public Mono<String> createPaymentIntent(Integer amount) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("amount", String.valueOf(amount)); // 금액: 센트 단위 (100 = 1달러)
        formData.add("currency", "usd"); // 통화
        formData.add("payment_method_types[]", "card"); // 카드결제 허용

        return webClient.post()
                .uri("/v1/payment_intents")
                .headers(headers -> headers.setBasicAuth(secretKey, "")) // Stripe는 Basic 인증 (secret_key:)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .bodyValue(formData)
                .retrieve()
                .bodyToMono(String.class);
    }
}