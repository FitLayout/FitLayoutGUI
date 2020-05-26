/**
 * SegmentationTab.java
 *
 * Created on 22. 4. 2020, 16:25:23 by burgetr
 */
package cz.vutbr.fit.layout.ide.tabs;

import javax.swing.JPanel;

import cz.vutbr.fit.layout.ide.BlockBrowser;

/**
 * 
 * @author burgetr
 */
public class SegmentationTab extends BrowserTabBase
{
    private SegmentationPanel segmentationPanel;
    
    
    public SegmentationTab(BlockBrowser browser)
    {
        super(browser);
        segmentationPanel = new SegmentationPanel(browser);
    }

    @Override
    public String getTitle()
    {
        return "Segmentation";
    }

    @Override
    public JPanel getTabPanel()
    {
        return segmentationPanel;
    }

    @Override
    public void reloadServiceParams()
    {
        segmentationPanel.reloadServiceParams();
    }

}
