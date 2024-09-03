package dev.mayaqq.estrogen.client.cosmetics.animations;

import org.joml.Vector3f;

public interface Animatable {
    void updateOffset(Vector3f vec);
    void updateRotation(Vector3f vec);
    void updateScale(Vector3f vec);
}
