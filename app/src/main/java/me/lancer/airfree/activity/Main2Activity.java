package me.lancer.airfree.activity;

import android.content.Intent;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

import me.lancer.distance.R;

public class Main2Activity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Intent intent = new Intent(Main2Activity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
//        Timer timer = new Timer();
//        TimerTask task = new TimerTask() {
//            public void run() {
//                Intent intent = new Intent(Main2Activity.this, MainActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
//            }
//        };
//        timer.schedule(task, 3000);
    }
}
