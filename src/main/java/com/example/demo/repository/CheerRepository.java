package com.example.demo.repository;

import com.example.demo.entity.Cheer;
import com.example.demo.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface CheerRepository extends JpaRepository<Cheer, Long> {
    boolean existsByFromUserAndToUserAndCheerDate(UserAccount fromUser, UserAccount toUser, LocalDate cheerDate);
    // ホーム画面で「今日応援してくれた人」を表示するために使う
    List<Cheer> findByToUserAndCheerDate(UserAccount toUser, LocalDate cheerDate);
    // ★フレンドリーダーボードのN+1対策：行ごとに問い合わせるのではなく、
    // 「自分が今日送った応援」「リーダーボードの全員が今日受け取った応援」をそれぞれ1回でまとめて取得する
    List<Cheer> findByFromUserAndCheerDate(UserAccount fromUser, LocalDate cheerDate);
    List<Cheer> findByToUserInAndCheerDate(List<UserAccount> toUsers, LocalDate cheerDate);
}
