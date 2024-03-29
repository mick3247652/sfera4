package ru.club.sfera.MarketApp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ru.club.sfera.MarketApp.Config;

import ru.club.sfera.BuildConfig;
import ru.club.sfera.R;
import ru.club.sfera.MarketApp.callbacks.CallbackUser;
import ru.club.sfera.MarketApp.fragment.FragmentCategory;
import ru.club.sfera.MarketApp.models.Setting;
import ru.club.sfera.MarketApp.models.User;
import ru.club.sfera.MarketApp.rests.ApiInterface;
import ru.club.sfera.MarketApp.rests.RestAdapter;
import ru.club.sfera.MarketApp.utils.AppBarLayoutBehavior;
import ru.club.sfera.MarketApp.utils.Constant;
import ru.club.sfera.MarketApp.utils.HttpTask;
import ru.club.sfera.MarketApp.utils.NetworkCheck;
import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.club.sfera.app.App;

public class MarketMainActivity extends AppCompatActivity {

    private long exitTime = 0;
    App app;
    private BottomNavigationView navigation;
    private ViewPager viewPager;
    private Toolbar toolbar;
    MenuItem prevMenuItem;
    //int pager_number = 5;
    int pager_number = 1;
    SharedPreferences preferences;
    BroadcastReceiver broadcastReceiver;
    View view;
    User user;
    String androidId;
    private Call<CallbackUser> callbackCall = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.market_activity_main);
        view = findViewById(android.R.id.content);

        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        if (Config.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }

        //GDPR.updateConsentStatus(this);

        AppBarLayout appBarLayout = findViewById(R.id.tab_appbar_layout);
        ((CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams()).setBehavior(new AppBarLayoutBehavior());

        app = App.getInstance();

        toolbar = findViewById(R.id.toolbarMarket);
        setSupportActionBar(toolbar);
        //toolbar.setTitle(R.string.market_app_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(pager_number);


        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    /*case R.id.navigation_home:
                        viewPager.setCurrentItem(0);
                        return true;*/
                    case R.id.navigation_category:
                        //viewPager.setCurrentItem(1);
                        viewPager.setCurrentItem(0);
                        return true;
                    /*case R.id.navigation_video:
                        viewPager.setCurrentItem(2);
                        return true;
                    case R.id.navigation_favorite:
                        viewPager.setCurrentItem(3);
                        return true;
                    case R.id.navigation_profile:
                        viewPager.setCurrentItem(4);
                        return true;*/
                }
                return false;
            }
        });
        if (Config.ENABLE_FIXED_BOTTOM_NAVIGATION) {
            navigation.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    navigation.getMenu().getItem(0).setChecked(false);
                }
                navigation.getMenu().getItem(position).setChecked(true);
                prevMenuItem = navigation.getMenu().getItem(position);

                //if (viewPager.getCurrentItem() == 1) {
                if (viewPager.getCurrentItem() == 0) {
                    toolbar.setTitle(getResources().getString(R.string.title_activity_market));
                } else if (viewPager.getCurrentItem() == 2) {
                    toolbar.setTitle(getResources().getString(R.string.market_title_nav_video));
                }else if (viewPager.getCurrentItem() == 0) {
                    toolbar.setTitle(getResources().getString(R.string.market_title_nav_home));
                } else if (viewPager.getCurrentItem() == 3) {
                    toolbar.setTitle(getResources().getString(R.string.market_title_nav_favorite));
                } else if (viewPager.getCurrentItem() == 4) {
                    toolbar.setTitle(getResources().getString(R.string.market_title_nav_profile));
                } else {
                    toolbar.setTitle(R.string.market_app_name);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (Config.ENABLE_RTL_MODE) {
            viewPager.setRotationY(180);
        }

        onReceiveNotification();

        Intent intent = getIntent();
        long id = intent.getLongExtra("id", 1L);
        String url = intent.getStringExtra("link");
        if (id != 1L) {
            if (id == 0) {
                if (!url.equals("")) {
                    if (Config.OPEN_LINK_INSIDE_APP) {
                        if (url.startsWith("http://")) {
                            Intent a = new Intent(getApplicationContext(), ActivityWebView.class);
                            a.putExtra("url", url);
                            startActivity(a);
                        }
                        if (url.startsWith("https://")) {
                            Intent b = new Intent(getApplicationContext(), ActivityWebView.class);
                            b.putExtra("url", url);
                            startActivity(b);
                        }
                        if (url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith(".png")) {
                            Intent c = new Intent(getApplicationContext(), ActivityWebViewImage.class);
                            c.putExtra("image_url", url);
                            startActivity(c);
                        }
                        if (url.endsWith(".pdf")) {
                            Intent d = new Intent(Intent.ACTION_VIEW);
                            d.setData(Uri.parse(url));
                            startActivity(d);
                        }
                    } else {
                        Intent e = new Intent(Intent.ACTION_VIEW);
                        e.setData(Uri.parse(url));
                        startActivity(e);
                    }
                }
                Log.d("FCM_INFO", " id : " + id);
            } else {
                Intent action = new Intent(MarketMainActivity.this, ActivityFCMDetail.class);
                action.putExtra("id", id);
                startActivity(action);
                Log.d("FCM_INFO", "id : " + id);
            }
        }

        requestDetailsPostApi();
        validate();

        viewPager.setCurrentItem(1);
    }

    public void onReceiveNotification() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals(Constant.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    final long id = intent.getLongExtra("id", 1L);
                    final String title = intent.getStringExtra("title");
                    final String message = intent.getStringExtra("message");
                    final String image_url = intent.getStringExtra("image_url");
                    final String url = intent.getStringExtra("link");

                    LayoutInflater layoutInflaterAndroid = LayoutInflater.from(MarketMainActivity.this);
                    View mView = layoutInflaterAndroid.inflate(R.layout.market_custom_dialog, null);

                    final AlertDialog.Builder alert = new AlertDialog.Builder(MarketMainActivity.this);
                    alert.setView(mView);

                    final TextView notification_title = mView.findViewById(R.id.title);
                    final TextView notification_message = mView.findViewById(R.id.message);
                    final ImageView notification_image = mView.findViewById(R.id.big_image);

                    if (id != 1L) {
                        if (id == 0) {
                            if (!url.equals("")) {
                                notification_title.setText(title);
                                notification_message.setText(Html.fromHtml(message));
                                Picasso.with(MarketMainActivity.this)
                                        .load(image_url.replace(" ", "%20"))
                                        .placeholder(R.drawable.market_ic_thumbnail)
                                        .into(notification_image);
                                alert.setPositiveButton("Open link", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (Config.OPEN_LINK_INSIDE_APP) {
                                            if (url.startsWith("http://")) {
                                                Intent intent = new Intent(getApplicationContext(), ActivityWebView.class);
                                                intent.putExtra("url", url);
                                                startActivity(intent);
                                            }
                                            if (url.startsWith("https://")) {
                                                Intent intent = new Intent(getApplicationContext(), ActivityWebView.class);
                                                intent.putExtra("url", url);
                                                startActivity(intent);
                                            }
                                            if (url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith(".png")) {
                                                Intent intent = new Intent(getApplicationContext(), ActivityWebViewImage.class);
                                                intent.putExtra("image_url", url);
                                                startActivity(intent);
                                            }
                                            if (url.endsWith(".pdf")) {
                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                intent.setData(Uri.parse(url));
                                                startActivity(intent);
                                            }
                                        } else {
                                            Intent intent = new Intent(Intent.ACTION_VIEW);
                                            intent.setData(Uri.parse(url));
                                            startActivity(intent);
                                        }
                                    }
                                });
                                alert.setNegativeButton(getResources().getString(R.string.market_dialog_dismiss), null);
                            } else {
                                notification_title.setText(title);
                                notification_message.setText(Html.fromHtml(message));
                                Picasso.with(MarketMainActivity.this)
                                        .load(image_url.replace(" ", "%20"))
                                        .placeholder(R.drawable.market_ic_thumbnail)
                                        .into(notification_image);
                                alert.setPositiveButton(getResources().getString(R.string.market_dialog_ok), null);
                            }
                        } else {
                            notification_title.setText(title);
                            notification_message.setText(Html.fromHtml(message));
                            Picasso.with(MarketMainActivity.this)
                                    .load(image_url.replace(" ", "%20"))
                                    .placeholder(R.drawable.market_ic_thumbnail)
                                    .into(notification_image);

                            alert.setPositiveButton(getResources().getString(R.string.market_dialog_read_more), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getApplicationContext(), ActivityFCMDetail.class);
                                    intent.putExtra("id", id);
                                    startActivity(intent);
                                }
                            });
                            alert.setNegativeButton(getResources().getString(R.string.market_dialog_dismiss), null);
                        }
                        alert.setCancelable(false);
                        alert.show();
                    }

                }
            }
        };
    }

    public class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                /*case 0:
                    return new FragmentRecent();*/
                //case 1:
                case 0:
                    return new FragmentCategory();
                /*case 2:
                    return new FragmentVideo();
                case 3:
                    return new FragmentFavorite();
                case 4:
                    return new FragmentProfile();*/
            }
            return null;
        }

        @Override
        public int getCount() {
            return pager_number;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.market_menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {

            case R.id.search:
                Intent intent = new Intent(getApplicationContext(), ActivitySearch.class);
                startActivity(intent);
                return true;

            case android.R.id.home: {

                finish();
                return true;
            }

            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public void onBackPressed() {
        /*if (viewPager.getCurrentItem() != 0) {
            viewPager.setCurrentItem((0), true);
        } else {
            exitApp();
        }*/
        finish();
    }

    public void exitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, getString(R.string.market_press_again_to_exit), Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    public void validate() {
        ApiInterface apiInterface = RestAdapter.createAPI();
        Call<Setting> call = apiInterface.getPackageName();
        call.enqueue(new Callback<Setting>() {
            @Override
            public void onResponse(Call<Setting> call, Response<Setting> response) {
                try {
                    String package_name = response.body().getPackage_name();
                    if (BuildConfig.APPLICATION_ID.equals(package_name)) {
                        Log.d("INFO", "Validated");
                    } else {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(MarketMainActivity.this);
                        dialog.setTitle(getResources().getString(R.string.market_whops));
                        dialog.setMessage(getResources().getString(R.string.market_msg_validate));
                        dialog.setPositiveButton(getResources().getString(R.string.market_dialog_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        });
                        dialog.setCancelable(false);
                        dialog.show();
                    }
                } catch (Exception e) {
                    Log.d("onResponse", "There is an error");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Setting> call, Throwable t) {
            }

        });
    }

    private void requestDetailsPostApi() {
        ApiInterface apiInterface = RestAdapter.createAPI();
        callbackCall = apiInterface.getUserToken('"' + androidId + '"');
        callbackCall.enqueue(new Callback<CallbackUser>() {
            @Override
            public void onResponse(Call<CallbackUser> call, Response<CallbackUser> response) {
                CallbackUser resp = response.body();
                if (resp != null && resp.status.equals("ok")) {
                    user = resp.response;
                    String token = user.token;
                    String unique_id = user.user_unique_id;
                    String pref_token = preferences.getString("fcm_token", null);

                    if (token.equals(pref_token) && unique_id.equals(androidId)) {
                        Log.d("TOKEN", "FCM Token already exists");
                    } else {
                        updateRegistrationIdToBackend();
                    }
                } else {
                    onFailRequest();
                }
            }

            @Override
            public void onFailure(Call<CallbackUser> call, Throwable t) {
                if (!call.isCanceled()) onFailRequest();
            }

        });
    }

    private void onFailRequest() {
        if (NetworkCheck.isConnect(this)) {
            sendRegistrationIdToBackend();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.market_msg_no_network), Toast.LENGTH_SHORT).show();
        }
    }

    private void sendRegistrationIdToBackend() {

        Log.d("FCM_TOKEN", "Send data to server...");

        String token = preferences.getString("fcm_token", null);
        String appVersion = BuildConfig.VERSION_CODE + " (" + BuildConfig.VERSION_NAME + ")";
        String osVersion = currentVersion() + " " + Build.VERSION.RELEASE;
        String model = android.os.Build.MODEL;
        String manufacturer = android.os.Build.MANUFACTURER;

        if (token != null) {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("user_android_token", token));
            nameValuePairs.add(new BasicNameValuePair("user_unique_id", androidId));
            nameValuePairs.add(new BasicNameValuePair("user_app_version", appVersion));
            nameValuePairs.add(new BasicNameValuePair("user_os_version", osVersion));
            nameValuePairs.add(new BasicNameValuePair("user_device_model", model));
            nameValuePairs.add(new BasicNameValuePair("user_device_manufacturer", manufacturer));
            new HttpTask(null, MarketMainActivity.this, Config.ADMIN_PANEL_URL + "/token-register.php", nameValuePairs, false).execute();
            Log.d("FCM_TOKEN_VALUE", token + " " + androidId);
        }

    }

    private void updateRegistrationIdToBackend() {

        Log.d("FCM_TOKEN", "Update data to server...");
        String token = preferences.getString("fcm_token", null);
        if (token != null) {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("user_android_token", token));
            nameValuePairs.add(new BasicNameValuePair("user_unique_id", androidId));
            new HttpTask(null, MarketMainActivity.this, Config.ADMIN_PANEL_URL + "/token-update.php", nameValuePairs, false).execute();
            Log.d("FCM_TOKEN_VALUE", token + " " + androidId);
        }

    }

    public static String currentVersion() {
        double release = Double.parseDouble(Build.VERSION.RELEASE.replaceAll("(\\d+[.]\\d+)(.*)", "$1"));
        String codeName = "Unsupported";
        if (release >= 4.1 && release < 4.4) codeName = "Jelly Bean";
        else if (release < 5) codeName = "Kit Kat";
        else if (release < 6) codeName = "Lollipop";
        else if (release < 7) codeName = "Marshmallow";
        else if (release < 8) codeName = "Nougat";
        else if (release < 9) codeName = "Oreo";
        return codeName;
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(Constant.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(Constant.PUSH_NOTIFICATION));
        //NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

}
