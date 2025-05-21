package com.example.capstone.auth;

import com.example.capstone.member.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.expiryDate = LocalDateTime.now().plusDays(14);
//        this.expiryDate = LocalDateTime.now().plusMinutes(2);
        this.createdAt = LocalDateTime.now();
    }
}

