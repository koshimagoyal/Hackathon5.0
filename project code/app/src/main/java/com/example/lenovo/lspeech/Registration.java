package com.example.lenovo.lspeech;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Locale;


public class Registration extends Activity implements View.OnClickListener,TextToSpeech.OnInitListener{

    private TextView mStatusText,mDetailText;
    private FirebaseAuth mAuth;
    private EditText memail,mpass;
    private static final String TAG = "Email Password";
    int no=0;
    boolean thread_s=false;
    final int delay_b=250;
    private TextToSpeech tts;
    private final int REQ_CODE_SPEECH_INPUT=10000;
    private final int REQ_CODE_SPEECH_INPUT1=20000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        //firebase
        mAuth = FirebaseAuth.getInstance();
        //speech
        tts = new TextToSpeech(this,this);
        //from ui
        mStatusText = findViewById(R.id.statustext);
        mDetailText = findViewById(R.id.detailtext);
        memail = findViewById(R.id.email);
        mpass = findViewById(R.id.password);
        findViewById(R.id.login).setOnClickListener(this);
        findViewById(R.id.signup).setOnClickListener(this);
        findViewById(R.id.signout).setOnClickListener(this);
        //findViewById(R.id.verify_btn).setOnClickListener(this);

        mStatusText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               speakOutStatus();
            }
        });

        mDetailText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakOutDetail();
            }
        });

        memail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ++no;
                if(!thread_s) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            thread_s = true;
                            try {
                                Thread.sleep(delay_b);
                                if (no == 1) {
                                    speakOutEmail();
                                } else if (no == 2) {
                                    promptSpeechInput();
                                }
                                no = 0;
                                thread_s = false;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        });

        mpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ++no;
                if(!thread_s) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            thread_s = true;
                            try {
                                Thread.sleep(delay_b);
                                if (no == 1) {
                                    speakOutPass();
                                } else if (no == 2) {
                                    promptSpeechInput1();
                                }
                                no = 0;
                                thread_s = false;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        });


    }

    //PromptSpeech1
    private void promptSpeechInput1(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,getString(R.string.speech_prompt));
        try{
            startActivityForResult(intent,REQ_CODE_SPEECH_INPUT1);
            speakOutSpeak();
        }
        catch (ActivityNotFoundException a)
        {
            Toast.makeText(getApplicationContext(),getString(R.string.speech_not_prompted),Toast.LENGTH_LONG).show();
            speakOutFailure();
        }
    }

    //PromptSpeech
    private void promptSpeechInput(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,getString(R.string.speech_prompt));
        try{
            startActivityForResult(intent,REQ_CODE_SPEECH_INPUT);
            speakOutSpeak();
        }
        catch (ActivityNotFoundException a)
        {
            Toast.makeText(getApplicationContext(),getString(R.string.speech_not_prompted),Toast.LENGTH_LONG).show();
            speakOutFailure();
        }
    }

    /** Receive Speech Input **/
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        switch (requestCode){

            case REQ_CODE_SPEECH_INPUT: {

                if(resultCode == RESULT_OK && null!=data)
                {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String str = result.get(0).toLowerCase();
                    memail.setText(str.replace(" ",""));
                }
                break;
            }

            case REQ_CODE_SPEECH_INPUT1: {

                if(resultCode == RESULT_OK && null!=data)
                {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    mpass.setText(result.get(0));
                }
                break;
            }
        }
    }


    //Speech
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
            tts.setSpeechRate(1.0f);
            tts.setPitch(1.0f);
            if(result == TextToSpeech.LANG_MISSING_DATA||result == TextToSpeech.LANG_NOT_SUPPORTED){

                Log.e("TTS","This language is not supported");
                speakOutFailure();
            }

            else
            {
                speakOutStatus();
            }
        }
        else
        {
            Log.e("TTS","Initization Failed!");
            speakOutFailure();
        }
    }
    private void speakOutSignOut1(){
        String text1 = "You have been successfully logged out";
        tts.speak(text1,TextToSpeech.QUEUE_FLUSH,null);
    }
    private void speakOutStatus(){
        String text1 = mStatusText.getText().toString();
        tts.speak(text1,TextToSpeech.QUEUE_FLUSH,null);
    }
    private void speakOutDetail()
    {
        String text1 = mDetailText.getText().toString();
        tts.speak(text1,TextToSpeech.QUEUE_FLUSH,null);
    }
    private void speakOutEmail()
    {
        String text1 = "enter your email";
        tts.speak(text1,TextToSpeech.QUEUE_FLUSH,null);
    }
    private void speakOutPass()
    {
        String text1 = "enter your password";
        tts.speak(text1,TextToSpeech.QUEUE_FLUSH,null);
    }
    private void speakOutSignIn()
    {
        String text1 = "Double Click to sign in";
        tts.speak(text1,TextToSpeech.QUEUE_FLUSH,null);
    }
    private void speakOutSignOut()
    {
        String text1 = "Double Click to sign out";
        tts.speak(text1,TextToSpeech.QUEUE_FLUSH,null);
    }
    /*private void speakOutVerify()
    {
        String text1 = "Double Click to verify your account";
        tts.speak(text1,TextToSpeech.QUEUE_FLUSH,null);
    }*/
    private void speakOutSignUp()
    {
        String text1 = "Double Click to sign up";
        tts.speak(text1,TextToSpeech.QUEUE_FLUSH,null);
    }
    private void speakOutSpeak()
    {
        String text = "Please Speak Slowly";
        tts.speak(text,TextToSpeech.QUEUE_FLUSH,null);
    }
    private void speakOut()
    {
        String text = "Please Fill your details";
        tts.speak(text,TextToSpeech.QUEUE_FLUSH,null);
    }
    private void speakOutSuccess()
    {
        String text = "Success";
        tts.speak(text,TextToSpeech.QUEUE_FLUSH,null);
    }
    private void speakOutFailure()
    {
        String text = "Failure";
        tts.speak(text,TextToSpeech.QUEUE_FLUSH,null);
    }
    private void speakOutEmailError()
    {
        String text = "Error in Email Field";
        tts.speak(text,TextToSpeech.QUEUE_FLUSH,null);
    }
    private void speakOutPassError()
    {
        String text = "Error in Password Field";
        tts.speak(text,TextToSpeech.QUEUE_FLUSH,null);
    }
    //firebase
    @Override
    public void onStart(){
        super.onStart();
        /*FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);*/
    }

    private void createAccount(String email,String password){
        Log.d(TAG,"CreateAccount:"+email);
        if(!validateForm()){
            return;
        }

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful())
                {
                    Log.d(TAG,"createUserWithEmail:Success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                    speakOutSuccess();
                    speakOutStatus();
                }
                else
                {
                    Log.w(TAG,"createUserWithEmail:Failure");
                    Toast.makeText(Registration.this,"Authentication Failed",Toast.LENGTH_SHORT).show();
                    updateUI(null);
                    speakOutFailure();
                }
            }
        });
    }

    private void signIn(String email,String password){
        Log.d(TAG,"signIn:"+email);
        if(!validateForm())
        {
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful())
                {
                    Log.d(TAG,"signInWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                    speakOutStatus();
                }
                else
                {
                    Log.w(TAG,"signInWithEmail:failure",task.getException());
                    Toast.makeText(Registration.this,"Authentication Failed",Toast.LENGTH_SHORT).show();
                    updateUI(null);
                    speakOutFailure();
                }

                if(!task.isSuccessful())
                {
                    mStatusText.setText(R.string.auth_failed);
                    speakOutStatus();
                }
            }
        });
    }

    private void signOut()
    {
        mAuth.signOut();
        updateUI(null);
        speakOutSignOut1();
    }

   /* private void sendEmailVerification()
    {
        findViewById(R.id.verify_btn).setEnabled(false);

        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                findViewById(R.id.verify_btn).setEnabled(true);

                if(!task.isSuccessful())
                {
                    Toast.makeText(Registration.this,"Verification sent to "+user.getEmail(),Toast.LENGTH_SHORT).show();
                    speakOutSuccess();
                }
                else
                {
                    Log.e(TAG,"sendEmailVerification",task.getException());
                    Toast.makeText(Registration.this,"Failed to send verification email",Toast.LENGTH_SHORT).show();
                    speakOutFailure();
                }
            }
        });
    }*/

    private boolean validateForm()
    {
        boolean valid=true;

        String email = memail.getText().toString();
        if(TextUtils.isEmpty(email))
        {
            memail.setError("Required");
            valid = false;
            speakOutEmailError();
        }
        else
        {
            memail.setError(null);
        }
        String pass = mpass.getText().toString();
        if(TextUtils.isEmpty(pass)){
            mpass.setError("Required");
            valid = false;
            speakOutPassError();
        }
        else
        {
            mpass.setError(null);
        }
        return valid;
    }

    private void updateUI(FirebaseUser user){

        if(user!=null)
        {
            mStatusText.setText(getString(R.string.sign_in));
            mDetailText.setText(getString(R.string.firebase_status_fmt,user.getUid()));

            findViewById(R.id.email_password_buttons).setVisibility(View.GONE);
            findViewById(R.id.email_password_fields).setVisibility(View.GONE);
            findViewById(R.id.signed_in_buttons).setVisibility(View.VISIBLE);

            //findViewById(R.id.verify_btn).setEnabled(!user.isEmailVerified());
        }
        else
        {
            mStatusText.setText(R.string.signed_out);
            mDetailText.setText(null);

            findViewById(R.id.email_password_buttons).setVisibility(View.VISIBLE);
            findViewById(R.id.email_password_fields).setVisibility(View.VISIBLE);
            findViewById(R.id.signed_in_buttons).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v){
        int i = v.getId();
        if(i==R.id.signup)
        {
            ++no;
            if(!thread_s) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        thread_s = true;
                        try {
                            Thread.sleep(delay_b);
                            if (no == 1) {
                                speakOutSignUp();
                            } else if (no == 2) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        createAccount(memail.getText().toString(),mpass.getText().toString());
                                    }
                                });
                            }
                            no = 0;
                            thread_s = false;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }
        else if(i==R.id.login)
        {
            ++no;
            if(!thread_s) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        thread_s = true;
                        try {
                            Thread.sleep(delay_b);
                            if (no == 1) {
                                speakOutSignIn();
                            } else if (no == 2) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        signIn(memail.getText().toString(),mpass.getText().toString());
                                    }
                                });
                            }
                            no = 0;
                            thread_s = false;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }
        else if(i==R.id.signout)
        {
            ++no;
            if(!thread_s) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        thread_s = true;
                        try {
                            Thread.sleep(delay_b);
                            if (no == 1) {
                                speakOutSignOut();
                            } else if (no == 2) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        signOut();
                                    }
                                });
                            }
                            no = 0;
                            thread_s = false;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }
       /* else if(i==R.id.verify_btn)
        {
            sendEmailVerification();

        }*/
    }


}


