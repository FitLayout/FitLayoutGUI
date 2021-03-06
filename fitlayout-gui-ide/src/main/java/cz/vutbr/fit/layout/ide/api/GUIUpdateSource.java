/**
 * GUIUpdateSource.java
 *
 * Created on 4. 2. 2016, 23:58:41 by burgetr
 */
package cz.vutbr.fit.layout.ide.api;

/**
 * A source of GUI update events.
 * 
 * @author burgetr
 */
public interface GUIUpdateSource
{

    /**
     * Registers a new listener that should be notified when the GUI should be updated.
     * @param listener the new listener to be added.
     */
    public void registerGUIUpdateListener(GUIUpdateListener listener);
    
}
