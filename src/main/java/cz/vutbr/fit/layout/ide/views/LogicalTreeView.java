/**
 * LogicalTreeView.java
 *
 * Created on 26. 5. 2020, 20:57:13 by burgetr
 */
package cz.vutbr.fit.layout.ide.views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;

import org.eclipse.rdf4j.model.IRI;

import cz.vutbr.fit.layout.gui.CanvasClickListener;
import cz.vutbr.fit.layout.ide.BlockBrowser;
import cz.vutbr.fit.layout.ide.misc.AreaTreeModel;
import cz.vutbr.fit.layout.ide.misc.ArtifactTreeCellRenderer;
import cz.vutbr.fit.layout.ide.misc.LogicalTreeModel;
import cz.vutbr.fit.layout.model.Artifact;
import cz.vutbr.fit.layout.model.LogicalAreaTree;
import cz.vutbr.fit.layout.ontology.SEGM;

/**
 * 
 * @author burgetr
 */
public class LogicalTreeView extends ArtifactViewBase implements CanvasClickListener
{
    private static final Color BGCOLOR = new Color(225, 225, 255);

    private JPanel structurePanel;
    private JPanel propertiesPanel;
    private JPanel viewPanel;
    
    private JTable infoTable;
    private JTree areaJTree;

    private LogicalAreaTree currentAreaTree;

    
    public LogicalTreeView(BlockBrowser browser)
    {
        super(browser);
        viewPanel = createViewPanel();
        browser.addCanvasClickListener(null, this, false);
    }

    @Override
    public IRI getArtifactType()
    {
        return SEGM.LogicalAreaTree;
    }

    @Override
    public String getTitle()
    {
        return "Logical tree";
    }

    @Override
    public JPanel getViewPanel()
    {
        return viewPanel;
    }

    @Override
    public void show(Artifact artifact)
    {
        if (artifact instanceof LogicalAreaTree)
        {
            currentAreaTree = (LogicalAreaTree) artifact;
            areaJTree.setModel(new LogicalTreeModel(currentAreaTree.getRoot()));
        }
    }
    
    //=================================================================================================
    
    @Override
    public void canvasClicked(int x, int y)
    {
        //TODO
    }
    
    //=================================================================================================

    private JPanel createViewPanel()
    {
        JPanel ret = new JPanel();
        ret.setLayout(new GridLayout(1, 1));
        
        structurePanel = createStructurePanel();
        propertiesPanel = createPropertiesPanel();
        
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        split.setTopComponent(structurePanel);
        split.setBottomComponent(propertiesPanel);
        ret.add(split);
        split.setDividerLocation(500);
        
        return ret;
    }

    private JPanel createStructurePanel()
    {
        GridLayout gridLayout = new GridLayout();
        gridLayout.setRows(1);
        JPanel structurePanel = new JPanel();
        structurePanel.setPreferredSize(new Dimension(200, 408));
        structurePanel.setLayout(gridLayout);
        structurePanel.add(createAreaTreePanel(), null);
        return structurePanel;
    }

    private JPanel createAreaTreePanel()
    {
        GridLayout gridLayout4 = new GridLayout();
        gridLayout4.setRows(1);
        gridLayout4.setColumns(1);
        JPanel areaTreePanel = new JPanel();
        areaTreePanel.setLayout(gridLayout4);
        
        JScrollPane areaTreeScroll = new JScrollPane();
        areaTreeScroll.setBackground(BGCOLOR);
        areaTreeScroll.setViewportView(getAreaJTree());

        areaTreePanel.add(areaTreeScroll, null);
        return areaTreePanel;
    }

    private JTree getAreaJTree()
    {
        if (areaJTree == null)
        {
            areaJTree = new JTree();
            areaJTree.setCellRenderer(new ArtifactTreeCellRenderer());
            areaJTree.setBackground(BGCOLOR);
            areaJTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener()
            {
                public void valueChanged(javax.swing.event.TreeSelectionEvent e)
                {
                    /*if (showTreeSelection)
                    {
                        Area node = (Area) areaJTree.getLastSelectedPathComponent();
                        if (node != null)
                        {
                            showArea(node);
                        }
                        browser.notifyAreaSelection(node);
                    }*/
                }
            });
            areaJTree.setModel(new AreaTreeModel(null));
        }
        return areaJTree;
    }
    
    
    //=================================================================================================
    
    private JPanel createPropertiesPanel()
    {
        GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
        gridBagConstraints10.insets = new Insets(0, 0, 5, 0);
        gridBagConstraints10.fill = GridBagConstraints.BOTH;
        gridBagConstraints10.gridy = 0;
        gridBagConstraints10.weightx = 1.0;
        gridBagConstraints10.weighty = 0.5;
        gridBagConstraints10.gridx = 0;
        JPanel objectInfoPanel = new JPanel();
        
        GridBagLayout gbl_objectInfoPanel = new GridBagLayout();
        gbl_objectInfoPanel.rowWeights = new double[]{0.0, 0.0};
        gbl_objectInfoPanel.columnWeights = new double[]{1.0};
        objectInfoPanel.setLayout(gbl_objectInfoPanel);
        
        JScrollPane objectInfoScroll = new JScrollPane();
        objectInfoScroll.setViewportView(getInfoTable());
        objectInfoPanel.add(objectInfoScroll, gridBagConstraints10);
        
        return objectInfoPanel;
    }

    private JTable getInfoTable()
    {
        if (infoTable == null)
        {
            infoTable = new JTable();
        }
        return infoTable;
    }
    
    
}
