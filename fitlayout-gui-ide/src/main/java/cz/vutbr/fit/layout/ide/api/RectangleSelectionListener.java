/**
 * RectangleSelectionListener.java
 *
 * Created on 19. 10. 2016, 3:24:58 by burgetr
 */
package cz.vutbr.fit.layout.ide.api;

import cz.vutbr.fit.layout.model.Rectangular;

/**
 * An interface that allows the plugin to implement the reactions to
 * a rectangular area selection in the browser window.
 * @author burgetr
 */
public interface RectangleSelectionListener
{

    public void rectangleCreated(Rectangular rect);
    
}
