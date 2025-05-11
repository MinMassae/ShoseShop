package com.example.shoseshop.entity;

import com.example.shoseshop.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity

public class BBS {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Member user;

    private String titel;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime date = LocalDateTime.now();
}
