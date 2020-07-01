/**
 * ArtifactTreeCellRenderer.java
 *
 * Created on 26. 5. 2020, 19:16:15 by burgetr
 */
package cz.vutbr.fit.layout.ide.misc;

import java.awt.Color;

import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * 
 * @author burgetr
 */
public class ArtifactTreeCellRenderer extends DefaultTreeCellRenderer
{
    private static final long serialVersionUID = 1L;

    @Override
    public Color getBackgroundNonSelectionColor()
    {
        return (null);
    }

    @Override
    public Color getBackgroundSelectionColor()
    {
        return super.getBackgroundSelectionColor();
    }

    @Override
    public Color getBackground()
    {
        return (null);
    }

    /*@Override
    public Component getTreeCellRendererComponent(final JTree tree,
            final Object value, final boolean sel, final boolean expanded,
            final boolean leaf, final int row, final boolean hasFocus)
    {
        final Component ret = super.getTreeCellRendererComponent(tree, value,
                sel, expanded, leaf, row, hasFocus);

        final DefaultMutableTreeNode node = ((DefaultMutableTreeNode) (value));
        this.setText(value.toString());
        return ret;
    }*/
}
