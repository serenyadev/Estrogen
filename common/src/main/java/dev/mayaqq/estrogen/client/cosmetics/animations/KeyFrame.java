package dev.mayaqq.estrogen.client.cosmetics.animations;

import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.data.models.ModelProvider;
import org.joml.Vector3f;

public record KeyFrame(float timeStamp, Vector3f data) {
}
