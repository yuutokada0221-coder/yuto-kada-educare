package com.example.demo;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Lv30で解放されるカスタム背景画像を配信するための静的リソース設定
// アップロード先はプロジェクト直下の uploads/backgrounds/（jarの外＝再デプロイしても消えない場所）
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/backgrounds/**")
                .addResourceLocations("file:uploads/backgrounds/");
        // ジャーナルの自由記述に添付する写真も同じ考え方でjarの外に保存・配信する
        registry.addResourceHandler("/journal-photos/**")
                .addResourceLocations("file:uploads/journal-photos/");
    }
}
