package com.example.demo.repository;

import com.example.demo.entity.ThemeOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ThemeOptionRepository extends JpaRepository<ThemeOption, Long> {
    List<ThemeOption> findByActiveTrueOrderByUnlockLevelAsc();
}
