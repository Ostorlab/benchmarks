package com.example.myapplication5;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.appcompat.app.AppCompatActivity;

public class DeepLinkRouter extends AppCompatActivity {
    private String deepLinkUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Extract deeplink URL from intent
        if (TextUtils.isEmpty(this.deepLinkUrl) && getIntent() != null && getIntent().getAction() != null && "android.intent.action.VIEW".equals(getIntent().getAction())) {
            this.deepLinkUrl = getIntent().getData().toString();
        }

        // Process the deeplink
        this.processDeepLink(this.deepLinkUrl);
    }

    private void processDeepLink(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }

        Uri uri = Uri.parse(url);

        // Basic scheme validation
        if (!"foodapp".equals(uri.getScheme()) && !"fooddelivery".equals(uri.getScheme())) {
            return;
        }

        String host = uri.getHost();
        if ("webview".equals(host)) {
            handleWebViewDeepLink(uri);
        } else if ("promotion".equals(host)) {
            handlePromotionDeepLink(uri);
        } else if ("support".equals(host)) {
            handleSupportDeepLink(uri);
        }
    }

    private void handleWebViewDeepLink(Uri uri) {
        String url = uri.getQueryParameter("url");
        if (TextUtils.isEmpty(url)) {
            return;
        }

        String theme = uri.getQueryParameter("theme");
        String fullscreen = uri.getQueryParameter("fullscreen");

        if ("dark".equals(theme) && "true".equals(fullscreen)) {
            loadUrlDirectly(uri);
        } else {
            loadUrlWithValidation(uri);
        }
    }

    private void handlePromotionDeepLink(Uri uri) {
        String promoType = uri.getQueryParameter("type");
        String targetUrl = uri.getQueryParameter("target");

        if ("external".equals(promoType)) {
            if (!TextUtils.isEmpty(targetUrl)) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("url", targetUrl);
                intent.putExtra("title", "Promotion");
                startActivity(intent);
            }
        } else {
            handleInternalPromotion(uri);
        }
    }

    private void handleSupportDeepLink(Uri uri) {
        String section = uri.getQueryParameter("section");
        String customUrl = uri.getQueryParameter("custom_url");

        if ("admin".equals(section)) {
            if (!TextUtils.isEmpty(customUrl)) {
                loadAdminWebView(customUrl);
            }
        } else {
            loadSupportPage(section);
        }
    }

    private void loadUrlDirectly(Uri uri) {
        String url = uri.getQueryParameter("url");
        String title = uri.getQueryParameter("title");

        if (TextUtils.isEmpty(title)) {
            title = "Food App";
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        startActivity(intent);
    }

    private void loadUrlWithValidation(Uri uri) {
        String url = uri.getQueryParameter("url");
        String title = uri.getQueryParameter("title");

        if (TextUtils.isEmpty(title)) {
            title = "Food App";
        }

        if (isValidUrl(url)) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("title", title);
            startActivity(intent);
        }
    }

    private void loadAdminWebView(String url) {
        Intent intent = new Intent(this, AdminWebViewActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("enable_js_interface", true);
        startActivity(intent);
    }

    private void handleInternalPromotion(Uri uri) {
        String promoId = uri.getQueryParameter("id");
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("promo_id", promoId);
        startActivity(intent);
    }

    private void loadSupportPage(String section) {
        String[] validSections = {"faq", "contact", "terms", "privacy"};
        for (String validSection : validSections) {
            if (validSection.equals(section)) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("section", section);
                startActivity(intent);
                return;
            }
        }
    }

    private boolean isValidUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }

        String[] trustedDomains = {
            "foodapp.com",
            "api.foodapp.com",
            "cdn.foodapp.com",
            "help.foodapp.com"
        };

        try {
            Uri uri = Uri.parse(url);
            String host = uri.getHost();
            for (String domain : trustedDomains) {
                if (domain.equals(host) || host.endsWith("." + domain)) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }

        return false;
    }
}