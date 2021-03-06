/**
 * ArtifactTreeModel.java
 *
 * Created on 12. 10. 2020, 15:13:32 by burgetr
 */
package cz.vutbr.fit.layout.ide.misc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.eclipse.rdf4j.model.IRI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.vutbr.fit.layout.ide.GUIProcessor;
import cz.vutbr.fit.layout.model.Artifact;

/**
 * A tree model which is backed with an ArtifactRepository.
 * 
 * @author burgetr
 */
public class ArtifactTreeModel extends DefaultTreeModel
{
    private static final long serialVersionUID = 1L;
    private static Logger log = LoggerFactory.getLogger(ArtifactTreeModel.class);
    
    private GUIProcessor proc;
    private Map<IRI, DefaultMutableTreeNode> nodeMap;
    

    public ArtifactTreeModel(GUIProcessor proc)
    {
        super(new DefaultMutableTreeNode("Pages"));
        this.proc = proc;
        nodeMap = new HashMap<>();
        updateArtifactTree();
    }
    
    /**
     * Finds the tree node that contains the given artifact.
     * @param a the artifact to find
     * @return the tree node or {@code null} when the artifacti is not contained in any tree node
     */
    public DefaultMutableTreeNode getNodeForArtifact(Artifact a)
    {
        return nodeMap.get(a.getIri());
    }
    
    /**
     * Reloads the tree model from the repository from scratch.
     */
    public void reloadArtifactTree()
    {
        ((DefaultMutableTreeNode) getRoot()).removeAllChildren();
        nodeMap = new HashMap<>();
        updateArtifactTree();
    }
    
    /**
     * Updates the tree model according to the repository.
     */
    public void updateArtifactTree()
    {
        Collection<Artifact> infos = proc.getRepository().getArtifactInfo();
        // collect new nodes to add
        Set<Artifact> toAdd = new LinkedHashSet<>();
        for (Artifact info : infos)
        {
            if (!nodeMap.containsKey(info.getIri()))
                toAdd.add(info);
        }
        addToTree(toAdd);
        if (!toAdd.isEmpty())
            log.error("Some artifacts don't fit to the tree: {}", toAdd);
        // remove nodes of artifacts that are not in the repository anymore
        retainChildNodes((DefaultMutableTreeNode) getRoot(), infos);
        // update the component
        reload();
    }
    
    /**
     * Adds the artifact to the tree and removes the successfully added artifacts from the source collection.
     * @param artifacts
     */
    private void addToTree(Collection<Artifact> artifacts)
    {
        boolean changed = true;
        while (changed)
        {
            changed = false;
            for (Iterator<Artifact> it = artifacts.iterator(); it.hasNext();)
            {
                Artifact a = it.next();
                if (a.getParentIri() == null) // root nodes (pages)
                {
                    addArtifact((DefaultMutableTreeNode) getRoot(), a);
                    it.remove();
                    changed = true;
                }
                else // non-root nodes
                {
                    DefaultMutableTreeNode parent = nodeMap.get(a.getParentIri());
                    if (parent != null) // parent already exists
                    {
                        addArtifact(parent, a);
                        it.remove();
                        changed = true;
                    }
                }
            }
        }
    }
    
    private void addArtifact(DefaultMutableTreeNode parent, Artifact artifact)
    {
        var newChild = new DefaultMutableTreeNode(artifact);
        insertNodeInto(newChild, parent, parent.getChildCount());
        nodeMap.put(artifact.getIri(), newChild);
    }
    
    private void retainChildNodes(DefaultMutableTreeNode root, Collection<Artifact> toRetain)
    {
        List<DefaultMutableTreeNode> toRemove = new ArrayList<>();
        // find the nodes to remove
        for (Enumeration<TreeNode> e = root.children(); e.hasMoreElements();)
        {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
            Artifact a = (Artifact) node.getUserObject();
            if (!toRetain.contains(a))
                toRemove.add(node);
            else
                retainChildNodes(node, toRetain);
        }
        // remove the nodes
        for (DefaultMutableTreeNode node : toRemove)
        {
            removeNodeFromParent(node);
        }
    }

}
