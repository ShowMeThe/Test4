package com.example.ken.test4;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;

/**
 * Created by ken on 2017/12/26.
 */

public class MyView extends View{
    private PointF mFixedCircle = new PointF(350f, 350f);
    private PointF mMoveCircle = new PointF(350f,350f);
    private PointF pointA,pointB,pointC,pointD,pointE;
    private Paint mPaint,paint;
    private Path path;
    private int mWidth,mHeight;
    private int fRadius = 20,mRadius = 20;
    private final float longDis = 150;
    private boolean isOutOfRang = false;
    private boolean isDraw = false;
    private float distance;
    public MyView(Context context) {
        this(context,null);
    }

    public MyView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

     private void init(){
         mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
         mPaint.setColor(Color.RED);
         mPaint.setStyle(Paint.Style.FILL);

         paint = new Paint(Paint.ANTI_ALIAS_FLAG);
         paint.setColor(Color.RED);
         paint.setStyle(Paint.Style.FILL);
         path = new Path();

         pointA = new PointF();
         pointB = new PointF();
         pointC = new PointF();
         pointD = new PointF();
         pointE = new PointF();
     }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        setPoint();
        drawCircle(canvas);
        if(isDraw){
            drawBezier(canvas);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

         PointF temp = new PointF();
         switch (event.getAction()){
             case MotionEvent.ACTION_MOVE:
                  float x = event.getX();
                  float y = event.getY();
                  temp.set(x,y);
                  mMoveCircle.set(temp);
                   distance = MathUtil.getPointDis(mFixedCircle,mMoveCircle);
                    if(distance> fRadius && distance > longDis){
                        isDraw  = true;
                        changeRadius(distance);
                        invalidate();
                    }else if(distance>fRadius && distance<longDis){
                        isDraw = true;
                        changeRadius(distance);
                        invalidate();

                    }else{
                        isDraw = false;
                    }
                   invalidate();
                 break;
             case MotionEvent.ACTION_UP:
                 if(distance> fRadius && distance > longDis){
                     anim();
                 }else if(distance>fRadius && distance<longDis){
                   anim2();
                 }else{
                     anim2();
                 }
                 break;
             case MotionEvent.ACTION_DOWN:
                 float x1 = event.getX();
                 float y1 = event.getY();
                 mMoveCircle.set(x1,y1);
                 mFixedCircle.set(x1,y1);
                 invalidate();
                 break;
         }
         return true;
    }

    private void setPoint(){

        float xOffset = mMoveCircle.x - mFixedCircle.x;
        float yOffset = mMoveCircle.y - mFixedCircle.y;

        pointE.set((mFixedCircle.x+ mMoveCircle.x)/2,(mFixedCircle.y + mMoveCircle.y)/2);

        double rate;
        rate = xOffset / yOffset;
        //角度  根据反正切函数算角度
        float angle = (float) Math.atan(rate);

        pointA.x = (float) (mFixedCircle.x + Math.cos(angle) * fRadius);
        pointA.y = (float) (mFixedCircle.y - Math.sin(angle) * fRadius);

        pointB.x = (float) (mMoveCircle.x + Math.cos(angle) * mRadius);
        pointB.y = (float) (mMoveCircle.y - Math.sin(angle) * mRadius);

        pointC.x = (float) (mMoveCircle.x - Math.cos(angle) * mRadius);
        pointC.y = (float) (mMoveCircle.y + Math.sin(angle) * mRadius);

        pointD.x = (float) (mFixedCircle.x - Math.cos(angle) * fRadius);
        pointD.y = (float) (mFixedCircle.y + Math.sin(angle) * fRadius);

    }

    private void drawBezier(Canvas canvas) {
        path.reset();
        path.moveTo(pointA.x, pointA.y);
        path.quadTo(pointE.x, pointE.y, pointB.x, pointB.y);
        path.lineTo(pointC.x, pointC.y);
        path.quadTo(pointE.x, pointE.y, pointD.x, pointD.y);
        path.lineTo(pointA.x, pointA.y);
        path.close();

        canvas.drawPath(path, paint);
    }
    //固定圆移动到拖拉圆
    private void anim(){
        final float ran = (mFixedCircle.y - mMoveCircle.y )/(mFixedCircle.x - mMoveCircle.x);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(mFixedCircle.x, mMoveCircle.x);
        valueAnimator.setDuration(300);
        valueAnimator.setInterpolator(new BounceInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float x = (float) valueAnimator.getAnimatedValue();
                float y =  mFixedCircle.y + ran*(x - mFixedCircle.x);
                mFixedCircle.set(x,y);
                setPoint();
                fRadius = 20;
                invalidate();
            }
        });
        valueAnimator.start();
    }
    //拖拉圆移动到固定圆
    private void anim2(){
        final float ran = (mFixedCircle.y - mMoveCircle.y )/(mFixedCircle.x - mMoveCircle.x);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat( mMoveCircle.x,mFixedCircle.x);
        valueAnimator.setDuration(300);
        valueAnimator.setInterpolator(new BounceInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float x = (float) valueAnimator.getAnimatedValue();
                float y =  mMoveCircle.y + ran*(x - mMoveCircle.x);
                mMoveCircle.set(x,y);
                setPoint();
                fRadius = 20;
                invalidate();
            }
        });
        valueAnimator.start();
    }

    private void changeRadius(float distance) {
        float d = Math.abs(distance/longDis);
        if(d >= 0.8){
            fRadius = fRadius<5?5:(int) ((1- d*0.02f)*fRadius);
        }else {
            fRadius = fRadius<10? 10:(int) ((1- d*0.02f)*fRadius);
        }
    }

    private void drawCircle(Canvas canvas){
        canvas.drawCircle(mFixedCircle.x, mFixedCircle.y, fRadius, mPaint);
        canvas.drawCircle(mMoveCircle.x,mMoveCircle.y,mRadius,mPaint);
    }
}
