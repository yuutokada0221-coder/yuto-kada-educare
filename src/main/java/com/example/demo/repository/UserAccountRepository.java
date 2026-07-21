package com.example.demo.repository;

import com.example.demo.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    // ユーザー名からアカウントを探し出すための特別な指示
    UserAccount findByUsername(String username);
    // パスワードリセットで、登録済みメールアドレスからアカウントを探す
    UserAccount findByEmail(String email);
    // 週間リーグ：同じ階層のユーザーを一括取得する
    java.util.List<UserAccount> findByLeagueTier(int leagueTier);
}
