package com.gohar_amin.tz.utils

import android.R
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.gohar_amin.tz.MainActivity
import com.gohar_amin.tz.acitivities.ui.ChatActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val NOTIFICATION_REQUEST_CODE: Int=10
    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        if(p0.notification!=null ){
            createNotification(p0)
        }
    }

    private fun createNotification(remoteMessage: RemoteMessage) {
        val body:String?= remoteMessage.notification!!.body
        val title:String?= remoteMessage.notification!!.title
        var seller:String?=null
        var user:String?=null
        if(remoteMessage.data.containsKey("seller")){
            seller= remoteMessage.data["seller"]
        }
        if(remoteMessage.data.containsKey("user")){
            user= remoteMessage.data["user"]
        }
        val intent = Intent(applicationContext, ChatActivity::class.java)
        intent.putExtra("purchasedGig", "" + body)
        intent.putExtra("openChat", user)
        intent.putExtra("notification",true);
        val pIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val n: Notification = NotificationCompat.Builder(this, "gigOrderChannelId")
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.sym_def_app_icon)
            .setContentIntent(pIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true).build()
        //.addAction(R.drawable.ic_microphone, "Accept", pIntent).build();
        //.addAction(R.drawable.ic_microphone, "Accept", pIntent).build();
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(111, n)
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        PrefHelper.getInstance(applicationContext)!!.saveString("fcm", "" + p0);
    }
}