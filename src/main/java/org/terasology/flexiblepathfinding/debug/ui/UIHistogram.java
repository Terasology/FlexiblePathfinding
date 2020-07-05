// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.flexiblepathfinding.debug.ui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.terasology.engine.Time;
import org.terasology.entitySystem.Component;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.Vector2i;
import org.terasology.registry.CoreRegistry;
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
    private Binding<Map<? extends Comparable, Integer>> value = new DefaultBinding<Map<? extends Comparable, Integer>>(Maps.newHashMap());
    private Time time = CoreRegistry.get(Time.class);
    private int defaultHeight = 100;
    private int defaultWidth = 100;

    @Override
    public void onDraw(Canvas canvas) {
        Map<? extends Comparable, Integer> data = value.get();
        if(data == null || data.size() == 0) {
            return;
        }

        int rowSize = canvas.size().y / data.size();
        int width = (int) (canvas.size().x / 1.618f);
        int minY = 0; // data.values().stream().min(Integer::compareTo).get();
        int maxY = data.values().stream().max(Integer::compareTo).get();

        if(maxY - minY == 0) {
            maxY = minY + 1;
        }

        int offsetX = canvas.size().x - width;
        int offsetY = 0;

        List<Comparable> keys = Lists.newArrayList(data.keySet());
        Collections.sort(keys);
        for(Comparable key : keys) {
            Integer value = data.get(key);
            float t = (float) (value - minY) / (float) (maxY - minY);
            int barWidth = (int) (t * width);
            Rect2i rect = Rect2i.createFromMinAndMax(offsetX, offsetY, offsetX + barWidth, offsetY + rowSize - 2);
            Color color = Color.WHITE;
            canvas.drawFilledRectangle(rect, color);

            Rect2i textRect = Rect2i.createFromMinAndMax(0, offsetY, offsetX, offsetY + rowSize - 2);
            canvas.drawText(key.toString().substring(0,Math.min(key.toString().length() - 1, 4)), textRect);
            offsetY += rowSize;
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

    public void bindValue( Binding<Map<? extends Comparable, Integer>> binding) {
        value = binding;
    }
}
