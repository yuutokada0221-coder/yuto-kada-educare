package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

// 管理者が追加・編集できる「カラーテーマ」の定義
@Entity
public class ThemeOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;      // 表示名（例：サクラピンク）
    private String emoji;     // アイコン絵文字
    private String cssClass;  // bodyに付与するCSSクラス名
    private int unlockLevel;  // このレベルで解放
    private boolean active = true;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmoji() { return emoji; }
    public void setEmoji(String emoji) { this.emoji = emoji; }
    public String getCssClass() { return cssClass; }
    public void setCssClass(String cssClass) { this.cssClass = cssClass; }
    public int getUnlockLevel() { return unlockLevel; }
    public void setUnlockLevel(int unlockLevel) { this.unlockLevel = unlockLevel; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
