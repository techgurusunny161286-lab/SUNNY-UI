package com.sunnyui.launcher;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public final class Sunny3DIconRenderer {

    private Sunny3DIconRenderer() {
    }

    public static View create(
            Context context,
            Drawable drawable,
            String label) {

        return new Crystal3DView(
                context,
                drawable,
                label
        );
    }

    private static class Crystal3DView
            extends View {

        private final Drawable sourceIcon;
        private final String label;

        private final Paint paint =
                new Paint(Paint.ANTI_ALIAS_FLAG);

        private final Paint iconPaint =
                new Paint(Paint.ANTI_ALIAS_FLAG);

        private final Paint textPaint =
                new Paint(Paint.ANTI_ALIAS_FLAG);

        private final RectF iconRect =
                new RectF();

        private final RectF shadowRect =
                new RectF();

        private float downX;
        private float downY;

        private boolean pressed = false;

        Crystal3DView(
                Context context,
                Drawable drawable,
                String label) {

            super(context);

            this.sourceIcon = drawable;
            this.label =
                    label == null ? "" : label;

            setLayerType(
                    View.LAYER_TYPE_SOFTWARE,
                    null
            );

            setClickable(true);

            textPaint.setAntiAlias(true);
            textPaint.setColor(
                    Color.WHITE
            );
            textPaint.setTextSize(
                    dp(10)
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

            float w = getWidth();
            float h = getHeight();

            float size =
                    Math.min(
                            dp(76),
                            w * 0.72f
                    );

            float cx = w / 2f;

            float top =
                    dp(6);

            float left =
                    cx - size / 2f;

            float right =
                    cx + size / 2f;

            float bottom =
                    top + size;

            float radius =
                    dp(22);

            float depth =
                    dp(7);

            iconRect.set(
                    left,
                    top,
                    right,
                    bottom
            );

            /*
             * 1. Floating shadow
             */

            shadowRect.set(
                    left + dp(5),
                    top + depth + dp(7),
                    right + dp(5),
                    bottom + depth + dp(7)
            );

            paint.setStyle(
                    Paint.Style.FILL
            );

            paint.setColor(
                    Color.argb(
                            145,
                            0,
                            0,
                            0
                    )
            );

            paint.setShadowLayer(
                    dp(10),
                    0,
                    dp(5),
                    Color.argb(
                            180,
                            0,
                            0,
                            0
                    )
            );

            canvas.drawRoundRect(
                    shadowRect,
                    radius,
                    radius,
                    paint
            );

            paint.clearShadowLayer();

            /*
             * 2. Deep 3D extrusion
             */

            LinearGradient extrusion =
                    new LinearGradient(
                            left,
                            top,
                            right,
                            bottom + depth,
                            new int[] {
                                    Color.rgb(
                                            220,
                                            225,
                                            255
                                    ),
                                    Color.rgb(
                                            115,
                                            125,
                                            175
                                    ),
                                    Color.rgb(
                                            38,
                                            43,
                                            75
                                    )
                            },
                            null,
                            Shader.TileMode.CLAMP
                    );

            paint.setShader(
                    extrusion
            );

            for (int i = 7; i >= 1; i--) {

                RectF layer =
                        new RectF(
                                left,
                                top + dp(i),
                                right,
                                bottom + dp(i)
                        );

                canvas.drawRoundRect(
                        layer,
                        radius,
                        radius,
                        paint
                );
            }

            paint.setShader(null);

            /*
             * 3. Main crystal surface
             */

            LinearGradient crystal =
                    new LinearGradient(
                            left,
                            top,
                            right,
                            bottom,
                            new int[] {
                                    Color.argb(
                                            245,
                                            255,
                                            255,
                                            255
                                    ),
                                    Color.argb(
                                            225,
                                            215,
                                            225,
                                            255
                                    ),
                                    Color.argb(
                                            215,
                                            120,
                                            145,
                                            220
                                    ),
                                    Color.argb(
                                            230,
                                            65,
                                            75,
                                            125
                                    )
                            },
                            new float[] {
                                    0f,
                                    .28f,
                                    .67f,
                                    1f
                            },
                            Shader.TileMode.CLAMP
                    );

            paint.setShader(
                    crystal
            );

            paint.setStyle(
                    Paint.Style.FILL
            );

            canvas.drawRoundRect(
                    iconRect,
                    radius,
                    radius,
                    paint
            );

            paint.setShader(null);

            /*
             * 4. Inner crystal layer
             */

            float inset =
                    dp(5);

            RectF inner =
                    new RectF(
                            left + inset,
                            top + inset,
                            right - inset,
                            bottom - inset
                    );

            LinearGradient innerGlass =
                    new LinearGradient(
                            inner.left,
                            inner.top,
                            inner.right,
                            inner.bottom,
                            new int[] {
                                    Color.argb(
                                            130,
                                            255,
                                            255,
                                            255
                                    ),
                                    Color.argb(
                                            45,
                                            255,
                                            255,
                                            255
                                    ),
                                    Color.argb(
                                            100,
                                            20,
                                            25,
                                            70
                                    )
                            },
                            null,
                            Shader.TileMode.CLAMP
                    );

            paint.setShader(
                    innerGlass
            );

            canvas.drawRoundRect(
                    inner,
                    dp(18),
                    dp(18),
                    paint
            );

            paint.setShader(null);

            /*
             * 5. Render original app icon
             */

            if (sourceIcon != null) {

                float iconInset =
                        dp(14);

                RectF original =
                        new RectF(
                                left + iconInset,
                                top + iconInset,
                                right - iconInset,
                                bottom - iconInset
                        );

                sourceIcon.setBounds(
                        (int) original.left,
                        (int) original.top,
                        (int) original.right,
                        (int) original.bottom
                );

                sourceIcon.setAlpha(
                        255
                );

                sourceIcon.draw(
                        canvas
                );
            }

            /*
             * 6. Glass surface overlay
             */

            LinearGradient surface =
                    new LinearGradient(
                            left,
                            top,
                            right,
                            bottom,
                            new int[] {
                                    Color.argb(
                                            115,
                                            255,
                                            255,
                                            255
                                    ),
                                    Color.argb(
                                            20,
                                            255,
                                            255,
                                            255
                                    ),
                                    Color.argb(
                                            35,
                                            255,
                                            255,
                                            255
                                    )
                            },
                            null,
                            Shader.TileMode.CLAMP
                    );

            paint.setShader(
                    surface
            );

            canvas.drawRoundRect(
                    iconRect,
                    radius,
                    radius,
                    paint
            );

            paint.setShader(null);

            /*
             * 7. Diagonal glossy reflection
             */

            Path shine =
                    new Path();

            shine.moveTo(
                    left + dp(8),
                    top + dp(5)
            );

            shine.lineTo(
                    right - dp(12),
                    top + dp(5)
            );

            shine.lineTo(
                    right - dp(27),
                    top + dp(22)
            );

            shine.lineTo(
                    left + dp(22),
                    top + dp(22)
            );

            shine.close();

            LinearGradient shineGradient =
                    new LinearGradient(
                            left,
                            top,
                            right,
                            top + dp(24),
                            new int[] {
                                    Color.argb(
                                            190,
                                            255,
                                            255,
                                            255
                                    ),
                                    Color.argb(
                                            0,
                                            255,
                                            255,
                                            255
                                    )
                            },
                            null,
                            Shader.TileMode.CLAMP
                    );

            paint.setShader(
                    shineGradient
            );

            canvas.drawPath(
                    shine,
                    paint
            );

            paint.setShader(null);

            /*
             * 8. Bright crystal edge
             */

            paint.setStyle(
                    Paint.Style.STROKE
            );

            paint.setStrokeWidth(
                    dp(1.4f)
            );

            paint.setShader(
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
                                            100,
                                            255,
                                            255,
                                            255
                                    ),
                                    Color.argb(
                                            220,
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
                    iconRect,
                    radius,
                    radius,
                    paint
            );

            paint.setShader(null);
            paint.setStyle(
                    Paint.Style.FILL
            );

            /*
             * 9. Small crystal light
             */

            paint.setColor(
                    Color.argb(
                            230,
                            255,
                            255,
                            255
                    )
            );

            canvas.drawCircle(
                    right - dp(13),
                    top + dp(13),
                    dp(2),
                    paint
            );

            /*
             * 10. App name
             */

            if (!label.isEmpty()) {

                textPaint.setAlpha(
                        235
                );

                canvas.drawText(
                        label,
                        cx,
                        bottom + dp(20),
                        textPaint
                );
            }
        }

        @Override
        public boolean onTouchEvent(
                MotionEvent event) {

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:

                    downX = event.getX();
                    downY = event.getY();

                    pressed = true;

                    animate()
                            .scaleX(.86f)
                            .scaleY(.86f)
                            .translationY(
                                    dp(5)
                            )
                            .setDuration(80)
                            .setInterpolator(
                                    new DecelerateInterpolator()
                            )
                            .start();

                    return true;

                case MotionEvent.ACTION_UP:

                    if (pressed) {

                        pressed = false;

                        animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .translationY(0)
                                .setDuration(190)
                                .setInterpolator(
                                        new DecelerateInterpolator()
                                )
                                .start();

                        performClick();
                    }

                    return true;

                case MotionEvent.ACTION_CANCEL:

                    pressed = false;

                    animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .translationY(0)
                            .setDuration(150)
                            .start();

                    return true;
            }

            return true;
        }

        @Override
        public boolean performClick() {

            super.performClick();

            return true;
        }

        private float dp(float value) {

            return value *
                    getResources()
                            .getDisplayMetrics()
                            .density;
        }
    }
}
