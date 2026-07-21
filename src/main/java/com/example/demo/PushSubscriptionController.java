package com.example.demo;

import com.example.demo.entity.PushSubscriptionEntity;
import com.example.demo.entity.UserAccount;
import com.example.demo.repository.PushSubscriptionRepository;
import com.example.demo.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

// ブラウザのService Workerからのプッシュ購読の登録・解除を受け付けるAPI
@RestController
public class PushSubscriptionController {

    @Autowired private PushSubscriptionRepository subscriptionRepository;
    @Autowired private UserAccountRepository userRepository;
    @Autowired private PushNotificationService pushNotificationService;

    public static class SubscriptionRequest {
        public String endpoint;
        public Keys keys;
        public static class Keys {
            public String p256dh;
            public String auth;
        }
    }

    @GetMapping(value = "/push/vapid-public-key", produces = MediaType.TEXT_PLAIN_VALUE)
    public String vapidPublicKey() {
        return pushNotificationService.getPublicKey();
    }

    @PostMapping("/push/subscribe")
    public ResponseEntity<Void> subscribe(@RequestBody SubscriptionRequest req, Principal principal) {
        UserAccount user = userRepository.findByUsername(principal.getName());
        PushSubscriptionEntity entity = subscriptionRepository
                .findByUserAccountAndEndpoint(user, req.endpoint)
                .orElseGet(PushSubscriptionEntity::new);
        entity.setUserAccount(user);
        entity.setEndpoint(req.endpoint);
        entity.setP256dh(req.keys.p256dh);
        entity.setAuth(req.keys.auth);
        subscriptionRepository.save(entity);
        return ResponseEntity.ok().build();
    }

    // ★deleteByUserAccountAndEndpoint単体でも、コントローラーの素のメソッド呼び出しからだと
    // トランザクションが張られておらず削除が反映されないことがある（このセッション中に既に3回踏んだパターン）
    @Transactional
    @PostMapping("/push/unsubscribe")
    public ResponseEntity<Void> unsubscribe(@RequestBody SubscriptionRequest req, Principal principal) {
        UserAccount user = userRepository.findByUsername(principal.getName());
        subscriptionRepository.deleteByUserAccountAndEndpoint(user, req.endpoint);
        return ResponseEntity.ok().build();
    }
}
