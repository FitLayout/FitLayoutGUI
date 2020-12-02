/**
 * BrowserPanel.java
 *
 * Created on 4.9.2007, 13:57:43 by burgetr
 */
package cz.vutbr.fit.layout.ide.tabs;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

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

    protected Page page;
    protected BufferedImage img;
    protected Graphics2DDisplay disp;
    
    public BrowserPanel(Page page)
    {
        this.page = page;
        setSize(page.getWidth(), page.getHeight());
        setPreferredSize(new Dimension(page.getWidth(), page.getHeight()));
        img = new BufferedImage(page.getWidth(), page.getHeight(), BufferedImage.TYPE_INT_RGB);
        disp = new Graphics2DDisplay(img.createGraphics());
        disp.drawPage(page);
    }

    public void redrawPage()
    {
        disp.drawPage(page);
    }
    
    public OutputDisplay getOutputDisplay()
    {
        return disp;
    }
    
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(img, 0, 0, null);
    }

}
