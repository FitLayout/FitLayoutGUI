/**
 * SegmentationTab.java
 *
 * Created on 22. 4. 2020, 16:25:23 by burgetr
 */
package cz.vutbr.fit.layout.ide.tabs;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import cz.vutbr.fit.layout.api.ArtifactService;
import cz.vutbr.fit.layout.ide.Browser;

/**
 * 
 * @author burgetr
 */
public class SegmentationTab extends BrowserTabBase
{
    private SegmentationPanel segmentationPanel;
    
    
    public SegmentationTab(Browser browser)
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
    
    @Override
    public Map<String, Object> getState()
    {
        Map<String, Object> ret = new HashMap<>();
        ArtifactService selected = segmentationPanel.getSelectedProvider();
        if (selected != null)
            ret.put("service", selected.getId());
        ret.put("autorun", segmentationPanel.isAutoRun());
        return ret;
    }

    @Override
    public void setState(Map<String, Object> state)
    {
        Object serviceId = state.get("service");
        if (serviceId != null && serviceId instanceof String)
        {
            segmentationPanel.setSelectedProviderId((String) serviceId);
        }
        Object autorun = state.get("autorun");
        if (autorun != null && autorun instanceof Boolean)
        {
            segmentationPanel.setAutoRun((boolean) autorun);
        }
    }

}
