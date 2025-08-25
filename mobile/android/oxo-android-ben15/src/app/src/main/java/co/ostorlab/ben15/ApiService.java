package co.ostorlab.ben15;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ApiService {
    private static final String BASE_URL = "http://192.168.1.49:5000";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final OkHttpClient client;
    private final Gson gson;
    
    public ApiService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }
    
    public interface LoginCallback {
        void onSuccess(String token, String username);
        void onError(String error);
    }
    
    public interface PasswordChangeCallback {
        void onSuccess();
        void onError(String error);
    }
    
    public void login(String username, String password, LoginCallback callback) {
        JsonObject loginData = new JsonObject();
        loginData.addProperty("username", username);
        loginData.addProperty("password", password);
        
        RequestBody body = RequestBody.create(gson.toJson(loginData), JSON);
        Request request = new Request.Builder()
                .url(BASE_URL + "/login")
                .post(body)
                .build();
        
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("Network error: " + e.getMessage());
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                
                if (response.isSuccessful()) {
                    try {
                        JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
                        String token = jsonResponse.get("access_token").getAsString();
                        String user = jsonResponse.getAsJsonObject("user").get("username").getAsString();
                        callback.onSuccess(token, user);
                    } catch (Exception e) {
                        callback.onError("Failed to parse response");
                    }
                } else {
                    try {
                        JsonObject errorResponse = gson.fromJson(responseBody, JsonObject.class);
                        String error = errorResponse.get("error").getAsString();
                        callback.onError(error);
                    } catch (Exception e) {
                        callback.onError("Login failed");
                    }
                }
            }
        });
    }
    
    public void changePassword(String token, String currentPassword, String newPassword, PasswordChangeCallback callback) {
        JsonObject passwordData = new JsonObject();
        passwordData.addProperty("current_password", currentPassword);
        passwordData.addProperty("new_password", newPassword);
        
        RequestBody body = RequestBody.create(gson.toJson(passwordData), JSON);
        Request request = new Request.Builder()
                .url(BASE_URL + "/change-password")
                .post(body)
                .header("Authorization", "Bearer " + token)
                .build();
        
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("Network error: " + e.getMessage());
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    try {
                        JsonObject errorResponse = gson.fromJson(responseBody, JsonObject.class);
                        String error = errorResponse.get("error").getAsString();
                        callback.onError(error);
                    } catch (Exception e) {
                        callback.onError("Password change failed");
                    }
                }
            }
        });
    }
}