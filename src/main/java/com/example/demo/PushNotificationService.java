package com.example.demo;

import com.example.demo.entity.PushSubscriptionEntity;
import com.example.demo.entity.UserAccount;
import com.example.demo.repository.PushSubscriptionRepository;
import jakarta.annotation.PostConstruct;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import org.apache.http.HttpResponse;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Security;
import java.util.List;

// ★ブラウザへのWebプッシュ通知の送信窓口。
// VAPID鍵はapplication.propertiesで管理し、実際の暗号化・署名はweb-pushライブラリに任せる。
@Service
public class PushNotificationService {

    private static final Logger log = LoggerFactory.getLogger(PushNotificationService.class);

    @Autowired private PushSubscriptionRepository subscriptionRepository;

    @Value("${push.vapid.public-key}")
    private String vapidPublicKey;

    @Value("${push.vapid.private-key}")
    private String vapidPrivateKey;

    @Value("${push.vapid.subject}")
    private String vapidSubject;

    private PushService pushService;

    @PostConstruct
    public void init() throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        pushService = new PushService(vapidPublicKey, vapidPrivateKey, vapidSubject);
    }

    public String getPublicKey() {
        return vapidPublicKey;
    }

    /** そのユーザーの全端末（購読）にリマインド通知を送る。無効になった購読はここで自動的に削除する。 */
    public void sendToUser(UserAccount user, String title, String body) {
        List<PushSubscriptionEntity> subs = subscriptionRepository.findByUserAccount(user);
        for (PushSubscriptionEntity sub : subs) {
            try {
                Subscription subscription = new Subscription(sub.getEndpoint(),
                        new Subscription.Keys(sub.getP256dh(), sub.getAuth()));
                String payload = "{\"title\":\"" + escapeJson(title) + "\",\"body\":\"" + escapeJson(body) + "\"}";
                Notification notification = new Notification(subscription, payload);
                HttpResponse response = pushService.send(notification);
                int status = response.getStatusLine().getStatusCode();
                // 410 Gone / 404 Not Found ＝ ブラウザ側でこの購読はもう無効。DBからも消しておく
                if (status == 404 || status == 410) {
                    subscriptionRepository.delete(sub);
                }
            } catch (Exception e) {
                log.warn("プッシュ通知の送信に失敗しました（userId={}）: {}", user.getId(), e.getMessage());
            }
        }
    }

    private String escapeJson(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
