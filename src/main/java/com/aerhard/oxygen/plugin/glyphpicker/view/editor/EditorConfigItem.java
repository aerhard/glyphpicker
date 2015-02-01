package com.aerhard.oxygen.plugin.glyphpicker.view.editor;

import javax.swing.JComponent;

public class EditorConfigItem {
    private final JComponent component;
    private final String label;

    public EditorConfigItem(String label, JComponent component) {
        this.component = component;
        this.label = label;
    }

    public JComponent getComponent() {
        return component;
    }

    public String getLabel() {
        return label;
    }
}
