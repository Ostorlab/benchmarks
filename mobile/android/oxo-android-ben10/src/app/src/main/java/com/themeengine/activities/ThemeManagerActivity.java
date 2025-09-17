package com.themeengine.activities;

import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.themeengine.R;
import com.themeengine.ThemeLoader;
import com.themeengine.IThemePlugin;

public class ThemeManagerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_manager);
        
        loadInstalledThemes();
    }

    private void loadInstalledThemes() {
        ListView themeList = findViewById(R.id.theme_list);
        PackageInfo[] themes = ThemeLoader.getInstalledThemes(this);
        ArrayAdapter<PackageInfo> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, themes);
        themeList.setAdapter(adapter);
        for (int i = 0; i < themes.length; i++) {
            PackageInfo themePackage = themes[i];
            IThemePlugin theme = ThemeLoader.loadTheme(this, themePackage.packageName);

            if (theme != null) {
                theme.applyTheme();
                Toast.makeText(this, "Theme " + theme.getThemeName() + " applied!",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
