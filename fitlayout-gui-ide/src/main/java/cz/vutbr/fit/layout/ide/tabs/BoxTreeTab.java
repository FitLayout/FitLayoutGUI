/**
 * BoxTreeTab.java
 *
 * Created on 21. 4. 2020, 23:40:03 by burgetr
 */
package cz.vutbr.fit.layout.ide.tabs;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import cz.vutbr.fit.layout.api.ArtifactService;
import cz.vutbr.fit.layout.ide.Browser;

/**
 * A composition of the box sources panel and the corresponding gui parts.
 * 
 * @author burgetr
 */
public class BoxTreeTab extends BrowserTabBase
{
    private BoxSourcePanel boxSourcePanel;
    
    
    public BoxTreeTab(Browser browser)
    {
        super(browser);
        boxSourcePanel = new BoxSourcePanel(browser);
    }
    
    @Override
    public String getTitle()
    {
        return "Rendering";
    }

    @Override
    public JPanel getTabPanel()
    {
        return boxSourcePanel;
    }
    
    @Override
    public void reloadServiceParams()
    {
        boxSourcePanel.reloadServiceParams();
    }

    @Override
    public Map<String, Object> getState()
    {
        Map<String, Object> ret = new HashMap<>();
        ArtifactService selected = boxSourcePanel.getSelectedProvider();
        if (selected != null)
            ret.put("service", selected.getId());
        return ret;
    }

    @Override
    public void setState(Map<String, Object> state)
    {
        Object serviceId = state.get("service");
        if (serviceId != null && serviceId instanceof String)
        {
            boxSourcePanel.setSelectedProviderId((String) serviceId);
        }
    }
    
}
