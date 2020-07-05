// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.flexiblepathfinding.debug.ui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.terasology.engine.Time;
import org.terasology.entitySystem.Component;
import org.terasology.flexiblepathfinding.metrics.Histogram;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.Vector2i;
import org.terasology.registry.CoreRegistry;
import org.terasology.registry.In;
import org.terasology.rendering.assets.texture.TextureRegion;
import org.terasology.rendering.nui.*;
import org.terasology.rendering.nui.databinding.Binding;
import org.terasology.rendering.nui.databinding.DefaultBinding;
import org.terasology.rendering.nui.widgets.UIList;
import org.terasology.world.generation.Region;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class UIHistogram extends CoreWidget {
    public Color color = Color.BLUE;

    private Binding<Histogram> value = new DefaultBinding(new Histogram());

    @In
    private Time time;
    private int defaultHeight = 100;
    private int defaultWidth = 100;

    @Override
    public void onDraw(Canvas canvas) {
        Map<? extends Comparable, Integer> data = value.get().getBucketData();
        if(data == null || data.size() == 0) {
            return;
        }

        final int statsHeight = 50;

        int columnSize = canvas.size().x / data.size();
        int graphHeight = Math.min((int) (canvas.size().x / 1.618f), canvas.size().y - statsHeight);
        int minY = 0; // data.values().stream().min(Integer::compareTo).get();
        int maxY = Math.max(1, data.values().stream().max(Integer::compareTo).get());

        int offsetX = 0;
        int offsetY = canvas.size().y - statsHeight;
        List<Comparable> keys = Lists.newArrayList(data.keySet());
        Collections.sort(keys);
        for (Comparable key : keys) {
            Integer n = data.get(key);

            float t = (float) (n - minY) / (float) (maxY - minY);
            int barHeight = (int) (t * graphHeight);

            Rect2i rect = Rect2i.createFromMinAndMax(offsetX, offsetY - barHeight, offsetX + columnSize - 2, offsetY);
            canvas.drawFilledRectangle(rect, color);
            offsetX += columnSize;
        }
    }

    @Override
    public Vector2i getPreferredContentSize(Canvas canvas, Vector2i sizeHint) {
        return new Vector2i(defaultWidth, defaultHeight);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }

    public void bindValue( Binding<Histogram> binding) {
        value = binding;
    }
}
