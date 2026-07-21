package com.example.demo;

import com.example.demo.entity.UserAccount;
import com.example.demo.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Principal;

// Lv30で解放される「好きな画像の背景」機能：アップロードされた画像を検証・再エンコードして保存する
@Controller
public class BackgroundController {

    private static final int UNLOCK_LEVEL = 30;
    private static final Path UPLOAD_DIR = Path.of("uploads", "backgrounds");

    @Autowired private UserAccountRepository userRepository;

    @PostMapping("/uploadBackground")
    public String upload(@RequestParam("image") MultipartFile file, Principal principal) {
        UserAccount currentUser = userRepository.findByUsername(principal.getName());
        int level = LevelingUtil.levelOf(currentUser.getExp());
        if (level < UNLOCK_LEVEL) {
            return "redirect:/?error=bgLocked";
        }
        if (file == null || file.isEmpty()) {
            return "redirect:/?error=bgEmpty";
        }

        try {
            // 拡張子や申告されたContent-Typeを信用せず、実際に画像として読めるかで検証する。
            // 読み込めた画像をPNGとして描き直して保存することで、画像に偽装した不正ファイルを無害化する。
            // （readSafelyはヘッダの解像度を先にチェックし、展開爆弾を本デコード前に弾く）
            BufferedImage image = ImageUploadUtil.readSafely(file.getInputStream());
            if (image == null) {
                return "redirect:/?error=bgInvalid";
            }

            Files.createDirectories(UPLOAD_DIR);
            String filename = "user-" + currentUser.getId() + ".png";
            File destination = UPLOAD_DIR.resolve(filename).toFile();
            ImageIO.write(image, "png", destination);

            currentUser.setCustomBackgroundFilename(filename);
            currentUser.setTheme("custom");
            userRepository.save(currentUser);
        } catch (ImageUploadUtil.TooLargeException e) {
            return "redirect:/?error=bgTooLargeDimensions";
        } catch (IOException e) {
            return "redirect:/?error=bgFailed";
        }

        return "redirect:/";
    }

}
