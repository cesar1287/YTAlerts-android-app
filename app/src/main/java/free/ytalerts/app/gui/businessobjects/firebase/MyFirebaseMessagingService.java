package free.ytalerts.app.gui.businessobjects.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import free.ytalerts.app.R;
import free.ytalerts.app.gui.activities.MainActivity;
import free.ytalerts.app.gui.activities.NotificationActivity;
import free.ytalerts.app.gui.activities.YouTubePlayerActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService{

    String title, body, link;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        title = remoteMessage.getNotification().getTitle();
        body = remoteMessage.getNotification().getBody();
        link = remoteMessage.getData().get(FirebaseHelper.FIREBASE_NOTIFICATION_LINK);

        sendNotification();
    }

    private void sendNotification() {
        Intent i = new Intent(this, YouTubePlayerActivity.class);

        i.setAction(Intent.ACTION_VIEW);
        i.setData(Uri.parse("https://www.youtube.com/watch?v="+link));
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /*Request code*/ , i,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(getNotificationIcon())
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private int getNotificationIcon() {

        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.ic_icon : R.mipmap.ic_launcher;
    }
}
