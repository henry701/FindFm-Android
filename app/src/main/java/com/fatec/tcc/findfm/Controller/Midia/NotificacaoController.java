package com.fatec.tcc.findfm.Controller.Midia;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Views.TelaPrincipal;

public class NotificacaoController {

    private Context parent;
    private NotificationManager nManager;
    private NotificationCompat.Builder nBuilder;
    private RemoteViews remoteView;

    public NotificacaoController(Context parent) {
        // TODO Auto-generated constructor stub
        this.parent = parent;

        int NOTIFICATION_ID = 234;

        NotificationManager notificationManager = (NotificationManager) parent.getSystemService(Context.NOTIFICATION_SERVICE);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {


            String CHANNEL_ID = "my_channel_01";
            CharSequence name = "my_channel";
            String Description = "This is my channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);
        }

        nBuilder = new NotificationCompat.Builder(parent, "my_channel_01")
                .setContentTitle("Parking Meter")
                .setSmallIcon(R.drawable.ic_audio)
                .setOngoing(true);

        Intent resultIntent = new Intent(parent, TelaPrincipal.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(parent);
        stackBuilder.addParentStack(TelaPrincipal.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        nBuilder.setContentIntent(resultPendingIntent);

        notificationManager.notify(NOTIFICATION_ID, nBuilder.build());

        remoteView = new RemoteViews(parent.getPackageName(), R.layout.notificacao_view);

        //set the button listeners
        setListeners(remoteView);
        nBuilder.setContent(remoteView);

        nManager = (NotificationManager) parent.getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(2, nBuilder.build());
    }





    public void setListeners(RemoteViews view){
        //listener 1
        Intent volume = new Intent(parent, TelaPrincipal.class);
        volume.putExtra("DO", "volume");
        PendingIntent btn1 = PendingIntent.getActivity(parent, 0, volume, 0);
        view.setOnClickPendingIntent(R.id.btn1, btn1);

        //listener 2
        Intent stop = new Intent(parent, TelaPrincipal.class);
        stop.putExtra("DO", "stop");
        PendingIntent btn2 = PendingIntent.getActivity(parent, 1, stop, 0);
        view.setOnClickPendingIntent(R.id.btn2, btn2);
    }

    public void notificationCancel() {
        nManager.cancel(2);
    }
}
