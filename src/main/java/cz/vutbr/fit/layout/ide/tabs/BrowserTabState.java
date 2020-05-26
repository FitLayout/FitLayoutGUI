/**
 * BrowserTabState.java
 *
 * Created on 15. 5. 2020, 22:06:38 by burgetr
 */
package cz.vutbr.fit.layout.ide.tabs;

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
    
}
