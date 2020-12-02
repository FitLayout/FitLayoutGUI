/**
 * ArtifactTreeCellRenderer.java
 *
 * Created on 31. 10. 2020, 14:47:39 by burgetr
 */
package cz.vutbr.fit.layout.ide.misc;

import java.awt.Component;
import java.text.DateFormat;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import cz.vutbr.fit.layout.api.IRIDecoder;
import cz.vutbr.fit.layout.model.Artifact;

/**
 * 
 * @author burgetr
 */
public class ArtifactTreeCellRenderer extends DefaultTreeCellRenderer
{
    private static final long serialVersionUID = 1L;

    private IRIDecoder iriDecoder;
    
    public ArtifactTreeCellRenderer(IRIDecoder iriDecoder)
    {
        this.iriDecoder = iriDecoder;
    }

    @Override
    public Component getTreeCellRendererComponent(final JTree tree,
            final Object value, final boolean sel, final boolean expanded,
            final boolean leaf, final int row, final boolean hasFocus)
    {
        final Component ret = super.getTreeCellRendererComponent(tree, value,
                sel, expanded, leaf, row, hasFocus);

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        Object nodeVal = node.getUserObject();
        
        if (nodeVal instanceof Artifact)
        {
            final Artifact art = (Artifact) nodeVal;
            final String iriStr = iriDecoder.encodeIri(art.getIri());
            final String typeStr = iriDecoder.encodeIri(art.getArtifactType());
            final String descr = (art.getLabel() != null) ? art.getLabel() : typeStr;
            setText("[" + iriStr + "] " + descr);
            setToolTipText("<html>" + getCreatorDescr(art) + "</html>");
        }
        
        return ret;
    }
    
    private String getCreatorDescr(Artifact art)
    {
        String ret = "";
        if (art.getLabel() != null)
            ret += "<b>" + art.getLabel() + "</b><br>";
        if (art.getCreator() != null)
            ret += "<b>Creator:</b> " + art.getCreator() + "<br>";
        if (art.getCreatorParams() != null)
            ret += "<b>Params:</b> " + art.getCreatorParams() + "<br>";
        if (art.getCreatedOn() != null)
            ret += "<i>Created on " + DateFormat.getDateTimeInstance().format(art.getCreatedOn()) + "</i>";
        ret += "";
        return ret;
    }
    
}
