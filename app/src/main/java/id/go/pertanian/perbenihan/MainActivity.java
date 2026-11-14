package id.go.pertanian.perbenihan;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String URL_APLIKASI = "https://script.google.com/macros/s/AKfycbx2nEODieGm7rw30W5EZjqFtvlx04_uSlM2XQkSi3-4rVgqthFwgiOSbdGYEyIAf7BJNg/exec";

    private WebView webView;
    private View splashView;
    private View errorView;
    private String urlGagal = null;

    private ValueCallback<Uri[]> filePathCallback;
    private ActivityResultLauncher<Intent> fileChooserLauncher;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.web_view);
        splashView = findViewById(R.id.splash_view);
        errorView = findViewById(R.id.error_view);
        TextView tvError = findViewById(R.id.tv_error);
        Button btnRetry = findViewById(R.id.btn_retry);

        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setLoadWithOverviewMode(true);
        s.setUseWideViewPort(true);

        CookieManager cm = CookieManager.getInstance();
        cm.setAcceptCookie(true);
        cm.setAcceptThirdPartyCookies(webView, true);

        // Kembali: mundur 1 halaman web; jika sudah di halaman awal, keluar aplikasi
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (webView.canGoBack()) webView.goBack();
                else finish();
            }
        });

        // Pemilih file (untuk tombol Unggah di dashboard)
        fileChooserLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (filePathCallback == null) return;
                    Uri[] hasil = null;
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        hasil = WebChromeClient.FileChooserParams
                                .parseResult(result.getResultCode(), result.getData());
                    }
                    filePathCallback.onReceiveValue(hasil);
                    filePathCallback = null;
                });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView vw, ValueCallback<Uri[]> callback,
                                             FileChooserParams params) {
                if (filePathCallback != null) filePathCallback.onReceiveValue(null);
                filePathCallback = callback;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                try {
                    fileChooserLauncher.launch(Intent.createChooser(intent, "Pilih file"));
                } catch (Exception e) {
                    filePathCallback = null;
                    return false;
                }
                return true;
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Halaman web (Google Script / Google Drive / login Google) tetap di dalam aplikasi
                if (url.startsWith("http://") || url.startsWith("https://")) return false;
                // mailto:, tel:, intent: dsb → buka aplikasi lain
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                } catch (Exception ignored) { }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                splashView.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request,
                                        WebResourceError error) {
                if (request.isForMainFrame()) {
                    urlGagal = request.getUrl().toString();
                    tvError.setText(getString(R.string.pesan_offline));
                    errorView.setVisibility(View.VISIBLE);
                    webView.setVisibility(View.INVISIBLE);
                }
            }
        });

        btnRetry.setOnClickListener(v -> {
            errorView.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
            webView.loadUrl(urlGagal != null ? urlGagal : URL_APLIKASI);
        });

        // Tombol Unduh di dashboard → simpan ke folder Download HP
        webView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {
            try {
                DownloadManager.Request req = new DownloadManager.Request(Uri.parse(url));
                String cookie = CookieManager.getInstance().getCookie(url);
                if (cookie != null) req.addRequestHeader("cookie", cookie);
                req.addRequestHeader("User-Agent", userAgent);
                req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                String nama = URLUtil.guessFileName(url, contentDisposition, mimetype);
                req.setTitle(nama);
                req.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, nama);
                DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                dm.enqueue(req);
                Toast.makeText(this, "Mengunduh: " + nama, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                } catch (Exception ignored) { }
            }
        });

        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState);
            splashView.setVisibility(View.GONE);
        } else {
            webView.loadUrl(URL_APLIKASI);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }
}
