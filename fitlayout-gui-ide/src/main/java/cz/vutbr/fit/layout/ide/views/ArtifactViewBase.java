/**
 * ArtifactViewBase.java
 *
 * Created on 24. 5. 2020, 18:12:53 by burgetr
 */
package cz.vutbr.fit.layout.ide.views;

import cz.vutbr.fit.layout.ide.Browser;

/**
 * 
 * @author burgetr
 */
public abstract class ArtifactViewBase implements ArtifactView
{
    private Browser browser;
    private boolean active;

    
    public ArtifactViewBase(Browser browser)
    {
        this.browser = browser;
    }

    public Browser getBrowser()
    {
        return browser;
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
