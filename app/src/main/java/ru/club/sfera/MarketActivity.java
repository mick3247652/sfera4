package ru.club.sfera;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import ru.club.sfera.adapter.SectionsPagerAdapter;
import ru.club.sfera.common.ActivityBase;

public class MarketActivity extends ActivityBase {

    Toolbar mToolbar;

    ViewPager mViewPager;
    TabLayout mTabLayout;

    SectionsPagerAdapter adapter;

    private Boolean restore = false;

    private WebViewFragment createWebViewFragment(String url){
        WebViewFragment f = new WebViewFragment();
        f.setURL(url);
        return f;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_market);

        if (savedInstanceState != null) {

            restore = savedInstanceState.getBoolean("restore");

        } else {

            restore = false;
        }

        // Toolbar

        mToolbar = (Toolbar) findViewById(R.id.toolbarMarket);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.title_market));

        // ViewPager

        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        //adapter.addFragment(new MarketFragment(), getString(R.string.tab_market));
        //adapter.addFragment(new MarketMyItemsFragment(), getString(R.string.tab_my_products));
        adapter.addFragment(createWebViewFragment("http://lenta.ru"), "lenta.ru");
        adapter.addFragment(createWebViewFragment("http://russian.rt.com"), "RT");
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(0);

        // TabLayout

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putBoolean("restore", true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
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
