package com.quansoon.facecamera.utils;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.animation.DecelerateInterpolator;

import com.quansoon.facecamera.widget.CustomViewEmployeeDetails;

public class AnimationUtil {

    public static final Long ANIMA_TIME = 300L;

    /**
     * 缩放动画 与 平移动画
     *
     * @param view
     * @param fromScale
     * @param toScale
     * @param fromX
     * @param toX
     * @param fromY
     * @param toY
     * @param listener
     */
    public static void startZoomAnim(View view,
                                     // 缩放
                                     float fromScale, float toScale,
                                     // 平移
                                     float fromX, float toX, float fromY, float toY,
                                     // 动画
                                     Animator.AnimatorListener listener
    ) {

        if (view == null) {
            return;
        }
        if (view.getAnimation() != null) {
            view.getAnimation().cancel();
            view.clearAnimation();
            return;
        }

        AnimatorSet localAnimatorSet = new AnimatorSet();
        // ############## scaleX ###############
        float[] scaleX = new float[2];
        scaleX[0] = fromScale;
        scaleX[1] = toScale;
        ObjectAnimator scaleXAnima = ObjectAnimator.ofFloat(view,
                "scaleX", scaleX);
        scaleXAnima.setDuration(ANIMA_TIME);
        scaleXAnima.setInterpolator(new DecelerateInterpolator());
        // ############## scaleY ###############
        float[] scaleY = new float[2];
        scaleY[0] = fromScale;
        scaleY[1] = toScale;
        ObjectAnimator scaleYAnima = ObjectAnimator.ofFloat(view,
                "scaleY", scaleY);
        scaleYAnima.setDuration(ANIMA_TIME);
        scaleYAnima.setInterpolator(new DecelerateInterpolator());
        // #############translationX ##########
        float[] translationX = new float[2];
        translationX[0] = fromX;
        translationX[1] = toX;
        ObjectAnimator translationXAnima = ObjectAnimator.ofFloat(view,
                "translationX", translationX);
        translationXAnima.setDuration(ANIMA_TIME);
        translationXAnima.setInterpolator(new DecelerateInterpolator());
        // ###############translationY##################
        float[] translationY = new float[2];
        translationY[0] = fromY;
        translationY[1] = toY;
        ObjectAnimator translationYAnima = ObjectAnimator.ofFloat(view,
                "translationY", translationY);
        translationYAnima.setDuration(ANIMA_TIME);
        translationYAnima.setInterpolator(new DecelerateInterpolator());

        //
        Animator[] arrayOfAnimator = new Animator[4];
        arrayOfAnimator[0] = scaleXAnima;
        arrayOfAnimator[1] = scaleYAnima;
        arrayOfAnimator[2] = translationXAnima;
        arrayOfAnimator[3] = translationYAnima;
        localAnimatorSet.playTogether(arrayOfAnimator);
        if (listener != null) {
            localAnimatorSet.addListener(listener);
        }
        //
        localAnimatorSet.start();
    }

