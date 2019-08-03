package ru.club.sfera;

import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.VideoView;

public class ViewVideoURLActivity extends AppCompatActivity {
    private String videoURL = "";
    Toolbar mToolbar;
    VideoView video;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_video_url);
        videoURL = getIntent().getStringExtra("videoURL");
        Log.v("VIDEO",videoURL);
        mToolbar = (Toolbar) findViewById(R.id.videoToolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Видео");

        video = (VideoView) findViewById(R.id.videoViewUrl);

        Uri uri = Uri.parse(videoURL);
        video.setVideoURI(uri);
        video.requestFocus();
        video.start();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {

            case android.R.id.home: {

                finish();

                return true;
            }

            default: {

                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    public void onBackPressed() {
        // your code.

        finish();
    }

}
