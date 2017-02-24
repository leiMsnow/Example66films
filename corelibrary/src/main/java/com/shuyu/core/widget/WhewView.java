package com.shuyu.core.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class WhewView extends View {

    private Paint paint;
    private int maxWidth = 120;
    private int maxAlpha = 255 / 2;

    private List<Integer> alphaList = new ArrayList<>();
    private List<Integer> startWidthList = new ArrayList<>();
    private MyHandler myHandler;

    public WhewView(Context context) {
        this(context, null);
    }

    public WhewView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WhewView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        // 设置颜色
        paint.setColor(0x0000ce9b);
        alphaList.add(maxAlpha);// 圆心的不透明度
        startWidthList.add(0);
        myHandler = new MyHandler(this);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setBackgroundColor(Color.TRANSPARENT);
        // 依次绘制 同心圆
        for (int i = 0; i < alphaList.size(); i++) {
            int alpha = alphaList.get(i);
            // 圆半径
            int startWidth = startWidthList.get(i);
            paint.setAlpha(alpha);
            // 这个半径决定你想要多大的扩散面积
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, startWidth, paint);
            // 同心圆扩散
            if (alpha > 0 && startWidth < maxWidth) {
                alphaList.set(i, (alpha - 1));
                startWidthList.set(i, (startWidth + 1));
            }
        }
        if (startWidthList.get(startWidthList.size() - 1) == maxWidth / 5) {
            alphaList.add(maxAlpha);
            startWidthList.add(0);
        }
        // 同心圆数量达到，删除最外层圆
        if (startWidthList.size() == 10) {
            startWidthList.remove(0);
            alphaList.remove(0);
        }
    }

    private static class MyHandler extends Handler {

        private WeakReference<WhewView> weakReference;

        public MyHandler(WhewView weakObj) {
            weakReference = new WeakReference<>(weakObj);
        }

        @Override
        public void handleMessage(Message msg) {
            WhewView weakObj = weakReference.get();
            if (weakObj != null) {
                weakObj.invalidate();
                this.sendEmptyMessage(0);
            }
        }
    }

    // 执行动画
    public void start() {
        setVisibility(VISIBLE);
        myHandler.sendEmptyMessage(0);
    }

    // 停止动画
    public void stop() {
        setVisibility(GONE);
        myHandler.removeMessages(0);
    }
}