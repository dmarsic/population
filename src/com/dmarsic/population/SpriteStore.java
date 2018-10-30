package com.dmarsic.population;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

public class SpriteStore {

    /** The single instance of this class. */
    private static SpriteStore single = new SpriteStore();

    public int blockSize = 8;

    /**
     * Get the single instance of this class.
     *
     * @return The single instance of this class.
     */
    public static SpriteStore get() {
        return single;
    }

    /** The cached sprite map, from reference to sprite instance. */
    private HashMap sprites = new HashMap();

    /** Cache of loaded images. */
    private HashMap loadedImages = new HashMap();

    /**
     * Retrieve a sprite from the store
     *
     * @param filePath The reference to the image to use for the sprite.
     * @return A sprite instance containing an accelerate image of the request reference.
     */
    public Sprite getSprite(String filePath, int blockX, int blockY) {
        // If we've already got the sprite in the cache
        // then just return the existing version

        // REMOVE
        if (sprites.get(filePath) != null) {
            return (Sprite) sprites.get(filePath);
        }

        BufferedImage sourceImage = null;
        if (loadedImages.get(filePath) != null) {
            sourceImage = (BufferedImage) loadedImages.get(filePath);
            // return (Sprite) cachedImage.getSubimage(blockX * blockSize, blockY * blockSize, blockSize, blockSize);
        } else {

            try {
                // ClassLoader.getResource() ensures we get the sprite
                // from the appropriate place. This helps with deploying the
                // game with things like webstart. You could equally do a file
                // lookup here.
                URL url = this.getClass().getClassLoader().getResource(filePath);

                if (url == null) {
                    fail("Can't find file path: " + filePath);
                }

                // use ImageIO to read the image in
                sourceImage = ImageIO.read(url);

                // Cache the image.

            } catch (IOException e) {
                fail("Failed to load: " + filePath);
            }
        }

        String spriteId = filePath + "_" + blockX + "_" + blockY;
        if (sprites.get(spriteId) != null) {
            return (Sprite) sprites.get(spriteId);
        }

        sourceImage = sourceImage.getSubimage(blockX * blockSize, blockY * blockSize, blockSize, blockSize);

        // Create an accelerated image of the right size to store our sprite in
        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment()
                                                      .getDefaultScreenDevice()
                                                      .getDefaultConfiguration();
        Image image = gc.createCompatibleImage(sourceImage.getWidth(),
                                               sourceImage.getHeight(),
                                               Transparency.BITMASK);

        // Draw source image into the accelerated image
        image.getGraphics().drawImage(sourceImage, 0, 0, null);

        // Create a sprite, cache it, and return it.
        Sprite sprite = new Sprite(image);
        sprites.put(spriteId, sprite);

        return sprite;
    }

    /**
     * Utility method to handle resource loading failure
     *
     * @param message The message to display on failure.
     */
    private void fail(String message) {
        System.err.println(message);
        System.exit(0);
    }
}
