/**
 * AreaTreeView.java
 *
 * Created on 24. 5. 2020, 22:51:15 by burgetr
 */
package cz.vutbr.fit.layout.ide.views;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.TreePath;

import org.eclipse.rdf4j.model.IRI;

import cz.vutbr.fit.layout.ide.BlockBrowser;
import cz.vutbr.fit.layout.ide.Utils;
import cz.vutbr.fit.layout.ide.api.CanvasClickListener;
import cz.vutbr.fit.layout.ide.misc.AreaTreeModel;
import cz.vutbr.fit.layout.ide.misc.ArtifactTreeCellRenderer;
import cz.vutbr.fit.layout.impl.DefaultTag;
import cz.vutbr.fit.layout.model.Area;
import cz.vutbr.fit.layout.model.AreaTree;
import cz.vutbr.fit.layout.model.Artifact;
import cz.vutbr.fit.layout.model.Tag;
import cz.vutbr.fit.layout.ontology.SEGM;

/**
 * 
 * @author burgetr
 */
public class AreaTreeView extends ArtifactViewBase implements CanvasClickListener
{
    private static final Color BGCOLOR = new Color(255, 225, 225);
    public static final float TAG_PROBABILITY_THRESHOLD = 0.3f; 

    private JPanel structurePanel;
    private JPanel propertiesPanel;
    private JPanel viewPanel;
    
    private JTable infoTable;
    private JTree areaJTree;
    private JTable probTable;

    private AreaTree currentAreaTree;
    private boolean showTreeSelection = true;

    
    public AreaTreeView(BlockBrowser browser)
    {
        super(browser);
        viewPanel = createViewPanel();
        browser.addCanvasClickListener(null, this, false);
    }

    @Override
    public IRI getArtifactType()
    {
        return SEGM.AreaTree;
    }

    @Override
    public String getTitle()
    {
        return "Area tree";
    }

    @Override
    public JPanel getViewPanel()
    {
        return viewPanel;
    }

    @Override
    public void show(Artifact artifact)
    {
        if (artifact instanceof AreaTree)
        {
            currentAreaTree = (AreaTree) artifact;
            areaJTree.setModel(new AreaTreeModel(currentAreaTree.getRoot()));
        }
    }
    
    //=================================================================================================
    
    @Override
    public void canvasClicked(int x, int y)
    {
        System.out.println("Active: " + isActive());
        if (isActive())
        {
            List<Area> nodes = currentAreaTree.getAreasAt(x, y);
            if (!nodes.isEmpty())
            {
                System.out.println("All: " + nodes);
                Area node = nodes.get(nodes.size() - 1);
                if (getSelectedArea() != null)
                {
                    int i = nodes.indexOf(getSelectedArea());
                    if (i != -1) //already selected; try the previous one
                    {
                        if (i == 0)
                            i = nodes.size() - 1;
                        else
                            i = i - 1;
                        node = nodes.get(i);
                    }
                }
                System.out.println("Using: " + node);
                showAreaInTree(node);
                //showAreaInLogicalTree(node);
            }
        }
    }
    
    private void showAreaInTree(Area node)
    {
        //find the path to root
        int len = 0;
        for (Area a = node; a != null; a = a.getParent())
            len++;
        Area[] path = new Area[len];
        for (Area a = node; a != null; a = a.getParent())
            path[--len] = a;
        
        TreePath select = new TreePath(path);
        areaJTree.setSelectionPath(select);
        //areaTree.expandPath(select);
        areaJTree.scrollPathToVisible(new TreePath(path));
    }
    
    //=================================================================================================
    
    public boolean isShowTreeSelection()
    {
        return showTreeSelection;
    }

    /**
     * Set whether to show the area selected in the tree in the displayed page.
     * 
     * @param showTreeSelection
     */
    public void setShowTreeSelection(boolean showTreeSelection)
    {
        this.showTreeSelection = showTreeSelection;
    }
    
    public Area getSelectedArea()
    {
        if (areaJTree == null)
            return null;
        else                   
            return (Area) areaJTree.getLastSelectedPathComponent();
    }
    
