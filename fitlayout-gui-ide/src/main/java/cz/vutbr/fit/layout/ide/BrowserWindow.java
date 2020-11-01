/**
 * BrowserWindow.java
 *
 * Created on 11. 10. 2020, 11:45:46 by burgetr
 */
package cz.vutbr.fit.layout.ide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.rdf4j.model.IRI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.vutbr.fit.layout.api.OutputDisplay;
import cz.vutbr.fit.layout.ide.api.AreaSelectionListener;
import cz.vutbr.fit.layout.ide.api.CanvasClickListener;
import cz.vutbr.fit.layout.ide.api.RectangleSelectionListener;
import cz.vutbr.fit.layout.ide.api.TreeListener;
import cz.vutbr.fit.layout.ide.misc.ArtifactTreeCellRenderer;
import cz.vutbr.fit.layout.ide.misc.ArtifactTreeModel;
import cz.vutbr.fit.layout.ide.tabs.BrowserPanel;
import cz.vutbr.fit.layout.ide.tabs.BrowserTab;
import cz.vutbr.fit.layout.ide.tabs.BrowserTabState;
import cz.vutbr.fit.layout.ide.views.ArtifactView;
import cz.vutbr.fit.layout.model.Area;
import cz.vutbr.fit.layout.model.Artifact;
import cz.vutbr.fit.layout.model.Box;
import cz.vutbr.fit.layout.model.Page;
import cz.vutbr.fit.layout.model.Rectangular;

import javax.swing.JSplitPane;
import javax.swing.JToolBar;

import java.awt.GridBagConstraints;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.GridBagLayout;

import javax.swing.JToggleButton;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.FlowLayout;
import java.awt.Graphics;

import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;

import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;

/**
 * @author burgetr
 *
 */
public class BrowserWindow
{
    private static Logger log = LoggerFactory.getLogger(BrowserWindow.class);
    
    public static final float TAG_PROBABILITY_THRESHOLD = 0.3f; 
    private static final java.awt.Color selectionColor = new java.awt.Color(127, 127, 255, 127);
    
    private Browser browser;
    private ArtifactTreeModel artifactTreeModel;
    
    private Page currentPage; //currently displayed page
    private boolean rectSelection = false; //rectangle area selection in progress
    private int rectX1, rectY1; //rectangle selection start point
    private Selection selection; //selection box
    
    private List<AreaSelectionListener> areaListeners;
    private List<TreeListener> treeListeners;
    private List<RectangleSelectionListener> rectangleListeners;
    private List<CanvasClickListener> canvasClickAlwaysListeners;
    private Map<JToggleButton, CanvasClickListener> canvasClickToggleListeners;
    
    //main tabs
    private List<BrowserTabState> browserTabs;
    
    //artifact tree
    private DefaultMutableTreeNode artifactTreeRoot;
    
    //artifact views
    private Map<IRI, List<ArtifactView>> artifactViews; //registered views
    private List<ArtifactView> currentArtifactViews; //currently open views

    //basic UI components
    private JFrame mainWindow = null;  //  @jve:decl-index=0:visual-constraint="-239,28"
    private JPanel container = null;
    private JPanel mainPanel = null;
    private JPanel contentPanel = null;
    private JPanel statusPanel = null;
    private JTextField statusText = null;
    private JTabbedPane sidebarPane = null;
    private JScrollPane contentScroll = null;
    private JPanel contentCanvas = null;
    private JSplitPane mainSplitter = null;
    private JToolBar showToolBar = null;
    private JButton redrawButton = null;
    private JButton showBoxButton = null;
    private JButton showAreaButton = null;
    private JToolBar lookupToolBar = null;
    private JPanel toolPanel = null;
    private JButton refreshButton = null;
    private JSplitPane infoSplitter = null;
    private JButton showArtAreaButton = null;
    private JButton showColumnsButton = null;
    private JTabbedPane toolTabs = null;
    private List<JCheckBoxMenuItem> tabViewItems;
    private JPanel artifactTreePanel;
    private JScrollPane artifactTreeScroll;
    private JTree artifactTree;
    
