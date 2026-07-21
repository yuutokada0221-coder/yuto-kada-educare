package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

// フレンド関係。申請した側=requester、申請された側=addressee。
// status="PENDING"（申請中）→"ACCEPTED"（成立）の一方向遷移。拒否/解除は行削除で表現する。
@Entity
@Table(name = "friendship", indexes = {
        @Index(name = "idx_friendship_requester", columnList = "requester_id"),
        @Index(name = "idx_friendship_addressee", columnList = "addressee_id")
})
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private UserAccount requester;

    @ManyToOne
    private UserAccount addressee;

    private String status = "PENDING";

    private LocalDate createdAt = LocalDate.now();

    public Long getId() { return id; }
    public UserAccount getRequester() { return requester; }
    public void setRequester(UserAccount requester) { this.requester = requester; }
    public UserAccount getAddressee() { return addressee; }
    public void setAddressee(UserAccount addressee) { this.addressee = addressee; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }
}
