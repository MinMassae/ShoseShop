package com.example.shoseshop.repository;

import com.example.shoseshop.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // userId로 회원 찾기 (Optional)
    Optional<Member> findByUserId(String userId);
}