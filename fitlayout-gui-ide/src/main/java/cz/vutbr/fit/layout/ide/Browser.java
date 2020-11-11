/**
 * Browser.java
 *
 * Created on 11. 10. 2020, 18:17:45 by burgetr
 */
package cz.vutbr.fit.layout.ide;

import java.awt.EventQueue;
import java.net.URL;
import java.util.Enumeration;

import javax.swing.JFrame;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import cz.vutbr.fit.layout.api.ArtifactRepository;
import cz.vutbr.fit.layout.ide.tabs.BoxSourcePanel;
import cz.vutbr.fit.layout.ide.tabs.BoxTreeTab;
import cz.vutbr.fit.layout.ide.tabs.SegmentationTab;
import cz.vutbr.fit.layout.ide.views.AreaTreeView;
import cz.vutbr.fit.layout.ide.views.PageView;
import cz.vutbr.fit.layout.impl.DefaultArtifactRepository;
import cz.vutbr.fit.layout.model.Artifact;
import cz.vutbr.fit.layout.rdf.RDFArtifactRepository;
import cz.vutbr.fit.layout.rdf.RDFStorage;

/**
 * 
 * @author burgetr
 */
public class Browser
{
    //private static Logger log = LoggerFactory.getLogger(Browser.class);
    
    private BrowserWindow window;
    private BoxTreeTab boxTreeTab;
    private SegmentationTab segmentationTab;
    
    private GUIProcessor processor;
    private ArtifactRepository repository;
    
    
    public Browser()
    {
        repository = new DefaultArtifactRepository();
        //RDFStorage storage = RDFStorage.createNative(System.getProperty("user.home") + "/.fitlayout/storage");
        //RDFStorage storage = RDFStorage.createHTTP("http://localhost:8080/rdf4j-server", "fitlayout2");
        //repository = new RDFArtifactRepository(storage);
        processor = new GUIProcessor(repository);
    }
    
    public BrowserWindow getWindow()
    {
        return window;
    }

    public GUIProcessor getProcessor()
    {
        return processor;
    }

    public void setProcessor(GUIProcessor processor)
    {
        this.processor = processor;
    }

    public ArtifactRepository getRepository()
    {
        return repository;
    }

    public void setRepository(ArtifactRepository repository)
    {
        this.repository = repository;
    }

    //=========================================================================
    
    public void setLocation(String url)
    {
        ((BoxSourcePanel) boxTreeTab.getTabPanel()).setUrl(url);
        ((BoxSourcePanel) boxTreeTab.getTabPanel()).displaySelectedURL();
    }
    
    //=========================================================================
    
    /**
     * Adds the artifact to the repository and updates the tree view.
     * @param artifact the artifact to add
     */
    public void addArtifact(Artifact artifact)
    {
        repository.addArtifact(artifact);
        window.updateArtifactTree();
        window.selectArtifact(artifact);
    }

    /**
     * Deletes the artifact from repository.
     * @param artifact
     */
    public void deleteArtifact(Artifact artifact)
    {
        DefaultMutableTreeNode node = window.getArtifactTreeModel().getNodeForArtifact(artifact);
        if (node != null)
        {
            Enumeration<TreeNode> e = node.depthFirstEnumeration();
            while (e.hasMoreElements())
            {
                Object desc = ((DefaultMutableTreeNode) e.nextElement()).getUserObject();
                if (desc != null && desc instanceof Artifact)
                {
                    repository.removeArtifact(((Artifact) desc).getIri());
                }
            }
            window.updateArtifactTree();
        }
    }
    
    
    //=========================================================================
    
    /**
     * A place for adding custom GUI component to the main window
     */
    protected void initGUI()
    {
        window = new BrowserWindow(this);
        
        JFrame main = window.getMainWindow();
        //main.setSize(1000,600);
        //main.setMinimumSize(new Dimension(1200, 600));
        //main.setSize(1500,600);
        main.setSize(1600,1000);
        main.setVisible(true);
        
        window.init();
        //add the default tabs
        boxTreeTab = new BoxTreeTab(this);
        window.addTab(boxTreeTab, false, true);
        segmentationTab = new SegmentationTab(this);
        window.addTab(segmentationTab, true, true);
        //add artifact views
        window.addArtifactView(new PageView(this));
        window.addArtifactView(new AreaTreeView(this));
    }

    //=========================================================================
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        final String urlstring = (args.length > 0) ? args[0] : null;
        EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                try
                {
                    var browser = new Browser();
                    browser.initGUI();
                    
                    //urlstring = "http://www.reuters.com/article/2014/03/28/us-trading-momentum-analysis-idUSBREA2R09M20140328";
                    String urlstring = "http://cssbox.sf.net";
                    if (urlstring != null)
                    {
                        URL url = new URL(urlstring);
                        browser.setLocation(url.toString());
                    }
                        
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

}
