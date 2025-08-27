package co.ostorlab.newsreader

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val userToken = intent.getStringExtra("user_token")
        val userId = intent.getStringExtra("user_id")
        val articleIntent = Intent(context, ArticleActivity::class.java)
        articleIntent.putExtra("user_token", userToken)
        articleIntent.putExtra("user_id", userId)
        articleIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(articleIntent)
    }
}
