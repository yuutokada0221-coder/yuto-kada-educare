package com.example.demo.repository;

import com.example.demo.entity.Quest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuestRepository extends JpaRepository<Quest, Long> {
    List<Quest> findByActiveTrueAndPeriod(Quest.Period period);
}
