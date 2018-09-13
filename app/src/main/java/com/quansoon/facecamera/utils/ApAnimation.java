//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.quansoon.facecamera.utils;

import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ApAnimation extends Animation {
    private final float mV;
    private final float mW;
    private final float ma;
    private final float mb;
    private final float mc;
    private final boolean mX;
    private Camera mY;
    private Canvas canvas;

    public ApAnimation(float fromDegrees, float toDegrees, float centerX, float centerY, float depthZ, boolean reverse) {
        this.mV = fromDegrees;
        this.mW = toDegrees;
        this.ma = centerX;
        this.mb = centerY;
        this.mc = depthZ;
        this.mX = reverse;
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        this.mY = new Camera();
        canvas = new Canvas();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        float fromDegrees = this.mV;
        float degrees = fromDegrees + (this.mW - fromDegrees) * interpolatedTime;
        float centerX = this.ma;
        float centerY = this.mb;
        Camera camera = this.mY;
        Matrix matrix = t.getMatrix();
        camera.save();
        if(this.mX) {
            camera.translate(0.0F, 0.0F, this.mc * interpolatedTime);
        } else {
            camera.translate(0.0F, 0.0F, this.mc * (1.0F - interpolatedTime));
        }

        camera.rotateY(degrees);
        camera.getMatrix(matrix);
        camera.restore();
        matrix.preTranslate(-centerX, -centerY);
        matrix.postTranslate(centerX, centerY);


//       使用OpenGl实现，在片元着色器中针对不同的s(纹理坐标)值，设置不同的alpha值。
//       利用Canvas绘制
//        canvas.drawBitmap(mBitmap, 0, 0, new Paint());
        Paint p = new Paint();
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
//        canvas.saveLayer(new RectF(0, 0, mBitmap.getWidth(), mBitmap.getHeight()), p);
//        canvas.drawPaint(getAlphaPaint(mBitmap.getWidth(), mBitmap.getHeight()));
        canvas.restore();

//        getAlphaPaint可以是透明度从左到右逐渐变化的paint,类似以下代码。
    }

    private Paint getAlphaPaint(int imageW, int imageH,float mProgress) {
        Paint paint = new Paint();
        if (mProgress > 0f && mProgress < 0.2f) {
            paint.setShader(new LinearGradient(0, imageH / 2, imageW * mProgress, imageH / 2, Color.TRANSPARENT, Color.BLACK, Shader.TileMode.CLAMP));
        } else {
            paint.setShader(new LinearGradient(0, imageH / 2, imageW / 5, imageH / 2, Color.TRANSPARENT, Color.BLACK, Shader.TileMode.CLAMP));
        }
        return paint;
    }
}
