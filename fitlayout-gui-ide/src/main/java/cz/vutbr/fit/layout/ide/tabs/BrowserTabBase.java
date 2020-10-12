/**
 * BrowserTabBase.java
 *
 * Created on 22. 4. 2020, 20:32:50 by burgetr
 */
package cz.vutbr.fit.layout.ide.tabs;

import cz.vutbr.fit.layout.ide.Browser;

/**
 * 
 * @author burgetr
 */
public abstract class BrowserTabBase implements BrowserTab
{
    protected Browser browser;
    private boolean active;

    
    public BrowserTabBase(Browser browser)
    {
        this.browser = browser;
    }

    public boolean isActive()
    {
        return active;
    }

    @Override
    public void setActive(boolean active)
    {
        this.active = active;
    }

}
