package co.ostorlab.myapplication

import android.app.AlertDialog
import android.os.Bundle
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity

class HtmlViewerActivity : ComponentActivity() {
    private lateinit var webView: WebView
    private lateinit var titleView: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Create main layout
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }
        
        // Title view
        titleView = TextView(this).apply {
            textSize = 20f
            setPadding(0, 0, 0, 16)
        }
        mainLayout.addView(titleView)
        
        // WebView for workout content
        webView = WebView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            webViewClient = WebViewClient()
            webChromeClient = object : WebChromeClient() {
                override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                    AlertDialog.Builder(this@HtmlViewerActivity)
                        .setMessage(message)
                        .setPositiveButton("OK") { _, _ -> result?.confirm() }
                        .show()
                    return true
                }
            }
        }
        
        // Configure WebView settings
        setupWebView()
        mainLayout.addView(webView)
        
        setContentView(mainLayout)
        
        // Load workout content
        loadWorkoutContent()
    }
    
    private fun setupWebView() {
        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.allowFileAccess = false
        settings.allowContentAccess = true
        settings.setSupportZoom(true)
        settings.builtInZoomControls = true
        settings.displayZoomControls = false
    }
    
    private fun loadWorkoutContent() {
        // Get workout information from intent - VULNERABLE to Event Handler Injection
        val workoutTitle = intent.getStringExtra("title") ?: "Fitness Activity"
        val articleContent = intent.getStringExtra("content") ?: ""
        
        // Set the workout title
        titleView.text = workoutTitle
        
        // Load content into WebView - THIS IS THE VULNERABILITY!
        // Attack Vector 2: Event Handler Injection with Social Engineering
        if (articleContent.isNotEmpty()) {
            // VULNERABLE: Directly inject user content into HTML template
            // This allows event handler injection attacks (onclick, onmouseover, onerror, etc.)
            val htmlContent = """
                <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <style>
                        body { font-family: Arial, sans-serif; padding: 20px; background: #f5f5f5; }
                        .workout-content { background: white; padding: 20px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                        .fitness-stats { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 15px; border-radius: 8px; margin: 15px 0; }
                        button { background: #4caf50; color: white; padding: 10px 20px; border: none; border-radius: 5px; cursor: pointer; margin: 5px; }
                        .interactive { cursor: pointer; padding: 10px; margin: 5px; background: #e3f2fd; border-radius: 5px; }
                    </style>
                </head>
                <body>
                    <div class="fitness-stats">
                        <h3>🏃‍♂️ FitTracker Pro - Workout Session</h3>
                        <p>Your personalized fitness content below:</p>
                    </div>
                    <div class="workout-content">
                        $articleContent
                    </div>
                </body>
                </html>
            """
            webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
        } else {
            // Default content showcasing interactive elements vulnerable to event handler injection
            val defaultContent = """
                <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <style>
                        body { font-family: Arial, sans-serif; padding: 20px; background: #f5f5f5; }
                        .card { background: white; padding: 20px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                        h2 { color: #333; margin-top: 0; }
                        .workout-tip { background: #e3f2fd; padding: 15px; border-radius: 8px; border-left: 4px solid #2196f3; margin: 10px 0; }
                        .cta-button { background: #4caf50; color: white; padding: 12px 24px; border: none; border-radius: 6px; cursor: pointer; font-size: 16px; margin: 10px 5px; }
                        .premium-offer { background: #ff9800; color: white; padding: 15px; border-radius: 8px; margin: 15px 0; text-align: center; }
                        .stats-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; margin: 15px 0; }
                        .stat-item { background: #f0f8ff; padding: 10px; border-radius: 6px; text-align: center; cursor: pointer; }
                        .social-share { background: #2196f3; color: white; padding: 10px; border-radius: 5px; margin: 5px; text-align: center; cursor: pointer; }
                    </style>
                </head>
                <body>
                    <div class="card">
                        <h2>💪 Welcome to FitTracker Pro</h2>
                        <p>Your comprehensive fitness tracking companion is ready!</p>
                        
                        <div class="workout-tip">
                            <strong>🎯 Pro Tip:</strong> Click on any element below to interact with your fitness data!
                        </div>
                        
                        <div class="stats-grid">
                            <div class="stat-item" onclick="alert('🔥 Today: 245 calories burned! Keep going!')">
                                🔥 Calories<br><strong>245</strong>
                            </div>
                            <div class="stat-item" onclick="alert('👣 Steps: 8,432 today! Almost at your goal!')">
                                👣 Steps<br><strong>8,432</strong>
                            </div>
                            <div class="stat-item" onclick="alert('⏰ Active time: 2h 15m - Great progress!')">
                                ⏰ Active<br><strong>2h 15m</strong>
                            </div>
                            <div class="stat-item" onclick="alert('💧 Stay hydrated! 6/8 glasses completed')">
                                💧 Water<br><strong>6/8</strong>
                            </div>
                        </div>
                        
                        <div class="premium-offer">
                            <h3>🚀 Unlock Premium Features!</h3>
                            <p>Get advanced analytics, meal planning, and personal trainer tips</p>
                            <button class="cta-button" onclick="alert('✨ Premium upgrade successful! Welcome to FitTracker Pro+')">
                                ✨ Upgrade Now - 50% Off!
                            </button>
                            <button class="cta-button" onclick="alert('🎁 Free trial started! Enjoy 7 days of premium features')">
                                🎁 Start Free Trial
                            </button>
                        </div>
                        
                        <div class="social-share" onclick="alert('📱 Share feature activated! Your progress has been shared with friends')">
                            📱 Share Your Progress
                        </div>
                        
                        <p><small>Join over 100,000 users achieving their fitness goals! 🌟</small></p>
                    </div>
                </body>
                </html>
            """
            webView.loadDataWithBaseURL(null, defaultContent, "text/html", "UTF-8", null)
        }
    }
}
