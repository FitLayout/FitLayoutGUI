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
import cz.vutbr.fit.layout.model.AreaTree;
import cz.vutbr.fit.layout.model.Artifact;
import cz.vutbr.fit.layout.model.Page;

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
        
        if (nodeVal instanceof AreaTree)
        {
            final AreaTree art = (AreaTree) nodeVal;
            final String iriStr = iriDecoder.encodeIri(art.getIri());
            final String descr;
            if (art.getCreator() != null)
                descr = art.getCreator();
            else
                descr = "Area tree";
            setText("[" + iriStr + "] " + descr);
            setToolTipText("<html>" + getCreatorDescr(art) + "</html>");
        }
        else if (nodeVal instanceof Page)
        {
            final Page art = (Page) nodeVal;
            final String iriStr = iriDecoder.encodeIri(art.getIri());
            final String descr;
            if (art.getTitle() != null)
                descr = art.getTitle();
            else if (art.getSourceURL() != null)
                descr = art.getSourceURL().toString();
            else
                descr = "Page";
            setText("[" + iriStr + "] " + descr);
            setToolTipText("<html>" + getPageTooltip(art) + "</html>");
        }
        
        return ret;
    }
    
    private String getPageTooltip(Page page)
    {
        String ret = "";
        if (page.getTitle() != null)
            ret += "<b>Title:</b> " + page.getTitle() + "<br>";
        if (page.getSourceURL() != null)
            ret += "<b>URL:</b> " + page.getSourceURL() + "<br>";
        ret += getCreatorDescr(page);
        return ret;
    }
    
    private String getCreatorDescr(Artifact art)
    {
        String ret = "";
        if (art.getCreator() != null)
            ret += "<b>Creator:</b> " + art.getCreator() + "<br>";
        if (art.getCreatorParams() != null)
            ret += "<b>Params:</b> " + art.getCreator() + "<br>";
        if (art.getCreatedOn() != null)
            ret += "<i>Created on " + DateFormat.getDateTimeInstance().format(art.getCreatedOn()) + "</i>";
        ret += "";
        return ret;
    }
    
}
