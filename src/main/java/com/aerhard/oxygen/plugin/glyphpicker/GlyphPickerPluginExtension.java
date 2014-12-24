/**
 * Copyright 2014 Alexander Erhard
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

package com.aerhard.oxygen.plugin.glyphpicker;

import javax.swing.JComponent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Position;

import org.apache.log4j.Logger;

import com.aerhard.oxygen.plugin.glyphpicker.controller.GlyphEventListener;
import com.aerhard.oxygen.plugin.glyphpicker.controller.MainController;
import com.aerhard.oxygen.plugin.glyphpicker.model.GlyphDefinition;

import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension;
import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.editor.page.WSEditorPage;
import ro.sync.exml.workspace.api.editor.page.author.WSAuthorEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.WSTextEditorPage;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.standalone.ViewComponentCustomizer;
import ro.sync.exml.workspace.api.standalone.ViewInfo;

/**
 * The oXygen plugin extension.
 */
public class GlyphPickerPluginExtension implements
        WorkspaceAccessPluginExtension {

    /** The logger. */
    private static final Logger LOGGER = Logger
            .getLogger(GlyphPickerPluginExtension.class.getName());

    private MainController mainController;

    /*
     * (non-Javadoc)
     * 
     * @see ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension#
     * applicationStarted
     * (ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace)
     */
    @Override
    public void applicationStarted(final StandalonePluginWorkspace workspace) {

        mainController = new MainController(workspace);
        mainController.addListener(new GlyphEventListener() {
            @Override
            public void eventOccured(String type, GlyphDefinition model) {
                if ("insert".equals(type)) {
                    insertFragment(workspace, model);
                }
            }
        });

        workspace.addViewComponentCustomizer(new ViewComponentCustomizer() {
            /**
             * @see ro.sync.exml.workspace.api.standalone.ViewComponentCustomizer#customizeView(ro.sync.exml.workspace.api.standalone.ViewInfo)
             */
            @Override
            public void customizeView(ViewInfo viewInfo) {
                if ("GlyphPicker".equals(viewInfo.getViewID())) {

                    JComponent panel = mainController.getPanel();
                    mainController.loadData();

                    viewInfo.setComponent(panel);
                    viewInfo.setTitle("GlyphPicker");

                    // TODO add icon
                    // viewInfo.setIcon(Icons
                    // .getIcon(Icons.CMS_MESSAGES_CUSTOM_VIEW_STRING));
                }

            }
        });

    }

    private String getXmlString(GlyphDefinition model, Boolean setNs) {
        String ns = (setNs) ? " xmlns=\"http://www.tei-c.org/ns/1.0\"" : "";
        return "<g" + ns + " ref=\"" + model.getRefString() + "\"/>";
    }

    private void insertFragment(StandalonePluginWorkspace workspace,
            GlyphDefinition model) {
        WSEditorPage currentPage = workspace.getCurrentEditorAccess(
                PluginWorkspace.MAIN_EDITING_AREA).getCurrentPage();
        if (currentPage instanceof WSTextEditorPage) {
            WSTextEditorPage page = (WSTextEditorPage) currentPage;

            // TODO make this namespace aware

            page.beginCompoundUndoableEdit();
            int selectionOffset = page.getSelectionStart();
            page.deleteSelection();
            try {
                page.getDocument().insertString(selectionOffset,
                        getXmlString(model, false),
                        javax.swing.text.SimpleAttributeSet.EMPTY);
            } catch (BadLocationException e) {
                LOGGER.error(e);
            }
            page.endCompoundUndoableEdit();

        } else if (currentPage instanceof WSAuthorEditorPage) {
            WSAuthorEditorPage page = (WSAuthorEditorPage) currentPage;

            AuthorAccess authorAccess = page.getAuthorAccess();
            try {
                int offset = authorAccess.getEditorAccess().getCaretOffset();
                Position endOffsetPos = null;
                try {
                    endOffsetPos = authorAccess.getDocumentController()
                            .createPositionInContent(offset + 1);
                } catch (BadLocationException e1) {
                    LOGGER.error(e1);
                }

                authorAccess.getDocumentController()
                        .insertXMLFragmentSchemaAware(
                                getXmlString(model, true), offset);

                int endOffset = endOffsetPos != null ? endOffsetPos.getOffset() - 1
                        : offset;
                authorAccess.getEditorAccess().setCaretPosition(endOffset);
            } catch (AuthorOperationException e) {
                LOGGER.error(e);
            }

        } else {
            workspace
                    .showInformationMessage("No editor pane found to insert the glyph.");
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension#
     * applicationClosing()
     */
    @Override
    public boolean applicationClosing() {

        mainController.saveData();

        return true;
    };

}
