package ru.club.sfera.MarketApp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.appbar.AppBarLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.core.view.ViewCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import ru.club.sfera.MarketApp.Config;
import ru.club.sfera.R;
import ru.club.sfera.MarketApp.models.News;
import ru.club.sfera.MarketApp.utils.AppBarLayoutBehavior;
import ru.club.sfera.MarketApp.utils.Constant;
import ru.club.sfera.MarketApp.utils.DbHandler;
import ru.club.sfera.MarketApp.utils.Tools;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ActivityNewsDetail extends AppCompatActivity {

    public static final String EXTRA_OBJC = "key.EXTRA_OBJC";

    // give preparation animation activity transition
    public static void navigate(AppCompatActivity activity, View transitionView, News obj) {
        Intent intent = new Intent(activity, ActivityNewsDetail.class);
        intent.putExtra(EXTRA_OBJC, obj);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionView, EXTRA_OBJC);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    private News post;
    View parent_view, lyt_parent;
    private Menu menu;
    TextView txt_title, txt_category, txt_date, txt_comment_count, txt_comment_text;
    ImageView btn_comment, img_thumb_video;
    private WebView webview;
    private AdView adView;
    BroadcastReceiver broadcastReceiver;
    private InterstitialAd interstitialAd;
    DbHandler databaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.market_activity_news_detail);

        if (Config.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }

        databaseHandler = new DbHandler(getApplicationContext());

        AppBarLayout appBarLayout = findViewById(R.id.appBarLayout);
        ((CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams()).setBehavior(new AppBarLayoutBehavior());

        parent_view = findViewById(android.R.id.content);
        webview = findViewById(R.id.news_description);
        lyt_parent = findViewById(R.id.lyt_parent);

        txt_title = findViewById(R.id.title);
        txt_category = findViewById(R.id.category);
        txt_date = findViewById(R.id.date);
        txt_comment_count = findViewById(R.id.txt_comment_count);
        txt_comment_text = findViewById(R.id.txt_comment_text);
        btn_comment = findViewById(R.id.btn_comment);
        img_thumb_video = findViewById(R.id.thumbnail_video);

        btn_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityComments.class);
                intent.putExtra("nid", post.nid);
                intent.putExtra("count", post.comments_count);
                startActivity(intent);
            }
        });

        txt_comment_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityComments.class);
                intent.putExtra("nid", post.nid);
                intent.putExtra("count", post.comments_count);
                startActivity(intent);
            }
        });

        // animation transition
        ViewCompat.setTransitionName(findViewById(R.id.image), EXTRA_OBJC);

        // get extra object
        post = (News) getIntent().getSerializableExtra(EXTRA_OBJC);

        initToolbar();

        if (Config.ENABLE_RTL_MODE) {
            displayDataRTL();
        } else {
            displayData();
        }

        initAds();
        loadBannerAd();
        loadInterstitialAd();
        onReceiveNotification();

    }

    private void initToolbar() {
        final Toolbar toolbar = findViewById(R.id.toolbarMarket);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle("");
        }
    }

    private void displayData() {
        txt_title.setText(Html.fromHtml(post.news_title));
        txt_comment_count.setText("" + post.comments_count);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (post.comments_count == 0) {
                    txt_comment_text.setText(R.string.market_txt_no_comment);
                }
                if (post.comments_count == 1) {
                    txt_comment_text.setText(getResources().getString(R.string.market_txt_read) + " " + post.comments_count + " " + getResources().getString(R.string.market_txt_comment));
                } else if (post.comments_count > 1) {
                    txt_comment_text.setText(getResources().getString(R.string.market_txt_read) + " " + post.comments_count + " " + getResources().getString(R.string.market_txt_comments));
                }
            }
        }, 1000);

        webview.setBackgroundColor(Color.parseColor("#ffffff"));
        webview.getSettings().setDefaultTextEncodingName("UTF-8");
        webview.setFocusableInTouchMode(false);
        webview.setFocusable(false);

        if (!Config.ENABLE_TEXT_SELECTION) {
            webview.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return true;
                }
            });
            webview.setLongClickable(false);
        }

        webview.getSettings().setJavaScriptEnabled(true);

        WebSettings webSettings = webview.getSettings();
        Resources res = getResources();
        int fontSize = res.getInteger(R.integer.font_size);
        webSettings.setDefaultFontSize(fontSize);

        String mimeType = "text/html; charset=UTF-8";
        String encoding = "utf-8";
        String htmlText = post.news_description;

        String text = "<html><head>"
                + "<style>img{max-width:100%;height:auto;} figure{max-width:100%;height:auto;} iframe{width:100%;}</style> "
                + "<style type=\"text/css\">body{color: #000000;}"
                + "</style></head>"
                + "<body>"
                + htmlText
                + "</body></html>";

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
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
                return true;
            }
        });

        webview.loadData(text, mimeType, encoding);

        txt_category.setText(post.category_name);
        txt_category.setBackgroundColor(ContextCompat.getColor(this, R.color.market_colorCategory));

        if (Config.ENABLE_DATE_DISPLAY) {
            txt_date.setVisibility(View.VISIBLE);
        } else {
            txt_date.setVisibility(View.GONE);
        }
        txt_date.setText(Tools.getFormatedDate(post.news_date));

        ImageView news_image = findViewById(R.id.image);

        if (post.content_type != null && post.content_type.equals("youtube")) {
            Picasso.with(this)
                    .load(Constant.YOUTUBE_IMG_FRONT + post.video_id + Constant.YOUTUBE_IMG_BACK)
                    .placeholder(R.drawable.market_ic_thumbnail)
                    .into(news_image);

            news_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ActivityYoutubePlayer.class);
                    intent.putExtra("video_id", post.video_id);
                    startActivity(intent);

                    showInterstitialAd();
                }
            });

        } else if (post.content_type != null && post.content_type.equals("Url")) {

            Picasso.with(this)
                    .load(Config.ADMIN_PANEL_URL + "/upload/" + post.news_image.replace(" ", "%20"))
                    .placeholder(R.drawable.market_ic_thumbnail)
                    .into(news_image);

            news_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ActivityVideoPlayer.class);
                    intent.putExtra("video_url", post.video_url);
                    startActivity(intent);

                    showInterstitialAd();
                }
            });
        } else if (post.content_type != null && post.content_type.equals("Upload")) {

            Picasso.with(this)
                    .load(Config.ADMIN_PANEL_URL + "/upload/" + post.news_image.replace(" ", "%20"))
                    .placeholder(R.drawable.market_ic_thumbnail)
                    .into(news_image);

            news_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ActivityVideoPlayer.class);
                    intent.putExtra("video_url", Config.ADMIN_PANEL_URL + "/upload/video/" + post.video_url);
                    startActivity(intent);

                    showInterstitialAd();
                }
            });
        } else {
            Picasso.with(this)
                    .load(Config.ADMIN_PANEL_URL + "/upload/" + post.news_image.replace(" ", "%20"))
                    .placeholder(R.drawable.market_ic_thumbnail)
                    .into(news_image);

            news_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), ActivityFullScreenImage.class);
                    intent.putExtra("image", post.news_image);
                    startActivity(intent);
                }
            });
        }

        if (!post.content_type.equals("Post")) {
            img_thumb_video.setVisibility(View.VISIBLE);
        } else {
            img_thumb_video.setVisibility(View.GONE);
        }

    }

    private void displayDataRTL() {
        txt_title.setText(Html.fromHtml(post.news_title));
        txt_comment_count.setText("" + post.comments_count);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (post.comments_count == 0) {
                    txt_comment_text.setText(R.string.market_txt_no_comment);
                }
                if (post.comments_count == 1) {
                    txt_comment_text.setText(getResources().getString(R.string.market_txt_read) + " " + post.comments_count + " " + getResources().getString(R.string.market_txt_comment));
                } else if (post.comments_count > 1) {
                    txt_comment_text.setText(getResources().getString(R.string.market_txt_read) + " " + post.comments_count + " " + getResources().getString(R.string.market_txt_comments));
                }
            }
        }, 1000);

        webview.setBackgroundColor(Color.parseColor("#ffffff"));
        webview.getSettings().setDefaultTextEncodingName("UTF-8");
        webview.setFocusableInTouchMode(false);
        webview.setFocusable(false);

        if (!Config.ENABLE_TEXT_SELECTION) {
            webview.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return true;
                }
            });
            webview.setLongClickable(false);
        }

        webview.getSettings().setJavaScriptEnabled(true);

        WebSettings webSettings = webview.getSettings();
        Resources res = getResources();
        int fontSize = res.getInteger(R.integer.font_size);
        webSettings.setDefaultFontSize(fontSize);

        String mimeType = "text/html; charset=UTF-8";
        String encoding = "utf-8";
        String htmlText = post.news_description;

        String text = "<html dir='rtl'><head>"
                + "<style>img{max-width:100%;height:auto;} figure{max-width:100%;height:auto;} iframe{width:100%;}</style> "
                + "<style type=\"text/css\">body{color: #000000;}"
                + "</style></head>"
                + "<body>"
                + htmlText
                + "</body></html>";

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
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
                return true;
            }
        });

        webview.loadData(text, mimeType, encoding);

        txt_category.setText(post.category_name);
        txt_category.setBackgroundColor(ContextCompat.getColor(this, R.color.market_colorCategory));

        txt_date.setText(Tools.getFormatedDate(post.news_date));

        ImageView news_image = findViewById(R.id.image);

        if (post.content_type != null && post.content_type.equals("youtube")) {
            Picasso.with(this)
                    .load(Constant.YOUTUBE_IMG_FRONT + post.video_id + Constant.YOUTUBE_IMG_BACK)
                    .placeholder(R.drawable.market_ic_thumbnail)
                    .into(news_image);

            news_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ActivityYoutubePlayer.class);
                    intent.putExtra("video_id", post.video_id);
                    startActivity(intent);

                    showInterstitialAd();
                }
            });

        } else if (post.content_type != null && post.content_type.equals("Url")) {

            Picasso.with(this)
                    .load(Config.ADMIN_PANEL_URL + "/upload/" + post.news_image.replace(" ", "%20"))
                    .placeholder(R.drawable.market_ic_thumbnail)
                    .into(news_image);

            news_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ActivityVideoPlayer.class);
                    intent.putExtra("video_url", post.video_url);
                    startActivity(intent);

                    showInterstitialAd();
                }
            });
        } else if (post.content_type != null && post.content_type.equals("Upload")) {

            Picasso.with(this)
                    .load(Config.ADMIN_PANEL_URL + "/upload/" + post.news_image.replace(" ", "%20"))
                    .placeholder(R.drawable.market_ic_thumbnail)
                    .into(news_image);

            news_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ActivityVideoPlayer.class);
                    intent.putExtra("video_url", Config.ADMIN_PANEL_URL + "/upload/video/" + post.video_url);
                    startActivity(intent);

                    showInterstitialAd();
                }
            });
        } else {
            Picasso.with(this)
                    .load(Config.ADMIN_PANEL_URL + "/upload/" + post.news_image.replace(" ", "%20"))
                    .placeholder(R.drawable.market_ic_thumbnail)
                    .into(news_image);

            news_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), ActivityFullScreenImage.class);
                    intent.putExtra("image", post.news_image);
                    startActivity(intent);
                }
            });
        }

        if (!post.content_type.equals("Post")) {
            img_thumb_video.setVisibility(View.VISIBLE);
        } else {
            img_thumb_video.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.market_menu_news_detail, menu);
        this.menu = menu;
        //addToFavorite();
        return true;
    }

    public void addToFavorite() {
        List<News> data = databaseHandler.getFavRow(post.nid);
        if (data.size() == 0) {
            menu.getItem(0).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.market_ic_favorite_outline_white));
        } else {
            if (data.get(0).getNid() == post.nid) {
                menu.getItem(0).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.market_ic_favorite_white));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            /*case R.id.action_later:

                List<News> data = databaseHandler.getFavRow(post.nid);
                if (data.size() == 0) {
                    databaseHandler.AddtoFavorite(new News(
                            post.nid,
                            post.news_title,
                            post.category_name,
                            post.news_date,
                            post.news_image,
                            post.news_description,
                            post.content_type,
                            post.video_url,
                            post.video_id,
                            post.comments_count
                    ));
                    Snackbar.make(parent_view, R.string.market_favorite_added, Snackbar.LENGTH_SHORT).show();
                    menu.getItem(0).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.market_ic_favorite_white));

                } else {
                    if (data.get(0).getNid() == post.nid) {
                        databaseHandler.RemoveFav(new News(post.nid));
                        Snackbar.make(parent_view, R.string.market_favorite_removed, Snackbar.LENGTH_SHORT).show();
                        menu.getItem(0).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.market_ic_favorite_outline_white));
                    }
                }

                break;*/

            case R.id.action_share:

                String formattedString = android.text.Html.fromHtml(post.news_description).toString();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, post.news_title + "\n" + formattedString + "\n" + getResources().getString(R.string.market_share_content) + "https://play.google.com/store/apps/details?id=" + getPackageName());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);

                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    public void initAds() {
        if (Config.ENABLE_ADMOB_BANNER_ADS || Config.ENABLE_ADMOB_INTERSTITIAL_ADS) {
            MobileAds.initialize(this, getResources().getString(R.string.market_admob_app_id));
        }
    }

    public void loadBannerAd() {
        if (Config.ENABLE_ADMOB_BANNER_ADS) {
            adView = findViewById(R.id.adView);
            adView.loadAd(Tools.getAdRequest(this));
            adView.setAdListener(new AdListener() {

                @Override
                public void onAdClosed() {
                }

                @Override
                public void onAdFailedToLoad(int error) {
                    adView.setVisibility(View.GONE);
                }

                @Override
                public void onAdLeftApplication() {
                }

                @Override
                public void onAdOpened() {
                }

                @Override
                public void onAdLoaded() {
                    adView.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private void loadInterstitialAd() {
        if (Config.ENABLE_ADMOB_INTERSTITIAL_ADS_ON_CLICK_VIDEO) {
            interstitialAd = new InterstitialAd(getApplicationContext());
            interstitialAd.setAdUnitId(getResources().getString(R.string.market_admob_interstitial_unit_id));
            interstitialAd.loadAd(Tools.getAdRequest(this));
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    interstitialAd.loadAd(new AdRequest.Builder().build());
                }
            });
        }
    }

    private void showInterstitialAd() {
        if (Config.ENABLE_ADMOB_INTERSTITIAL_ADS_ON_CLICK_VIDEO) {
            if (interstitialAd != null && interstitialAd.isLoaded()) {
                interstitialAd.show();
            }
        }
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

                    LayoutInflater layoutInflaterAndroid = LayoutInflater.from(ActivityNewsDetail.this);
                    View mView = layoutInflaterAndroid.inflate(R.layout.market_custom_dialog, null);

                    final AlertDialog.Builder alert = new AlertDialog.Builder(ActivityNewsDetail.this);
                    alert.setView(mView);

                    final TextView notification_title = mView.findViewById(R.id.title);
                    final TextView notification_message = mView.findViewById(R.id.message);
                    final ImageView notification_image = mView.findViewById(R.id.big_image);

                    if (id != 1L) {
                        if (id == 0) {
                            if (!url.equals("")) {
                                notification_title.setText(title);
                                notification_message.setText(Html.fromHtml(message));
                                Picasso.with(ActivityNewsDetail.this)
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
                                Picasso.with(ActivityNewsDetail.this)
                                        .load(image_url.replace(" ", "%20"))
                                        .placeholder(R.drawable.market_ic_thumbnail)
                                        .into(notification_image);
                                alert.setPositiveButton(getResources().getString(R.string.market_dialog_ok), null);
                            }
                        } else {
                            notification_title.setText(title);
                            notification_message.setText(Html.fromHtml(message));
                            Picasso.with(ActivityNewsDetail.this)
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
