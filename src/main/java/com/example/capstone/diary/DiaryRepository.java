package com.example.capstone.diary;

import com.example.capstone.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

    List<Diary> findByMemberAndDiaryDateBetween(Member member, LocalDate start, LocalDate end);

    Optional<Diary> findByIdAndMember(Long id, Member member);


}
