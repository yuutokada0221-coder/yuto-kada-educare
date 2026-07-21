package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

// みんチャレ/Strava的な「応援（Kudos）」。1日1組（誰から誰へ）につき1回まで。
@Entity
@Table(name = "cheer", indexes = @Index(name = "idx_cheer_lookup", columnList = "from_user_id, to_user_id, cheer_date"))
public class Cheer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private UserAccount fromUser;

    @ManyToOne
    private UserAccount toUser;

    private LocalDate cheerDate;

    public Long getId() { return id; }
    public UserAccount getFromUser() { return fromUser; }
    public void setFromUser(UserAccount fromUser) { this.fromUser = fromUser; }
    public UserAccount getToUser() { return toUser; }
    public void setToUser(UserAccount toUser) { this.toUser = toUser; }
    public LocalDate getCheerDate() { return cheerDate; }
    public void setCheerDate(LocalDate cheerDate) { this.cheerDate = cheerDate; }
}
