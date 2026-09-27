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
import android.view.MotionEvent;
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

    private int whiteGlass =
            Color.argb(48, 255, 255, 255);

    private int darkGlass =
            Color.argb(62, 10, 15, 25);

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

        vision.setText("VISION");
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

        title.setText("YOUR APPS");
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
                   
