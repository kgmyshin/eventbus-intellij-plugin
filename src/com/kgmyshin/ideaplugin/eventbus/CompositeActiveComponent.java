package com.kgmyshin.ideaplugin.eventbus;

import com.intellij.ui.ActiveComponent;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * Created by kgmyshin on 2015/06/07.
 */
class CompositeActiveComponent implements ActiveComponent {
    private final ActiveComponent[] myComponents;
    private final JPanel myComponent;

    public CompositeActiveComponent(@NotNull ActiveComponent... components) {
        myComponents = components;

        myComponent = new JPanel(new FlowLayout());
        myComponent.setOpaque(false);
        for (ActiveComponent component : components) {
            myComponent.add(component.getComponent());
        }
    }

    @Override
    public void setActive(boolean active) {
        for (ActiveComponent component : myComponents) {
            component.setActive(active);
        }
    }

    @Override
    public JComponent getComponent() {
        return myComponent;
    }
}