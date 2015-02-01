package com.aerhard.oxygen.plugin.glyphpicker.view.renderer;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

import java.awt.Component;
import java.util.ResourceBundle;

import com.aerhard.oxygen.plugin.glyphpicker.model.GlyphDefinition;

public class DescriptionRenderer extends JLabel implements TableCellRenderer {

    private static final long serialVersionUID = 1L;

    public static final int BORDER_WIDTH = 4;

    private static ResourceBundle i18n = ResourceBundle
            .getBundle("GlyphPicker");
    private static String className = DescriptionRenderer.class.getSimpleName();

    private static final String CODEPOINT_LABEL = i18n.getString(className
            + ".codepoint");
    private static final String RANGE_LABEL = i18n.getString(className
            + ".range");
    private static final String XML_ID_LABEL = i18n.getString(className
            + ".xmlId");

    public DescriptionRenderer() {
        setBorder(new EmptyBorder(BORDER_WIDTH, BORDER_WIDTH, BORDER_WIDTH,
                BORDER_WIDTH));
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        if (value == null) {
            setText("");
        } else {
            GlyphDefinition d = (GlyphDefinition) value;
            setText(getHTML(d));
        }

        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        } else {
            setBackground(table.getBackground());
            setForeground(table.getForeground());
        }

        setOpaque(true);
        return this;
    }

    public static String getHTML(GlyphDefinition d) {
        StringBuilder sb = new StringBuilder();

        sb.append("<html><p>");

        if (d.getCharName() != null) {
            sb.append("<nobr><b>");
            sb.append(d.getCharName());
            sb.append("</b></nobr><br>");
        }

        if (d.getCodePoint() != null) {
            sb.append("<nobr>" + CODEPOINT_LABEL + ": ");
            sb.append(d.getCodePointString());
            sb.append("</nobr><br>");
        }

        if (d.getRange() != null) {
            sb.append("<nobr>" + RANGE_LABEL + ": ");
            sb.append(d.getRange());
            sb.append("</nobr><br>");
        }

        if (d.getId() != null) {
            sb.append("<nobr>" + XML_ID_LABEL + ": <em>");
            sb.append(d.getId());
            sb.append("</em></nobr><br>");
        }

        sb.append("</p></html>");

        return sb.toString();

    }

}