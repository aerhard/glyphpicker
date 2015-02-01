package com.aerhard.oxygen.plugin.glyphpicker.controller.autocomplete;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.UniqueList;
import ca.odell.glazedlists.matchers.TextMatcherEditor;

import com.aerhard.oxygen.plugin.glyphpicker.model.GlyphDefinition;
import com.aerhard.oxygen.plugin.glyphpicker.model.trans.AllSelector;
import com.aerhard.oxygen.plugin.glyphpicker.model.trans.CharNameSelector;
import com.aerhard.oxygen.plugin.glyphpicker.model.trans.CodePointSelector;
import com.aerhard.oxygen.plugin.glyphpicker.model.trans.IdSelector;
import com.aerhard.oxygen.plugin.glyphpicker.model.trans.PropertySelector;
import com.aerhard.oxygen.plugin.glyphpicker.model.trans.RangeSelector;
import com.aerhard.oxygen.plugin.glyphpicker.model.trans.TransformedGlyphList;

public class AutoCompleteController {

    private static final Logger LOGGER = Logger
            .getLogger(AutoCompleteController.class.getName());

    private final Map<String, PropertySelector> autoCompleteScope = new LinkedHashMap<>();

    private CustomAutoCompleteSupport<String> autoCompleteSupport = null;

    private final JComboBox<String> autoCompleteCombo;

    private final GlyphSelect glyphSelect = new GlyphSelect();

    private final SortedList<GlyphDefinition> sortedList;

    public AutoCompleteController(int scopeIndex,
            JComboBox<String> autoCompleteCombo, JComboBox<String> scopeCombo,
            SortedList<GlyphDefinition> sortedList) {

        this.autoCompleteCombo = autoCompleteCombo;
        this.sortedList = sortedList;

        initAutoCompleteScope();

        PropertySelector initialPropertySelector = getPropertySelectorByIndex(scopeIndex);

        glyphSelect.setFilterator(new GlyphTextFilterator(
                initialPropertySelector));

        ((JTextField) autoCompleteCombo.getEditor().getEditorComponent())
                .getDocument().addDocumentListener(glyphSelect);

        setAutoCompleteSupport(initialPropertySelector);

        initAutoCompleteScopeCombo(scopeIndex, scopeCombo);
    }

    private void initAutoCompleteScope() {

        // TODO add entity field

        ResourceBundle i18n = ResourceBundle.getBundle("GlyphPicker");
        String className = this.getClass().getSimpleName();

        autoCompleteScope.put(i18n.getString(className + ".charName"),
                new CharNameSelector());
        autoCompleteScope.put(i18n.getString(className + ".xmlId"),
                new IdSelector());
        autoCompleteScope.put(i18n.getString(className + ".codepoint"),
                new CodePointSelector());
        autoCompleteScope.put(i18n.getString(className + ".range"),
                new RangeSelector());
        autoCompleteScope.put(i18n.getString(className + ".allFields"),
                new AllSelector());
    }

    private void initAutoCompleteScopeCombo(int scopeIndex,
            JComboBox<String> scopeCombo) {
        DefaultComboBoxModel<String> autoCompleteScopeModel = new DefaultComboBoxModel<>();

        for (String property : autoCompleteScope.keySet()) {
            autoCompleteScopeModel.addElement(property);
        }

        scopeCombo.setModel(autoCompleteScopeModel);

        scopeCombo.setSelectedIndex(scopeIndex);

        scopeCombo.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    String item = (String) e.getItem();
                    PropertySelector selector = autoCompleteScope.get(item);
                    if (selector != null) {
                        setAutoCompleteSupport(selector);
                        glyphSelect.setFilterator(new GlyphTextFilterator(
                                selector));
                    } else {
                        LOGGER.error("Item not found");
                    }
                }
            }
        });
    }

    public final PropertySelector getPropertySelectorByIndex(int index) {
        List<String> l = new ArrayList<>(autoCompleteScope.keySet());
        return autoCompleteScope.get(l.get(index));
    }

    private void setAutoCompleteSupport(final PropertySelector propertySelector) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TransformedGlyphList propertyList = new TransformedGlyphList(
                        sortedList, propertySelector);
                UniqueList<String> uniquePropertyList = new UniqueList<>(
                        propertyList);
                if (autoCompleteSupport != null) {
                    autoCompleteSupport.uninstall();
                }
                autoCompleteSupport = CustomAutoCompleteSupport.install(
                        autoCompleteCombo, uniquePropertyList);
                autoCompleteSupport.setFilterMode(TextMatcherEditor.CONTAINS);
            }
        });
    }

    public GlyphSelect getGlyphSelect() {
        return glyphSelect;
    }

}
