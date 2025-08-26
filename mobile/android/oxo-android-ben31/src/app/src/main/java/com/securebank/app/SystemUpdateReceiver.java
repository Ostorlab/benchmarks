package com.securebank.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class SystemUpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if ("com.securebank.UPDATE_BALANCE".equals(action)) {
            String newBalance = intent.getStringExtra("balance");
            String accountNumber = intent.getStringExtra("account");

            if (newBalance != null && accountNumber != null) {
                SharedPreferences prefs = context.getSharedPreferences("bank_data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("current_balance", newBalance);
                editor.putString("account_number", accountNumber);
                editor.apply();

                Log.d("BankUpdate", "Balance updated to: " + newBalance + " for account: " + accountNumber);
            }
        }

        if ("com.securebank.SYNC_DATA".equals(action)) {
            String userData = intent.getStringExtra("user_data");
            String sessionToken = intent.getStringExtra("session_token");

            if (userData != null) {
                SharedPreferences prefs = context.getSharedPreferences("bank_data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("user_profile", userData);
                if (sessionToken != null) {
                    editor.putString("session_token", sessionToken);
                }
                editor.apply();

                Log.d("BankSync", "User data synchronized: " + userData);
            }
        }

        if ("android.intent.action.CONNECTIVITY_CHANGE".equals(action)) {
            SharedPreferences prefs = context.getSharedPreferences("bank_data", Context.MODE_PRIVATE);
            String currentBalance = prefs.getString("current_balance", "0.00");
            String accountNum = prefs.getString("account_number", "");

            Log.d("ConnectivityChange", "Network changed, current balance: " + currentBalance + " for account: " + accountNum);
        }
    }
}
