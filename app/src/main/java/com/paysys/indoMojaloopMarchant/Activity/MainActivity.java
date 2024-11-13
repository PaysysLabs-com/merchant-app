package com.paysys.indoMojaloopMarchant.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.paysys.indoMojaloopMarchant.R;
import com.paysys.indoMojaloopMarchant.utils.Constants;

public class MainActivity extends AppCompatActivity {
    public String NotifyData;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Button btn_seccessfull = findViewById(R.id.btn_seccessfull);
        TextView tv_dlgMsg = findViewById(R.id.tv_dlgMsg);

        NotifyData = getIntent().getStringExtra("NotifyData");
        String message = NotifyData.replace("\"", "");
        String message1 = message.replace("\\u0027", "'");
        tv_dlgMsg.setText(message1);

        btn_seccessfull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.ACTIVITY2 = true;
                finish();
            }
        });
    }
    @Override
    public void onBackPressed() {
        Constants.ACTIVITY2 = true;
        finish();

    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
