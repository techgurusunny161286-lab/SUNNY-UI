package com.sunnyui.launcher;

import android.app.Activity;
import android.os.Bundle;
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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends Activity {

    LinearLayout appGrid;
    EditText searchBox;
    PackageManager pm;
    List<ApplicationInfo> apps;

    int glassWhite = Color.argb(105, 255, 255, 255);
    int glassBorder = Color.argb(150, 255, 255, 255);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pm = getPackageManager();

        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        buildUI();
        loadApps();
    }

    private void buildUI() {

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(18), dp(18), dp(12));

        GradientDrawable background = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{
                        Color.rgb(245, 247, 255),
                        Color.rgb(230, 225, 250),
                        Color.rgb(248, 240, 255)
                }
        );

        root.setBackground(background);

        TextView title = new TextView(this);
        title.setText("SUNNY UI");
        title.setTextSize(30);
        title.setTextColor(Color.rgb(25, 25, 35));
        title.setGravity(Gravity.CENTER);
        title.setTypeface(null, android.graphics.Typeface.BOLD);

        root.addView(title,
                new LinearLayout.LayoutParams(
                        -1, dp(48)
                ));

        TextView subtitle = new TextView(this);
        subtitle.setText("Premium • Glass • 3D Experience");
        subtitle.setTextSize(13);
        subtitle.setTextColor(Color.rgb(90, 90, 105));
        subtitle.setGravity(Gravity.CENTER);

        root.addView(subtitle,
                new LinearLayout.LayoutParams(
                        -1, dp(30)
                ));

        searchBox = new EditText(this);
        searchBox.setHint("Search apps...");
        searchBox.setTextSize(15);
        searchBox.setSingleLine(true);
        searchBox.setPadding(dp(18), 0, dp(18), 0);

        GradientDrawable searchGlass = new GradientDrawable();
        searchGlass.setColor(Color.argb(90, 255, 255, 255));
        searchGlass.setCornerRadius(dp(35));
        searchGlass.setStroke(dp(1), Color.argb(100, 255, 255, 255));

        searchBox.setBackground(searchGlass);

        LinearLayout.LayoutParams searchParams =
                new LinearLayout.LayoutParams(
                        -1, dp(56)
                );

        searchParams.setMargins(0, dp(10), 0, dp(16));

        root.addView(searchBox, searchParams);

        appGrid = new LinearLayout(this);
        appGrid.setOrientation(LinearLayout.VERTICAL);

        root.addView(appGrid,
                new LinearLayout.LayoutParams(
                        -1, 0, 1
                ));

        TextView footer = new TextView(this);
        footer.setText("Sunny UI  •  Designed for Android");
        footer.setTextSize(11);
        footer.setTextColor(Color.rgb(115, 115, 125));
        footer.setGravity(Gravity.CENTER);

        root.addView(footer,
                new LinearLayout.LayoutParams(
                        -1, dp(32)
                ));

        searchBox.addTextChangedListener(
                new android.text.TextWatcher() {

                    public void beforeTextChanged(
                            CharSequence s, int start, int count, int after) {}

                    public void onTextChanged(
                            CharSequence s, int start, int before, int count) {
                        showApps(s.toString());
                    }

                    public void afterTextChanged(
                            android.text.Editable s) {}
                });

        setContentView(root);
    }

    private void loadApps() {

        apps = new ArrayList<>();

        List<ApplicationInfo> installed =
                pm.getInstalledApplications(
                        PackageManager.GET_META_DATA
                );

        for (ApplicationInfo app : installed) {

            if (pm.getLaunchIntentForPackage(
                    app.packageName) != null) {

                apps.add(app);
            }
        }

        Collections.sort(apps,
                new Comparator<ApplicationInfo>() {
                    @Override
                    public int compare(
                            ApplicationInfo a,
                            ApplicationInfo b) {

                        return pm.getApplicationLabel(a)
                                .toString()
                                .compareToIgnoreCase(
                                        pm.getApplicationLabel(b)
                                                .toString());
                    }
                });

        showApps("");
    }

    private void showApps(String query) {

        appGrid.removeAllViews();

        List<ApplicationInfo> filtered =
                new ArrayList<>();

        for (ApplicationInfo app : apps) {

            String name =
                    pm.getApplicationLabel(app)
                            .toString();

            if (name.toLowerCase()
                    .contains(query.toLowerCase())) {

                filtered.add(app);
            }
        }

        LinearLayout row = null;
        int column = 0;

        for (ApplicationInfo app : filtered) {

            if (column == 0) {

                row = new LinearLayout(this);
                row.setOrientation(
                        LinearLayout.HORIZONTAL
                );

                appGrid.addView(row,
                        new LinearLayout.LayoutParams(
                                -1, dp(105)
                        ));
            }

            row.addView(
                    createGlassCard(app),
                    new LinearLayout.LayoutParams(
                            0, -1, 1
                    )
            );

            column++;

            if (column == 3) {
                column = 0;
            }
        }
    }

    private View createGlassCard(
            final ApplicationInfo app) {

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setGravity(Gravity.CENTER);

        GradientDrawable glass =
                new GradientDrawable();

        glass.setColor(glassWhite);
        glass.setCornerRadius(dp(24));
        glass.setStroke(dp(1), glassBorder);

        card.setBackground(glass);
        card.setElevation(dp(8));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        -1, -1
                );

        params.setMargins(
                dp(4), dp(5), dp(4), dp(5)
        );

        ImageView icon =
                new ImageView(this);

        icon.setImageDrawable(
                app.loadIcon(pm)
        );

        card.addView(icon,
                new LinearLayout.LayoutParams(
                        dp(42), dp(42)
                ));

        TextView name =
                new TextView(this);

        name.setText(
                pm.getApplicationLabel(app)
                        .toString()
        );

        name.setTextSize(11);
        name.setTextColor(
                Color.rgb(55, 55, 65)
        );

        name.setGravity(Gravity.CENTER);

        card.addView(name,
                new LinearLayout.LayoutParams(
                        -1, dp(30)
                ));

        card.setOnTouchListener(
                new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(
                            View v,
                            MotionEvent event) {

                        if (event.getAction() ==
                                MotionEvent.ACTION_DOWN) {

                            v.animate()
                                    .scaleX(0.93f)
                                    .scaleY(0.93f)
                                    .rotationX(2f)
                                    .rotationY(-2f)
                                    .setDuration(120)
                                    .setInterpolator(
                                            new DecelerateInterpolator())
                                    .start();

                            return true;
                        }

                        if (event.getAction() ==
                                MotionEvent.ACTION_UP) {

                            v.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .rotationX(0f)
                                    .rotationY(0f)
                                    .setDuration(180)
                                    .setInterpolator(
                                            new DecelerateInterpolator())
                                    .start();

                            launchApp(app);

                            return true;
                        }

                        if (event.getAction() ==
                                MotionEvent.ACTION_CANCEL) {

                            v.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .rotationX(0f)
                                    .rotationY(0f)
                                    .setDuration(150)
                                    .start();

                            return true;
                        }

                        return true;
                    }
                });

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

    private int dp(int value) {

        return (int) (
                value *
                getResources()
                        .getDisplayMetrics()
                        .density
        );
    }
}
