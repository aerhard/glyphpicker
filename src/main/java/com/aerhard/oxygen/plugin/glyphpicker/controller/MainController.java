package com.aerhard.oxygen.plugin.glyphpicker.controller;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

import com.aerhard.oxygen.plugin.glyphpicker.model.GlyphDefinition;
import com.aerhard.oxygen.plugin.glyphpicker.view.MainPanel;

public class MainController extends Controller {

    private static final Logger LOGGER = Logger.getLogger(MainController.class
            .getName());
    private MainPanel mainPanel;

    private ConfigLoader configLoader;

    private Controller browserController;
    private Controller userCollectionController;

    public MainController(StandalonePluginWorkspace workspace) {

        Properties properties = new Properties();
        try {
            properties.load(MainController.class
                    .getResourceAsStream("/plugin.properties"));
        } catch (IOException e) {
            LOGGER.error("Could not read \"plugin.properties\".");
        }

        configLoader = new ConfigLoader(workspace, properties);
        configLoader.load();

        browserController = new BrowserController(configLoader.getConfig());
        browserController.addListener(this);

        userCollectionController = new UserCollectionController(workspace,
                properties);
        userCollectionController.addListener(this);
        addListener(userCollectionController);

        // TODO enable this feature again

        // set same model for viewCombo in brower view and userCollection view
        // ComboBoxModel<String> browserViewComboModel =
        // ((BrowserController)browserController).getPanel().getViewCombo().getModel();
        // ((UserCollectionController)userCollectionController).getPanel().getViewCombo().setModel(browserViewComboModel);

        mainPanel = new MainPanel(browserController.getPanel(),
                userCollectionController.getPanel());

        mainPanel.getTabbedPane().setSelectedIndex(1);
    }

    public ConfigLoader getConfigLoader() {
        return configLoader;
    }

    @Override
    public MainPanel getPanel() {
        return mainPanel;
    }

    @Override
    public void eventOccured(String type, GlyphDefinition glyphDefinition) {

        if ("insert".equals(type)) {
            fireEvent("insert", glyphDefinition);
        }

        else if ("copyToUserCollection".equals(type)) {
            try {
                GlyphDefinition clone = glyphDefinition.clone();
                fireEvent("copyToUserCollection", clone);
                mainPanel.highlightTabTitle(0);
            } catch (CloneNotSupportedException e) {
                LOGGER.error(e);
            }
        }
    }

    @Override
    public void loadData() {
        userCollectionController.loadData();
        browserController.loadData();
    }

    public void saveData() {
        getConfigLoader().save();

        // TODO ask if there are unsaved changes to user collection list
        userCollectionController.saveData();
    }

}
