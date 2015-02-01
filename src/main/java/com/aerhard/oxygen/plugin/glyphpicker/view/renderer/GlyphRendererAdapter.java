/**
 * Copyright 2015 Alexander Erhard
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aerhard.oxygen.plugin.glyphpicker.view.renderer;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.table.TableCellRenderer;

import com.aerhard.oxygen.plugin.glyphpicker.model.DataSource;
import com.aerhard.oxygen.plugin.glyphpicker.model.GlyphDefinition;

import java.awt.Component;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

/**
 * A glyph rendering adapter class which selects the GlyphRenderer based on the
 * glyph definition's displayMode property. .
 */
public class GlyphRendererAdapter extends JLabel implements TableCellRenderer,
        ListCellRenderer<Object> {

    private static final long serialVersionUID = 1L;

    /** The renderers. */
    private final Map<String, GlyphRenderer> renderers = new HashMap<>();

    /**
     * Instantiates a new GlyphRendererAdapter.
     *
     * @param container
     *            the container
     */
    public GlyphRendererAdapter(JComponent container) {
        setText(null);
        renderers.put(DataSource.DISPLAY_MODE_VECTOR_FIT,
                new GlyphShapeRenderer(container));
        renderers.put(DataSource.DISPLAY_MODE_VECTOR_PROPORTIONAL,
                new GlyphTextRenderer(container));
        renderers.put(DataSource.DISPLAY_MODE_BITMAP, new GlyphBitmapRenderer(
                container));
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.JComponent#setPreferredSize(java.awt.Dimension)
     */
    @Override
    public void setPreferredSize(Dimension d) {
        super.setPreferredSize(d);
        for (GlyphRenderer renderer : renderers.values()) {
            renderer.setPreferredSize(d);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax
     * .swing.JTable, java.lang.Object, boolean, boolean, int, int)
     */
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        if (value != null) {
            GlyphDefinition glyphDefinition = ((GlyphDefinition) value);
            GlyphRenderer renderer = renderers.get(glyphDefinition
                    .getDataSource().getDisplayMode());

            if (renderer != null) {
                return renderer.getRendererComponent(glyphDefinition,
                        isSelected);
            }
        }
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing
     * .JList, java.lang.Object, int, boolean, boolean)
     */
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        if (value != null) {
            GlyphDefinition glyphDefinition = ((GlyphDefinition) value);
            GlyphRenderer renderer = renderers.get(glyphDefinition
                    .getDataSource().getDisplayMode());

            if (renderer != null) {
                return renderer.getRendererComponent(glyphDefinition,
                        isSelected);
            }
        }
        return this;
    }
}