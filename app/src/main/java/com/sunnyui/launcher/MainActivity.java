package com.sunnyui.launcher;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {

    private LinearLayout root;
    private LinearLayout appGrid;
    private EditText searchBox;
    private TextView clockView;
    private TextView dateView;
    private TextView modeButton;

    private PackageManager pm;
    private Handler handler = new Handler();

    private ArrayList<AppItem> allApps = new ArrayList<>();

    private boolean darkMode = false;

    private final int LIGHT_BG = Color.rgb(244, 247, 252);
    private final int DARK_BG = Color.rgb(12, 15, 22);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pm = getPackageManager();

        SharedPreferences pref =
                getSharedPreferences("sunny_ui", MODE_PRIVATE);

        darkMode = pref.getBoolean("dark", false);

        setupWindow();
        createInterface();
        loadApps();
        updateTime();
    }

    private void setupWindow() {

        Window window = getWindow();

        if (darkMode) {
            window.setStatusBarColor(DARK_BG);
            window.setNavigationBarColor(DARK_BG);
        } else {
            window.setStatusBarColor(Color.WHITE);
            window.setNavigationBarColor(Color.WHITE);
        }

        window.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        );
    }

    private void createInterface() {

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(18), dp(18), dp(12));

        root.setBackgroundColor(
                darkMode ? DARK_BG : LIGHT_BG
        );

        setContentView(root);

        createTopBar();
        createClock();
        createSearch();
        createAppArea();
        createFooter();
    }

    private void createTopBar() {

        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setGravity(Gravity.CENTER_VERTICAL);

        TextView title = new TextView(this);
        title.setText("SUNNY UI");
        title.setTextSize(25);
        title.setTypeface(null, 1);
        title.setTextColor(textColor());

        LinearLayout.LayoutParams titleParams =
                new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1
                );

        bar.addView(title, titleParams);

        modeButton = new TextView(this);
        modeButton.setText(darkMode ? "☀" : "☾");
        modeButton.setTextSize(22);
        modeButton.setGravity(Gravity.CENTER);

        GradientDrawable modeBg = roundedBackground(
                darkMode
                        ? Color.rgb(35, 40, 52)
                        : Color.WHITE,
                Color.argb(40, 0, 0, 0),
                50
        );

        modeButton.setBackground(modeBg);
        modeButton.setPadding(
                dp(13), dp(8), dp(13), dp(8)
        );

        modeButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggleMode();
                    }
                }
        );

        bar.addView(modeButton);

        root.addView(bar);
    }

    private void createClock() {

        LinearLayout clockBox = new LinearLayout(this);
        clockBox.setOrientation(LinearLayout.VERTICAL);
        clockBox.setGravity(Gravity.CENTER);
        clockBox.setPadding(
                dp(8),
                dp(22),
                dp(8),
                dp(20)
        );

        clockView = new TextView(this);
        clockView.setTextSize(48);
        clockView.setTypeface(null, 1);
        clockView.setGravity(Gravity.CENTER);
        clockView.setTextColor(textColor());

        clockBox.addView(clockView);

        dateView = new TextView(this);
        dateView.setTextSize(15);
        dateView.setGravity(Gravity.CENTER);
        dateView.setTextColor(
                darkMode
                        ? Color.rgb(180, 188, 202)
                        : Color.rgb(100, 108, 120)
        );

        clockBox.addView(dateView);

        TextView subtitle = new TextView(this);
        subtitle.setText("Premium Android Experience");
        subtitle.setTextSize(13);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, dp(6), 0, 0);
        subtitle.setTextColor(
                darkMode
                        ? Color.rgb(135, 145, 165)
                        : Color.rgb(120, 128, 140)
        );

        clockBox.addView(subtitle);

        root.addView(clockBox);
    }

    private void createSearch() {

        searchBox = new EditText(this);

        searchBox.setSingleLine(true);
        searchBox.setTextSize(16);
        searchBox.setHint("Search apps...");
        searchBox.setHintTextColor(
                darkMode
                        ? Color.rgb(145, 150, 160)
                        : Color.rgb(125, 130, 140)
        );

        searchBox.setTextColor(textColor());
        searchBox.setPadding(
                dp(18),
                dp(4),
                dp(18),
                dp(4)
        );

        GradientDrawable searchBg = roundedBackground(
                darkMode
                        ? Color.rgb(28, 33, 43)
                        : Color.WHITE,
                darkMode
                        ? Color.rgb(55, 62, 76)
                        : Color.rgb(225, 229, 237),
                32
        );

        searchBox.setBackground(searchBg);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(54)
                );

        params.setMargins(0, 0, 0, dp(18));

        root.addView(searchBox, params);

        searchBox.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count) {

                        showApps(s.toString());
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }
                }
        );
    }

    private void createAppArea() {

        ScrollView scroll = new ScrollView(this);

        scroll.setFillViewport(true);
        scroll.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);

        appGrid = new LinearLayout(this);
        appGrid.setOrientation(LinearLayout.VERTICAL);

        scroll.addView(
                appGrid,
                new ScrollView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );

        LinearLayout.LayoutParams scrollParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        0,
                        1
                );

        root.addView(scroll, scrollParams);
    }

    private void createFooter() {

        TextView footer = new TextView(this);

        footer.setText(
                "Sunny UI  •  Designed for Android  •  V6"
        );

        footer.setTextSize(12);
        footer.setGravity(Gravity.CENTER);
        footer.setPadding(0, dp(12), 0, dp(4));

        footer.setTextColor(
                darkMode
                        ? Color.rgb(125, 135, 150)
                        : Color.rgb(125, 132, 145)
        );

        root.addView(footer);
    }

    private void loadApps() {

        allApps.clear();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> results =
                pm.queryIntentActivities(intent, 0);

        for (ResolveInfo info : results) {

            if (info.activityInfo == null) {
                continue;
            }

            String packageName =
                    info.activityInfo.packageName;

            String label =
                    info.loadLabel(pm).toString();

            Drawable icon =
                    info.loadIcon(pm);

            Intent launchIntent =
                    new Intent(Intent.ACTION_MAIN);

            launchIntent.addCategory(
                    Intent.CATEGORY_LAUNCHER
            );

            launchIntent.setPackage(packageName);

            List<ResolveInfo> launchers =
                    pm.queryIntentActivities(
                            launchIntent,
                            0
                    );

            if (!launchers.isEmpty()) {

                ResolveInfo launcher =
                        launchers.get(0);

                Intent finalIntent =
                        new Intent(Intent.ACTION_MAIN);

                finalIntent.addCategory(
                        Intent.CATEGORY_LAUNCHER
                );

                finalIntent.setClassName(
                        packageName,
                        launcher.activityInfo.name
                );

                allApps.add(
                        new AppItem(
                                label,
                                packageName,
                                icon,
                                finalIntent
                        )
                );
            }
        }

        Collections.sort(
                allApps,
                new Comparator<AppItem>() {
                    @Override
                    public int compare(
                            AppItem a,
                            AppItem b) {

                        return a.name.compareToIgnoreCase(
                                b.name
                        );
                    }
                }
        );

        showApps("");
    }

    private void showApps(String query) {

        if (appGrid == null) {
            return;
        }

        appGrid.removeAllViews();

        String search =
                query == null
                        ? ""
                        : query.trim().toLowerCase(
                                Locale.getDefault()
                        );

        ArrayList<AppItem> filtered =
                new ArrayList<>();

        for (AppItem app : allApps) {

            if (search.length() == 0
                    || app.name.toLowerCase(
                            Locale.getDefault()
                    ).contains(search)) {

                filtered.add(app);
            }
        }

        int columns = 3;

        LinearLayout row = null;

        for (int i = 0; i < filtered.size(); i++) {

            if (i % columns == 0) {

                row = new LinearLayout(this);
                row.setOrientation(
                        LinearLayout.HORIZONTAL
                );

                row.setGravity(Gravity.CENTER);

                appGrid.addView(
                        row,
                        new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                );
            }

            View card =
                    createAppCard(filtered.get(i));

            LinearLayout.LayoutParams cardParams =
                    new LinearLayout.LayoutParams(
                            0,
                            dp(128),
                            1
                    );

            cardParams.setMargins(
                    dp(5),
                    dp(5),
                    dp(5),
                    dp(5)
            );

            row.addView(card, cardParams);
        }

        if (filtered.isEmpty()) {

            TextView empty = new TextView(this);

            empty.setText("No apps found");
            empty.setTextSize(16);
            empty.setGravity(Gravity.CENTER);
            empty.setTextColor(textColor());

            empty.setPadding(
                    0,
                    dp(50),
                    0,
                    dp(50)
            );

            appGrid.addView(empty);
        }
    }

    private View createAppCard(final AppItem app) {

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setGravity(Gravity.CENTER);

        card.setPadding(
                dp(8),
                dp(10),
                dp(8),
                dp(8)
        );

        GradientDrawable background =
                roundedBackground(
                        darkMode
                                ? Color.rgb(27, 32, 42)
                                : Color.WHITE,
                        darkMode
                                ? Color.rgb(55, 62, 76)
                                : Color.rgb(225, 229, 237),
                        24
                );

        card.setBackground(background);
        card.setElevation(dp(5));

        ImageView icon =
                new ImageView(this);

        icon.setImageDrawable(app.icon);
        icon.setScaleType(
                ImageView.ScaleType.FIT_CENTER
        );

        LinearLayout.LayoutParams iconParams =
                new LinearLayout.LayoutParams(
                        dp(50),
                        dp(50)
                );

        card.addView(icon, iconParams);

        TextView name =
                new TextView(this);

        name.setText(app.name);
        name.setTextSize(12);
        name.setGravity(Gravity.CENTER);
        name.setMaxLines(2);
        name.setTextColor(textColor());

        LinearLayout.LayoutParams nameParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        nameParams.setMargins(
                0,
                dp(8),
                0,
                0
        );

        card.addView(name, nameParams);

        card.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        launchApp(app);
                    }
                }
        );

        card.setOnTouchListener(
                new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(
                            View v,
                            android.view.MotionEvent event) {

                        if (event.getAction()
                                == android.view.MotionEvent.ACTION_DOWN) {

                            v.animate()
                                    .scaleX(0.94f)
                                    .scaleY(0.94f)
                                    .setDuration(100)
                                    .setInterpolator(
                                            new DecelerateInterpolator()
                                    )
                                    .start();

                        } else if (
                                event.getAction()
                                        == android.view.MotionEvent.ACTION_UP
                                        ||
                                event.getAction()
                                        == android.view.MotionEvent.ACTION_CANCEL
                        ) {

                            v.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(150)
                                    .setInterpolator(
                                            new DecelerateInterpolator()
                                    )
                                    .start();
                        }

                        return false;
                    }
                }
        );

        return card;
    }

    private void launchApp(AppItem app) {

        try {

            startActivity(app.intent);

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Unable to open " + app.name,
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void toggleMode() {

        darkMode = !darkMode;

        getSharedPreferences(
                "sunny_ui",
                MODE_PRIVATE
        )
                .edit()
                .putBoolean("dark", darkMode)
                .apply();

        recreate();
    }

    private void updateTime() {

        if (clockView == null) {
            return;
        }

        String time =
                new SimpleDateFormat(
                        "hh:mm",
                        Locale.getDefault()
                ).format(new Date());

        String date =
                new SimpleDateFormat(
                        "EEEE, dd MMMM yyyy",
                        Locale.getDefault()
                ).format(new Date());

        clockView.setText(time);
        dateView.setText(date);

        handler.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        updateTime();
                    }
                },
                1000
        );
    }

    private GradientDrawable roundedBackground(
            int fill,
            int stroke,
            int radius) {

        GradientDrawable drawable =
                new GradientDrawable();

        drawable.setColor(fill);
        drawable.setCornerRadius(dp(radius));

        drawable.setStroke(
                dp(1),
                stroke
        );

        return drawable;
    }

    private int textColor() {

        return darkMode
                ? Color.WHITE
                : Color.rgb(30, 35, 45);
    }

    private int dp(int value) {

        float density =
                getResources()
                        .getDisplayMetrics()
                        .density;

        return (int) (
                value * density + 0.5f
        );
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (pm != null) {

            loadApps();

            if (searchBox != null) {

                showApps(
                        searchBox.getText().toString()
                );
            }
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        handler.removeCallbacksAndMessages(null);
    }

    private static class AppItem {

        String name;
        String packageName;
        Drawable icon;
        Intent intent;

        AppItem(
                String name,
                String packageName,
                Drawable icon,
                Intent intent) {

            this.name = name;
            this.packageName = packageName;
            this.icon = icon;
            this.intent = intent;
        }
    }
}
