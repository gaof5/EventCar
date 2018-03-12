package com.gaof.eventcar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.gaof.event.EventCar;

import java.util.Date;

public class SendActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
    }

    public void send(View view) {
        EventCar.getDefault().post(new User("1","张三",45,new Date()));
    }
}
