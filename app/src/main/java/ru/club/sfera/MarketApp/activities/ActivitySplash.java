package ru.club.sfera.MarketApp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import ru.club.sfera.MarketApp.Config;
import ru.club.sfera.R;

public class ActivitySplash extends AppCompatActivity {

    Boolean isCancelled = false;
    private ProgressBar progressBar;
    long nid = 0;
    String url = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.market_activity_splash);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        if(getIntent().hasExtra("nid")) {
            nid = getIntent().getLongExtra("nid", 0);
            url = getIntent().getStringExtra("external_link");
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!isCancelled) {
                    if(nid == 0) {
                        if (url.equals("") || url.equals("no_url")) {
                            Intent intent = new Intent(getApplicationContext(), MarketMainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent a = new Intent(getApplicationContext(), MarketMainActivity.class);
                            startActivity(a);

                            Intent b = new Intent(getApplicationContext(), ActivityWebView.class);
                            b.putExtra("url", url);
                            startActivity(b);

                            finish();
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), ActivityOneSignalDetail.class);
                        intent.putExtra("id", nid);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        },Config.SPLASH_TIME);

    }
}
