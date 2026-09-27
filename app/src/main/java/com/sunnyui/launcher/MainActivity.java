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
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

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
    private PackageManager pm;
    private final List<ApplicationInfo> allApps = new ArrayList<>();
    private final Handler handler = new Handler();

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pm = getPackageManager();

        buildUI();
        loadApps();
        showApps("");

        updateClock();
    }

    private void buildUI() {

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(20), dp(18), dp(12));

        GradientDrawable background = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{
                        Color.rgb(247, 245, 255),
                        Color.rgb(232, 238, 255),
                        Color.rgb(247, 242, 255)
                }
        );
        root.setBackground(background);

        TextView clock = new TextView(this);
        clock.setText("SUNNY UI");
        clock.setTextSize(30);
        clock.setTextColor(Color.rgb(25, 25, 35));
        clock.setGravity(Gravity.CENTER);
        clock.setTypeface(null, 1);

        root.addView(clock, new LinearLayout.LayoutParams(
                -1, dp(48)
        ));

        TextView subtitle = new TextView(this);
        subtitle.setText("Premium • Glass • 3D Experience");
        subtitle.setTextSize(13);
        subtitle.setTextColor(Color.rgb(95, 95, 110));
        subtitle.setGravity(Gravity.CENTER);

        root.addView(subtitle, new LinearLayout.LayoutParams(
                -1, dp(28)
        ));

        searchBox = new EditText(this);
        searchBox.setHint("Search apps...");
        searchBox.setTextSize(15);
        searchBox.setSingleLine(true);
        searchBox.setPadding(dp(18), 0, dp(18), 0);
        searchBox.setTextColor(Color.DKGRAY);
        searchBox.setHintTextColor(Color.rgb(125, 125, 140));

        GradientDrawable searchBackground = new GradientDrawable();
        searchBackground.setColor(Color.argb(90, 255, 255, 255));
        searchBackground.setCornerRadius(dp(35));
        searchBackground.setStroke(dp(1), Color.argb(80, 255, 255, 255));

        searchBox.setBackground(searchBackground);

        LinearLayout.LayoutParams searchParams =
                new LinearLayout.LayoutParams(-1, dp(52));
        searchParams.setMargins(0, dp(8), 0, dp(16));

        root.addView(searchBox, searchParams);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);

        appGrid = new LinearLayout(this);
        appGrid.setOrientation(LinearLayout.VERTICAL);

        scroll.addView(appGrid);

        root.addView(scroll, new LinearLayout.LayoutParams(
                -1, 0, 1
        ));

        TextView footer = new TextView(this);
        footer.setText("Sunny UI  •  Designed for Android");
        footer.setTextSize(11);
        footer.setTextColor(Color.rgb(130, 130, 145));
        footer.setGravity(Gravity.CENTER);

        root.addView(footer, new LinearLayout.LayoutParams(
                -1, dp(32)
        ));

        setContentView(root);

        searchBox.addTextChangedListener(
                new android.text.TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(
                            CharSequence s, int start, int before, int count) {
                        showApps(s.toString());
                    }

                    @Override
                    public void afterTextChanged(
                            android.text.Editable s) {}
                }
        );
    }

    private void loadApps() {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ApplicationInfo> apps =
                pm.queryIntentActivities(
                        intent,
                        PackageManager.ResolveInfoFlags.of(0)
                ).stream()
                        .map(info -> info.activityInfo.applicationInfo)
                        .collect(java.util.stream.Collectors.toList());

        allApps.clear();

        for (ApplicationInfo app : apps) {
            if (!allApps.contains(app)) {
                allApps.add(app);
            }
        }

        Collections.sort(allApps, new Comparator<ApplicationInfo>() {
            @Override
            public int compare(ApplicationInfo a, ApplicationInfo b) {
                return pm.getApplicationLabel(a).toString()
                        .compareToIgnoreCase(
                                pm.getApplicationLabel(b).toString()
                        );
            }
        });
    }

    private void showApps(String query) {

        appGrid.removeAllViews();

        String text = query.toLowerCase(Locale.getDefault());

        LinearLayout row = null;
        int count = 0;

        for (ApplicationInfo app : allApps) {

            String name = pm.getApplicationLabel(app).toString();

            if (!name.toLowerCase(Locale.getDefault()).contains(text)) {
                continue;
            }

            if (count % 3 == 0) {
                row = new LinearLayout(this);
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setGravity(Gravity.CENTER);

                appGrid.addView(row,
                        new LinearLayout.LayoutParams(
                                -1, dp(112)
                        ));
            }

            row.addView(createAppCard(app),
                    new LinearLayout.LayoutParams(
                            0, dp(98), 1
                    ));

            count++;
        }

        if (count == 0) {
            TextView empty = new TextView(this);
            empty.setText("No apps found");
            empty.setTextSize(16);
            empty.setTextColor(Color.GRAY);
            empty.setGravity(Gravity.CENTER);

            appGrid.addView(empty,
                    new LinearLayout.LayoutParams(
                            -1, dp(100)
                    ));
        }
    }

    private View createAppCard(final ApplicationInfo app) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER);

        card.setPadding(dp(6), dp(8), dp(6), dp(8));

        GradientDrawable glass = new GradientDrawable();
        glass.setColor(Color.argb(95, 255, 255, 255));
        glass.setCornerRadius(dp(25));
        glass.setStroke(dp(1), Color.argb(100, 255, 255, 255));

        card.setBackground(glass);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        0, dp(98), 1
                );

        params.setMargins(dp(4), dp(5), dp(4), dp(5));

        ImageView icon = new ImageView(this);
        icon.setImageDrawable(pm.getApplicationIcon(app));

        card.addView(icon,
                new LinearLayout.LayoutParams(
                        dp(42), dp(42)
                ));

        TextView name = new TextView(this);
        name.setText(pm.getApplicationLabel(app).toString());
        name.setTextSize(11);
        name.setTextColor(Color.rgb(45, 45, 55));
        name.setGravity(Gravity.CENTER);
        name.setMaxLines(1);

        LinearLayout.LayoutParams nameParams =
                new LinearLayout.LayoutParams(
                        -1, dp(30)
                );

        nameParams.topMargin = dp(4);

        card.addView(name, nameParams);

        card.setOnClickListener(v -> launchApp(app));

        card.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    v.animate()
                            .scaleX(0.94f)
                            .scaleY(0.94f)
                            .setDuration(80)
                            .setInterpolator(
                                    new DecelerateInterpolator()
                            )
                            .start();

                } else if (
                        event.getAction() == MotionEvent.ACTION_UP ||
                        event.getAction() == MotionEvent.ACTION_CANCEL
                ) {

                    v.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(120)
                            .setInterpolator(
                                    new DecelerateInterpolator()
                            )
                            .start();
                }

                return false;
            }
        });

        card.setLayoutParams(params);

        return card;
    }

    private void launchApp(ApplicationInfo app) {

        try {

            Intent intent =
                    pm.getLaunchIntentForPackage(
                            app.packageName
                    );

            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

        } catch (Exception ignored) {
        }
    }

    private void updateClock() {

        handler.postDelayed(new Runnable() {

            @Override
            public void run() {

                updateClock();

            }

        }, 60000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadApps();

        if (searchBox != null) {
            showApps(searchBox.getText().toString());
        }
    }
}
