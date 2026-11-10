package jp.educure.problem5.userprofilecontroller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class UserProfileController {

    // データベースは使わず、ID 1 のユーザー1人分をフィールドに保持する
    private UserProfileForm profile = new UserProfileForm(1, "田中太郎", 30, "よろしくお願いします！");

    // プロフィール表示（IDはパスパラメータ）
    @GetMapping("/profile/{id}")
    public String view(@PathVariable("id") int id, Model model) {
        checkId(id);
        model.addAttribute("profile", profile);
        return "profile-view";
    }

    // 編集画面（IDはクエリパラメータ）
    @GetMapping("/profile/edit")
    public String edit(@RequestParam("id") int id, Model model) {
        checkId(id);
        model.addAttribute("form", profile);
        return "profile-edit";
    }

    // 確認画面
    @PostMapping("/profile/confirm")
    public String confirm(@ModelAttribute("form") UserProfileForm form) {
        checkId(form.getId());
        return "profile-confirm";
    }

    // 確認画面の「戻る」: 入力内容を保ったまま編集画面へ
    @PostMapping("/profile/back-to-edit")
    public String back(@ModelAttribute("form") UserProfileForm form) {
        return "profile-edit";
    }

    // 確認画面の「確定」: 更新してプロフィール画面を表示（redirectは使わない）
    @PostMapping("/profile/save")
    public String save(@ModelAttribute("form") UserProfileForm form, Model model) {
        checkId(form.getId());
        profile = form;
        model.addAttribute("profile", profile);
        model.addAttribute("message", "プロフィールが更新されました！");
        return "profile-view";
    }

    private void checkId(int id) {
        if (id != profile.getId()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ユーザーが見つかりません");
        }
    }
}
