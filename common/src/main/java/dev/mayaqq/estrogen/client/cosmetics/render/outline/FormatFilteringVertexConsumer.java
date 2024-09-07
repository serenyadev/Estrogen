package dev.mayaqq.estrogen.client.cosmetics.render.outline;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.DefaultedVertexConsumer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;

import java.util.List;

public class FormatFilteringVertexConsumer extends DefaultedVertexConsumer {

    private final VertexConsumer delegate;
    private final boolean position;
    private final boolean color;
    private final boolean texture;
    private final boolean overlay;
    private final boolean light;
    private final boolean normal;


    public FormatFilteringVertexConsumer(VertexConsumer delegate, VertexFormat format) {
        List<String> elements = format.getElementAttributeNames();

        position = elements.contains("Position");
        color = elements.contains("Color");
        texture = elements.contains("UV0") || elements.contains("UV");
        overlay = elements.contains("UV1");
        light = elements.contains("UV2");
        normal = elements.contains("Normal");
        this.delegate = delegate;
    }

    @Override
    public VertexConsumer vertex(double x, double y, double z) {
        if(position) delegate.vertex(x, y, z);
        return this;
    }

    @Override
    public VertexConsumer color(int red, int green, int blue, int alpha) {
        if(color) delegate.color(red, green, blue, alpha);
        return this;
    }

    @Override
    public VertexConsumer uv(float u, float v) {
        if(texture) delegate.uv(u, v);
        return this;
    }

    @Override
    public VertexConsumer overlayCoords(int u, int v) {
        if(overlay) delegate.overlayCoords(u, v);
        return this;
    }

    @Override
    public VertexConsumer uv2(int u, int v) {
        if(light) delegate.uv2(u, v);
        return this;
    }

    @Override
    public VertexConsumer normal(float x, float y, float z) {
        if(normal) delegate.normal(x, y, z);
        return this;
    }

    @Override
    public void endVertex() {
        delegate.endVertex();
    }
}
