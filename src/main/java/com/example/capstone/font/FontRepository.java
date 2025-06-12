package com.example.capstone.font;

import com.example.capstone.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FontRepository extends JpaRepository<Font, Long> {

    List<Font> findByMember(Member member);

    boolean existsByMemberAndFontName(Member member, String fontName);

    Optional<Font> findByIdAndMember(Long id, Member member);
}
