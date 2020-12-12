/**
 * BrowserPanel.java
 *
 * Created on 4.9.2007, 13:57:43 by burgetr
 */
package cz.vutbr.fit.layout.ide.tabs;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import cz.vutbr.fit.layout.api.OutputDisplay;
import cz.vutbr.fit.layout.io.Graphics2DDisplay;
import cz.vutbr.fit.layout.model.Page;


/**
 * @author burgetr
 *
 */
public class BrowserPanel extends JPanel
{
    private static final long serialVersionUID = 1L;

    private Page page;
    private BufferedImage contentImg;
    private Graphics2DDisplay contentDisplay;
    private BufferedImage overlayImg;
    private Graphics2DDisplay overlayDisplay;
    private BufferedImage screenShotImg;
    private boolean screenShotMode;
    
    
    public BrowserPanel(Page page)
    {
        this.page = page;
        setSize(page.getWidth(), page.getHeight());
        setPreferredSize(new Dimension(page.getWidth(), page.getHeight()));
        overlayImg = new BufferedImage(page.getWidth(), page.getHeight(), BufferedImage.TYPE_INT_ARGB);
        overlayDisplay = new Graphics2DDisplay(overlayImg.createGraphics());
        contentImg = new BufferedImage(page.getWidth(), page.getHeight(), BufferedImage.TYPE_INT_RGB);
        contentDisplay = new Graphics2DDisplay(contentImg.createGraphics());
        contentDisplay.drawPage(page);
        if (page.getPngImage() != null)
            screenShotImg = decodeScreenShot(page.getPngImage());
    }

    public void showScreenShot(boolean show)
    {
        screenShotMode = show;
    }
    
    public boolean screenShotAvailable()
    {
        return (screenShotImg != null);
    }
    
    public void redrawPage()
    {
        contentDisplay.drawPage(page);
    }
    
    public void clearOverlay()
    {
        overlayDisplay.clearArea(0, 0, page.getWidth(), page.getHeight());
    }
    
    /**
     * Gets the content display used for drawing the main page content.
     * @return
     */
    public OutputDisplay getContentDisplay()
    {
        return contentDisplay;
    }
    
    /**
     * Gets the overlay display used for drawing area boundaries and other auxiliary graphics.
     * @return
     */
    public OutputDisplay getOverlayDisplay()
    {
        return overlayDisplay;
    }
    
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if (screenShotMode && screenShotImg != null)
            g.drawImage(screenShotImg, 0, 0, null);
        else
            g.drawImage(contentImg, 0, 0, null);
        g.drawImage(overlayImg, 0, 0, null);
    }
    
    private BufferedImage decodeScreenShot(byte[] pngData)
    {
        BufferedImage image;
        try {
            image = ImageIO.read(new ByteArrayInputStream(pngData));
        } catch (IOException e) {
            image = null;
        }
        return image;
    }

}
