package com.aerhard.oxygen.plugin.glyphpicker.controller.user;

import java.io.File;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;
import javax.xml.bind.DataBindingException;
import javax.xml.bind.JAXB;

import org.apache.log4j.Logger;

import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

import com.aerhard.oxygen.plugin.glyphpicker.model.GlyphDefinitions;

public class UserCollectionLoader {

    private static final Logger LOGGER = Logger
            .getLogger(UserCollectionLoader.class.getName());

    private String pathName;
    private String fileName;

    private ResourceBundle i18n;

    public UserCollectionLoader(StandalonePluginWorkspace workspace,
            Properties properties) {

        i18n = ResourceBundle.getBundle("GlyphPicker");

        pathName = workspace.getPreferencesDirectory() + "/"
                + properties.getProperty("config.path");
        fileName = properties.getProperty("userdata.filename");
    }

    public void save(GlyphDefinitions glyphDefinitions) {
        File path = new File(pathName);
        Boolean pathExists = (path.exists()) ? true : path.mkdir();
        if (pathExists) {
            File file = new File(path, fileName);
            LOGGER.info("Storing user list.");
            try {
                JAXB.marshal(glyphDefinitions, file);
            } catch (DataBindingException e) {
                LOGGER.error("Error storing config.", e);
            }
        } else {
            JOptionPane.showMessageDialog(
                    null,
                    String.format(
                            i18n.getString(this.getClass().getSimpleName()
                                    + ".couldNotCreateFolder"), pathName),
                    i18n.getString(this.getClass().getSimpleName()
                            + ".storeError"), JOptionPane.ERROR_MESSAGE);
        }
    }

    public GlyphDefinitions load() {
        File file = new File(pathName + "/" + fileName);
        GlyphDefinitions glyphDefinitions = null;

        if (file.exists()) {
            try {
                glyphDefinitions = JAXB.unmarshal(file, GlyphDefinitions.class);
            } catch (DataBindingException e) {
                LOGGER.error("Error unmarshalling user data.", e);
            }
        }

        if (glyphDefinitions == null) {
            glyphDefinitions = new GlyphDefinitions();
        }
        return glyphDefinitions;
    }

}
