package com.example.shoseshop.controller;

import com.example.shoseshop.domain.Member;
import com.example.shoseshop.dto.OrderItemResponseDTO;
import com.example.shoseshop.dto.StripePaymentRequestDTO;
import com.example.shoseshop.entity.*;
import com.example.shoseshop.jwtoken.JwtToken;
import com.example.shoseshop.repository.*;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    private final JwtToken jwtToken;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Value("${stripe.secret-key}")
    private String stripeSecretKey;

    @PostMapping("/stripe/create-checkout-session")
    public ResponseEntity<?> createStripeCheckoutSession(@RequestBody StripePaymentRequestDTO requestDTO,
                                                         HttpServletRequest request) {
        log.info("✅ createStripeCheckoutSession 진입");
        String token = jwtToken.resolveToken(request);

        if (token == null || !jwtToken.validateToken(token)) {
            return ResponseEntity.status(403).body("ログインが必要です。");
        }

        String userId = jwtToken.getUserIdFromToken(token);
        Member user = memberRepository.findByUserId(userId).orElseThrow();

        Stripe.apiKey = stripeSecretKey;

        try {
            List<OrderItemResponseDTO> items = new ArrayList<>();
            String successUrl = "http://localhost:8080/api/payment/success?token=" + token;
            String cartIdParam = "";

            if (requestDTO.isDirect()) {
                Product product = productRepository.findById(requestDTO.getProductId()).orElseThrow();

                OrderItemResponseDTO item = new OrderItemResponseDTO();
                item.setProductName(product.getName());
                item.setPrice(product.getPrice());
                item.setQuantity(requestDTO.getQuantity());
                items.add(item);

                // 단일 주문 파라미터
                successUrl += "&direct=true"
                        + "&productId=" + requestDTO.getProductId()
                        + "&size=" + requestDTO.getSize()
                        + "&quantity=" + requestDTO.getQuantity();

            } else {
                List<Cart> carts = cartRepository.findAllById(requestDTO.getCartIds());

                items = carts.stream().map(cart -> {
                    OrderItemResponseDTO item = new OrderItemResponseDTO();
                    item.setProductName(cart.getProduct().getName());
                    item.setPrice(cart.getProduct().getPrice());
                    item.setQuantity(cart.getQuantity());
                    return item;
                }).collect(Collectors.toList());

                cartIdParam = requestDTO.getCartIds().stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(","));
                successUrl += "&cartIds=" + cartIdParam;
            }

            SessionCreateParams.Builder builder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(successUrl)
                    .setCancelUrl("http://localhost:8080/api/payment/fail");

            for (OrderItemResponseDTO item : items) {
                builder.addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                        .setCurrency("jpy")
                                        .setUnitAmount((long) item.getPrice())
                                        .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                .setName(item.getProductName())
                                                .build())
                                        .build())
                                .setQuantity((long) item.getQuantity())
                                .build()
                );
            }

            Session session = Session.create(builder.build());
            return ResponseEntity.ok(session.getUrl());

        } catch (Exception e) {
            log.error("❌ Stripe 세션 생성 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Stripeセッション作成失敗");
        }
    }

    @GetMapping("/success")
    public void paymentSuccess(@RequestParam("token") String token,
                               @RequestParam(value = "cartIds", required = false) String cartIds,
                               @RequestParam(value = "direct", required = false) Boolean isDirect,
                               @RequestParam(value = "productId", required = false) Long productId,
                               @RequestParam(value = "size", required = false) String size,
                               @RequestParam(value = "quantity", required = false) Integer quantity,
                               HttpServletResponse response) throws IOException {

        if (!jwtToken.validateToken(token)) {
            response.sendRedirect("/login");
            return;
        }

        String userId = jwtToken.getUserIdFromToken(token);
        Member member = memberRepository.findByUserId(userId).orElseThrow();

        Order order = new Order();
        order.setUser(member);
        order.setDate(LocalDateTime.now());
        order.setStatus("注文完了");
        order = orderRepository.save(order);

        List<OrderItem> orderItems = new ArrayList<>();

        if (Boolean.TRUE.equals(isDirect) && productId != null && size != null && quantity != null) {
            Product product = productRepository.findById(productId).orElseThrow();

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setSize(size);
            item.setQuantity(quantity);
            item.setPrice(product.getPrice());
            orderItems.add(item);

        } else if (cartIds != null && !cartIds.isEmpty()) {
            List<Long> cartIdList = Arrays.stream(cartIds.split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());

            List<Cart> carts = cartRepository.findAllById(cartIdList);

            for (Cart cart : carts) {
                OrderItem item = new OrderItem();
                item.setOrder(order);
                item.setProduct(cart.getProduct());
                item.setSize(cart.getSize());
                item.setQuantity(cart.getQuantity());
                item.setPrice(cart.getProduct().getPrice());
                orderItems.add(item);
            }

            cartRepository.deleteAllById(cartIdList);
        }

        if (!orderItems.isEmpty()) {
            orderItemRepository.saveAll(orderItems);
        }

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println("<script>alert('決済が完了しました。'); location.href='/'</script>");
    }

    @GetMapping("/fail")
    public void paymentFail(HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println("<script>alert('決済に失敗しました。'); location.href='/'</script>");
    }

    @GetMapping("/cancel")
    public void paymentCancel(HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println("<script>alert('決済がキャンセルされました。'); location.href='/'</script>");
    }
}
