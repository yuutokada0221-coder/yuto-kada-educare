package com.example.demo;

import com.example.demo.entity.Cheer;
import com.example.demo.entity.Friendship;
import com.example.demo.entity.UserAccount;
import com.example.demo.repository.CheerRepository;
import com.example.demo.repository.FriendshipRepository;
import com.example.demo.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDate;

// フレンド申請・承認・拒否・解除・応援（みんチャレ/Strava風のkudos）
@Controller
public class FriendController {

    @Autowired private UserAccountRepository userRepository;
    @Autowired private FriendshipRepository friendshipRepository;
    @Autowired private CheerRepository cheerRepository;
    @Autowired private PushNotificationService pushNotificationService;

    @PostMapping("/friends/request")
    public String request(@RequestParam String username, Principal principal) {
        UserAccount currentUser = userRepository.findByUsername(principal.getName());
        UserAccount target = userRepository.findByUsername(username.trim());

        if (target == null) {
            return "redirect:/?error=friendNotFound";
        }
        if (target.getId().equals(currentUser.getId())) {
            return "redirect:/?error=friendSelf";
        }
        if (friendshipRepository.findBetween(currentUser, target).isPresent()) {
            return "redirect:/?error=friendAlreadyExists";
        }

        Friendship friendship = new Friendship();
        friendship.setRequester(currentUser);
        friendship.setAddressee(target);
        friendship.setStatus("PENDING");
        friendshipRepository.save(friendship);
        return "redirect:/?friendRequestSent=true";
    }

    @PostMapping("/friends/accept/{id}")
    public String accept(@PathVariable Long id, Principal principal) {
        UserAccount currentUser = userRepository.findByUsername(principal.getName());
        friendshipRepository.findById(id).ifPresent(f -> {
            // 申請された側（addressee）本人しか承認できない
            if (f.getAddressee().getId().equals(currentUser.getId()) && "PENDING".equals(f.getStatus())) {
                f.setStatus("ACCEPTED");
                friendshipRepository.save(f);
            }
        });
        return "redirect:/?tab=friends";
    }

    // 申請の拒否（addressee側）／取り消し（requester側）／成立済みの解除、いずれもこのエンドポイントで行削除するだけ
    @PostMapping("/friends/remove/{id}")
    public String remove(@PathVariable Long id, Principal principal) {
        UserAccount currentUser = userRepository.findByUsername(principal.getName());
        friendshipRepository.findById(id).ifPresent(f -> {
            boolean isParty = f.getRequester().getId().equals(currentUser.getId())
                    || f.getAddressee().getId().equals(currentUser.getId());
            if (isParty) {
                friendshipRepository.delete(f);
            }
        });
        return "redirect:/?tab=friends";
    }

    // ★みんチャレ/Strava風の「応援（kudos）」。フレンド1人につき1日1回まで、
    // 応援された側には+1EXPの小さなボーナスと、繋がっていればプッシュ通知が届く。
    @PostMapping("/friends/cheer/{userId}")
    public String cheer(@PathVariable Long userId, Principal principal) {
        UserAccount currentUser = userRepository.findByUsername(principal.getName());
        UserAccount target = userRepository.findById(userId).orElse(null);
        if (target == null || target.getId().equals(currentUser.getId())) {
            return "redirect:/?tab=friends";
        }

        Friendship friendship = friendshipRepository.findBetween(currentUser, target).orElse(null);
        if (friendship == null || !"ACCEPTED".equals(friendship.getStatus())) {
            return "redirect:/?tab=friends"; // フレンドでない相手は応援できない
        }

        LocalDate today = LocalDate.now();
        if (cheerRepository.existsByFromUserAndToUserAndCheerDate(currentUser, target, today)) {
            return "redirect:/?tab=friends"; // 同じ相手への応援は1日1回まで
        }

        Cheer cheer = new Cheer();
        cheer.setFromUser(currentUser);
        cheer.setToUser(target);
        cheer.setCheerDate(today);
        cheerRepository.save(cheer);

        target.setExp(target.getExp() + 1);
        userRepository.save(target);

        pushNotificationService.sendToUser(target, "目標達成RPG",
                currentUser.getUsername() + "さんがあなたを応援しました！🔥");

        return "redirect:/?tab=friends";
    }
}
