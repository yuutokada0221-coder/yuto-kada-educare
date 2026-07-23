package com.example.demo.repository;

import com.example.demo.entity.JournalGrowthInsight;
import com.example.demo.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface JournalGrowthInsightRepository extends JpaRepository<JournalGrowthInsight, Long> {
    Optional<JournalGrowthInsight> findByUserAccount(UserAccount userAccount);
}
