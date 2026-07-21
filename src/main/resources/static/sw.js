// 目標達成RPG：プッシュ通知を受け取って表示するためのService Worker

self.addEventListener('push', event => {
    let data = { title: '目標達成RPG', body: '新しい通知があります' };
    try {
        if (event.data) data = event.data.json();
    } catch (e) {
        // JSONでなければそのままテキストとして扱う
        data.body = event.data ? event.data.text() : data.body;
    }

    event.waitUntil(
        self.registration.showNotification(data.title || '目標達成RPG', {
            body: data.body || '',
            icon: '/favicon.ico',
            badge: '/favicon.ico'
        })
    );
});

// 通知をクリックしたらアプリのタブを開く（既に開いていればそこにフォーカス）
self.addEventListener('notificationclick', event => {
    event.notification.close();
    event.waitUntil(
        clients.matchAll({ type: 'window', includeUncontrolled: true }).then(windowClients => {
            for (const client of windowClients) {
                if ('focus' in client) return client.focus();
            }
            if (clients.openWindow) return clients.openWindow('/');
        })
    );
});
