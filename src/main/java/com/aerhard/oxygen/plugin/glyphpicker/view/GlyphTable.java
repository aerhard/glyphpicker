package com.aerhard.oxygen.plugin.glyphpicker.view;

import java.awt.AWTKeyStroke;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import com.aerhard.oxygen.plugin.glyphpicker.view.renderer.DescriptionRenderer;

public class GlyphTable extends JTable {

    private static final int COL_1_PREFERRED_WIDTH = 70;
    private static final int COL_1_MIN_WIDTH = 30;
    private static final int COL_2_PREFERRED_WIDTH = 600;
    private static final int COL_2_MIN_WIDTH = 10;

    private static final long serialVersionUID = 1L;

    private TableCellRenderer tableIconRenderer;
    private final TableCellRenderer tableDescriptionRenderer;

    public GlyphTable(TableModel tableModel) {
        tableDescriptionRenderer = new DescriptionRenderer();
        getTableHeader().setReorderingAllowed(false);
        setShowVerticalLines(false);
        setIntercellSpacing(new Dimension(0, 1));

        setFillsViewportHeight(true);

        setModel(tableModel);

        setDefaultFocusTraversal();

        getColumnModel().getColumn(0).setPreferredWidth(COL_1_PREFERRED_WIDTH);
        getColumnModel().getColumn(0).setMinWidth(COL_1_MIN_WIDTH);
        getColumnModel().getColumn(1).setPreferredWidth(COL_2_PREFERRED_WIDTH);
        getColumnModel().getColumn(1).setMinWidth(COL_2_MIN_WIDTH);

    }

    private void setDefaultFocusTraversal() {
        Set<AWTKeyStroke> forward = new HashSet<>(
                getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        forward.add(KeyStroke.getKeyStroke("TAB"));
        setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
                forward);
        Set<AWTKeyStroke> backward = new HashSet<>(
                getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
        backward.add(KeyStroke.getKeyStroke("shift TAB"));
        setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
                backward);
    }

    public int getTopVisibleRow() {
        JViewport viewport = (JViewport) getParent();
        Point pt = viewport.getViewPosition();
        return rowAtPoint(pt);
    }

    public void setTopVisibleRow(int row) {
        Rectangle cellBounds = getCellRect(row, 0, true);
        int h = getVisibleRect().height;
        Rectangle targetViewRect = new Rectangle(cellBounds.x - h
                + cellBounds.height, cellBounds.y, cellBounds.width, h);
        scrollRectToVisible(targetViewRect);
    }

    public void setTableIconRenderer(TableCellRenderer renderer) {
        tableIconRenderer = renderer;
    }

    public TableCellRenderer getCellRenderer(int row, int column) {
        if (column == 0) {
            return tableIconRenderer;
        }
        if (column == 1) {
            return tableDescriptionRenderer;
        }
        return super.getCellRenderer(row, column);
    }

}
