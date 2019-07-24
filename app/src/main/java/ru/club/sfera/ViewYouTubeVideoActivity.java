package ru.club.sfera;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import ru.club.sfera.constants.Constants;

public class ViewYouTubeVideoActivity extends AppCompatActivity implements YouTubePlayer.OnInitializedListener {
    private static final int RECOVERY_REQUEST = 1;
    ImageLoader imageLoader;
    String mVideoCode;
    YouTubePlayerSupportFragment mYouTubeView;
    Toolbar mToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_youtube_video);
        mToolbar = (Toolbar) findViewById(R.id.toolbarMarket);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mYouTubeView = (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.youtube_fragment);
        mYouTubeView.initialize(Constants.YOUTUBE_API_KEY, this);
        Intent i = getIntent();

        mVideoCode = i.getStringExtra("videoCode");
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

    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {

        if (!wasRestored) {

            player.cueVideo(mVideoCode);
        }
    }

    @Override
    public void onInitializationFailure(Provider provider, YouTubeInitializationResult errorReason) {

        if (errorReason.isUserRecoverableError()) {

            errorReason.getErrorDialog(this, RECOVERY_REQUEST).show();

        } else {

            String error = String.format(getString(R.string.youtube_player_error), errorReason.toString());
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RECOVERY_REQUEST) {

            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(Constants.YOUTUBE_API_KEY, this);
        }
    }

    protected Provider getYouTubePlayerProvider() {

        return mYouTubeView;
    }

}
    /*
public class ViewYouTubeVideoActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    Toolbar mToolbar;

    YouTubePlayerView mYouTubeView;

    private static final int RECOVERY_REQUEST = 1;

    ImageLoader imageLoader;

    String mVideoCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_youtube_video);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mYouTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);

        mYouTubeView.initialize(Constants.YOUTUBE_API_KEY, this);

        Intent i = getIntent();

        mVideoCode = i.getStringExtra("videoCode");
    }

    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {

        if (!wasRestored) {

            player.cueVideo(mVideoCode);
        }
    }

    @Override
    public void onInitializationFailure(Provider provider, YouTubeInitializationResult errorReason) {

        if (errorReason.isUserRecoverableError()) {

            errorReason.getErrorDialog(this, RECOVERY_REQUEST).show();

        } else {

            String error = String.format(getString(R.string.youtube_player_error), errorReason.toString());
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RECOVERY_REQUEST) {

            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(Constants.YOUTUBE_API_KEY, this);
        }
    }

    protected Provider getYouTubePlayerProvider() {

        return mYouTubeView;
    }
}*/
