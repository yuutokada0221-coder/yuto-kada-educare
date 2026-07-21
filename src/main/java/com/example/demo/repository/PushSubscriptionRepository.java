package com.example.demo.repository;

import com.example.demo.entity.PushSubscriptionEntity;
import com.example.demo.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PushSubscriptionRepository extends JpaRepository<PushSubscriptionEntity, Long> {
    List<PushSubscriptionEntity> findByUserAccount(UserAccount userAccount);
    Optional<PushSubscriptionEntity> findByUserAccountAndEndpoint(UserAccount userAccount, String endpoint);
    void deleteByUserAccountAndEndpoint(UserAccount userAccount, String endpoint);
}
