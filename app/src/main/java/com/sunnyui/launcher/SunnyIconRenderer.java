package com.sunnyui.launcher;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class SunnyIconRenderer {

    private SunnyIconRenderer() {
        // Utility class
    }

    /**
     * Creates a Sunny UI Crystal 3D icon.
     */
    public static View create(
            Context context,
            Drawable icon,
            String label) {

        CrystalIconView view =
                new CrystalIconView(
                        context,
                        icon,
                        label
                );

        return view;
    }

    /**
     * Custom icon renderer.
     *
     * Draws:
     * - 3D depth
     * - glass body
     * - bright edge
     * - inner glow
     * - top reflection
     * - soft shadow
     * - original application icon
     */
    private static class CrystalIconView
            extends View {

        private final Drawable icon;
        private final String label;

        private final Paint bodyPaint =
                new Paint(Paint.ANTI_ALIAS_FLAG);

        private final Paint depthPaint =
                new Paint(Paint.ANTI_ALIAS_FLAG);

        private final Paint edgePaint =
                new Paint(Paint.ANTI_ALIAS_FLAG);

        private final Paint highlightPaint =
                new Paint(Paint.ANTI_ALIAS_FLAG);

        private final Paint iconPaint =
                new Paint(Paint.ANTI_ALIAS_FLAG);

        private final Paint textPaint =
                new Paint(Paint.ANTI_ALIAS_FLAG);

        private final RectF bodyRect =
                new RectF();

        private final RectF iconRect =
                new RectF();

        private float pressScale = 1f;
        private float pressDepth = 0f;

        private final float radius;
        private final float depth;

        public CrystalIconView(
                Context context,
                Drawable icon,
                String label) {

            super(context);

            this.icon = icon;
            this.label = label == null
                    ? ""
                    : label;

            radius = dp(context, 22);
            depth = dp(context, 5);

            setLayerType(
                    View.LAYER_TYPE_SOFTWARE,
                    null
            );

            setClickable(true);

            textPaint.setColor(
                    Color.WHITE
            );

            textPaint.setTextSize(
                    dp(context, 11)
            );

            textPaint.setTextAlign(
                    Paint.Align.CENTER
            );

            textPaint.setTypeface(
                    android.graphics.Typeface
                            .create(
                                    "sans",
                                    android.graphics.Typeface.BOLD
                            )
            );
        }

        @Override
        protected void onDraw(Canvas canvas) {

            super.onDraw(canvas);

            float width = getWidth();
            float height = getHeight();

            float centerX = width / 2f;

            float iconSize =
                    Math.min(
                            dp(getContext(), 64),
                            width * 0.62f
                    );

            float top =
                    dp(getContext(), 8);

            float left =
                    centerX - iconSize / 2f;

            float right =
                    centerX + iconSize / 2f;

            float bottom =
                    top + iconSize;

            bodyRect.set(
                    left,
                    top,
                    right,
                    bottom
            );

            /*
             * 1. Deep shadow
             */
            Paint shadowPaint =
                    new Paint(
                            Paint.ANTI_ALIAS_FLAG
                    );

            shadowPaint.setColor(
                    Color.argb(
                            170,
                            0,
                            0,
                            0
                    )
            );

            shadowPaint.setShadowLayer(
                    dp(getContext(), 9),
                    0,
                    dp(getContext(), 7),
                    Color.argb(
                            150,
                            0,
                            0,
                            0
                    )
            );

            RectF shadowRect =
                    new RectF(bodyRect);

            shadowRect.offset(
                    0,
                    depth
            );

            canvas.drawRoundRect(
                    shadowRect,
                    radius,
                    radius,
                    shadowPaint
            );

            /*
             * 2. 3D lower extrusion
             */
            LinearGradient depthGradient =
                    new LinearGradient(
                            left,
                            top,
                            right,
                            bottom + depth,
                            new int[] {
                                    Color.rgb(
                                            210,
                                            215,
                                            235
                                    ),
                                    Color.rgb(
                                            105,
                                            115,
                                            150
                                    ),
                                    Color.rgb(
                                            45,
                                            50,
                                            75
                                    )
                            },
                            null,
                            Shader.TileMode.CLAMP
                    );

            depthPaint.setShader(
                    depthGradient
            );

            RectF extrusion =
                    new RectF(bodyRect);

            extrusion.offset(
                    0,
                    depth
            );

            canvas.drawRoundRect(
                    extrusion,
                    radius,
                    radius,
                    depthPaint
            );

            /*
             * 3. Crystal glass body
             */
            LinearGradient glassGradient =
                    new LinearGradient(
                            left,
                            top,
                            right,
                            bottom,
                            new int[] {
                                    Color.argb(
                                            235,
                                            255,
                                            255,
                                            255
                                    ),
                                    Color.argb(
                                            185,
                                            225,
                                            230,
                                            255
                                    ),
                                    Color.argb(
                                            150,
                                            155,
                                            165,
                                            215
                                    )
                            },
                            null,
                            Shader.TileMode.CLAMP
                    );

            bodyPaint.setShader(
                    glassGradient
            );

            bodyPaint.setShadowLayer(
                    dp(getContext(), 3),
                    0,
                    dp(getContext(), 2),
                    Color.argb(
                            100,
                            0,
                            0,
                            0
                    )
            );

            canvas.drawRoundRect(
                    bodyRect,
                    radius,
                    radius,
                    bodyPaint
            );

            bodyPaint.clearShadowLayer();

            /*
             * 4. Inner dark glass layer
             */
            float inset =
                    dp(getContext(), 5);

            RectF inner =
                    new RectF(
                            left + inset,
                            top + inset,
                            right - inset,
                            bottom - inset
                    );

            LinearGradient innerGradient =
                    new LinearGradient(
                            inner.left,
                            inner.top,
                            inner.right,
                            inner.bottom,
                            new int[] {
                                    Color.argb(
                                            125,
                                            255,
                                            255,
                                            255
                                    ),
                                    Color.argb(
                                            65,
                                            90,
                                            105,
                                            160
                                    ),
                                    Color.argb(
                                            95,
                                            20,
                                            25,
                                            50
                                    )
                            },
                            null,
                            Shader.TileMode.CLAMP
                    );

            Paint innerPaint =
                    new Paint(
                            Paint.ANTI_ALIAS_FLAG
                    );

            innerPaint.setShader(
                    innerGradient
            );

            canvas.drawRoundRect(
                    inner,
                    dp(getContext(), 17),
                    dp(getContext(), 17),
                    innerPaint
            );

            /*
             * 5. Original application icon
             */
            float iconInset =
                    dp(getContext(), 12);

            iconRect.set(
                    left + iconInset,
                    top + iconInset,
                    right - iconInset,
                    bottom - iconInset
            );

            if (icon != null) {

                icon.setBounds(
                        (int) iconRect.left,
                        (int) iconRect.top,
                        (int) iconRect.right,
                        (int) iconRect.bottom
                );

                icon.setAlpha(255);
                icon.draw(canvas);
            }

            /*
             * 6. Glass edge
             */
            edgePaint.setStyle(
                    Paint.Style.STROKE
            );

            edgePaint.setStrokeWidth(
                    dp(getContext(), 1.5f)
            );

            edgePaint.setShader(
                    new LinearGradient(
                            left,
                            top,
                            right,
                            bottom,
                            new int[] {
                                    Color.argb(
                                            255,
                                            255,
                                            255,
                                            255
                                    ),
                                    Color.argb(
                                            115,
                                            255,
                                            255,
                                            255
                                    ),
                                    Color.argb(
                                            230,
                                            255,
                                            255,
                                            255
                                    )
                            },
                            null,
                            Shader.TileMode.CLAMP
                    )
            );

            canvas.drawRoundRect(
                    bodyRect,
                    radius,
                    radius,
                    edgePaint
            );

            /*
             * 7. Top glass reflection
             */
            RectF reflection =
                    new RectF(
                            left + dp(getContext(), 8),
                            top + dp(getContext(), 6),
                            right - dp(getContext(), 8),
                            top + dp(getContext(), 18)
                    );

            LinearGradient reflectionGradient =
                    new LinearGradient(
                            reflection.left,
                            reflection.top,
                            reflection.right,
                            reflection.bottom,
                            new int[] {
                                    Color.argb(
                                            180,
                                            255,
                                            255,
                                            255
                                    ),
                                    Color.argb(
                                            45,
                                            255,
                                            255,
                                            255
                                    )
                            },
                            null,
                            Shader.TileMode.CLAMP
                    );

            highlightPaint.setShader(
                    reflectionGradient
            );

            canvas.drawRoundRect(
                    reflection,
                    dp(getContext(), 8),
                    dp(getContext(), 8),
                    highlightPaint
            );

            /*
             * 8. Small light point
             */
            Paint light =
                    new Paint(
                            Paint.ANTI_ALIAS_FLAG
                    );

            light.setColor(
                    Color.argb(
                            210,
                            255,
                            255,
                            255
                    )
            );

            canvas.drawCircle(
                    right - dp(getContext(), 13),
                    top + dp(getContext(), 13),
                    dp(getContext(), 2),
                    light
            );

            /*
             * 9. App label
             */
            if (!label.isEmpty()) {

                float labelY =
                        bottom +
                                dp(getContext(), 21);

                textPaint.setAlpha(235);

                canvas.drawText(
                        label,
                        centerX,
                        labelY,
                        textPaint
                );
            }
        }

        @Override
        public boolean onTouchEvent(
                MotionEvent event) {

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:

                    animateDown();
                    return true;

                case MotionEvent.ACTION_UP:

                    animateUp();

                    performClick();

                    return true;

                case MotionEvent.ACTION_CANCEL:

                    animateUp();
                    return true;
            }

            return true;
        }

        private void animateDown() {

            animate()
                    .scaleX(0.91f)
                    .scaleY(0.91f)
                    .translationY(
                            dp(getContext(), 3)
                    )
                    .setDuration(90)
                    .setInterpolator(
                            new DecelerateInterpolator()
                    )
                    .start();

            pressScale = 0.91f;
            pressDepth = dp(
                    getContext(),
                    3
            );

            invalidate();
        }

        private void animateUp() {

            animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .translationY(0)
                    .setDuration(180)
                    .setInterpolator(
                            new DecelerateInterpolator()
                    )
                    .start();

            pressScale = 1f;
            pressDepth = 0f;

            invalidate();
        }

        @Override
        public boolean performClick() {

            super.performClick();

            return true;
        }

        private static float dp(
                Context context,
                float value) {

            return value *
                    context
                            .getResources()
                            .getDisplayMetrics()
                            .density;
        }
    }
}
