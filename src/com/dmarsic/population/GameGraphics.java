package com.dmarsic.population;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;

/**
 * GameGraphics inspiration and copy/paste originates from:
 * https://github.com/marcliberatore/spaceinvaders-102-java
 */

public class GameGraphics extends Canvas {

    int screenWidth;
    int screenHeight;
    int statusBarHeight = 20;
    int blockSize = 8;
    int flipSleepTime = 500;  // ms

    int fontSizeStandard = 10;

    BufferStrategy strategy;
    Graphics2D context;

    public GameGraphics(int blocksX, int blocksY) {

        // Determine the size of the screen based on the world size
        screenWidth = blocksX * blockSize;
        screenHeight = blocksY * blockSize + statusBarHeight;

        // Create a frame to contain our game
        JFrame container = new JFrame("Population");

        // Get hold of the content of the frame and set up the resolution
        JPanel panel = (JPanel) container.getContentPane();
        panel.setPreferredSize(new Dimension(screenWidth, screenHeight));
        panel.setLayout(null);

        // Set up canvas size and put it into the content of the frame.
        // setBounds() comes from Canvas.
        setBounds(0, 0, screenWidth, screenHeight);
        panel.add(this);

        // Tell awt not to repaint canvas, we'll do it in accelerated mode
        setIgnoreRepaint(true);

        // Make the window visible
        container.pack();
        container.setResizable(false);
        container.setVisible(true);

        // Listener that exits if user closes the window
        container.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        // Key input system to respond to keys pressed
        addKeyListener(new KeyInputHandler());

        // Request the focus so key event come to us
        requestFocus();

        // Create the buffering strategy to allow awt manage
        // accelerated graphics
        createBufferStrategy(2);
        strategy = getBufferStrategy();

        // Get hold of a graphics context for the accelerated
        // surface and blank it out.
        setContext();
    }

    public void setContext() {
        Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, screenWidth, screenHeight);
        context = g;
    }

    public Graphics2D getContext() {
        return context;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public void flip() {
        context.dispose();
        strategy.show();
    }

    public void putText(int x, int y, String text) {
        context.setFont(new Font("Helvetica", Font.PLAIN, fontSizeStandard));
        context.setColor(Color.WHITE);
        context.drawString(text, x, y);
    }
}
