package fr.charleslabs.tinwhistletabs;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class SheetActivity extends AppCompatActivity {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheet);

        // Fetch intent
        final Intent intent = getIntent();
        if (!intent.hasExtra(TabActivity.EXTRA_ABC) ||
                !intent.hasExtra(TabActivity.EXTRA_SHEET_TITLE))
            finish();
        final String abc = escape((String)intent.getSerializableExtra(TabActivity.EXTRA_ABC));
        final String title = (String)intent.getSerializableExtra(TabActivity.EXTRA_SHEET_TITLE);

        // Set action bar title
        try{
            ActionBar actionBar = this.getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.sheetActivity_title,title));
        }catch (Exception ignored){}

        // Render page
        final WebView myWebView = findViewById(R.id.sheetActivity_sheet);
        myWebView.loadUrl("file:///android_asset/sheet.html");
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView web, String url) {
                web.loadUrl("javascript:" + "(function(){ABCJS.renderAbc(\"paper\", \"" + abc +
                        "\", {responsive:\"resize\"});})()");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return true;
    }

    /**
     * escape()
     *
     * Escape a give String to make it safe to be printed or stored.
     *
     * @param s The input String.
     * @return The output String.
     **/
    public static String escape(String s){
        return s.replace("\\", "\\\\")
                .replace("\t", "\\t")
                .replace("\b", "\\b")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\f", "\\f")
                .replace("\'", "\\'")
                .replace("\"", "\\\"");
    }

}
