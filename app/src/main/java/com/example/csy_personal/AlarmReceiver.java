package com.example.csy_personal;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.widget.Toast;

/**
 * Created by 차승용 on 2016-02-24.
 */
public class AlarmReceiver extends BroadcastReceiver {
    MediaPlayer mPlayer = new MediaPlayer();         // 객체생성
    Vibrator vide;

    @Override
    public void onReceive(Context context, Intent intent) {
        vide = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vide.vibrate(1000);

        /*Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

        try {
            // 이렇게 URI 객체를 그대로 삽입해줘야한다.
            //인터넷에서 url.toString() 으로 하는것이 보이는데 해보니까 안된다 -_-;
            mPlayer.setDataSource(context, alert);


            // 출력방식(재생시 사용할 방식)을 설정한다. STREAM_RING 은 외장 스피커로,
            // STREAM_VOICE_CALL 은 전화-수신 스피커를 사용한다.
            mPlayer.setAudioStreamType(AudioManager.STREAM_RING);

            mPlayer.setLooping(true);  // 반복여부 지정
            mPlayer.prepare();    // 실행전 준비
        } catch (IOException e) {
            e.printStackTrace();
        }
        mPlayer.start();   // 실행 시작*/

        try {
            //((DetailActivity) DetailActivity.mContext).startSound(context);
            intent = new Intent(context, AlarmSound.class);
            PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            try {
                pi.send();
            } catch (PendingIntent.CanceledException e) {
                ;
            }
            //Toast.makeText(context, "소리 성공", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(context,"소리 실패",Toast.LENGTH_LONG).show();
        }
    }
}
