package com.example.demo.repository;

import com.example.demo.entity.DailyJournal;
import com.example.demo.entity.UserAccount;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyJournalRepository extends JpaRepository<DailyJournal, Long> {
    // 魔法の機能で、ここに何も書かなくても「保存」「検索」「削除」の機能が使えるようになります！
    List<DailyJournal> findByUserAccount(UserAccount userAccount, Sort sort);
    Optional<DailyJournal> findByUserAccountAndDate(UserAccount userAccount, LocalDate date);
    // 週次サマリー通知：直近1週間分のジャーナル記録をまとめて取得する
    List<DailyJournal> findByUserAccountAndDateBetween(UserAccount userAccount, LocalDate startInclusive, LocalDate endInclusive);
    void deleteByUserAccount(UserAccount userAccount);
}
