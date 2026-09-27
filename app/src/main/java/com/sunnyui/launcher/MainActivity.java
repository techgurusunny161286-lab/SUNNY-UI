package com.sunnyui.launcher;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.text.Editable;
import android.text.TextWatcher;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {

    private LinearLayout appGrid;
    private EditText searchBox;
    private TextView clockView;
    private TextView dateView;
    private TextView batteryView;

    private PackageManager pm;
    private final List<ApplicationInfo> allApps = new ArrayList<>();
    private final Handler handler = new Handler();

    private int dp(int value) {
        return (int) (value *
                getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pm = getPackageManager();

        buildUI();
        loadApps();
        showApps("");

        updateTime();
    }

    private void buildUI() {

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(18), dp(16), dp(10));

        GradientDrawable bg = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{
                        Color.rgb(245, 243, 255),
                        Color.rgb(228, 235, 255),
                        Color.rgb(247, 241, 255)
                }
        );

        root.setBackground(bg);

        // TOP BAR
        LinearLayout topBar = new LinearLayout(this);
        topBar.setOrientation(LinearLayout.HORIZONTAL);
        topBar.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout titleBox = new LinearLayout(this);
        titleBox.setOrientation(LinearLayout.VERTICAL);

        TextView title = new TextView(this);
        title.setText("SUNNY UI");
        title.setTextSize(27);
        title.setTextColor(Color.rgb(25, 25, 35));
        title.setTypeface(null, 1);

        TextView subtitle = new TextView(this);
        subtitle.setText("Premium Android Experience");
        subtitle.setTextSize(11);
        subtitle.setTextColor(Color.rgb(100, 100, 115));

        titleBox.addView(title);
        titleBox.addView(subtitle);

        topBar.addView(titleBox,
                new LinearLayout.LayoutParams(
                        0, dp(58), 1
                ));

        batteryView = new TextView(this);
        batteryView.setText("🔋");
        batteryView.setTextSize(17);
        batteryView.setGravity(Gravity.CENTER);

        topBar.addView(batteryView,
                new LinearLayout.LayoutParams(
                        dp(55), dp(50)
                ));

        root.addView(topBar);

        // CLOCK
        clockView = new TextView(this);
        clockView.setTextSize(42);
        clockView.setTextColor(Color.rgb(35, 35, 50));
        clockView.setGravity(Gravity.CENTER);
        clockView.setTypeface(null, 1);

        root.addView(clockView,
                new LinearLayout.LayoutParams(
                        -1, dp(62)
                ));

        // DATE
        dateView = new TextView(this);
        dateView.setTextSize(13);
        dateView.setTextColor(Color.rgb(105, 105, 120));
        dateView.setGravity(Gravity.CENTER);

        root.addView(dateView,
                new LinearLayout.LayoutParams(
                        -1, dp(26)
                ));

        // SEARCH
        searchBox = new EditText(this);
        searchBox.setHint("Search apps...");
        searchBox.setSingleLine(true);
        searchBox.setTextSize(15);
        searchBox.setPadding(
                dp(18), 0, dp(18), 0
        );

        searchBox.setTextColor(Color.DKGRAY);
        searchBox.setHintTextColor(
                Color.rgb(130, 130, 145)
        );

        GradientDrawable searchBg = new GradientDrawable();
        searchBg.setColor(
                Color.argb(110, 255, 255, 255)
        );
        searchBg.setCornerRadius(dp(30));
        searchBg.setStroke(
                dp(1),
                Color.argb(100, 255, 255, 255)
        );

        searchBox.setBackground(searchBg);

        LinearLayout.LayoutParams searchParams =
                new LinearLayout.LayoutParams(
                        -1, dp(52)
                );

        searchParams.setMargins(
                0, dp(8), 0, dp(12)
        );

        root.addView(searchBox, searchParams);

        // QUICK ACTIONS
        LinearLayout quick = new LinearLayout(this);
        quick.setOrientation(LinearLayout.HORIZONTAL);
        quick.setGravity(Gravity.CENTER);

        quick.addView(createQuickButton(
                "⚙", "Settings"
        ));

        quick.addView(createQuickButton(
                "📁", "Files"
        ));

        quick.addView(createQuickButton(
                "📷", "Camera"
        ));

        root.addView(quick,
                new LinearLayout.LayoutParams(
                        -1, dp(55)
                ));

        // APPS
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);

        appGrid = new LinearLayout(this);
        appGrid.setOrientation(LinearLayout.VERTICAL);

        scroll.addView(appGrid);

        root.addView(scroll,
                new LinearLayout.LayoutParams(
                        -1, 0, 1
                ));

        // FOOTER
        TextView footer = new TextView(this);
        footer.setText(
                "Sunny UI  •  Designed for Android  •  V5"
        );
        footer.setTextSize(10);
        footer.setTextColor(
                Color.rgb(130, 130, 145)
        );
        footer.setGravity(Gravity.CENTER);

        root.addView(footer,
                new LinearLayout.LayoutParams(
                        -1, dp(28)
                ));

        setContentView(root);

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

    private View createQuickButton(
            String icon,
            String label) {

        TextView button = new TextView(this);

        button.setText(
                icon + "  " + label
        );

        button.setTextSize(11);
        button.setGravity(Gravity.CENTER);
        button.setTextColor(
                Color.rgb(60, 60, 75)
        );

        GradientDrawable bg =
                new GradientDrawable();

        bg.setColor(
                Color.argb(85, 255, 255, 255)
        );

        bg.setCornerRadius(dp(22));

        button.setBackground(bg);

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(
                        0, dp(44), 1
                );

        p.setMargins(
                dp(3), 0, dp(3), 0
        );

        button.setLayoutParams(p);

        if (label.equals("Settings")) {
            button.setOnClickListener(v ->
                    launchPackage(
                            "com.android.settings"
                    )
            );
        }

        if (label.equals("Files")) {
            button.setOnClickListener(v -> {
                Intent i = new Intent(
                        Intent.ACTION_OPEN_DOCUMENT
                );
                i.setType("*/*");
                i.addCategory(
                        Intent.CATEGORY_OPENABLE
                );
                startActivity(i);
            });
        }

        if (label.equals("Camera")) {
            button.setOnClickListener(v -> {
                Intent i = new Intent(
                        "android.media.action.IMAGE_CAPTURE"
                );
                try {
                    startActivity(i);
                } catch (Exception ignored) {
                }
            });
        }

        return button;
    }

    private void loadApps() {

        Intent intent = new Intent(
                Intent.ACTION_MAIN
        );

        intent.addCategory(
                Intent.CATEGORY_LAUNCHER
        );

        List<android.content.pm.ResolveInfo> results =
                pm.queryIntentActivities(
                        intent, 0
                );

        allApps.clear();

        for (android.content.pm.ResolveInfo info
                : results) {

            ApplicationInfo app =
                    info.activityInfo.applicationInfo;

            if (!allApps.contains(app)) {
                allApps.add(app);
            }
        }

        Collections.sort(
                allApps,
                new Comparator<ApplicationInfo>() {

                    @Override
                    public int compare(
                            ApplicationInfo a,
                            ApplicationInfo b) {

                        return pm
                                .getApplicationLabel(a)
                                .toString()
                                .compareToIgnoreCase(
                                        pm.getApplicationLabel(b)
                                                .toString()
                                );
                    }
                }
        );
    }

    private void showApps(String query) {

        appGrid.removeAllViews();

        String q = query.toLowerCase(
                Locale.getDefault()
        );

        LinearLayout row = null;
        int count = 0;

        for (ApplicationInfo app : allApps) {

            String name =
                    pm.getApplicationLabel(app)
                            .toString();

            if (!name.toLowerCase(
                    Locale.getDefault()
            ).contains(q)) {
                continue;
            }

            if (count % 3 == 0) {

                row = new LinearLayout(this);
                row.setOrientation(
                        LinearLayout.HORIZONTAL
                );

                row.setGravity(
                        Gravity.CENTER
                );

                appGrid.addView(
                        row,
                        new LinearLayout.LayoutParams(
                                -1, dp(108)
                        )
                );
            }

            row.addView(
                    createAppCard(app),
                    new LinearLayout.LayoutParams(
                            0, dp(100), 1
                    )
            );

            count++;
        }

        if (count == 0) {

            TextView empty = new TextView(this);

            empty.setText("No apps found");
            empty.setTextSize(16);
            empty.setTextColor(Color.GRAY);
            empty.setGravity(Gravity.CENTER);

            appGrid.addView(
                    empty,
                    new LinearLayout.LayoutParams(
                            -1, dp(100)
                    )
            );
        }
    }

    private View createAppCard(
            final ApplicationInfo app) {

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setGravity(
                Gravity.CENTER
        );

        GradientDrawable glass =
                new GradientDrawable();

        glass.setColor(
                Color.argb(105, 255, 255, 255)
        );

        glass.setCornerRadius(dp(25));

        glass.setStroke(
                dp(1),
                Color.argb(90, 255, 255, 255)
        );

        card.setBackground(glass);

        card.setPadding(
                dp(5), dp(7),
                dp(5), dp(5)
        );

        ImageView icon =
                new ImageView(this);

        icon.setImageDrawable(
                pm.getApplicationIcon(app)
        );

        card.addView(
                icon,
                new LinearLayout.LayoutParams(
                        dp(44), dp(44)
                )
        );

        TextView name =
                new TextView(this);

        name.setText(
                pm.getApplicationLabel(app)
                        .toString()
        );

        name.setTextSize(11);
        name.setTextColor(
                Color.rgb(45, 45, 55)
        );

        name.setGravity(
                Gravity.CENTER
        );

        name.setMaxLines(1);

        card.addView(
                name,
                new LinearLayout.LayoutParams(
                        -1, dp(30)
                )
        );

        card.setOnClickListener(
                v -> launchApp(app)
        );

        card.setOnTouchListener(
                new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(
                            View v,
                            MotionEvent event) {

                        if (event.getAction()
                                == MotionEvent.ACTION_DOWN) {

                            v.animate()
                                    .scaleX(.93f)
                                    .scaleY(.93f)
                                    .setDuration(80)
                                    .setInterpolator(
                                            new DecelerateInterpolator()
                                    )
                                    .start();

                        } else if (
                                event.getAction()
                                        == MotionEvent.ACTION_UP
                                        ||
                                event.getAction()
                                        == MotionEvent.ACTION_CANCEL) {

                            v.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(120)
                                    .start();
                        }

                        return false;
                    }
                }
        );

        return card;
    }

    private void launchApp(
            ApplicationInfo app) {

        try {

            Intent intent =
                    pm.getLaunchIntentForPackage(
                            app.packageName
                    );

            if (intent != null) {
                intent.addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK
                );
                startActivity(intent);
            }

        } catch (Exception ignored) {
        }
    }

    private void launchPackage(
            String packageName) {

        try {

            Intent intent =
                    pm.getLaunchIntentForPackage(
                            packageName
                    );

            if (intent != null) {
                startActivity(intent);
            }

        } catch (Exception ignored) {
        }
    }

    private void updateTime() {

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

        if (clockView != null) {
            clockView.setText(time);
        }

        if (dateView != null) {
            dateView.setText(date);
        }

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

    @Override
    protected void onResume() {

        super.onResume();

        if (pm != null) {
            loadApps();

            if (searchBox != null) {
                showApps(
                        searchBox.getText()
                                .toString()
                );
            }
        }
    }
}
