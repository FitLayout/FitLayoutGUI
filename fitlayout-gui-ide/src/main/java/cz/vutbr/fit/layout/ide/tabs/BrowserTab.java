/**
 * BrowserTab.java
 *
 * Created on 22. 4. 2020, 15:48:45 by burgetr
 */
package cz.vutbr.fit.layout.ide.tabs;

import java.util.Map;

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

    /**
     * Returns the represenation of the current tab state. This may be used
     * for storing the tab configuration.
     * @return A map representing the tab state. The keys and their values depend on the tab implementation.
     */
    public Map<String, Object> getState();
    
    /**
     * Configures the tab according to the new state.
     * @param state the new state
     */
    public void setState(Map<String, Object> state);
    
}
