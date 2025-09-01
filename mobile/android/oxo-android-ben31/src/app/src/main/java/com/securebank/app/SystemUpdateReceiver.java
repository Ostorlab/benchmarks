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
                try {
                    double balanceAmount = Double.parseDouble(newBalance);

                    DatabaseHelper dbHelper = new DatabaseHelper(context);
                    User user = dbHelper.getUserByAccountNumber(accountNumber);

                    if (user != null) {
                        boolean updated = dbHelper.updateBalance(user.getUsername(), balanceAmount);

                        if (updated) {
                            SharedPreferences prefs = context.getSharedPreferences("bank_data", Context.MODE_PRIVATE);
                            String loggedInUser = prefs.getString("logged_in_user", "");

                            if (user.getUsername().equals(loggedInUser)) {
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("current_balance", String.format("%.2f", balanceAmount));
                                editor.apply();
                            }

                        }
                    } else {
                        Log.w("BankUpdate", "Account not found: " + accountNumber);
                    }
                } catch (NumberFormatException e) {
                    Log.e("BankUpdate", "Invalid balance format: " + newBalance);
                }
            }
        }

        if ("com.securebank.SYNC_DATA".equals(action)) {
            String userData = intent.getStringExtra("user_data");
            String sessionToken = intent.getStringExtra("session_token");

            if (userData != null || sessionToken != null) {
                SharedPreferences prefs = context.getSharedPreferences("bank_data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                if (userData != null) {
                    editor.putString("user_profile", userData);
                }

                if (sessionToken != null) {
                    editor.putString("session_token", sessionToken);
                }

                editor.apply();
            }
        }

        if ("android.intent.action.CONNECTIVITY_CHANGE".equals(action)) {
            SharedPreferences prefs = context.getSharedPreferences("bank_data", Context.MODE_PRIVATE);
            String currentBalance = prefs.getString("current_balance", "0.00");
            String accountNum = prefs.getString("account_number", "");
            String loggedInUser = prefs.getString("logged_in_user", "");
        }
    }
}
