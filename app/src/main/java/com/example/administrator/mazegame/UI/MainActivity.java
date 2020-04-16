package com.example.administrator.mazegame.UI;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.administrator.mazegame.Model.MazeGame;
import com.example.administrator.mazegame.R;

public class MainActivity extends AppCompatActivity {
    public static String SHARED_PREF = "SHAREDPREEF";
    public static String BEST_SCORE = "BESTSCORE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setButton(R.id.start_btn);
        setButton(R.id.setting_btn);
        setButton(R.id.help_btn);
    }

    private void setButton(final int btnId){
        final Button btn = (Button) findViewById(btnId);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                switch(btnId){
                    case R.id.start_btn:
                        startActivity(Game.makeIntent(MainActivity.this));
                        break;
                    case R.id.setting_btn:
                        break;
                    case R.id.help_btn:
                        break;
                }
            }
        });
    }
}
