/**
 * PageView.java
 *
 * Created on 24. 5. 2020, 18:09:14 by burgetr
 */
package cz.vutbr.fit.layout.ide.views;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.TreePath;

import org.eclipse.rdf4j.model.IRI;

import cz.vutbr.fit.layout.ide.Browser;
import cz.vutbr.fit.layout.ide.Utils;
import cz.vutbr.fit.layout.ide.api.CanvasClickListener;
import cz.vutbr.fit.layout.ide.misc.ArtifactTreeCellRenderer;
import cz.vutbr.fit.layout.ide.misc.BoxTreeModel;
import cz.vutbr.fit.layout.model.Artifact;
import cz.vutbr.fit.layout.model.Box;
import cz.vutbr.fit.layout.model.Page;
import cz.vutbr.fit.layout.ontology.BOX;

/**
 * 
 * @author burgetr
 */
public class PageView extends ArtifactViewBase implements CanvasClickListener
{
    private static final Color BGCOLOR = new Color(225, 255, 225);
    
    private JTree boxTree;
    private JTable infoTable;
    private JPanel boxTreePanel;
    private JPanel propertiesPanel;
    private JPanel viewPanel;
    
    private Page currentPage;


    public PageView(Browser browser)
    {
        super(browser);
        viewPanel = createViewPanel();
        browser.getWindow().addCanvasClickListener(null, this, false);
    }

    @Override
    public IRI getArtifactType()
    {
        return BOX.Page;
    }

    @Override
    public String getTitle()
    {
        return "Box tree";
    }

    @Override
    public JPanel getViewPanel()
    {
        return viewPanel;
    }

    @Override
    public void show(Artifact artifact)
    {
        if (artifact instanceof Page)
        {
            currentPage = (Page) artifact;
            boxTree.setModel(new BoxTreeModel(currentPage.getRoot()));
        }
    }
    
    //=================================================================================================
    
    @Override
    public void canvasClicked(int x, int y)
    {
        if (isActive())
        {
            Box node = currentPage.getBoxAt(x, y);
            if (node != null)
                showBoxInTree(node);
        }
    }

    private void showBoxInTree(Box node)
    {
        //find the path to root
        int len = 0;
        for (Box b = node; b != null; b = b.getParent())
            len++;
        Box[] path = new Box[len];
        for (Box b = node; b != null; b = b.getParent())
            path[--len] = b;
        
        TreePath select = new TreePath(path);
        boxTree.setSelectionPath(select);
        //boxTree.expandPath(select);
        boxTree.scrollPathToVisible(new TreePath(path));
    }
    
    //=================================================================================================
    
    public Box getSelectedBox()
    {
        if (boxTree == null)
            return null;
        else                   
            return (Box) boxTree.getLastSelectedPathComponent();
    }
    
    public void showBox(Box node)
    {
        //System.out.println("Node:" + node);
        browser.getWindow().getOutputDisplay().drawExtent(node);
        browser.getWindow().updateDisplay();
        displayBoxInfo(node);
    }
    
    public void displayBoxInfo(Box box)
    {
        Vector<String> cols = infoTableData("Property", "Value");
        
        Vector<Vector <String>> vals = new Vector<Vector <String>>();

        vals.add(infoTableData("Id", String.valueOf(box.getId())));
        vals.add(infoTableData("Src id", String.valueOf(box.getSourceNodeId())));
        vals.add(infoTableData("Tag", box.getTagName()));
        
        vals.add(infoTableData("Type", box.getType().toString()));
        vals.add(infoTableData("Display", box.getDisplayType() == null ? "-" : box.getDisplayType().toString()));
        vals.add(infoTableData("Visible", box.isVisible() ? "true" : "false"));
        
        vals.add(infoTableData("Bounds", box.getBounds().toString()));
        vals.add(infoTableData("C.Bounds", box.getContentBounds().toString()));
        vals.add(infoTableData("V.Bounds", box.getVisualBounds().toString()));

        vals.add(infoTableData("Color", Utils.colorString(box.getColor())));
        vals.add(infoTableData("Bg color", Utils.colorString(box.getBackgroundColor())));
        vals.add(infoTableData("Borders", Utils.borderString(box)));
        vals.add(infoTableData("Bg separated", (box.isBackgroundSeparated()) ? "true" : "false"));
        
        vals.add(infoTableData("Font", box.getFontFamily()));
        vals.add(infoTableData("Font size", String.valueOf(box.getTextStyle().getFontSize())));
        vals.add(infoTableData("Font weight", String.valueOf(box.getTextStyle().getFontWeight())));
        vals.add(infoTableData("Font style", String.valueOf(box.getTextStyle().getFontStyle())));
        vals.add(infoTableData("Underline", String.valueOf(box.getTextStyle().getUnderline())));
        vals.add(infoTableData("Line through", String.valueOf(box.getTextStyle().getLineThrough())));
        vals.add(infoTableData("Cont. length", String.valueOf(box.getTextStyle().getContentLength())));
        
        DefaultTableModel tab = new DefaultTableModel(vals, cols);
        infoTable.setModel(tab);
    }
    
    private Vector<String> infoTableData(String prop, String value)
    {
        Vector<String> cols = new Vector<String>(2);
        cols.add(prop);
        cols.add(value);
        return cols;
    }
    
    //==================================================================================
    
    private JPanel createViewPanel()
    {
        JPanel ret = new JPanel();
        ret.setLayout(new GridLayout(1, 1));
        
        boxTreePanel = createBoxTreePanel();
        propertiesPanel = createPropertiesPanel();
        
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        split.setTopComponent(boxTreePanel);
        split.setBottomComponent(propertiesPanel);
        ret.add(split);
        split.setDividerLocation(500);
        
        return ret;
    }

    private JPanel createBoxTreePanel()
    {
        GridLayout layout = new GridLayout();
        layout.setRows(1);
        JPanel boxTreePanel = new JPanel();
        boxTreePanel.setBackground(BGCOLOR);
        boxTreePanel.setLayout(layout);
        
        JScrollPane boxTreeScroll = new JScrollPane();
        boxTreeScroll.setViewportView(getBoxTree());
        boxTreeScroll.setBackground(BGCOLOR);
        boxTreePanel.add(boxTreeScroll, null);
        
        return boxTreePanel;
    }

    private JTree getBoxTree()
    {
        if (boxTree == null)
        {
            boxTree = new JTree();
            boxTree.setBackground(BGCOLOR);
            boxTree.setCellRenderer(new ArtifactTreeCellRenderer());
            boxTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener()
            {
                public void valueChanged(javax.swing.event.TreeSelectionEvent e)
                {
                    Box node = (Box) boxTree.getLastSelectedPathComponent();
                    if (node != null)
                    {
                        showBox(node);
                    }
                }
            });
        }
        return boxTree;
    }

    private JPanel createPropertiesPanel()
    {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.5;
        gbc.gridx = 0;
        JPanel objectInfoPanel = new JPanel();
        
        GridBagLayout gbl_objectInfoPanel = new GridBagLayout();
        gbl_objectInfoPanel.rowWeights = new double[]{0.0, 0.0};
        gbl_objectInfoPanel.columnWeights = new double[]{1.0};
        objectInfoPanel.setLayout(gbl_objectInfoPanel);
        
        JScrollPane objectInfoScroll = new JScrollPane();
        objectInfoScroll.setViewportView(getInfoTable());
        objectInfoPanel.add(objectInfoScroll, gbc);
        
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
