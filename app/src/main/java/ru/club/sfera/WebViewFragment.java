package ru.club.sfera;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.Toast;

import ru.club.sfera.app.App;


/**
 * A simple {@link Fragment} subclass.
 */
public class WebViewFragment extends Fragment {

    private String mURL = "";

    RelativeLayout mWebViewLoadingScreen, mWebViewErrorScreen, mWebViewContentScreen;

    WebView mWebView;

    String sUrl;

    public WebViewFragment() {
        // Required empty public constructor
    }

    public void setURL(String url){
        mURL = url;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_web_view, container, false);
        sUrl = mURL;

        mWebViewErrorScreen = (RelativeLayout) rootView.findViewById(R.id.WebViewErrorScreen);
        mWebViewLoadingScreen = (RelativeLayout) rootView.findViewById(R.id.WebViewLoadingScreen);
        mWebViewContentScreen = (RelativeLayout) rootView.findViewById(R.id.WebViewContentScreen);

        mWebView = (WebView) rootView.findViewById(R.id.webView);

        mWebView.setWebViewClient(new WebViewClient() {

            //If you will not use this method url links are opeen in new brower not in webview
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                view.loadUrl(url);
                return true;
            }

            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                showLoadingScreen();
            }

            public void onPageFinished(WebView view, String url) {

                showContentScreen();
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

                showErrorScreen();
                Toast.makeText(getActivity().getApplicationContext(), description, Toast.LENGTH_SHORT).show();
            }

        });

        mWebView.getSettings().setJavaScriptEnabled(true);

        if (App.getInstance().isConnected()) {

            mWebView.loadUrl(sUrl);

        } else {

            showErrorScreen();
        }

        return rootView;
    }

    public void showLoadingScreen() {

        mWebViewErrorScreen.setVisibility(View.GONE);
        mWebViewContentScreen.setVisibility(View.GONE);

        mWebViewLoadingScreen.setVisibility(View.VISIBLE);
    }

    public void showErrorScreen() {

        mWebViewLoadingScreen.setVisibility(View.GONE);
        mWebViewContentScreen.setVisibility(View.GONE);

        mWebViewErrorScreen.setVisibility(View.VISIBLE);
    }

    public void showContentScreen() {

        mWebViewErrorScreen.setVisibility(View.GONE);
        mWebViewLoadingScreen.setVisibility(View.GONE);

        mWebViewContentScreen.setVisibility(View.VISIBLE);
    }

}
