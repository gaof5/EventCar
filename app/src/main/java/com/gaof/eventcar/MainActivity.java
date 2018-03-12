package com.gaof.eventcar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.gaof.event.EventCar;
import com.gaof.event.Subscriber;
import com.gaof.event.ThreadMode;

public class MainActivity extends AppCompatActivity {

    private TextView tvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvContent=findViewById(R.id.tvContent);
        EventCar.getDefault().register(this);
    }

    public void jump(View view) {
        startActivity(new Intent(MainActivity.this,SendActivity.class));
    }

    @Subscriber(ThreadMode.PostThread)
    void reciever(User user){
        tvContent.setText(user.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventCar.getDefault().unregister(this);
    }
}
