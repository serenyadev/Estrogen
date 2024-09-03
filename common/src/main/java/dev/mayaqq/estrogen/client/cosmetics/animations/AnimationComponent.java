package dev.mayaqq.estrogen.client.cosmetics.animations;

import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import org.joml.Vector3f;

import java.util.function.BiConsumer;

public class AnimationComponent {

    private final Target target;
    private final KeyFrame[] keyframes;
    private final LerpedFloat actor;
    private int nextFrame;

    public AnimationComponent(Target target, KeyFrame... keyframes) {
        this.target = target;
        this.keyframes = keyframes;
        this.actor = (target == Target.ROTATION) ? LerpedFloat.angular() : LerpedFloat.linear();
    }

    public void tick() {
        actor.tickChaser();
        if(actor.settled()) {
            KeyFrame next = keyframes[nextFrame];

        }
    }

    public enum Target {
        OFFSET(Animatable::updateOffset),
        ROTATION(Animatable::updateRotation),
        SCALE(Animatable::updateScale);

        private final BiConsumer<Animatable, Vector3f> onUpdate;

        Target(BiConsumer<Animatable, Vector3f> animation) {
            this.onUpdate = animation;
        }

        public void update(Animatable animatable, Vector3f data) {
            onUpdate.accept(animatable, data);
        }
    }

    public static class AnimatedVec {
        private float x;
        private float y;
        private float z;
        private float prevX;
        private float prevY;
        private float prevZ;
        private float currentTime;
    }
}
