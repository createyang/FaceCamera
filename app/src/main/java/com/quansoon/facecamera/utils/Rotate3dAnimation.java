//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.quansoon.facecamera.utils;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class Rotate3dAnimation extends Animation {
    private final float mV;
    private final float mW;
    private final float ma;
    private final float mb;
    private final float mc;
    private final boolean mX;
    private Camera mY;

    public Rotate3dAnimation(float fromDegrees, float toDegrees, float centerX, float centerY, float depthZ, boolean reverse) {
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
    }
}
