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

import com.aerhard.oxygen.plugin.glyphpicker.model.selector.*;
import org.apache.log4j.Logger;

import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.UniqueList;
import ca.odell.glazedlists.matchers.TextMatcherEditor;

import com.aerhard.oxygen.plugin.glyphpicker.model.GlyphDefinition;
import com.aerhard.oxygen.plugin.glyphpicker.model.GlyphPropertyEventList;

/**
 * The auto complete controller.
 */
public class AutoCompleteController {

    /** The logger. */
    private static final Logger LOGGER = Logger
            .getLogger(AutoCompleteController.class.getName());

    /** Maps PropertySelector labels to PropertySelector objects. */
    private final Map<String, PropertySelector> autoCompleteScope = new LinkedHashMap<>();

    /** GlazedLists' modified auto complete support. */
    private CustomAutoCompleteSupport<String> autoCompleteSupport = null;

    /** The auto complete combo component. */
    private final JComboBox<String> autoCompleteCombo;

    /**
     * A TextMatcherEditor handling glyph list filtering triggered by changes in
     * the autocomplete combo's editor box.
     */
    private final GlyphSelect glyphSelect = new GlyphSelect();

    /** The sorted list model. */
    private final SortedList<GlyphDefinition> sortedList;

    /**
     * Instantiates a new AutoCompleteController.
     *
     * @param scopeIndex
     *            the index of the auto complete scope combo's item to be selected initially
     * @param autoCompleteCombo
     *            the auto complete combo component
     * @param scopeCombo
     *            the scope combo component
     * @param sortedList
     *            the sorted list model
     */
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

    /**
     * Adds the content key-value pairs to autoCompleteScope.
     */
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

    /**
     * Initializes the auto complete scope combo component.
     *
     * @param scopeIndex
     *            the index of the auto complete scope combo's item to be selected initially
     * @param scopeCombo
     *            the scope combo component
     */
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

    /**
     * Gets a propertySelector from the autoCompleteScope by index.
     *
     * @param index
     *            the index
     * @return the propertySelector
     */
    public final PropertySelector getPropertySelectorByIndex(int index) {
        List<String> l = new ArrayList<>(autoCompleteScope.keySet());
        return autoCompleteScope.get(l.get(index));
    }

    /**
     * Sets the auto complete support implementing the supplied PropertySelector.
     *
     * @param propertySelector
     *            the property selector object
     */
    private void setAutoCompleteSupport(final PropertySelector propertySelector) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                GlyphPropertyEventList propertyList = new GlyphPropertyEventList(
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

    /**
     * Gets the glyphSelect TextMatcherEditor.
     *
     * @return the TextMatcherEditor
     */
    public GlyphSelect getGlyphSelect() {
        return glyphSelect;
    }

}
