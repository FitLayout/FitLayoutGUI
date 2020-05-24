/**
 * ArtifactViewBase.java
 *
 * Created on 24. 5. 2020, 18:12:53 by burgetr
 */
package cz.vutbr.fit.layout.ide;

/**
 * 
 * @author burgetr
 */
public abstract class ArtifactViewBase implements ArtifactView
{
    protected BlockBrowser browser;
    private boolean active;

    
    public ArtifactViewBase(BlockBrowser browser)
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
