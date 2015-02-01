package com.aerhard.oxygen.plugin.glyphpicker.controller.action;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;

import com.aerhard.oxygen.plugin.glyphpicker.model.GlyphDefinition;

public class AddAction extends AbstractPickerAction {
    private static final long serialVersionUID = 1L;
    private final DefaultEventSelectionModel<GlyphDefinition> selectionModel;

    private static final String className = AddAction.class.getSimpleName();

    public AddAction(PropertyChangeListener listener,
            DefaultEventSelectionModel<GlyphDefinition> selectionModel) {
        super(className, "/images/plus.png");

        addPropertyChangeListener(listener);
        this.selectionModel = selectionModel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (!selectionModel.isSelectionEmpty()) {
            GlyphDefinition d = selectionModel.getSelected().get(0);
            if (d != null) {
                firePropertyChange("copyToUserCollection", null, d);
            }
        }
    }
}