    /**
     * 开启Alpha 动画
     *
     * @param view
     * @param fromAlpha
     * @param toAlpha
     */
    public static void startAlphaAnima(View view,
                                       float fromAlpha, float toAlpha) {
        if (view == null) {
            return;
        }
        if (view.getAnimation() != null) {
            view.getAnimation().cancel();
            view.clearAnimation();
            return;
        }
        //-------Alpaha--------
        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator.ofFloat(view, "alpha", fromAlpha, toAlpha));
        set.setDuration(ANIMA_TIME);
        set.setInterpolator(new DecelerateInterpolator());
        set.start();

    }

    /**
     * 清理目标View的动画
     *
     * @param paramView
     */
    public static void clearAnimation(View paramView) {
        if (paramView == null)
            return;

        if (paramView.getAnimation() == null) {
            return;
        }

        paramView.getAnimation().cancel();
        paramView.clearAnimation();
    }

    /**
     * 旋转动画
     *
     * @param paramView
     * @param paramInt  时间
     */
    public static void startRotateAnimation(View paramView, int paramInt) {
        float[] arrayOfFloat = new float[2];
        arrayOfFloat[0] = 0.0F;
        arrayOfFloat[1] = 360.0F;
        ObjectAnimator localObjectAnimator = ObjectAnimator.ofFloat(paramView,
                "rotation", arrayOfFloat);
        localObjectAnimator.setDuration(paramInt);
        localObjectAnimator.setInterpolator(null);
        localObjectAnimator.setRepeatCount(-1);
        localObjectAnimator.start();
    }

    /**
     * 放大动画
     *
     * @param paramView             目标View
     * @param paramFloat            放大的比例
     * @param paramAnimatorListener
     * @return
     */
    public static AnimatorSet startScaleToBigAnimation(View paramView,
                                                       float paramFloat, Animator.AnimatorListener paramAnimatorListener) {
        if (paramView.getAnimation() != null)
            paramView.getAnimation().cancel();
        paramView.clearAnimation();
        AnimatorSet localAnimatorSet = new AnimatorSet();
        float[] arrayOfFloat1 = new float[2];
        arrayOfFloat1[0] = 0.0F;
        arrayOfFloat1[1] = paramFloat;
        ObjectAnimator localObjectAnimator1 = ObjectAnimator.ofFloat(paramView,
                "scaleY", arrayOfFloat1);
        localObjectAnimator1.setDuration(240L);
        localObjectAnimator1.setInterpolator(new DecelerateInterpolator());
        float[] arrayOfFloat2 = new float[2];
        arrayOfFloat2[0] = 0.0F;
        arrayOfFloat2[1] = paramFloat;
        ObjectAnimator localObjectAnimator2 = ObjectAnimator.ofFloat(paramView,
                "scaleX", arrayOfFloat2);
        localObjectAnimator2.setDuration(240L);
        localObjectAnimator2.setInterpolator(new DecelerateInterpolator());
        Animator[] arrayOfAnimator = new Animator[2];
        arrayOfAnimator[0] = localObjectAnimator1;
        arrayOfAnimator[1] = localObjectAnimator2;
        localAnimatorSet.playTogether(arrayOfAnimator);
        if (paramAnimatorListener != null)
            localAnimatorSet.addListener(paramAnimatorListener);
        localAnimatorSet.start();
        return localAnimatorSet;
    }

    /**
     * 缩小动画
     *
     * @param paramView
     * @param paramFloat            缩小的比例
     * @param paramAnimatorListener
     * @return
     */
    public static AnimatorSet startScaleToSmallAnimation(View paramView,
                                                         float paramFloat, Animator.AnimatorListener paramAnimatorListener) {
        if (paramView.getAnimation() != null)
            paramView.getAnimation().cancel();
        paramView.clearAnimation();
        AnimatorSet localAnimatorSet = new AnimatorSet();
        float[] arrayOfFloat1 = new float[2];
        arrayOfFloat1[0] = paramFloat;
        arrayOfFloat1[1] = 1.0F;
        ObjectAnimator localObjectAnimator1 = ObjectAnimator.ofFloat(paramView,
                "scaleY", arrayOfFloat1);
        localObjectAnimator1.setDuration(140L);
        localObjectAnimator1.setInterpolator(new DecelerateInterpolator());
        float[] arrayOfFloat2 = new float[2];
        arrayOfFloat2[0] = paramFloat;
        arrayOfFloat2[1] = 1.0F;
        ObjectAnimator localObjectAnimator2 = ObjectAnimator.ofFloat(paramView,
                "scaleX", arrayOfFloat2);
        localObjectAnimator2.setDuration(140L);
        localObjectAnimator2.setInterpolator(new DecelerateInterpolator());
        Animator[] arrayOfAnimator = new Animator[2];
        arrayOfAnimator[0] = localObjectAnimator1;
        arrayOfAnimator[1] = localObjectAnimator2;
        localAnimatorSet.playTogether(arrayOfAnimator);
        localAnimatorSet.start();
        return localAnimatorSet;
    }

    /**
     * 从上往下移动的位移动画
     *
     * @param paramView
     * @return
     */
    public static AnimatorSet startTranlateDownAnimation(View paramView) {
        if (paramView.getAnimation() != null)
            paramView.getAnimation().cancel();
        paramView.clearAnimation();
        AnimatorSet localAnimatorSet = new AnimatorSet();
        float[] arrayOfFloat = new float[2];
        arrayOfFloat[0] = -80.0F;
        arrayOfFloat[1] = 1.0F;
        ObjectAnimator localObjectAnimator = ObjectAnimator.ofFloat(paramView,
                "translationY", arrayOfFloat);
        localObjectAnimator.setDuration(240L);
        localObjectAnimator.setInterpolator(new DecelerateInterpolator());
        localAnimatorSet.play(localObjectAnimator);
        localAnimatorSet.start();
        return localAnimatorSet;
    }

    /**
     * 从下网上移动的位移动画
     *
     * @param paramView
     * @return
     */
    public static AnimatorSet startTranlateUpAnimation(View paramView) {
        if (paramView.getAnimation() != null)
            paramView.getAnimation().cancel();
        paramView.clearAnimation();
        AnimatorSet localAnimatorSet = new AnimatorSet();
        float[] arrayOfFloat = new float[2];
        arrayOfFloat[0] = 1.0F;
        arrayOfFloat[1] = -80.0F;
        ObjectAnimator localObjectAnimator = ObjectAnimator.ofFloat(paramView,
                "translationY", arrayOfFloat);
        localObjectAnimator.setDuration(240L);
        localObjectAnimator.setInterpolator(new DecelerateInterpolator());
        localAnimatorSet.play(localObjectAnimator);
        localAnimatorSet.start();
        return localAnimatorSet;
    }

    public static void startScaleToBigAnimationEdit(
            View view,
            float fromScale,
            float toScale,
            Animator.AnimatorListener listener) {
        if (view == null) {
            return;
        }
        if (view.getAnimation() != null) {
            view.getAnimation().cancel();
            view.clearAnimation();
            return;
        }
        view.clearAnimation();
        AnimatorSet localAnimatorSet = new AnimatorSet();
        // ############## scaleX ###############
        float[] scaleX = new float[2];
        scaleX[0] = fromScale;
        scaleX[1] = toScale;
        ObjectAnimator scaleXAnima = ObjectAnimator.ofFloat(view,
                "scaleX", scaleX);
        scaleXAnima.setDuration(ANIMA_TIME);
        scaleXAnima.setInterpolator(new DecelerateInterpolator());
        // ############## scaleY ###############
        float[] scaleY = new float[2];
        scaleY[0] = fromScale;
        scaleY[1] = toScale;
        ObjectAnimator scaleYAnima = ObjectAnimator.ofFloat(view,
                "scaleY", scaleY);
        scaleYAnima.setDuration(ANIMA_TIME);
        scaleYAnima.setInterpolator(new DecelerateInterpolator());

        Animator[] arrayOfAnimator = new Animator[4];
        arrayOfAnimator[0] = scaleXAnima;
        arrayOfAnimator[1] = scaleYAnima;

        localAnimatorSet.playTogether(arrayOfAnimator);
        if (listener != null) {
            localAnimatorSet.addListener(listener);
        }

        localAnimatorSet.start();
    }


    /**
     * 缩放动画 与 平移动画
     *
     * @param view
     * @param fromScale
     * @param toScale
     * @param fromX
     * @param toX
     * @param fromY
     * @param toY
     * @param listener
     */
    public static void starTranslationAndRotateAnim(final View view,
                                                    // 缩放
                                                    float fromScale, float toScale,
                                                    // 平移
                                                    float fromX, float toX, float fromY, float toY,
                                                    // 旋转
                                                    float fromRotationY, float toRotationY,
                                                    // 透明
                                                    float fromAlpha, float toAlpha,
                                                    // 动画
                                                    Animator.AnimatorListener listener
    ) {

        if (view == null) {
            return;
        }

        if (view.getAnimation() != null) {
            view.getAnimation().cancel();
            view.clearAnimation();
            return;
        }

        AnimatorSet localAnimatorSet = new AnimatorSet();
        // ############## scaleX ###############
        float[] scaleX = new float[2];
        scaleX[0] = fromScale;
        scaleX[1] = toScale;
        ObjectAnimator scaleXAnima = ObjectAnimator.ofFloat(view,
                "scaleX", scaleX);
        scaleXAnima.setDuration(ANIMA_TIME);
        scaleXAnima.setInterpolator(new DecelerateInterpolator());
        // ############## scaleY ###############
        float[] scaleY = new float[2];
        scaleY[0] = fromScale;
        scaleY[1] = toScale;
        ObjectAnimator scaleYAnima = ObjectAnimator.ofFloat(view,
                "scaleY", scaleY);
        scaleYAnima.setDuration(ANIMA_TIME);
        scaleYAnima.setInterpolator(new DecelerateInterpolator());

        // #############translationX ##########
        float[] translationX = new float[2];
        translationX[0] = fromX;
        translationX[1] = toX;
        ObjectAnimator translationXAnima = ObjectAnimator.ofFloat(view,
                "translationX", translationX);
        translationXAnima.setDuration(ANIMA_TIME);
        translationXAnima.setInterpolator(new DecelerateInterpolator());
        // ###############translationY##################
        float[] translationY = new float[2];
        translationY[0] = fromY;
        translationY[1] = toY;
        ObjectAnimator translationYAnima = ObjectAnimator.ofFloat(view,
                "translationY", translationY);
        translationYAnima.setDuration(ANIMA_TIME);
        translationYAnima.setInterpolator(new DecelerateInterpolator());

        // #############RotationY ##########
        float[] rotationY = new float[2];
        rotationY[0] = fromRotationY;
        rotationY[1] = toRotationY;
        ObjectAnimator rotationYAnima = ObjectAnimator.ofFloat(view,
                "RotationY", rotationY);
        rotationYAnima.setDuration(ANIMA_TIME);
        rotationYAnima.setInterpolator(new DecelerateInterpolator());

        // #############RotationY ##########
        float[] alpha = new float[2];
        alpha[0] = fromAlpha;
        alpha[1] = toAlpha;
        ObjectAnimator alphaAnima = ObjectAnimator.ofFloat(view,
                "alpha", alpha);
        alphaAnima.setDuration(ANIMA_TIME);
        alphaAnima.setInterpolator(new DecelerateInterpolator());

        //
        Animator[] arrayOfAnimator = new Animator[6];
        arrayOfAnimator[0] = scaleXAnima;
        arrayOfAnimator[1] = scaleYAnima;
        arrayOfAnimator[2] = translationXAnima;
        arrayOfAnimator[3] = translationYAnima;
        arrayOfAnimator[4] = rotationYAnima;
        arrayOfAnimator[5] = alphaAnima;
        localAnimatorSet.playTogether(arrayOfAnimator);
        if (listener != null) {
            localAnimatorSet.addListener(listener);
        }
        //
//        translationXAnima.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                if (view instanceof CustomViewEmployeeDetails) {
////                    CustomViewEmployeeDetails view1 = (CustomViewEmployeeDetails) view;
////                    view1.setBgTranAnimation((float) animation.getAnimatedValue());
////                   ((CustomViewEmployeeDetails)view).setBgTranAnimation((float) animation.getAnimatedValue());
//
//                }
//            }
//        });

        localAnimatorSet.start();
    }


}