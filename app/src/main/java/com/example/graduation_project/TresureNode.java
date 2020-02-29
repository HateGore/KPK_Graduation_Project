package com.example.graduation_project;

import android.animation.ObjectAnimator;
import android.graphics.Region;
import android.view.animation.LinearInterpolator;

import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.QuaternionEvaluator;
import com.google.ar.sceneform.math.Vector3;

import javax.annotation.Nullable;

public class TresureNode extends Node {
    @Nullable private ObjectAnimator animator = null;
    private float rotationSpeed = 5.0f;
    private final TresureSetting tresureSettings;

    public TresureNode(TresureSetting tresureSetting){
        this.tresureSettings = tresureSetting;
    }

    @Override
    public void onUpdate(FrameTime frameTime) {
        super.onUpdate(frameTime);

        if (animator == null) { return; }

        if (tresureSettings.getTurn_able() == false)
        {
            System.out.println("onUpdate stop");
            animator.pause();
        }
        else
        {
            System.out.println("onUpdate start");
            animator.resume();
        }
        float animatedFraction = animator.getAnimatedFraction();
        animator.setDuration((long)2000.0);
        animator.setCurrentFraction(animatedFraction);
    }




    @Override
    public void onActivate() {
        startAnimation();
    }

    @Override
    public void onDeactivate() {
        stopAnimation();
    }

    private void startAnimation() {
        if (animator != null) {
            return;
        }

        animator = createAnimator();
        animator.setTarget(this);
        animator.setDuration((long)2000.0);
        animator.start();
    }

    private void stopAnimation() {
        if (animator == null) {
            return;
        }
        animator.cancel();
        animator = null;
    }

    private ObjectAnimator createAnimator() {
        Quaternion[] orientations = new Quaternion[4];
        Quaternion baseOrientation = Quaternion.axisAngle(new Vector3(1.0f, 0.0f, 0.0f), 0.0f);
        for (int i = 0; i < orientations.length; i++) {
            float angle = i * 360 / (orientations.length - 1);
            boolean clockwise = true;
            if (clockwise == true) {
                angle = 360 - angle;
            }
            Quaternion orientation = Quaternion.axisAngle(new Vector3(0.0f,1.0f,0.0f), angle);
            orientations[i] = Quaternion.multiply(baseOrientation, orientation);

        }
        ObjectAnimator animation = new ObjectAnimator();
        animation.setObjectValues((Object[]) orientations);
        animation.setPropertyName("localRotation");
        animation.setEvaluator(new QuaternionEvaluator());
        animation.setRepeatCount(ObjectAnimator.INFINITE);
        animation.setRepeatMode(ObjectAnimator.RESTART);
        animation.setInterpolator(new LinearInterpolator());
        animation.setAutoCancel(true);
        System.out.println("system start");
        return animation;
    }
}
