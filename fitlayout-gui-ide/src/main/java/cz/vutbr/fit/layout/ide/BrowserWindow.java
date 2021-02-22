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
import cz.vutbr.fit.layout.ide.tabs.RepositoryConfigDialog;
import cz.vutbr.fit.layout.ide.views.ArtifactView;
import cz.vutbr.fit.layout.model.Area;
import cz.vutbr.fit.layout.model.Artifact;
import cz.vutbr.fit.layout.model.Box;
import cz.vutbr.fit.layout.model.Page;
import cz.vutbr.fit.layout.model.Rectangular;
import cz.vutbr.fit.layout.ontology.BOX;

import javax.swing.JSplitPane;

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
    
    private Page lastSelectedPage; //selected page cache
    private Artifact lastSelectedArtifact; //selected artifact cache
    
    private List<AreaSelectionListener> areaListeners;
    private List<TreeListener> treeListeners;
    private List<RectangleSelectionListener> rectangleListeners;
    private List<CanvasClickListener> canvasClickListeners;
    
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
    private JButton redrawButton = null;
    private JSplitPane infoSplitter = null;
    private JTabbedPane toolTabs = null;
    private JToggleButton screenShotButton = null;
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
    private JButton btnRepository;
    private JPanel contentToolsPanel;


    public BrowserWindow(Browser browser)
    {
        this.browser = browser;
        areaListeners = new LinkedList<>();
        treeListeners = new LinkedList<>();
        rectangleListeners = new LinkedList<>();
        canvasClickListeners = new LinkedList<>();
        browserTabs = new LinkedList<>();
        tabViewItems = new LinkedList<>();
        artifactViews = new HashMap<>();
    }
    
    public void init()
    {
        setArtifactTreeModel(new ArtifactTreeModel(browser.getProcessor()));
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

    public void addToolPanel(String title, JComponent component)
    {
        toolTabs.addTab(title, component);
    }

    public void addStructurePanel(String title, JComponent component)
    {
        sidebarPane.addTab(title, component);
    }

    public OutputDisplay getContentDisplay()
    {
        return ((BrowserPanel) contentCanvas).getContentDisplay();
    }

    public OutputDisplay getOverlayDisplay()
    {
        return ((BrowserPanel) contentCanvas).getOverlayDisplay();
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

    public void clearOverlay()
    {
        if (contentCanvas != null && contentCanvas instanceof BrowserPanel)
            ((BrowserPanel) contentCanvas).clearOverlay();
    }

    public void addAreaSelectionListener(AreaSelectionListener listener)
    {
        areaListeners.add(listener);
    }
    
    public void addTreeListener(TreeListener listener)
    {
        treeListeners.add(listener);
    }
    
    public void addCanvasClickListener(CanvasClickListener listener)
    {
        canvasClickListeners.add(listener);
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
    
    public void reloadArtifactTree()
    {
        artifactTreeModel.reloadArtifactTree();
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
    
    public int[] getGeometry()
    {
        return new int[] { mainWindow.getX(), mainWindow.getY(), mainWindow.getWidth(), mainWindow.getHeight() };
    }
    
    public void setGeometry(int[] geom)
    {
        mainWindow.setBounds(geom[0], geom[1], geom[2], geom[3]);
    }
    
    //================================================================================================================================
    
    public List<BrowserTabState> getTabStates()
    {
        return browserTabs;
    }
    
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
        Artifact ret = null;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) artifactTree.getLastSelectedPathComponent();
        if (node != null && node.getUserObject() != null)
            ret = (Artifact) node.getUserObject();
        //replace the artifact info with the real artifact from the repository
        if (ret != null)
        {
            if (lastSelectedArtifact != null && ret.getIri().equals(lastSelectedArtifact.getIri()))
                ret = lastSelectedArtifact;
            else
                ret = browser.getRepository().getArtifact(ret.getIri());
        }
        lastSelectedArtifact = ret; //store for repeated use
        if (ret instanceof Page)
            lastSelectedPage = (Page) ret;
        return ret;
    }

    /**
     * Finds the Page artifact related to the currently selected artifact.
     * @return the Page corresponding to the currently selected artifact or {@code null} when
     * nothing is selected or no such page exists.
     */
    public Page getSelectedPage()
    {
        Artifact selected = null;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) artifactTree.getLastSelectedPathComponent();
        while (node != null && node.getUserObject() != null)
        {
            final Artifact art = (Artifact) node.getUserObject();
            if (BOX.Page.equals(art.getArtifactType()))
            {
                selected = art;
                break;
            }
            else
                node = (DefaultMutableTreeNode) node.getParent();
        }
        //replace the artifact info with the real artifact from the repository
        Page selPage = null;
        if (selected != null)
        {
            if (lastSelectedPage != null && selected.getIri().equals(lastSelectedPage.getIri()))
                selPage = lastSelectedPage;
            else
                selPage = (Page) browser.getRepository().getArtifact(selected.getIri());
        }
        lastSelectedPage = selPage;
        lastSelectedArtifact = selPage; //store for repeated use
        return selPage;
    }
    
    private void setCurrentPage(Page page) 
    {
        contentCanvas = createContentCanvas(page);
        setupCanvasListeners(contentCanvas);
        contentScroll.setViewportView(contentCanvas);

        if (((BrowserPanel) contentCanvas).screenShotAvailable()) //if a screenshot is available
            screenShotButton.setEnabled(true);
        else
            screenShotButton.setEnabled(false);
        screenShotButton.setSelected(false);
        
        notifyBoxTreeUpdate(page);
    }

    private void setupCanvasListeners(JPanel canvas)
    {
        canvas.addMouseListener(new MouseListener() {
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
        canvas.addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent e)
            { 
                canvasDrag(e.getX(), e.getY());
            }
            public void mouseMoved(MouseEvent e) 
            { 
                String s = "Absolute: " + e.getX() + ":" + e.getY();
                /*Area node = segmentationTab.getSelectedArea();
                Area node = null; //TODO
                if (node != null)
                {
                    Area area = (Area) node;
                    int rx = e.getX() - area.getX1();
                    int ry = e.getY() - area.getY1();
                    s += "  Relative: " + rx + ":" + ry;
                }*/
                statusText.setText(s);
                canvasMove(e.getX(), e.getY());
            }
        });
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
    
    /** This is called when the browser canvas is clicked */
    private void canvasClick(int x, int y)
    {
        for (CanvasClickListener listener : canvasClickListeners)
            listener.canvasClicked(x, y);
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
        getOverlayDisplay().drawExtent(root);
        for (int i = 0; i < root.getChildCount(); i++)
            showAllBoxes(root.getChildAt(i));
    }
    
    public void showAreas(Area root, String name)
    {
        if (name == null || root.toString().contains(name))
            getOverlayDisplay().drawExtent(root);
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
                    browser.exit(0);
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
            contentPanel = new JPanel();
            GridBagLayout gbl_contentPanel = new GridBagLayout();
            contentPanel.setLayout(gbl_contentPanel);
            GridBagConstraints gbc_contentToolsPanel = new GridBagConstraints();
            gbc_contentToolsPanel.fill = GridBagConstraints.BOTH;
            gbc_contentToolsPanel.gridx = 0;
            gbc_contentToolsPanel.gridy = 0;
            contentPanel.add(getContentToolsPanel(), gbc_contentToolsPanel);
            GridBagConstraints gbc_contentScroll = new GridBagConstraints();
            gbc_contentScroll.insets = new Insets(0, 0, 5, 0);
            gbc_contentScroll.weightx = 1.0;
            gbc_contentScroll.weighty = 1.0;
            gbc_contentScroll.fill = GridBagConstraints.BOTH;
            gbc_contentScroll.gridx = 0;
            gbc_contentScroll.gridy = 1;
            contentPanel.add(getContentScroll(), gbc_contentScroll);
        }
        return contentPanel;
    }

    private JPanel getContentToolsPanel()
    {
        if (contentToolsPanel == null)
        {
            contentToolsPanel = new JPanel();
            
            GridBagLayout gbl_toolsPanel = new GridBagLayout();
            gbl_toolsPanel.rowHeights = new int[]{26, 0};
            gbl_toolsPanel.columnWeights = new double[]{1.0};
            gbl_toolsPanel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
            contentToolsPanel.setLayout(gbl_toolsPanel);
            
            GridBagConstraints gbc_btnRedraw = new GridBagConstraints();
            gbc_btnRedraw.anchor = GridBagConstraints.WEST;
            gbc_btnRedraw.insets = new Insets(2, 5, 2, 5);
            gbc_btnRedraw.gridx = 0;
            gbc_btnRedraw.gridy = 0;
            contentToolsPanel.add(getRedrawButton(), gbc_btnRedraw);
            
            screenShotButton = new JToggleButton("S");
            screenShotButton.setToolTipText("Show screenshot");
            screenShotButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e)
                {
                    ((BrowserPanel) contentCanvas).showScreenShot(screenShotButton.isSelected());
                    updateDisplay();
                }
            });
            screenShotButton.setMargin(new Insets(2, 5, 2, 5));
            screenShotButton.setEnabled(false); //will be enabled when a screen shot is available
            GridBagConstraints gbc_btnScreen = new GridBagConstraints();
            gbc_btnScreen.anchor = GridBagConstraints.EAST;
            gbc_btnScreen.insets = new Insets(2, 5, 2, 5);
            gbc_btnScreen.gridx = 2;
            gbc_btnScreen.gridy = 0;
            contentToolsPanel.add(screenShotButton, gbc_btnScreen);
        }
        return contentToolsPanel;
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
                    clearOverlay();
                    updateDisplay();
                }
            });
        }
        return redrawButton;
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
            
            artifactViewPanel.setLayout(new GridBagLayout());

            GridBagConstraints gbc_artifactViewTabs = new GridBagConstraints();
            gbc_artifactViewTabs.insets = new Insets(0, 2, 0, 2);
            gbc_artifactViewTabs.weightx = 1.0;
            gbc_artifactViewTabs.weighty = 1.0;
            gbc_artifactViewTabs.fill = GridBagConstraints.BOTH;
            gbc_artifactViewTabs.gridx = 0;
            gbc_artifactViewTabs.gridy = 1;
            artifactViewPanel.add(getArtifactViewTabs(), gbc_artifactViewTabs);
            
            infoSplitter.setRightComponent(getContentPanel());
        }
        return infoSplitter;
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
                    browser.exit(0);
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
            GridBagLayout gbl_artifactToolsPanel = new GridBagLayout();
            gbl_artifactToolsPanel.rowHeights = new int[]{26, 0};
            gbl_artifactToolsPanel.columnWeights = new double[]{0.0, 1.0, 0.0};
            gbl_artifactToolsPanel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
            artifactToolsPanel.setLayout(gbl_artifactToolsPanel);
            GridBagConstraints gbc_btnRepository = new GridBagConstraints();
            gbc_btnRepository.anchor = GridBagConstraints.WEST;
            gbc_btnRepository.insets = new Insets(2, 5, 2, 5);
            gbc_btnRepository.gridx = 0;
            gbc_btnRepository.gridy = 0;
            artifactToolsPanel.add(getBtnRepository(), gbc_btnRepository);
            GridBagConstraints gbc_paddingPanel = new GridBagConstraints();
            gbc_paddingPanel.insets = new Insets(0, 0, 0, 5);
            gbc_paddingPanel.fill = GridBagConstraints.BOTH;
            gbc_paddingPanel.gridx = 1;
            gbc_paddingPanel.gridy = 0;
            artifactToolsPanel.add(new JPanel(), gbc_paddingPanel);
            GridBagConstraints gbc_btnDeleteArtifact = new GridBagConstraints();
            gbc_btnDeleteArtifact.anchor = GridBagConstraints.EAST;
            gbc_btnDeleteArtifact.insets = new Insets(2, 5, 2, 5);
            gbc_btnDeleteArtifact.gridx = 2;
            gbc_btnDeleteArtifact.gridy = 0;
            artifactToolsPanel.add(getBtnDeleteArtifact(), gbc_btnDeleteArtifact);
        }
        return artifactToolsPanel;
    }

    private JButton getBtnRepository()
    {
        if (btnRepository == null)
        {
            btnRepository = new JButton("R");
            btnRepository.setToolTipText("Configure repository");
            btnRepository.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e)
                {
                    var repoDialog = new RepositoryConfigDialog(mainWindow, browser, 
                            browser.loadRepositoryServices(), browser.getSelectedRepoService());
                    repoDialog.setVisible(true);
                }
            });
            btnRepository.setMargin(new Insets(2, 5, 2, 5));
            try {
                Image img = ImageIO.read(
                        BrowserWindow.class.getResource("/icons/storage16.png"));
                btnRepository.setIcon(new ImageIcon(img));
                btnRepository.setText(null);
            } catch (Exception e) {
                log.error("Couldn't load icon: {}", e.getMessage());
            }
        }
        return btnRepository;
    }

    private JButton getBtnDeleteArtifact()
    {
        if (btnDeleteArtifact == null)
        {
            btnDeleteArtifact = new JButton("D");
            btnDeleteArtifact.setToolTipText("Delete artifact");
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