    private void showArea(Area area)
    {
        browser.getOutputDisplay().drawExtent(area);
        browser.updateDisplay();
        
        //show the info table
        displayAreaInfo(area);
    }

    public void displayAreaInfo(Area area)
    {
        Vector<String> cols = infoTableData("Property", "Value");
        
        Vector<Vector <String>> vals = new Vector<Vector <String>>();
        //vals.add(infoTableData("Layout", area.getLayoutType().toString()));
        if (area.getParent() == null)
            vals.add(infoTableData("GP", "---"));
        else
            vals.add(infoTableData("GP", area.getParent().getTopology().getPosition(area).toString()));
        vals.add(infoTableData("Tags", tagProbabilityString(area.getTags())));
        //if (proc.getVisualClassifier() != null)
        //    vals.add(infoTableData("V. class", proc.getVisualClassifier().classifyArea(area)));
        //vals.add(infoTableData("Style probs", tagProbabilityString(proc.getMsa() != null ? proc.getMsa().classifyNode(area) : null)));
        //vals.add(infoTableData("Total probs", tagProbabilityString(proc.getTagPredictor() != null ? proc.getTagPredictor().getTagProbabilities(area) : null)));
        //vals.add(infoTableData("Importance", String.valueOf(area.getImportance())));
        //vals.add(infoTableData("Separated", (area.isSeparated()) ? "true" : "false"));
        //vals.add(infoTableData("Atomic", (area.isAtomic()) ? "true" : "false"));
        //vals.add(infoTableData("Indent scale", area.getTopology().getMinIndent() + " - " + area.getTopology().getMaxIndent()));
        //vals.add(infoTableData("Indent value", String.valueOf(proc.getFeatures().getIndentation(area))));
        //vals.add(infoTableData("Centered", (area.isCentered()) ? "true" : "false"));
        //vals.add(infoTableData("Coherent", (area.isCoherent()) ? "true" : "false"));
        //vals.add(infoTableData("Parent perc.", String.valueOf(area.getParentPercentage())));
        
        //vals.add(infoTableData("Name", area.getName()));
        vals.add(infoTableData("Bounds", area.getBounds().toString()));
        //vals.add(infoTableData("Content", (a.getContentBounds() == null) ? "" : a.getContentBounds().toString()));
        //vals.add(infoTableData("Level", String.valueOf(a.getLevel())));
        vals.add(infoTableData("Borders", Utils.borderString(area)));
        vals.add(infoTableData("Bg separated", (area.isBackgroundSeparated()) ? "true" : "false"));
        vals.add(infoTableData("Is hor. sep.", (area.isHorizontalSeparator()) ? "true" : "false"));
        vals.add(infoTableData("Is vert. sep.", (area.isVerticalSeparator()) ? "true" : "false"));
        vals.add(infoTableData("Avg. fsize", String.valueOf(area.getFontSize())));
        vals.add(infoTableData("Avg. fweight", String.valueOf(area.getFontWeight())));
        vals.add(infoTableData("Avg. fstyle", String.valueOf(area.getFontStyle())));
        //vals.add(infoTableData("Decl. fsize", String.valueOf(area.getDeclaredFontSize())));
        //vals.add(infoTableData("Luminosity", String.valueOf(area.getColorLuminosity())));
        //vals.add(infoTableData("Start color", colorString(a.getBoxes().firstElement().getStartColor())));
        if (area.getBoxes().size() > 0)
            vals.add(infoTableData("First box clr", Utils.colorString(area.getBoxes().get(0).getColor())));
        vals.add(infoTableData("Bg color", Utils.colorString(area.getBackgroundColor())));
        vals.add(infoTableData("Efficient bg", Utils.colorString(area.getEffectiveBackgroundColor())));
        
        //vals.add(infoTableData("Fg color", colorString(area.getBoxes().firstElement().getColor())));
        
        //markednessText.setText(String.format("%.2f", proc.getFeatures().getMarkedness(area)));

        //classification result
        displayProbabilityTable(area);
        
        /*Vector<Vector <String>> fvals = new Vector<Vector <String>>();
        FeatureVector f = proc.getFeatures().getFeatureVector(area);
        if (f != null)
        {
            Method[] methods = f.getClass().getMethods();
            for (Method m : methods)
            {
                try
                {
                    if (m.getName().startsWith("get") && !m.equals("getClass"))
                    {
                        Object ret = m.invoke(f, (Object []) null);
                        if (ret != null)
                            fvals.add(infoTableData(m.getName().substring(3), ret.toString()));
                    }
                    if (m.getName().startsWith("is"))
                    {
                        Object ret = m.invoke(f, (Object []) null);
                        if (ret != null)
                            fvals.add(infoTableData(m.getName().substring(2), ret.toString()));
                    }
                } catch (Exception e) {}
            }
        }*/
        
        DefaultTableModel tab = new DefaultTableModel(vals, cols);
        infoTable.setModel(tab);
        //DefaultTableModel ftab = new DefaultTableModel(fvals, cols);
        //featureTable.setModel(ftab);
    }

