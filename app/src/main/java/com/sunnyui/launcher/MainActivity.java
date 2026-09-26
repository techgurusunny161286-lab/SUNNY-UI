package com.sunnyui.launcher;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends Activity {

    LinearLayout appGrid;
    EditText searchBox;
    List<ApplicationInfo> apps;
    PackageManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pm = getPackageManager();

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(28, 35, 28, 20);
        root.setBackgroundColor(Color.rgb(245, 247, 250));

        TextView clock = new TextView(this);
        clock.setText("SUNNY UI");
        clock.setTextSize(30);
        clock.setTextColor(Color.BLACK);
        clock.setGravity(Gravity.CENTER);
        root.addView(clock, new LinearLayout.LayoutParams(
                -1, 70));

        searchBox = new EditText(this);
        searchBox.setHint("🔍  Search apps");
        searchBox.setSingleLine(true);
        searchBox.setTextSize(17);
        root.addView(searchBox, new LinearLayout.LayoutParams(
                -1, 65));

        ScrollView scroll = new ScrollView(this);

        appGrid = new LinearLayout(this);
        appGrid.setOrientation(LinearLayout.VERTICAL);

        scroll.addView(appGrid);
        root.addView(scroll, new LinearLayout.LayoutParams(
                -1, 0, 1));

        TextView footer = new TextView(this);
        footer.setText("Sunny UI • Premium Android Experience");
        footer.setTextSize(13);
        footer.setTextColor(Color.DKGRAY);
        footer.setGravity(Gravity.CENTER);
        root.addView(footer, new LinearLayout.LayoutParams(
                -1, 50));

        setContentView(root);

        loadApps();

        searchBox.addTextChangedListener(new android.text.TextWatcher() {
            public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            public void onTextChanged(CharSequence s, int st, int b, int c) {
                showApps(s.toString());
            }
            public void afterTextChanged(android.text.Editable e) {}
        });
    }

    private void loadApps() {

        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        apps = pm.queryIntentActivities(intent, 0)
                .stream()
                .map(info -> info.activityInfo.applicationInfo)
                .collect(java.util.stream.Collectors.toList());

        Collections.sort(apps, new Comparator<ApplicationInfo>() {
            public int compare(ApplicationInfo a, ApplicationInfo b) {
                return pm.getApplicationLabel(a).toString()
                        .compareToIgnoreCase(
                                pm.getApplicationLabel(b).toString());
            }
        });

        showApps("");
    }

    private void showApps(String query) {

        appGrid.removeAllViews();

        for (ApplicationInfo app : apps) {

            String name = pm.getApplicationLabel(app).toString();

            if (!query.isEmpty() &&
                    !name.toLowerCase().contains(query.toLowerCase())) {
                continue;
            }

            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.HORIZONTAL);
            item.setGravity(Gravity.CENTER_VERTICAL);
            item.setPadding(18, 12, 18, 12);

            ImageView icon = new ImageView(this);

            try {
                Drawable drawable = pm.getApplicationIcon(app);
                icon.setImageDrawable(drawable);
            } catch (Exception ignored) {}

            item.addView(icon, new LinearLayout.LayoutParams(75, 75));

            TextView title = new TextView(this);
            title.setText(name);
            title.setTextSize(18);
            title.setTextColor(Color.BLACK);
            title.setPadding(25, 0, 0, 0);

            item.addView(title, new LinearLayout.LayoutParams(
                    0, 75, 1));

            item.setOnClickListener(v -> {

                Intent launchIntent =
                        pm.getLaunchIntentForPackage(app.packageName);

                if (launchIntent != null) {
                    startActivity(launchIntent);
                }
            });

            appGrid.addView(item);
        }
    }
}
