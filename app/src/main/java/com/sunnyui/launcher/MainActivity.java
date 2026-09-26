package com.sunnyui.launcher;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.text.Editable;
import android.text.TextWatcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends Activity {

    LinearLayout appGrid;
    EditText searchBox;
    PackageManager pm;

    List<ApplicationInfo> allApps = new ArrayList<>();

    int glassWhite = Color.argb(150, 255, 255, 255);
    int glassBorder = Color.argb(120, 255, 255, 255);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pm = getPackageManager();

        createSunnyUI();
        loadApps();
        showApps(allApps);
    }

    private void createSunnyUI() {

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(18), dp(18), dp(18));

        GradientDrawable background = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{
                        Color.rgb(235, 225, 255),
                        Color.rgb(215, 235, 255),
                        Color.rgb(245, 235, 255)
                }
        );

        root.setBackground(background);

        root.animate()
                .alpha(0.92f)
                .setDuration(1800)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        TextView title = new TextView(this);
        title.setText("SUNNY UI");
        title.setTextSize(30);
        title.setTextColor(Color.rgb(35, 35, 45));
        title.setGravity(Gravity.CENTER);
        title.setTypeface(null, android.graphics.Typeface.BOLD);

        root.addView(title,
                new LinearLayout.LayoutParams(-1, dp(55)));

        TextView subtitle = new TextView(this);
        subtitle.setText("Premium • Glass • 3D Experience");
        subtitle.setTextSize(13);
        subtitle.setTextColor(Color.rgb(80, 80, 95));
        subtitle.setGravity(Gravity.CENTER);

        root.addView(subtitle,
                new LinearLayout.LayoutParams(-1, dp(30)));

        LinearLayout searchCard = new LinearLayout(this);
        searchCard.setGravity(Gravity.CENTER_VERTICAL);
        searchCard.setPadding(dp(18), 0, dp(18), 0);

        GradientDrawable searchBackground = new GradientDrawable();
        searchBackground.setColor(Color.argb(135, 255, 255, 255));
        searchBackground.setCornerRadius(dp(28));
        searchBackground.setStroke(dp(1), glassBorder);

        searchCard.setBackground(searchBackground);
        searchCard.setElevation(dp(10));

        TextView searchIcon = new TextView(this);
        searchIcon.setText("⌕");
        searchIcon.setTextSize(25);
        searchIcon.setTextColor(Color.DKGRAY);

        searchCard.addView(searchIcon,
                new LinearLayout.LayoutParams(dp(35), dp(55)));

        searchBox = new EditText(this);
        searchBox.setHint("Search apps...");
        searchBox.setTextSize(15);
        searchBox.setSingleLine(true);
        searchBox.setBackgroundColor(Color.TRANSPARENT);
        searchBox.setPadding(0, 0, 0, 0);

        searchCard.addView(searchBox,
                new LinearLayout.LayoutParams(0, dp(55), 1));

        LinearLayout.LayoutParams searchParams =
                new LinearLayout.LayoutParams(-1, dp(58));

        searchParams.setMargins(0, dp(12), 0, dp(15));

        root.addView(searchCard, searchParams);

        appGrid = new LinearLayout(this);
        appGrid.setOrientation(LinearLayout.VERTICAL);

        root.addView(appGrid,
                new LinearLayout.LayoutParams(-1, 0, 1));

        TextView footer = new TextView(this);
        footer.setText("Sunny UI  •  Designed for Android");
        footer.setTextSize(11);
        footer.setTextColor(Color.rgb(90, 90, 105));
        footer.setGravity(Gravity.CENTER);

        root.addView(footer,
                new LinearLayout.LayoutParams(-1, dp(28)));

        setContentView(root);

        searchBox.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                filterApps(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void loadApps() {

        List<ApplicationInfo> apps =
                pm.getInstalledApplications(
                        PackageManager.GET_META_DATA);

        for (ApplicationInfo app : apps) {

            Intent launchIntent =
                    pm.getLaunchIntentForPackage(app.packageName);

            if (launchIntent != null &&
                    !app.packageName.equals(getPackageName())) {

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

                        return pm.getApplicationLabel(a)
                                .toString()
                                .compareToIgnoreCase(
                                        pm.getApplicationLabel(b)
                                                .toString());
                    }
                });
    }

    private void filterApps(String query) {

        List<ApplicationInfo> filtered =
                new ArrayList<>();

        String q = query.toLowerCase().trim();

        for (ApplicationInfo app : allApps) {

            String name =
                    pm.getApplicationLabel(app)
                            .toString()
                            .toLowerCase();

            if (name.contains(q)) {
                filtered.add(app);
            }
        }

        showApps(filtered);
    }

    private void showApps(List<ApplicationInfo> apps) {

        appGrid.removeAllViews();

        LinearLayout row = null;
        int column = 0;

        for (ApplicationInfo app : apps) {

            if (column == 0) {

                row = new LinearLayout(this);
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setGravity(Gravity.CENTER);

                appGrid.addView(row,
                        new LinearLayout.LayoutParams(
                                -1, dp(105)));
            }

            View card = createAppCard(app);

            row.addView(card,
                    new LinearLayout.LayoutParams(
                            0, dp(92), 1));

            column++;

            if (column == 3) {
                column = 0;
            }
        }
    }

    private View createAppCard(final ApplicationInfo app) {

        LinearLayout card = new LinearLayout(this);

        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER);

        card.setPadding(
                dp(8), dp(8), dp(8), dp(8));

        GradientDrawable glass = new GradientDrawable();

        glass.setColor(glassWhite);
        glass.setCornerRadius(dp(24));
        glass.setStroke(dp(1), glassBorder);

        card.setBackground(glass);
        card.setElevation(dp(12));

        ImageView icon = new ImageView(this);

        Drawable drawable = app.loadIcon(pm);
        icon.setImageDrawable(drawable);

        card.addView(icon,
                new LinearLayout.LayoutParams(
                        dp(45), dp(45)));

        TextView name = new TextView(this);

        name.setText(pm.getApplicationLabel(app));
        name.setTextSize(11);
        name.setTextColor(Color.rgb(35, 35, 45));
        name.setGravity(Gravity.CENTER);
        name.setMaxLines(1);

        card.addView(name,
                new LinearLayout.LayoutParams(
                        -1, dp(25)));

        card.setOnTouchListener(
                new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(
                            View v, MotionEvent event) {

                        switch (event.getAction()) {

                            case MotionEvent.ACTION_DOWN:

                                v.animate()
                                        .scaleX(0.92f)
                                        .scaleY(0.92f)
                                        .translationZ(dp(3))
                                        .setDuration(100)
                                        .start();

                                return true;

                            case MotionEvent.ACTION_UP:

                                v.animate()
                                        .scaleX(1f)
                                        .scaleY(1f)
                                        .translationZ(dp(12))
                                        .setDuration(180)
                                        .start();

                                launchApp(app);

                                return true;

                            case MotionEvent.ACTION_CANCEL:

                                v.animate()
                                        .scaleX(1f)
                                        .scaleY(1f)
                                        .translationZ(dp(12))
                                        .setDuration(180)
                                        .start();

                                return true;
                        }

                        return true;
                    }
                });

        return card;
    }

    private void launchApp(ApplicationInfo app) {

        try {

            Intent intent =
                    pm.getLaunchIntentForPackage(
                            app.packageName);

            if (intent != null) {

                intent.addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int dp(int value) {

        return (int) (
                value *
                getResources()
                        .getDisplayMetrics()
                        .density);
    }
}
