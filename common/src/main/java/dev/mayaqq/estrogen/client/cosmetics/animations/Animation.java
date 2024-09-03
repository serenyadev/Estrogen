package dev.mayaqq.estrogen.client.cosmetics.animations;

import java.util.Map;

public record Animation(float length, boolean looping, Map<String, AnimationComponent> components) {
}
