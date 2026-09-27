package com.sunnyui.launcher;

import android.app.Activity;
import android.app.WallpaperManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
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

    private FrameLayout scene;
    private LinearLayout content;
    private LinearLayout appGrid;
    private EditText searchBox;

    private TextView clockView;
    private TextView dateView;
    private TextView greetingView;

    private PackageManager pm;
    private Handler handler = new Handler();

    private ArrayList<AppItem> allApps =
            new ArrayList<>();

    private boolean darkMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        pm = getPackageManager();

        SharedPreferences preferences =
                getSharedPreferences(
                        "sunny_v7",
                        MODE_PRIVATE
                );

        darkMode =
                preferences.getBoolean(
                        "dark",
                        false
                );

        setupWindow();
        buildVisionUI();
        loadApps();
        updateClock();
    }

    private void setupWindow() {

        Window window = getWindow();

        window.setStatusBarColor(
                Color.TRANSPARENT
        );

        window.setNavigationBarColor(
                Color.TRANSPARENT
        );

        window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        window.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        );
    }

    private void buildVisionUI() {

        scene = new FrameLayout(this);

        scene.setBackgroundColor(
                Color.rgb(18, 20, 28)
        );

        setContentView(scene);

        createWallpaperLayer();
        createGlowLayer();
        createMainContent();
        createFloatingDock();
    }

    private void createWallpaperLayer() {

        ImageView wallpaper =
                new ImageView(this);

        wallpaper.setScaleType(
                ImageView.ScaleType.CENTER_CROP
        );

        try {

            Drawable drawable =
                    WallpaperManager
                            .getInstance(this)
                            .getDrawable();

            wallpaper.setImageDrawable(drawable);

        } catch (Exception e) {

            wallpaper.setBackgroundColor(
                    Color.rgb(32, 38, 55)
            );
        }

        if (Build.VERSION.SDK_INT >= 31) {

            wallpaper.setRenderEffect(
                    RenderEffect.createBlurEffect(
                            20f,
                            20f,
                            Shader.TileMode.CLAMP
                    )
            );
        }

        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                );

        scene.addView(wallpaper, params);
    }

    private void createGlowLayer() {

        View glow =
                new View(this);

        GradientDrawable gradient =
                new GradientDrawable(
                        GradientDrawable.Orientation.TL_BR,
                        new int[] {
                                Color.argb(90, 120, 90, 255),
                                Color.argb(45, 40, 180, 255),
                                Color.argb(20, 255, 255, 255),
                                Color.argb(70, 255, 90, 180)
                        }
                );

        glow.setBackground(gradient);

        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                );

        scene.addView(glow, params);
    }

    private void createMainContent() {

        content =
                new LinearLayout(this);

        content.setOrientation(
                LinearLayout.VERTICAL
        );

        content.setPadding(
                dp(20),
                dp(42),
                dp(20),
                dp(120)
        );

        ScrollView scroll =
                new ScrollView(this);

        scroll.setFillViewport(true);
        scroll.setBackgroundColor(
                Color.TRANSPARENT
        );

        scroll.addView(
                content,
                new ScrollView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );

        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                );

        scene.addView(scroll, params);

        createHeader();
        createClock();
        createSearch();
        createAppsTitle();
        createAppArea();
    }

    private void createHeader() {

        LinearLayout header =
                new LinearLayout(this);

        header.setOrientation(
                LinearLayout.HORIZONTAL
        );

        header.setGravity(
                Gravity.CENTER_VERTICAL
        );

        TextView logo =
                new TextView(this);

        logo.setText("SUNNY");
        logo.setTextSize(27);
        logo.setTypeface(null, 1);
        logo.setTextColor(Color.WHITE);
        logo.setLetterSpacing(0.08f);

        header.addView(
                logo,
                new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1
                )
        );

        TextView vision =
                new TextView(this);

        vision.setText("CRYSTAL");
        vision.setTextSize(11);
        vision.setTypeface(null, 1);
        vision.setTextColor(
                Color.argb(220, 255, 255, 255)
        );
        vision.setGravity(Gravity.CENTER);

        vision.setPadding(
                dp(15),
                dp(9),
                dp(15),
                dp(9)
        );

        vision.setBackground(
                glassBackground(42)
        );

        header.addView(vision);

        content.addView(header);
    }

    private void createClock() {

        LinearLayout box =
                new LinearLayout(this);

        box.setOrientation(
                LinearLayout.VERTICAL
        );

        box.setGravity(Gravity.CENTER);

        box.setPadding(
                dp(10),
                dp(28),
                dp(10),
                dp(20)
        );

        clockView =
                new TextView(this);

        clockView.setTextSize(54);
        clockView.setTypeface(null, 1);
        clockView.setTextColor(Color.WHITE);
        clockView.setGravity(Gravity.CENTER);
        clockView.setLetterSpacing(-0.02f);

        box.addView(clockView);

        dateView =
                new TextView(this);

        dateView.setTextSize(14);
        dateView.setTextColor(
                Color.argb(220, 255, 255, 255)
        );

        dateView.setGravity(Gravity.CENTER);

        box.addView(dateView);

        greetingView =
                new TextView(this);

        greetingView.setText(
                "A new way to experience Android"
        );

        greetingView.setTextSize(13);
        greetingView.setGravity(Gravity.CENTER);
        greetingView.setTextColor(
                Color.argb(175, 255, 255, 255)
        );

        LinearLayout.LayoutParams greetingParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        greetingParams.setMargins(
                0,
                dp(7),
                0,
                0
        );

        box.addView(
                greetingView,
                greetingParams
        );

        content.addView(box);
    }

    private void createSearch() {

        FrameLayout searchContainer =
                new FrameLayout(this);

        searchContainer.setBackground(
                glassBackground(30)
        );

        searchContainer.setPadding(
                dp(5),
                dp(3),
                dp(5),
                dp(3)
        );

        TextView searchIcon =
                new TextView(this);

        searchIcon.setText("⌕");
        searchIcon.setTextSize(25);
        searchIcon.setTextColor(Color.WHITE);
        searchIcon.setGravity(Gravity.CENTER);

        FrameLayout.LayoutParams iconParams =
                new FrameLayout.LayoutParams(
                        dp(48),
                        dp(52),
                        Gravity.START | Gravity.CENTER_VERTICAL
                );

        searchContainer.addView(
                searchIcon,
                iconParams
        );

        searchBox =
                new EditText(this);

        searchBox.setSingleLine(true);
        searchBox.setTextSize(16);
        searchBox.setHint(
                "Search your world..."
        );

        searchBox.setHintTextColor(
                Color.argb(150, 255, 255, 255)
        );

        searchBox.setTextColor(Color.WHITE);

        searchBox.setBackgroundColor(
                Color.TRANSPARENT
        );

        searchBox.setPadding(
                dp(48),
                0,
                dp(15),
                0
        );

        FrameLayout.LayoutParams searchParams =
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(58)
                );

        searchContainer.addView(
                searchBox,
                searchParams
        );

        LinearLayout.LayoutParams outerParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(60)
                );

        outerParams.setMargins(
                0,
                dp(4),
                0,
                dp(20)
        );

        content.addView(
                searchContainer,
                outerParams
        );

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

                        showApps(
                                s.toString()
                        );
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }
                }
        );
    }

    private void createAppsTitle() {

        LinearLayout row =
                new LinearLayout(this);

        row.setGravity(
                Gravity.CENTER_VERTICAL
        );

        TextView title =
                new TextView(this);

        title.setText("CRYSTAL APPS");
        title.setTextSize(12);
        title.setTypeface(null, 1);
        title.setTextColor(
                Color.argb(190, 255, 255, 255)
        );
        title.setLetterSpacing(0.12f);

        row.addView(
                title,
                new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1
                )
        );

        TextView line =
                new TextView(this);

        line.setText("●  ●  ●");
        line.setTextSize(7);
        line.setTextColor(
                Color.argb(150, 255, 255, 255)
        );

        row.addView(line);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(
                dp(3),
                0,
                dp(3),
                dp(8)
        );

        content.addView(row, params);
    }

    private void createAppArea() {

        appGrid =
                new LinearLayout(this);

        appGrid.setOrientation(
                LinearLayout.VERTICAL
        );

        content.addView(
                appGrid,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );
    }

    private void createFloatingDock() {

        LinearLayout dock =
                new LinearLayout(this);

        dock.setOrientation(
                LinearLayout.HORIZONTAL
        );

        dock.setGravity(
                Gravity.CENTER
        );

        dock.setPadding(
                dp(10),
                dp(8),
                dp(10),
                dp(8)
        );

        dock.setBackground(
                glassBackground(34)
        );

        TextView home =
                dockButton("⌂");

        TextView search =
                dockButton("⌕");

        TextView settings =
                dockButton("⚙");

        TextView mode =
                dockButton(
                        darkMode ? "☀" : "☾"
                );

        dock.addView(home);
        dock.addView(search);
        dock.addView(settings);
        dock.addView(mode);

        home.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        animateDockButton(v);
                    }
                }
        );

        search.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (searchBox != null) {
                            searchBox.requestFocus();
                        }

                        animateDockButton(v);
                    }
                }
        );

        settings.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {

                            Intent intent =
                                    new Intent(
                                            android.provider.Settings
                                                    .ACTION_SETTINGS
                                    );

                            startActivity(intent);

                        } catch (Exception e) {

                            Toast.makeText(
                                    MainActivity.this,
                                    "Settings unavailable",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
                }
        );

        mode.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        darkMode = !darkMode;

                        getSharedPreferences(
                                "sunny_v7",
                                MODE_PRIVATE
                        )
                                .edit()
                                .putBoolean(
                                        "dark",
                                        darkMode
                                )
                                .apply();

                        recreate();
                    }
                }
        );

        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(
                        dp(230),
                        dp(68),
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL
                );

        params.setMargins(
                dp(10),
                dp(10),
                dp(10),
                dp(22)
        );

        scene.addView(dock, params);
    }

    private TextView dockButton(
            String text) {

        TextView button =
                new TextView(this);

        button.setText(text);
        button.setTextSize(24);
        button.setGravity(Gravity.CENTER);
        button.setTextColor(Color.WHITE);

        button.setBackground(
                iconBackground()
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        dp(52),
                        dp(52)
                );

        params.setMargins(
                dp(3),
                0,
                dp(3),
                0
        );

        button.setLayoutParams(params);

        return button;
    }

    private void animateDockButton(
            View view) {

        view.animate()
                .scaleX(0.82f)
                .scaleY(0.82f)
                .setDuration(80)
                .start();

        view.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {

                        view.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(180)
                                .setInterpolator(
                                        new DecelerateInterpolator()
                                )
                                .start();
                    }
                },
                80
        );
    }

    private void loadApps() {

        allApps.clear();

        Intent launcherIntent =
                new Intent(Intent.ACTION_MAIN);

        launcherIntent.addCategory(
                Intent.CATEGORY_LAUNCHER
        );

        List<ResolveInfo> results =
                pm.queryIntentActivities(
                        launcherIntent,
                        0
                );

        for (ResolveInfo info : results) {

            if (info.activityInfo == null) {
                continue;
            }

            String packageName =
                    info.activityInfo.packageName;

            String name =
                    info.loadLabel(pm).toString();

            Drawable icon =
                    info.loadIcon(pm);

            Intent launch =
                    new Intent(
                            Intent.ACTION_MAIN
                    );

            launch.addCategory(
                    Intent.CATEGORY_LAUNCHER
            );

            launch.setClassName(
                    packageName,
                    info.activityInfo.name
            );

            allApps.add(
                    new AppItem(
                            name,
                            packageName,
                            icon,
                            launch
                    )
            );
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

    private void showApps(
            String query) {

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

        for (AppItem item : allApps) {

            if (search.length() == 0
                    || item.name.toLowerCase(
                            Locale.getDefault()
                    ).contains(search)) {

                filtered.add(item);
            }
        }

        int columns = 4;

        LinearLayout row = null;

        for (int i = 0;
                i < filtered.size();
                i++) {

            if (i % columns == 0) {

                row =
                        new LinearLayout(this);

                row.setOrientation(
                        LinearLayout.HORIZONTAL
                );

                row.setGravity(
                        Gravity.CENTER
                );

                appGrid.addView(
                        row,
                        new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                dp(116)
                        )
                );
            }

            View crystalIcon =
                    createCrystalIcon(
                            filtered.get(i)
                    );

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            0,
                            dp(108),
                            1
                    );

            params.setMargins(
                    dp(3),
                    dp(4),
                    dp(3),
                    dp(4)
            );

            row.addView(
                    crystalIcon,
                    params
            );
        }

        if (filtered.isEmpty()) {

            TextView empty =
                    new TextView(this);

            empty.setText(
                    "Nothing found in Sunny UI"
            );

            empty.setTextSize(15);
            empty.setTextColor(Color.WHITE);
            empty.setGravity(Gravity.CENTER);

            appGrid.addView(
                    empty,
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            dp(100)
                    )
            );
        }
    }

    /*
     * SUNNY CRYSTAL 3D ICON ENGINE
     *
     * This replaces the old V7 icon card.
     */
    private View createCrystalIcon(
            final AppItem app) {

        View crystal =
                SunnyIconRenderer.create(
                        this,
                        app.icon,
                        app.name
                );

        crystal.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        launchApp(app);
                    }
                }
        );

        crystal.setOnLongClickListener(
                new View.OnLongClickListener() {

                    @Override
                    public boolean onLongClick(
                            View v) {

                        Toast.makeText(
                                MainActivity.this,
                                app.name,
                                Toast.LENGTH_SHORT
                        ).show();

                        return true;
                    }
                }
        );

        return crystal;
    }

    private GradientDrawable glassBackground(
            int radius) {

        GradientDrawable glass =
                new GradientDrawable(
                        GradientDrawable.Orientation.TL_BR,
                        new int[] {

                                darkMode
                                        ? Color.argb(
                                                80,
                                                15,
                                                20,
                                                35
                                        )
                                        : Color.argb(
                                                68,
                                                255,
                                                255,
                                                255
                                        ),

                                darkMode
                                        ? Color.argb(
                                                42,
                                                100,
                                                120,
                                                170
                                        )
                                        : Color.argb(
                                                35,
                                                255,
                                                255,
                                                255
                                        ),

                                darkMode
                                        ? Color.argb(
                                                70,
                                                5,
                                                10,
                                                20
                                        )
                                        : Color.argb(
                                                42,
                                                255,
                                                255,
                                                255
                                        )
                        }
                );

        glass.setCornerRadius(
                dp(radius)
        );

        glass.setStroke(
                dp(1),
                Color.argb(
                        105,
                        255,
                        255,
                        255
                )
        );

        return glass;
    }

    private GradientDrawable iconBackground() {

        GradientDrawable bg =
                new GradientDrawable(
                        GradientDrawable.Orientation.TL_BR,
                        new int[] {
                                Color.argb(
                                        105,
                                        255,
                                        255,
                                        255
                                ),
                                Color.argb(
                                        40,
                                        255,
                                        255,
                                        255
                                )
                        }
                );

        bg.setCornerRadius(
                dp(19)
        );

        bg.setStroke(
                dp(1),
                Color.argb(
                        120,
                        255,
                        255,
                        255
                )
        );

        return bg;
    }

    private void launchApp(
            AppItem app) {

        try {

            startActivity(
                    app.intent
            );

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Unable to open " + app.name,
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void updateClock() {

        if (clockView == null) {
            return;
        }

        String time =
                new SimpleDateFormat(
                        "HH:mm",
                        Locale.getDefault()
                ).format(
                        new Date()
                );

        String date =
                new SimpleDateFormat(
                        "EEEE  •  dd MMMM",
                        Locale.getDefault()
                ).format(
                        new Date()
                );

        clockView.setText(time);
        dateView.setText(date);

        handler.postDelayed(
                new Runnable() {

                    @Override
                    public void run() {

                        updateClock();
                    }
                },
                1000
        );
    }

    private int dp(int value) {

        float density =
                getResources()
                        .getDisplayMetrics()
                        .density;

        return (int)
                (value * density + 0.5f);
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (pm != null) {
            loadApps();
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        handler.removeCallbacksAndMessages(
                null
        );
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
