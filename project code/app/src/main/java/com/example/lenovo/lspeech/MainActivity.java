package com.example.lenovo.lspeech;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends Activity implements TextToSpeech.OnInitListener{

    private TextView txt;
    private ProgressBar pb;
    private ImageView logo;
    private TextToSpeech tts;
    private int progressStatus=0;
    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt = (TextView) findViewById(R.id.txt);
        pb = (ProgressBar) findViewById(R.id.pb);
        logo = (ImageView) findViewById(R.id.logo);
        Animation myanim = AnimationUtils.loadAnimation(this,R.anim.splash_anim);
        logo.startAnimation(myanim);
        txt.startAnimation(myanim);
        pb.startAnimation(myanim);
        tts = new TextToSpeech(this,this);
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speakOut();

            }
        });
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speakOut1();
            }
        });
        pb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakOut2();
            }
        });
        final Intent my = new Intent(this,Registration.class);
        Thread timer = new Thread(){
            public  void run()
            {
                try {
                    sleep(4000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                finally {
                    startActivity(my);
                    finish();
                }
            }
        };
        timer.start();
    }

    @Override
    public void onDestroy() {
        if(tts!=null){
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status){
        if(status==TextToSpeech.SUCCESS){

            int result = tts.setLanguage(Locale.US);
            if(result == TextToSpeech.LANG_MISSING_DATA||result == TextToSpeech.LANG_NOT_SUPPORTED){

                Log.e("TTS","This language is not supported");
            }

            else
            {
                txt.setEnabled(true);
                speakOut();
            }
        }
        else
        {
            Log.e("TTS","Initization Failed!");
        }
    }
    private void speakOut1(){
        String text1 = "This is image";
        tts.speak(text1,TextToSpeech.QUEUE_FLUSH,null);
    }
    private void speakOut()
    {
        String text = txt.getText().toString();
        tts.speak(text,TextToSpeech.QUEUE_FLUSH,null);
    }
    private void speakOut2()
    {
        String text = "Loading";
        tts.speak(text,TextToSpeech.QUEUE_FLUSH,null);
    }
}