    private void displayProbabilityTable(Area area)
    {
        //TODO?
        /*Vector<String> cols = new Vector<String>(browser.getTagNames());
        Collections.sort(cols);
        cols.insertElementAt("Type", 0);
        Vector<String> types = new Vector<String>(browser.getTagTypes());
        Collections.sort(types);

        Vector<Vector <String>> lines = new Vector<Vector <String>>(types.size());
        for (String type : types)
            lines.add(getProbTableLine(type, cols, area.getTags()));
        probTable.setModel(new DefaultTableModel(lines, cols));*/
    }
    
    private Vector<String> getProbTableLine(String type, List<String> names, Map<Tag, Float> data)
    {
        Vector<String> ret = new Vector<String>();
        boolean first = true;
        for (String name : names)
        {
            if (first)
            {
                ret.add(type);
                first = false;
            }
            else
            {
                Tag search = new DefaultTag(type, name);
                if (data.containsKey(search))
                    ret.add(String.format("%1.2f", data.get(search)));
                else
                    ret.add("");
            }
        }
        return ret;
    }
    
    private Vector<String> infoTableData(String prop, String value)
    {
        Vector<String> cols = new Vector<String>(2);
        cols.add(prop);
        cols.add(value);
        return cols;
    }
    
    private String tagProbabilityString(Map<Tag, Float> map)
    {
        String ret = "";
        if (map != null)
        {
            for (Map.Entry<Tag, Float> entry : map.entrySet())
            {
                if (entry.getValue() > TAG_PROBABILITY_THRESHOLD)
                    ret += entry.getKey() + " (" + String.format("%1.2f", entry.getValue()) + ") "; 
            }
        }
        return ret;
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
                    if (showTreeSelection)
                    {
                        Area node = (Area) areaJTree.getLastSelectedPathComponent();
                        if (node != null)
                        {
                            showArea(node);
                            /*areasync = false;
                            showAreaInLogicalTree(node);
                            areasync = true;*/
                        }
                        browser.notifyAreaSelection(node);
                    }
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
        
        GridBagConstraints gbc_probabilityScroll = new GridBagConstraints();
        gbc_probabilityScroll.weighty = 0.25;
        gbc_probabilityScroll.weightx = 1.0;
        gbc_probabilityScroll.insets = new Insets(0, 0, 5, 0);
        gbc_probabilityScroll.fill = GridBagConstraints.BOTH;
        gbc_probabilityScroll.gridx = 0;
        gbc_probabilityScroll.gridy = 1;
        JScrollPane probabilityScroll = new JScrollPane();
        probabilityScroll.setViewportView(getProbTable());
        objectInfoPanel.add(probabilityScroll, gbc_probabilityScroll);
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
    
    private JTable getProbTable()
    {
        if (probTable == null)
        {
            probTable = new JTable();
            probTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer()
            {
                private static final long serialVersionUID = 1L;
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
                {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    try {
                        Double d = Double.parseDouble(value.toString().replace(',', '.'));
                        if (d <= TAG_PROBABILITY_THRESHOLD)
                            c.setForeground(new java.awt.Color(180, 180, 180));
                        else
                            c.setForeground(java.awt.Color.BLACK);
                    } catch (NumberFormatException e) {
                        c.setForeground(java.awt.Color.BLACK);
                    }
                    return c;
                }
            });
        }
        return probTable;
    }

    
}
