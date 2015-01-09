package com.aerhard.oxygen.plugin.glyphpicker.controller;

import ca.odell.glazedlists.gui.TableFormat;

import com.aerhard.oxygen.plugin.glyphpicker.model.GlyphDefinition;

public class GlyphTableFormat implements TableFormat<GlyphDefinition> {
    
    public int getColumnCount() {
        return 2;
    }

    public String getColumnName(int column) {
        if (column == 0) {
            return "Glyph";
        }
        else if (column == 1) {
            return "Description";
        }

        throw new IllegalStateException();
    }

    public Object getColumnValue(GlyphDefinition baseObject, int column) {
        
        if (column == 0 || column == 1) {
            return baseObject;
        }
        
        throw new IllegalStateException();
    }
}
