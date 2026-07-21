package com.example.demo.repository;

import com.example.demo.entity.Friendship;
import com.example.demo.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    List<Friendship> findByAddresseeAndStatus(UserAccount addressee, String status);
    List<Friendship> findByRequesterAndStatus(UserAccount requester, String status);

    // 自分がrequester/addresseeのどちらであっても成立済み(ACCEPTED)の関係を全部拾う
    @Query("SELECT f FROM Friendship f WHERE (f.requester = :user OR f.addressee = :user) AND f.status = 'ACCEPTED'")
    List<Friendship> findAcceptedByUser(@Param("user") UserAccount user);

    @Query("SELECT f FROM Friendship f WHERE " +
            "(f.requester = :a AND f.addressee = :b) OR (f.requester = :b AND f.addressee = :a)")
    Optional<Friendship> findBetween(@Param("a") UserAccount a, @Param("b") UserAccount b);
}