    //menu
    private JMenuBar menuBar;
    private JMenu mnFile;
    private JMenuItem mntmQuit;
    private JMenu mnView;
    private JTabbedPane artifactViewTabs;
    private JPanel artifactToolsPanel;
    private JButton btnDeleteArtifact;


    public BrowserWindow(Browser browser)
    {
        this.browser = browser;
        areaListeners = new LinkedList<>();
        treeListeners = new LinkedList<>();
        rectangleListeners = new LinkedList<>();
        canvasClickAlwaysListeners = new LinkedList<>();
        canvasClickToggleListeners = new HashMap<>();
        browserTabs = new LinkedList<>();
        tabViewItems = new LinkedList<>();
        artifactViews = new HashMap<>();
    }
    
    public void init()
    {
        setArtifactTreeModel(new ArtifactTreeModel(browser.getRepository()));
    }
    
    //=============================================================================================================
    
    public void displayErrorMessage(String text)
    {
        JOptionPane.showMessageDialog(mainWindow,
                text,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public void displayInfoMessage(String text)
    {
        JOptionPane.showMessageDialog(mainWindow,
                text,
                "Info",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void addToolBar(JToolBar toolbar)
    {
        toolPanel.add(toolbar);
        toolPanel.updateUI();
    }

    public void addToolPanel(String title, JComponent component)
    {
        toolTabs.addTab(title, component);
    }

    public void addStructurePanel(String title, JComponent component)
    {
        sidebarPane.addTab(title, component);
    }

    public OutputDisplay getOutputDisplay()
    {
        return ((BrowserPanel) contentCanvas).getOutputDisplay();
    }

    public void updateDisplay()
    {
        contentCanvas.repaint();
    }

    public void redrawPage()
    {
        if (contentCanvas != null && contentCanvas instanceof BrowserPanel)
            ((BrowserPanel) contentCanvas).redrawPage();
    }

    public void addAreaSelectionListener(AreaSelectionListener listener)
    {
        areaListeners.add(listener);
    }
    
    public void addTreeListener(TreeListener listener)
    {
        treeListeners.add(listener);
    }
    
    public void addCanvasClickListener(String toggleButtonTitle, CanvasClickListener listener, boolean select)
    {
        if (toggleButtonTitle == null)
            canvasClickAlwaysListeners.add(listener);
        else
        {
            JToggleButton button = createClickToggleButton(toggleButtonTitle);
            if (select)
                button.setSelected(true); //select the first button
            canvasClickToggleListeners.put(button, listener);
            getLookupToolBar().add(button);
        }
    }
    
    public void addRectangleSelectionListener(RectangleSelectionListener listener)
    {
        rectangleListeners.add(listener);
    }

    public void removeRectangleSelectionListener(RectangleSelectionListener listener)
    {
        rectangleListeners.remove(listener);
    }

    public ArtifactTreeModel getArtifactTreeModel()
    {
        return artifactTreeModel;
    }
    
    public void setArtifactTreeModel(ArtifactTreeModel model)
    {
        artifactTreeModel = model; 
        getArtifactTree().setModel(model);
    }
    
    public void updateArtifactTree()
    {
        artifactTreeModel.updateArtifactTree();
    }
    
    public void addArtifactView(ArtifactView view)
    {
        List<ArtifactView> list = artifactViews.get(view.getArtifactType());
        if (list == null)
        {
            list = new ArrayList<>();
            artifactViews.put(view.getArtifactType(), list);
        }
        list.add(view);
    }
    
    //================================================================================================================================
    
    public void addTab(BrowserTab tab, boolean optional, boolean visible)
    {
        BrowserTabState state = new BrowserTabState(tab, optional);
        state.setVisible(visible);
        browserTabs.add(state);
        if (optional)
        {
            var item = createTabViewItem(state);
            tabViewItems.add(item);
            mnView.add(item);
        }
        if (visible)
            getToolTabs().addTab(tab.getTitle(), null, tab.getTabPanel(), null);
    }
    
    private JCheckBoxMenuItem createTabViewItem(BrowserTabState tabState)
    {
        String title = tabState.getBrowserTab().getTitle();
        var item = new JCheckBoxMenuItem(title);
        item.setSelected(tabState.isVisible());
        item.addItemListener(new ItemListener()
        {
            @Override
            public void itemStateChanged(ItemEvent e)
            {
                if (e.getStateChange() == ItemEvent.SELECTED)
                {
                    getToolTabs().addTab(tabState.getBrowserTab().getTitle(), null, tabState.getBrowserTab().getTabPanel(), null);
                }
                else
                {
                    int i = getToolTabs().indexOfTab(title);
                    if (i != -1)
                        getToolTabs().remove(i);
                }
            }
        });
        return item;
    }
    
    private void tabSelected(int index)
    {
        BrowserTab tab = browserTabs.get(index).getBrowserTab();
        if (tab != null)
        {
            /*int mpos = getMainSplitter().getDividerLocation();
            int ipos = getInfoSplitter().getDividerLocation();
            getMainSplitter().setLeftComponent(tab.getStructurePanel());
            getInfoSplitter().setRightComponent(tab.getPropertiesPanel());
            getMainSplitter().setDividerLocation(mpos);
            getInfoSplitter().setDividerLocation(ipos);*/
        }
        for (int i = 0; i < browserTabs.size(); i++)
            browserTabs.get(i).getBrowserTab().setActive(i == index);
    }
    
    private void viewTabSelected(int index)
    {
        if (currentArtifactViews != null)
        {
            for (int i = 0; i < currentArtifactViews.size(); i++)
                currentArtifactViews.get(i).setActive(i == index);
        }
    }
    
    //=============================================================================================================
    
    /**
     * Selects the given artifact in the artifact tree.
     * @param a the artifact to select
     */
    public void selectArtifact(Artifact a)
    {
        ArtifactTreeModel model = (ArtifactTreeModel) artifactTree.getModel();
        DefaultMutableTreeNode node = model.getNodeForArtifact(a);
        if (node != null)
            artifactTree.setSelectionPath(new TreePath(node.getPath()));
    }
    
    /**
     * Gets the artifact that is currently selected in the artifact tree.
     * @return the selected artifact or {@code null} when nothing is selected.
     */
    public Artifact getSelectedArtifact()
    {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) artifactTree.getLastSelectedPathComponent();
        if (node != null && node.getUserObject() != null)
            return (Artifact) node.getUserObject();
        else
            return null;
    }

    /**
     * Finds the Page artifact related to the currently selected artifact.
     * @return the Page corresponding to the currently selected artifact or {@code null} when
     * nothing is selected or no such page exists.
     */
    private Page getSelectedPage()
    {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) artifactTree.getLastSelectedPathComponent();
        while (node != null && node.getUserObject() != null)
        {
            if (node.getUserObject() instanceof Page)
                return (Page) node.getUserObject();
            else
                node = (DefaultMutableTreeNode) node.getParent();
        }
        return null;
    }
    
    private void setCurrentPage(Page page) 
    {
        contentCanvas = createContentCanvas(page);
        
        contentCanvas.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e)
            {
                System.out.println("Click: " + e.getX() + ":" + e.getY());
                canvasClick(e.getX(), e.getY());
            }
            public void mousePressed(MouseEvent e) 
            { 
                canvasPress(e.getX(), e.getY());
            }
            public void mouseReleased(MouseEvent e)
            {
                canvasRelease(e.getX(), e.getY());
            }
            public void mouseEntered(MouseEvent e) { }
            public void mouseExited(MouseEvent e) 
            {
                statusText.setText("");
            }
        });
        contentCanvas.addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent e)
            { 
                canvasDrag(e.getX(), e.getY());
            }
            public void mouseMoved(MouseEvent e) 
            { 
                String s = "Absolute: " + e.getX() + ":" + e.getY();
                //Area node = segmentationTab.getSelectedArea();
                Area node = null; //TODO
                if (node != null)
                {
                    Area area = (Area) node;
                    int rx = e.getX() - area.getX1();
                    int ry = e.getY() - area.getY1();
                    s += "  Relative: " + rx + ":" + ry;
                }
                statusText.setText(s);
                canvasMove(e.getX(), e.getY());
            }
        });
        contentScroll.setViewportView(contentCanvas);
        
        notifyBoxTreeUpdate(page);
    }
    
    private void artifactSelected(Artifact a)
    {
        //change current page if changed
        if (getSelectedPage() != currentPage && getSelectedPage() != null)
            setCurrentPage(getSelectedPage());
        //show in the tab view
        artifactViewTabs.removeAll();
        List<ArtifactView> views = artifactViews.get(a.getArtifactType());
        if (views != null)
        {
            currentArtifactViews = views;
            for (ArtifactView view : views)
            {
                artifactViewTabs.add(view.getTitle(), view.getViewPanel());
                view.show(a);
            }
        }
    }
    
    //=============================================================================================================
    
    /** Creates the appropriate canvas based on the file type */
    private JPanel createContentCanvas(Page page)
    {
        if (contentCanvas != null)
        {
            contentCanvas = new BrowserPanel(page);
            contentCanvas.setLayout(null);
            selection = new Selection();
            contentCanvas.add(selection);
            selection.setVisible(false);
            selection.setLocation(0, 0);
        }
        return contentCanvas;
    }
    
    private JToggleButton createClickToggleButton(String label)
    {
        JToggleButton button = new JToggleButton(label);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (button.isSelected())
                {
                    for (JToggleButton other : canvasClickToggleListeners.keySet())
                    {
                        if (other != button)
                            other.setSelected(false);
                    }
                }
            }
        });
        //button.setText(label);
        button.setToolTipText("Show " + label.toLowerCase() + " when the canvas is clicked");
        return button;
    }
    
    /** This is called when the browser canvas is clicked */
    private void canvasClick(int x, int y)
    {
        //always called listeners
        for (CanvasClickListener listener : canvasClickAlwaysListeners)
            listener.canvasClicked(x, y);
        //selected listener by toggle buttons
        for (JToggleButton button : canvasClickToggleListeners.keySet())
        {
            if (button.isSelected())
                canvasClickToggleListeners.get(button).canvasClicked(x, y);
        }
    }
    
    private void canvasPress(int x, int y)
    {
        selection.setVisible(false);
        if (!rectangleListeners.isEmpty())
        {
            rectSelection = true;
            rectX1 = x;
            rectY1 = y;
            selection.setLocation(x, y);
            selection.setSize(0, 0);
            selection.setVisible(true);
        }
    }
    
    private void canvasRelease(int x, int y)
    {
        if (rectSelection)
        {
            rectSelection = false;
            Rectangular rect = new Rectangular(rectX1, rectY1, x, y);
            for (RectangleSelectionListener listener : rectangleListeners)
                listener.rectangleCreated(rect);
        }
    }
    
    private void canvasMove(int x, int y)
    {
    }
    
    private void canvasDrag(int x, int y)
    {
        if (rectSelection)
        {
            int x1 = Math.min(x, rectX1);
            int y1 = Math.min(y, rectY1);
            int x2 = Math.max(x, rectX1);
            int y2 = Math.max(y, rectY1);
            selection.setLocation(x1, y1);
            selection.setSize(x2 - x1, y2 - y1);
            updateDisplay();
        }
    }
    
    public void setSelection(Rectangular rect)
    {
        selection.setLocation(rect.getX1(), rect.getY1());
        selection.setSize(rect.getWidth(), rect.getHeight());
        selection.setVisible(true);
    }

    public void clearSelection()
    {
        selection.setSize(0, 0);
        selection.setVisible(false);
    }
    
    public void showAllBoxes(Box root)
    {
        getOutputDisplay().drawExtent(root);
        for (int i = 0; i < root.getChildCount(); i++)
            showAllBoxes(root.getChildAt(i));
    }
    
    public void showAreas(Area root, String name)
    {
        if (name == null || root.toString().contains(name))
            getOutputDisplay().drawExtent(root);
        for (int i = 0; i < root.getChildCount(); i++)
            showAreas(root.getChildAt(i), name);
    }
    
    public void notifyAreaSelection(Area area)
    {
        //notify area listeners
        for (AreaSelectionListener listener : areaListeners)
            listener.areaSelected(area);
    }
    
    private void notifyBoxTreeUpdate(Page page)
    {
        for (TreeListener listener : treeListeners)
            listener.pageRendered(page);
    }
    
    
    //===========================================================================
    
    /**
     * This method initializes jFrame   
     *  
     * @return javax.swing.JFrame   
     * @wbp.parser.entryPoint
     */
    public JFrame getMainWindow()
    {
        if (mainWindow == null)
        {
            mainWindow = new JFrame();
            mainWindow.setTitle("FITLayout GUI");
            mainWindow.setVisible(true);
            mainWindow.setBounds(new Rectangle(0, 0, 1489, 256));
            mainWindow.setMinimumSize(new Dimension(1200, 256));
            mainWindow.setJMenuBar(getMenuBar());
            mainWindow.setContentPane(getContainer());
            mainWindow.addWindowListener(new java.awt.event.WindowAdapter()
            {
                public void windowClosing(java.awt.event.WindowEvent e)
                {
                    mainWindow.setVisible(false);
                    System.exit(0);
                }
            });
        }
        return mainWindow;
    }

    private JPanel getContainer()
    {
        if (container == null)
        {
            container = new JPanel();
            container.setLayout(new BorderLayout());
            container.add(getToolPanel(), BorderLayout.NORTH);
            container.add(getMainPanel(), BorderLayout.CENTER);
        }
        return container;
    }
    
    private JPanel getMainPanel()
    {
        if (mainPanel == null)
        {
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridy = -1;
            gridBagConstraints2.anchor = GridBagConstraints.WEST;
            gridBagConstraints2.gridx = -1;
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.weighty = 1.0;
            gridBagConstraints11.insets = new Insets(0, 0, 5, 0);
            gridBagConstraints11.gridy = 1;
            gridBagConstraints11.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints11.gridx = 0;
            gridBagConstraints11.weightx = 1.0;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.insets = new Insets(0, 0, 5, 0);
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.weightx = 1.0;
            gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.gridwidth = 1;
            gridBagConstraints3.gridy = 2;
            mainPanel = new JPanel();
            GridBagLayout gbl_mainPanel = new GridBagLayout();
            gbl_mainPanel.rowWeights = new double[]{0.0, 0.0, 0.0};
            gbl_mainPanel.columnWeights = new double[]{1.0};
            mainPanel.setLayout(gbl_mainPanel);
            GridBagConstraints gbc_toolTabs = new GridBagConstraints();
            gbc_toolTabs.fill = GridBagConstraints.HORIZONTAL;
            gbc_toolTabs.weightx = 1.0;
            gbc_toolTabs.insets = new Insets(0, 0, 5, 0);
            gbc_toolTabs.gridx = 0;
            gbc_toolTabs.gridy = 0;
            mainPanel.add(getToolTabs(), gbc_toolTabs);
            mainPanel.add(getMainSplitter(), gridBagConstraints11);
            mainPanel.add(getStatusPanel(), gridBagConstraints3);
        }
        return mainPanel;
    }

    /**
     * This method initializes jPanel   
     *  
     * @return javax.swing.JPanel   
     */
    private JPanel getContentPanel()
    {
        if (contentPanel == null)
        {
            GridLayout gridLayout1 = new GridLayout();
            gridLayout1.setRows(1);
            contentPanel = new JPanel();
            contentPanel.setLayout(gridLayout1);
            contentPanel.add(getContentScroll(), null);
        }
        return contentPanel;
    }

    /**
     * This method initializes jPanel2  
     *  
     * @return javax.swing.JPanel   
     */
    private JPanel getStatusPanel()
    {
        if (statusPanel == null)
        {
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.weightx = 1.0;
            gridBagConstraints4.insets = new java.awt.Insets(0,7,0,0);
            gridBagConstraints4.gridy = 2;
            statusPanel = new JPanel();
            statusPanel.setLayout(new GridBagLayout());
            statusPanel.add(getStatusText(), gridBagConstraints4);
        }
        return statusPanel;
    }

    /**
     * This method initializes jTextField   
     *  
     * @return javax.swing.JTextField   
     */
    private JTextField getStatusText()
    {
        if (statusText == null)
        {
            statusText = new JTextField();
            statusText.setEditable(false);
            statusText.setText("Browser ready.");
        }
        return statusText;
    }

    /**
     * This method initializes jScrollPane  
     *  
     * @return javax.swing.JScrollPane  
     */
    private JScrollPane getContentScroll()
    {
        if (contentScroll == null)
        {
            contentScroll = new JScrollPane();
            contentScroll.setViewportView(getContentCanvas());
            contentScroll.getVerticalScrollBar().setUnitIncrement(10);
            contentScroll.addComponentListener(new java.awt.event.ComponentAdapter()
            {
                /*public void componentResized(java.awt.event.ComponentEvent e)
                {
                    if (contentCanvas != null && contentCanvas instanceof BrowserCanvas)
                    {
                        ((BrowserCanvas) contentCanvas).createLayout(contentScroll.getSize());
                        contentScroll.repaint();
                        BoxTree btree = new BoxTree(((BrowserCanvas) contentCanvas).getViewport());
                        boxTree.setModel(new DefaultTreeModel(btree.getRoot()));
                    }
                }*/
            });
        }
        return contentScroll;
    }

    /**
     * This method initializes jPanel   
     *  
     * @return javax.swing.JPanel   
     */
    private JPanel getContentCanvas()
    {
        if (contentCanvas == null)
        {
            contentCanvas = new JPanel();
            //contentCanvas.add(getInfoSplitter(), null);
        }
        return contentCanvas;
    }
    
    /**
     * This method initializes jSplitPane   
     *  
     * @return javax.swing.JSplitPane   
     */
    private JSplitPane getMainSplitter()
    {
        if (mainSplitter == null)
        {
            mainSplitter = new JSplitPane();
            mainSplitter.setDividerLocation(250);
            mainSplitter.setLeftComponent(getArtifactTreePanel());
            mainSplitter.setRightComponent(getInfoSplitter());
        }
        return mainSplitter;
    }

    /**
     * This method initializes jToolBar 
     *  
     * @return javax.swing.JToolBar 
     */
    private JToolBar getShowToolBar()
    {
        if (showToolBar == null)
        {
            showToolBar = new JToolBar();
            showToolBar.add(getRedrawButton());
            showToolBar.add(getRefreshButton());
            showToolBar.add(getShowBoxButton());
            showToolBar.add(getShowAreaButton());
            showToolBar.add(getShowArtAreaButton());
            showToolBar.add(getShowColumnsButton());
        }
        return showToolBar;
    }
    
    
    /**
     * This method initializes jButton  
     *  
     * @return javax.swing.JButton  
     */
    private JButton getRedrawButton()
    {
        if (redrawButton == null)
        {
            redrawButton = new JButton();
            redrawButton.setText("Clear");
            redrawButton.setMnemonic(KeyEvent.VK_UNDEFINED);
            redrawButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    redrawPage();
                    updateDisplay();
                }
            });
        }
        return redrawButton;
    }

    /**
     * This method initializes showBoxButton    
     *  
     * @return javax.swing.JButton  
     */
    private JButton getShowBoxButton()
    {
        if (showBoxButton == null)
        {
            showBoxButton = new JButton();
            showBoxButton.setText("Show boxes");
            showBoxButton.setToolTipText("Show all boxes in the selected tree");
            showBoxButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    /*Box node = boxTreeTab.getSelectedBox();
                    if (node != null)
                    {
                        showAllBoxes(node);
                        contentCanvas.repaint();
                    }*/
                }
            });
        }
        return showBoxButton;
    }

    /**
     * This method initializes showAreaButton   
     *  
     * @return javax.swing.JButton  
     */
    private JButton getShowAreaButton()
    {
        if (showAreaButton == null)
        {
            showAreaButton = new JButton();
            showAreaButton.setText("Show areas");
            showAreaButton.setToolTipText("Show all the areas in the selected area");
            showAreaButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    /*Area node = segmentationTab.getSelectedArea();
                    if (node != null)
                    {
                        showAreas(node, null);
                        contentCanvas.repaint();
                    }*/
                }
            });
        }
        return showAreaButton;
    }

    /**
     * This method initializes lookupToolBar    
     *  
     * @return javax.swing.JToolBar 
     */
    private JToolBar getLookupToolBar()
    {
        if (lookupToolBar == null)
        {
            lookupToolBar = new JToolBar();
            //the default buttons are added in initGUI()
        }
        return lookupToolBar;
    }

    /**
     * This method initializes jPanel   
     *  
     * @return javax.swing.JPanel   
     */
    private JPanel getToolPanel()
    {
        if (toolPanel == null)
        {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setAlignment(java.awt.FlowLayout.LEFT);
            toolPanel = new JPanel();
            //toolPanel.setLayout(new WrappingLayout(WrappingLayout.LEFT, 1, 1));
            toolPanel.setLayout(new ToolbarLayout());
            toolPanel.add(getShowToolBar());
            toolPanel.add(getLookupToolBar());
        }
        return toolPanel;
    }


    /**
     * This method initializes refreshButton    
     *  
     * @return javax.swing.JButton  
     */
    private JButton getRefreshButton()
    {
        if (refreshButton == null)
        {
            refreshButton = new JButton();
            refreshButton.setText("Refresh");
            refreshButton.setToolTipText("Refresh the tree views");
            refreshButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                }
            });
        }
        return refreshButton;
    }

    /**
     * This method initializes infoSplitter 
     *  
     * @return javax.swing.JSplitPane   
     */
    private JSplitPane getInfoSplitter()
    {
        if (infoSplitter == null)
        {
            infoSplitter = new JSplitPane();
            infoSplitter.setResizeWeight(1.0);
            infoSplitter.setDividerLocation(250);
            JPanel artifactViewPanel = new JPanel();
            infoSplitter.setLeftComponent(artifactViewPanel);
            artifactViewPanel.setLayout(new GridLayout(1, 1, 0, 0));
            artifactViewPanel.add(getArtifactViewTabs());
            infoSplitter.setRightComponent(getContentPanel());
        }
        return infoSplitter;
    }

    /**
     * This method initializes showArtAreaButton    
     *  
     * @return javax.swing.JButton  
     */
    private JButton getShowArtAreaButton()
    {
        if (showArtAreaButton == null)
        {
            showArtAreaButton = new JButton();
            showArtAreaButton.setText("Art. areas");
            showArtAreaButton.setToolTipText("Show artificial areas marked with <area>");       
            showArtAreaButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    /*Area node = segmentationTab.getSelectedArea();
                    if (node != null)
                    {
                        showAreas(node, "<area");
                        contentCanvas.repaint();
                    }*/
                }
            });
        }
        return showArtAreaButton;
    }

    /**
     * This method initializes showColumnsButton    
     *  
     * @return javax.swing.JButton  
     */
    private JButton getShowColumnsButton()
    {
        if (showColumnsButton == null)
        {
          showColumnsButton = new JButton();
          showColumnsButton.setText("Chunks");
          showColumnsButton.setToolTipText("Show chunk areas marked with <chunk:*>");
          showColumnsButton.addActionListener(new java.awt.event.ActionListener()
          {
              public void actionPerformed(java.awt.event.ActionEvent e)
              {
                  /*Area node = segmentationTab.getSelectedArea();
                  if (node != null)
                  {
                      showAreas(node, "<chunk");
                      contentCanvas.repaint();
                  }*/
              }
          });
        }
        return showColumnsButton;
    }

    private JTabbedPane getToolTabs()
    {
        if (toolTabs == null)
        {
            toolTabs = new JTabbedPane(JTabbedPane.TOP);
            toolTabs.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    tabSelected(toolTabs.getSelectedIndex());
                }
            });
        }
        return toolTabs;
    }

    private JPanel getArtifactTreePanel()
    {
        if (artifactTreePanel == null)
        {
            artifactTreePanel = new JPanel();
            GridBagLayout gbl_artifactTreePanel = new GridBagLayout();
            artifactTreePanel.setLayout(gbl_artifactTreePanel);
            GridBagConstraints gbc_artifactToolsPanel = new GridBagConstraints();
            gbc_artifactToolsPanel.fill = GridBagConstraints.BOTH;
            gbc_artifactToolsPanel.gridx = 0;
            gbc_artifactToolsPanel.gridy = 0;
            artifactTreePanel.add(getArtifactToolsPanel(), gbc_artifactToolsPanel);
            GridBagConstraints gbc_artifactTreeScroll = new GridBagConstraints();
            gbc_artifactTreeScroll.insets = new Insets(0, 2, 0, 2);
            gbc_artifactTreeScroll.weightx = 1.0;
            gbc_artifactTreeScroll.weighty = 1.0;
            gbc_artifactTreeScroll.fill = GridBagConstraints.BOTH;
            gbc_artifactTreeScroll.gridx = 0;
            gbc_artifactTreeScroll.gridy = 1;
            artifactTreePanel.add(getArtifactTreeScroll(), gbc_artifactTreeScroll);
        }
        return artifactTreePanel;
    }

    private JScrollPane getArtifactTreeScroll()
    {
        if (artifactTreeScroll == null)
        {
            artifactTreeScroll = new JScrollPane();
            artifactTreeScroll.setViewportView(getArtifactTree());
        }
        return artifactTreeScroll;
    }

    private JTree getArtifactTree()
    {
        if (artifactTree == null)
        {
            artifactTreeRoot = new DefaultMutableTreeNode("Pages");
            artifactTree = new JTree(artifactTreeRoot);
            artifactTree.setCellRenderer(new ArtifactTreeCellRenderer(browser.getRepository().getIriDecoder()));
            artifactTree.addTreeSelectionListener(new TreeSelectionListener()
            {
                public void valueChanged(TreeSelectionEvent e)
                {
                    Artifact a = getSelectedArtifact();
                    if (a != null)
                        artifactSelected(a);
                }
            });
            ToolTipManager.sharedInstance().registerComponent(artifactTree);
        }
        return artifactTree;
    }
    
    private JTabbedPane getArtifactViewTabs()
    {
        if (artifactViewTabs == null)
        {
            artifactViewTabs = new JTabbedPane(JTabbedPane.TOP);
            artifactViewTabs.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    viewTabSelected(artifactViewTabs.getSelectedIndex());
                }
            });
        }
        return artifactViewTabs;
    }

    private JMenuBar getMenuBar()
    {
        if (menuBar == null)
        {
            menuBar = new JMenuBar();
            menuBar.add(getMnFile());
            menuBar.add(getMnView());
        }
        return menuBar;
    }

    private JMenu getMnFile()
    {
        if (mnFile == null)
        {
            mnFile = new JMenu("File");
            mnFile.setMnemonic('F');
            mnFile.add(getMntmQuit());
        }
        return mnFile;
    }

    private JMenuItem getMntmQuit()
    {
        if (mntmQuit == null)
        {
            mntmQuit = new JMenuItem("Quit");
            mntmQuit.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    mainWindow.setVisible(false);
                    System.exit(0);
                }
            });
            mntmQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        }
        return mntmQuit;
    }

    private JMenu getMnView()
    {
        if (mnView == null)
        {
            mnView = new JMenu("View");
            mnView.setMnemonic('V');
        }
        return mnView;
    }

    private JPanel getArtifactToolsPanel()
    {
        if (artifactToolsPanel == null)
        {
            artifactToolsPanel = new JPanel();
            FlowLayout flowLayout = (FlowLayout) artifactToolsPanel.getLayout();
            flowLayout.setVgap(2);
            flowLayout.setAlignment(FlowLayout.RIGHT);
            artifactToolsPanel.add(getBtnDeleteArtifact());
        }
        return artifactToolsPanel;
    }

    private JButton getBtnDeleteArtifact()
    {
        if (btnDeleteArtifact == null)
        {
            btnDeleteArtifact = new JButton("D");
            btnDeleteArtifact.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e)
                {
                    var art = getSelectedArtifact();
                    if (art != null)
                    {
                        browser.deleteArtifact(art);
                    }
                }
            });
            btnDeleteArtifact.setMargin(new Insets(2, 5, 2, 5));
            try {
                Image img = ImageIO.read(
                        BrowserWindow.class.getResource("/icons/delete16.png"));
                btnDeleteArtifact.setIcon(new ImageIcon(img));
                btnDeleteArtifact.setText(null);
            } catch (Exception e) {
                log.error("Couldn't load icon: {}", e.getMessage());
            }
        }
        return btnDeleteArtifact;
    }
    
    //===========================================================================
    
    private class Selection extends JPanel
    {
        private static final long serialVersionUID = 1L;
        public void paintComponent(Graphics g)
        {
            //super.paintComponent(g);
            g.setColor(selectionColor);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

}
