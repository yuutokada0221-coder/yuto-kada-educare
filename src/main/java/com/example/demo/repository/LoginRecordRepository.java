package com.example.demo.repository;

import com.example.demo.entity.LoginRecord;
import com.example.demo.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoginRecordRepository extends JpaRepository<LoginRecord, Long> {
    // 特定のユーザーの「今日」の記録がすでにあるか確認する指示
    boolean existsByUserAccountAndLoginDate(UserAccount userAccount, LocalDate loginDate);
    // カレンダーを塗るために、特定のユーザーのログイン記録を全部取ってくる指示
    List<LoginRecord> findByUserAccount(UserAccount userAccount);
    void deleteByUserAccount(UserAccount userAccount);
}
