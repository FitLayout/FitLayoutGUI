/**
 * ArtifactView.java
 *
 * Created on 24. 5. 2020, 18:03:57 by burgetr
 */
package cz.vutbr.fit.layout.ide.views;

import javax.swing.JPanel;

import org.eclipse.rdf4j.model.IRI;

import cz.vutbr.fit.layout.model.Artifact;

/**
 * A view for an artifact type. It is displayed in the browser middle column.
 * @author burgetr
 */
public interface ArtifactView
{

    /**
     * The artifact type this view should be used for.
     * @return the type IRI
     */
    public IRI getArtifactType();
    
    /**
     * The title of the view used for the appropriate tab.
     * @return the title
     */
    public String getTitle();
    
    /**
     * Obtains a panel that is able to show the artifact details.
     * @return the view panel
     */
    public JPanel getViewPanel();
    
    /**
     * Shows the artifact details in the view.
     * @param artifact the artifact to be displated
     */
    public void show(Artifact artifact);
    
    /**
     * Sets the status of the view to active/inactive.
     * @param active
     */
    public void setActive(boolean active);

}
