/**
 * Browser.java
 *
 * Created on 11. 10. 2020, 18:17:45 by burgetr
 */
package cz.vutbr.fit.layout.ide;

import java.awt.EventQueue;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.eclipse.rdf4j.model.IRI;

import cz.vutbr.fit.layout.api.ArtifactRepository;
import cz.vutbr.fit.layout.api.ArtifactService;
import cz.vutbr.fit.layout.ide.tabs.BoxSourcePanel;
import cz.vutbr.fit.layout.ide.tabs.BoxTreeTab;
import cz.vutbr.fit.layout.ide.tabs.SegmentationTab;
import cz.vutbr.fit.layout.ide.views.AreaTreeView;
import cz.vutbr.fit.layout.ide.views.PageView;
import cz.vutbr.fit.layout.impl.DefaultArtifactRepository;
import cz.vutbr.fit.layout.model.Artifact;
import cz.vutbr.fit.layout.process.GUIProcessor;
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
        //repository = new DefaultArtifactRepository();
        //RDFStorage storage = RDFStorage.createNative(System.getProperty("user.home") + "/.fitlayout/storage");
        RDFStorage storage = RDFStorage.createHTTP("http://localhost:8080/rdf4j-server", "fitlayout2");
        repository = new RDFArtifactRepository(storage);
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
     * Creates a new artifact using a configured provider and adds it to the browser.
     * @param parent parent artifact ot {@code null} for root artifacts (pages)
     * @param provider the provider to use for creating the artifact
     * @param params the provider params
     * @return the new artifact added or {@code null} when the page could not be created
     */
    public Artifact createAndAddArtifact(Artifact parent, ArtifactService provider, Map<String, Object> params)
    {
        Artifact ret = processor.processArtifact(null, provider, params);
        repository.addArtifact(ret);
        window.updateArtifactTree();
        window.selectArtifact(ret);
        return ret;
    }
    
    /**
     * Creates a new artifact from the nearest applicable parent using the given provider
     * and adds the new artifact to the artifact tree.
     * @param provider
     * @return
     */
    public Artifact createAndAddArtifact(ArtifactService provider)
    {
        Artifact parent = null;
        if (provider.getConsumes() != null)
            parent = getNearestArtifact(provider.getConsumes());
        if (parent != null)
        {
            Artifact result = provider.process(parent);
            repository.addArtifact(result);
            window.updateArtifactTree();
            window.selectArtifact(result);
            return result;
        }
        else
            return null;
    }

    private Artifact getParentArtifact(Artifact artifact)
    {
        final IRI parentIri = artifact.getParentIri();
        if (parentIri != null)
            return repository.getArtifact(parentIri);
        else
            return null;
    }
    
    /**
     * Finds the nearest artifact of the given type in the artifact tree.
     * @param artifactType
     * @return
     */
    public Artifact getNearestArtifact(IRI artifactType)
    {
        Artifact ret = window.getSelectedArtifact();
        while (ret != null && !artifactType.equals(ret.getArtifactType()))
            ret = getParentArtifact(ret);
        return ret;
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
