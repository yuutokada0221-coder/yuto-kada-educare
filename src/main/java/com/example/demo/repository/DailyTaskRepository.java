package com.example.demo.repository;

import com.example.demo.entity.DailyTask;
import com.example.demo.entity.UserAccount;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyTaskRepository extends JpaRepository<DailyTask, Long> {
    List<DailyTask> findByUserAccount(UserAccount userAccount, Sort sort);
    Optional<DailyTask> findByUserAccountAndDate(UserAccount userAccount, LocalDate date);
    // 週次サマリー通知：直近1週間分のTODO記録をまとめて取得する
    List<DailyTask> findByUserAccountAndDateBetween(UserAccount userAccount, LocalDate startInclusive, LocalDate endInclusive);
    void deleteByUserAccount(UserAccount userAccount);
}
