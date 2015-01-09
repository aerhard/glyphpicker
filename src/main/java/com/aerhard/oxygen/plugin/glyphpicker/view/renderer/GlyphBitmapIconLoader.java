package com.aerhard.oxygen.plugin.glyphpicker.view.renderer;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.SwingWorker;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.aerhard.oxygen.plugin.glyphpicker.model.GlyphDefinition;

public class GlyphBitmapIconLoader extends SwingWorker<GlyphBitmapIcon, Void> {

    private static final Logger LOGGER = Logger
            .getLogger(GlyphBitmapIconLoader.class.getName());

    private GlyphDefinition glyphDefinition;
    private JComponent container;
    private int size;

    public GlyphBitmapIconLoader(GlyphDefinition glyphDefinition,
            JComponent container, int size) {
        this.glyphDefinition = glyphDefinition;
        this.container = container;
        this.size = size;
    }

    @Override
    public GlyphBitmapIcon doInBackground() throws IOException {
        BufferedImage bi = loadImage(glyphDefinition.getDataSource()
                .getBasePath(), glyphDefinition.getUrl());
        if (bi != null) {
            return new GlyphBitmapIcon(scaleToBound(bi, size, size), size);
        }
        return null;
    }

    @Override
    public void done() {
        try {
            GlyphBitmapIcon icon = get();
            if (icon != null) {
                glyphDefinition.setIcon(icon);
                if (container != null) {
                    container.repaint();
                }
            }
        } catch (Exception e) {
            LOGGER.warn(e);
        }
    }

    public Image scaleToBound(BufferedImage image, int boundX, int boundY) {

        int originalWidth = image.getWidth();
        int originalHeight = image.getHeight();

        int resultWidth = originalWidth;
        int resultHeight = originalHeight;

        if (originalWidth > boundX) {
            resultWidth = boundX;
            resultHeight = (resultWidth * originalHeight) / originalWidth;
        }

        if (resultHeight > boundY) {
            resultHeight = boundY;
            resultWidth = (resultHeight * originalWidth) / originalHeight;
        }

        // set minimum dimension of 1 px
        resultWidth = Math.max(resultWidth, 1);
        resultHeight = Math.max(resultHeight, 1);

        return image.getScaledInstance(resultWidth, resultHeight,
                Image.SCALE_AREA_AVERAGING);
    }

    public static Boolean isLocalFile(String path) {
        return (!path.matches("^\\w+:\\/\\/.*"));
    }

    public BufferedImage loadImage(String path, String relativePath) {
        BufferedImage image = null;
        if (relativePath != null) {
            if (isLocalFile(path)) {
                File a = new File(path);
                File parentFolder = new File(a.getParent());
                File b = new File(parentFolder, relativePath);
                image = getImageFromFile(b);
            } else {
                try {
                    String imagePath = (new URL(new URL(path), relativePath))
                            .toString();
                    image = getImageFromUrl("guest", "guest", imagePath);
                } catch (MalformedURLException e) {
                    LOGGER.info(e);
                }

            }
        }
        return image;
    }

    public BufferedImage getImageFromFile(File file) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
            LOGGER.warn("\"" + file.toPath() + "\" could not be loaded.", e);
        }
        return image;
    };

    public BufferedImage getImageFromUrl(String user, String password,
            String url) {
        HttpResponse response = null;
        BufferedImage image = null;
        DefaultHttpClient httpclient = new DefaultHttpClient();
        try {
            HttpGet httpGet = new HttpGet(url);
            httpGet.addHeader(BasicScheme.authenticate(
                    new UsernamePasswordCredentials(user, password), "UTF-8",
                    false));
            response = httpclient.execute(httpGet);

            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                byte[] bytes = EntityUtils.toByteArray(entity);
                image = ImageIO.read(new ByteArrayInputStream(bytes));
                return image;
            } else {
                throw new IOException("Download failed, HTTP response code "
                        + statusCode + " - " + statusLine.getReasonPhrase());
            }
        } catch (IOException e) {
            LOGGER.info("Error loading image from \"" + url + "\"", e);
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        return image;
    };

}
