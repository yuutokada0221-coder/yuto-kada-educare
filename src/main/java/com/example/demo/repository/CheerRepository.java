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
}
