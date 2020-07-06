// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.flexiblepathfinding.debug.ui;

import org.terasology.engine.Time;
import org.terasology.flexiblepathfinding.metrics.TimeSeries;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2i;
import org.terasology.registry.In;
import org.terasology.rendering.nui.Canvas;
import org.terasology.rendering.nui.Color;
import org.terasology.rendering.nui.CoreWidget;
import org.terasology.rendering.nui.databinding.Binding;
import org.terasology.rendering.nui.databinding.DefaultBinding;

import java.util.Queue;

public class UITimeSeries extends CoreWidget {
    public Color color = Color.WHITE;
    private final double padding = 2.0;

    private Binding<TimeSeries> binding = new DefaultBinding(new TimeSeries());

    @In
    private Time time;
    private int defaultHeight = 100;
    private int defaultWidth = 100;

    @Override
    public void onDraw(Canvas canvas) {
        TimeSeries timeSeries = binding.get();
        if (timeSeries == null) {
            return;
        }

        Queue<Double> data = timeSeries.getValues();
        if (data == null || data.size() == 0) {
            return;
        }

        final int statsHeight = 50;

        double columnSize = (canvas.size().x - padding * 2) / data.size();
        int graphHeight = Math.min((int) (canvas.size().x / 1.618f), canvas.size().y - statsHeight);
        double minY = 0; // data.values().stream().min(Integer::compareTo).get();
        double maxY = Math.max(1, data.stream().max(Double::compareTo).get());

        double offsetX = padding;
        double offsetY = canvas.size().y - statsHeight;
        double lastX = offsetX;
        double lastY = offsetY;
        double lastN = 0;
        for (double n : data) {
            lastN = n;
            float t = (float) (n - minY) / (float) (maxY - minY);
            int barHeight = (int) (t * graphHeight);

            double y = offsetY - barHeight;
            canvas.drawLine((int) lastX, (int) lastY, (int) offsetX, (int) y, color);
            lastX = offsetX;
            lastY = y;

            offsetX += columnSize;
        }

        Rect2i statsRegion = Rect2i.createFromMinAndMax(0, (int) offsetY, canvas.size().x, canvas.size().y);
        double min = data.stream().min(Double::compareTo).get();
        double max = data.stream().max(Double::compareTo).get();
        String stats = String.format("min: %.6g max: %.6g last: %.6g", min, max, lastN);
        canvas.drawText(stats, statsRegion);
    }

    @Override
    public Vector2i getPreferredContentSize(Canvas canvas, Vector2i sizeHint) {
        return new Vector2i(defaultWidth, defaultHeight);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }

    public void bindValue( Binding<TimeSeries> timeSeriesBinding) {
        binding = timeSeriesBinding;
    }
}
