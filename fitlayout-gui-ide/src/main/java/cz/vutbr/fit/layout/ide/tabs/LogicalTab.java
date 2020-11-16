/**
 * LogicalTab.java
 *
 * Created on 22. 4. 2020, 18:51:35 by burgetr
 */
package cz.vutbr.fit.layout.ide.tabs;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import cz.vutbr.fit.layout.ide.Browser;

/**
 * 
 * @author burgetr
 */
public class LogicalTab extends BrowserTabBase
{

    public LogicalTab(Browser browser)
    {
        super(browser);
        // TODO Auto-generated constructor stub
    }

    @Override
    public String getTitle()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JPanel getTabPanel()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void reloadServiceParams()
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public Map<String, Object> getState()
    {
        Map<String, Object> ret = new HashMap<>();
        return ret;
    }

    @Override
    public void setState(Map<String, Object> state)
    {
    }

}
