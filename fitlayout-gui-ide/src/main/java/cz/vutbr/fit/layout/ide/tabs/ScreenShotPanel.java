/**
 * ScreenShotPanel.java
 *
 * Created on 18. 11. 2020, 10:49:11 by burgetr
 */
package cz.vutbr.fit.layout.ide.tabs;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * 
 * @author burgetr
 */
public class ScreenShotPanel extends JPanel
{
    private static final long serialVersionUID = 1L;
    private BufferedImage image;

    public ScreenShotPanel(byte[] pngData) 
    {
       try {                
          image = ImageIO.read(new ByteArrayInputStream(pngData));
       } catch (IOException ex) {
           image = null;
       }
    }
    
    public boolean isOk()
    {
        return image != null;
    }

    @Override
    protected void paintComponent(Graphics g) 
    {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this);            
    }

    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(image.getWidth(), image.getHeight());
    }

}
