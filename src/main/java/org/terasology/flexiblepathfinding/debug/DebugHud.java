package org.terasology.flexiblepathfinding.debug;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.registry.In;
import org.terasology.rendering.nui.Canvas;
import org.terasology.rendering.nui.UIWidget;
import org.terasology.rendering.nui.databinding.Binding;
import org.terasology.rendering.nui.databinding.DefaultBinding;
import org.terasology.rendering.nui.databinding.ReadOnlyBinding;
import org.terasology.rendering.nui.layers.hud.CoreHudWidget;
import org.terasology.rendering.nui.widgets.UILabel;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by kaen on 8/25/16.
 */
public class DebugHud extends CoreHudWidget {
    @In
    FlexiblePathfindingDebugClientSystem system;

    boolean bound = false;

    public void bind() {
        UIHistogram successTimes = find("successTimes", UIHistogram.class);
        UIHistogram failureTimes = find("failureTimes", UIHistogram.class);
        UIHistogram sizes = find("sizes", UIHistogram.class);
        UIHistogram costs = find("costs", UIHistogram.class);

        successTimes.bindValue(new DefaultBinding<Map<? extends Comparable, Integer>>() {
            @Override
            public Map<? extends Comparable, Integer> get() {
                return system.getLastResponse().successTimes;
            }
        });

        failureTimes.bindValue(new DefaultBinding<Map<? extends Comparable, Integer>>() {
            @Override
            public Map<? extends Comparable, Integer> get() {
                return system.getLastResponse().failureTimes;
            }
        });

        sizes.bindValue(new DefaultBinding<Map<? extends Comparable, Integer>>() {
            @Override
            public Map<? extends Comparable, Integer> get() {
                return system.getLastResponse().sizes;
            }
        });

        costs.bindValue(new DefaultBinding<Map<? extends Comparable, Integer>>() {
            @Override
            public Map<? extends Comparable, Integer> get() {
                return system.getLastResponse().costs;
            }
        });
    }

    @Override
    public void initialise() {
        bind();
    }
}
