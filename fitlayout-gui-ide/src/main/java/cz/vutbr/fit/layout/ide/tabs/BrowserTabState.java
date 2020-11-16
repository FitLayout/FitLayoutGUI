/**
 * BrowserTabState.java
 *
 * Created on 15. 5. 2020, 22:06:38 by burgetr
 */
package cz.vutbr.fit.layout.ide.tabs;

import cz.vutbr.fit.layout.ide.config.TabConfig;

/**
 * This class summarizes the state of a browser tab.
 * 
 * @author burgetr
 */
public class BrowserTabState
{
    /** The browser tab */
    private BrowserTab browserTab;
    
    /** Is the browser tab optional? (can it be switched off?) */
    private boolean optional;

    /** Is the tab currently visible? */
    private boolean visible;
    
    
    public BrowserTabState(BrowserTab browserTab, boolean optional)
    {
        this.browserTab = browserTab;
        this.optional = optional;
    }

    public BrowserTab getBrowserTab()
    {
        return browserTab;
    }

    public void setBrowserTab(BrowserTab browserTab)
    {
        this.browserTab = browserTab;
    }

    public boolean isOptional()
    {
        return optional;
    }

    public void setOptional(boolean optional)
    {
        this.optional = optional;
    }

    public boolean isVisible()
    {
        return visible;
    }

    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }
    
    /**
     * Creates the tab configuration that can be saved.
     * @return the serializable config
     */
    public TabConfig getTabConfig()
    {
        TabConfig ret = new TabConfig();
        ret.id = browserTab.getClass().getCanonicalName();
        ret.visible = isVisible();
        ret.state = browserTab.getState();
        return ret;
    }
    
    /**
     * Restores a tab configuration based on a saved config.
     * @param config the tab state to restore
     * @return {@code true} when the tab state belongs to this tab and it was restored, {@code false} when
     * the config does not belong to this tab and it was ignored.
     */
    public boolean setTabConfig(TabConfig config)
    {
        if (browserTab.getClass().getCanonicalName().equals(config.id))
        {
            setVisible(config.visible);
            browserTab.setState(config.state);
            return true;
        }
        else
            return false; //this config is not for this tab
    }
    
}
