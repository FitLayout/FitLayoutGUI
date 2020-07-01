/**
 * BrowserTab.java
 *
 * Created on 22. 4. 2020, 15:48:45 by burgetr
 */
package cz.vutbr.fit.layout.ide.tabs;

import javax.swing.JPanel;

/**
 * A generic browser tab interface.
 * 
 * @author burgetr
 */
public interface BrowserTab
{
    
    /**
     * The title of the tab.
     * @return the tab title
     */
    public String getTitle();
    
    /**
     * Gets the main tab panel (displayed at the top tabs)
     * 
     * @return the tabs panel
     */
    public JPanel getTabPanel();
    
    /**
     * Sets the status of the tab to active/inactive.
     * 
     * @param active
     */
    public void setActive(boolean active);
    
    /**
     * Reloads the values of the service parametres in the configuration dialogs (if any)
     * if they have been changed in the background.
     */
    public void reloadServiceParams();

}
