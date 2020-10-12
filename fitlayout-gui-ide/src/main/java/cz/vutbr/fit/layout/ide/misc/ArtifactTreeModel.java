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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.eclipse.rdf4j.model.IRI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.vutbr.fit.layout.api.ArtifactRepository;
import cz.vutbr.fit.layout.model.Artifact;

/**
 * 
 * @author burgetr
 */
public class ArtifactTreeModel extends DefaultTreeModel
{
    private static final long serialVersionUID = 1L;
    private static Logger log = LoggerFactory.getLogger(ArtifactTreeModel.class);
    
    private ArtifactRepository repo;
    private Map<IRI, DefaultMutableTreeNode> nodeMap;
    

    public ArtifactTreeModel(ArtifactRepository repo)
    {
        super(new DefaultMutableTreeNode("Pages"));
        this.repo = repo;
        nodeMap = new HashMap<>();
        updateArtifactTree();
    }
    
    /**
     * Updates the tree model according to the repository.
     */
    public void updateArtifactTree()
    {
        Collection<IRI> iris = repo.getArtifactIRIs();
        // collect new nodes to add
        Set<Artifact> toAdd = new HashSet<>();
        for (IRI iri : iris)
        {
            if (!nodeMap.containsKey(iri))
            {
                Artifact a = repo.getArtifact(iri);
                if (a != null)
                    toAdd.add(a);
                else
                    log.error("Could't retrieve artifact {} from repository", iri);
            }
        }
        addToTree(toAdd);
        if (!toAdd.isEmpty())
            log.error("Some artifacts don't fit to the tree: {}", toAdd);
        // remove nodes of artifacts that are not in the repository anymore
        retainChildNodes((DefaultMutableTreeNode) getRoot(), iris);
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
        //parent.add(new DefaultMutableTreeNode(artifact));
    }
    
    private void retainChildNodes(DefaultMutableTreeNode root, Collection<IRI> toRetain)
    {
        List<DefaultMutableTreeNode> toRemove = new ArrayList<>();
        // find the nodes to remove
        for (Enumeration<TreeNode> e = root.children(); e.hasMoreElements();)
        {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
            Artifact a = (Artifact) node.getUserObject();
            if (!toRetain.contains(a.getIri()))
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
