package com.example.demo.repository;

import com.example.demo.entity.Quest;
import com.example.demo.entity.QuestClaim;
import com.example.demo.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuestClaimRepository extends JpaRepository<QuestClaim, Long> {
    Optional<QuestClaim> findByUserAccountAndQuestAndPeriodKey(UserAccount userAccount, Quest quest, String periodKey);
    List<QuestClaim> findByUserAccount(UserAccount userAccount);
    void deleteByUserAccount(UserAccount userAccount);
}
