package com.example.shoseshop.service;

import com.example.shoseshop.domain.Member;
import com.example.shoseshop.dto.MemberDTO;
import com.example.shoseshop.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 비밀번호 해시 비교 추가
    public Member login(String userId, String rawPassword) {
        Optional<Member> memberOpt = memberRepository.findByUserId(userId);
        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();

            if (passwordEncoder.matches(rawPassword, member.getPassword())) {
                return member;
            }
        }
        return null;
    }

    public Member register(MemberDTO dto) {
        Optional<Member> existing = memberRepository.findByUserId(dto.getUserId());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        Member member = new Member();
        member.setUserId(dto.getUserId());
        member.setPassword(passwordEncoder.encode(dto.getPassword())); // 암호화 저장
        member.setName(dto.getName());
        member.setAddress(dto.getAddress());
        member.setEmail(dto.getEmail());
        member.setPhone(dto.getPhone());
        member.setCreatedAt(LocalDateTime.now());

        return memberRepository.save(member);
    }

    public Member findByUserId(String userId) {
        return memberRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));
    }

    public Member updateMember(String userId, MemberDTO dto) {
        Member member = findByUserId(userId);
        member.setName(dto.getName());
        member.setAddress(dto.getAddress());
        member.setEmail(dto.getEmail());
        member.setPhone(dto.getPhone());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            member.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        return memberRepository.save(member);
    }
}