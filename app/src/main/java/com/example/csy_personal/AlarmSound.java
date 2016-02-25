package com.example.csy_personal;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by 차승용 on 2016-02-25.
 */
public class AlarmSound extends Activity {

    private ActivityManager actManager = ActivityManager.getInstance();
    MediaPlayer mPlayer = new MediaPlayer();         // 객체생성

    @Override
    public void onCreate(Bundle SaveInstanceState) {
        super.onCreate(SaveInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.alarm);

        actManager.addActivity(this);

        startSound();
    }

    public void startSound() {

        Toast.makeText(this, "소리 시도", Toast.LENGTH_SHORT).show();
        // TYPE_RINGTONE 을 하면 현재 설정되어 있는 밸소리를 가져온다.
        // 만약 알람음을 가져오고 싶다면 TYPE_ALARM 을 이용하면 된다
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

        try {
            // 이렇게 URI 객체를 그대로 삽입해줘야한다.
            //인터넷에서 url.toString() 으로 하는것이 보이는데 해보니까 안된다 -_-;
            mPlayer.setDataSource(this, alert);


            // 출력방식(재생시 사용할 방식)을 설정한다. STREAM_RING 은 외장 스피커로,
            // STREAM_VOICE_CALL 은 전화-수신 스피커를 사용한다.
            mPlayer.setAudioStreamType(AudioManager.STREAM_RING);

            mPlayer.setLooping(true);  // 반복여부 지정-
            mPlayer.prepare();    // 실행전 준비
        } catch (IOException e) {
            e.printStackTrace();
        }
        mPlayer.start();   // 실행 시작
    }

    public void stopAlram(View v){
        mPlayer.stop();
        actManager.finishAllActivity();
        finish();
    }
}
