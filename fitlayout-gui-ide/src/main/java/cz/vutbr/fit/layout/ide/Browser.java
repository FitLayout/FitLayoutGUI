/**
 * Browser.java
 *
 * Created on 11. 10. 2020, 18:17:45 by burgetr
 */
package cz.vutbr.fit.layout.ide;

import java.awt.EventQueue;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.vutbr.fit.layout.api.ArtifactRepository;
import cz.vutbr.fit.layout.api.ParametrizedOperation;
import cz.vutbr.fit.layout.api.ServiceManager;
import cz.vutbr.fit.layout.ide.config.EnvConfig;
import cz.vutbr.fit.layout.ide.config.IdeConfig;
import cz.vutbr.fit.layout.ide.config.ServiceConfig;
import cz.vutbr.fit.layout.ide.config.TabConfig;
import cz.vutbr.fit.layout.ide.service.BasicRepositoryService;
import cz.vutbr.fit.layout.ide.service.HTTPRDFRepositoryService;
import cz.vutbr.fit.layout.ide.service.MemoryRDFRepositoryService;
import cz.vutbr.fit.layout.ide.service.NativeRDFRepositoryService;
import cz.vutbr.fit.layout.ide.service.RepositoryService;
import cz.vutbr.fit.layout.ide.tabs.BoxSourcePanel;
import cz.vutbr.fit.layout.ide.tabs.BoxTreeTab;
import cz.vutbr.fit.layout.ide.tabs.BrowserTabState;
import cz.vutbr.fit.layout.ide.tabs.SegmentationTab;
import cz.vutbr.fit.layout.ide.views.AreaTreeView;
import cz.vutbr.fit.layout.ide.views.PageView;
import cz.vutbr.fit.layout.model.Artifact;

/**
 * 
 * @author burgetr
 */
public class Browser
{
    private static Logger log = LoggerFactory.getLogger(Browser.class);
    
    public static final String configDir = System.getProperty("user.home") + "/.fitlayout";
    
    private ConfigFile configFile;
    private List<RepositoryService> repoServices;
    private RepositoryService selectedRepoService;
    
    private BrowserWindow window;
    private BoxTreeTab boxTreeTab;
    private SegmentationTab segmentationTab;
    
    private GUIProcessor processor;
    //private ArtifactRepository repository;
    
    
    public Browser()
    {
    }
    
    public void init()
    {
        processor = new GUIProcessor();
        loadRepositoryServices();
        if (!repoServices.isEmpty())
            selectedRepoService = repoServices.get(0);
        configFile = new ConfigFile();
        loadConfig();
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

    public RepositoryService getSelectedRepoService()
    {
        return selectedRepoService;
    }

    public ArtifactRepository getRepository()
    {
        return processor.getRepository();
    }

    //=========================================================================
    
    public void setLocation(String url)
    {
        ((BoxSourcePanel) boxTreeTab.getTabPanel()).setUrl(url);
        ((BoxSourcePanel) boxTreeTab.getTabPanel()).displaySelectedURL();
    }
    
    public void exit(int exitcode)
    {
        saveConfig();
        System.exit(exitcode);
    }
    
    //=========================================================================
    
    /**
     * Adds the artifact to the repository and updates the tree view.
     * @param artifact the artifact to add
     */
    public void addArtifact(Artifact artifact)
    {
        getRepository().addArtifact(artifact);
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
                    getRepository().removeArtifact(((Artifact) desc).getIri());
                }
            }
            window.updateArtifactTree();
        }
    }
    
    
    //=========================================================================
    
    public void saveConfig()
    {
        IdeConfig config = new IdeConfig();
        //store service params
        Map<String, ParametrizedOperation> services = getProcessor().getServiceManager().getParametrizedServices();
        config.services = new ServiceConfig[services.keySet().size()];
        int i = 0;
        for (Map.Entry<String, ParametrizedOperation> entry : services.entrySet())
        {
            ServiceConfig sconf = new ServiceConfig();
            sconf.id = entry.getKey();
            sconf.params = ServiceManager.getServiceParams(entry.getValue());
            config.services[i++] = sconf;
        }
        //store tab states
        config.tabs = new TabConfig[window.getTabStates().size()];
        i = 0;
        for (BrowserTabState tabState : window.getTabStates())
        {
            config.tabs[i++] = tabState.getTabConfig();
        }
        //environment
        config.env = new EnvConfig();
        config.env.repos = new ServiceConfig[repoServices.size()];
        i = 0;
        for (RepositoryService serv : repoServices)
        {
            ServiceConfig sconf = new ServiceConfig();
            sconf.id = serv.getId();
            sconf.params = ServiceManager.getServiceParams(serv);
            config.env.repos[i++] = sconf;
        }
        config.env.repo = (selectedRepoService == null) ? null : selectedRepoService.getId();
        config.env.window = window.getGeometry();
        config.env.selectedOps = new String[processor.getSelectedOperators().size()];
        for (i = 0; i < processor.getSelectedOperators().size(); i++)
            config.env.selectedOps[i] = processor.getSelectedOperators().get(i).getId();
        //save
        try
        {
            configFile.save(config);
        } catch (IOException e) {
            log.error("Couldn't save GUI config: {}", e.getMessage());
        }
    }
    
    public void loadConfig()
    {
        try
        {
            IdeConfig config = configFile.load();
            //load repository config
            if (config.env != null)
            {
                if (config.env.repos != null)
                {
                    for (ServiceConfig sconf : config.env.repos)
                    {
                        var op = findRepositoryService(sconf.id);
                        if (op != null)
                            ServiceManager.setServiceParams(op, sconf.params);
                    }
                }
                if (config.env.repo != null)
                {
                    var op = findRepositoryService(config.env.repo);
                    if (op != null)
                        connectRepository(op);
                    else
                        log.warn("Repository service {} is not available anymore; using a default repository", config.env.repo);
                }
            }
            //restore service params
            if (config.services != null)
            {
                for (ServiceConfig sconf : config.services)
                {
                    final ServiceManager sm = getProcessor().getServiceManager(); 
                    var op = sm.findParmetrizedService(sconf.id);
                    if (op != null)
                        ServiceManager.setServiceParams(op, sconf.params);
                }
            }
            //selected operators
            if (config.env.selectedOps != null)
            {
                processor.setSelectedOperatorIDs(config.env.selectedOps);
            }
            
        } catch (IOException e) {
            log.warn("Couldn't load GUI config file: {}. Using defaults.", e.getMessage());
            loadDefaults();
        }
    }
    
    public void loadDefaults()
    {
        processor.setRepository(BasicRepositoryService.defaultRepository);
    }
    
    /**
     * Adds the repository services to the central service manager.
     * @return
     */
    public List<RepositoryService> loadRepositoryServices()
    {
        if (repoServices == null)
        {
            repoServices = new ArrayList<>();
            repoServices.add(new BasicRepositoryService());
            repoServices.add(new MemoryRDFRepositoryService());
            repoServices.add(new NativeRDFRepositoryService());
            repoServices.add(new HTTPRDFRepositoryService());
        }
        return repoServices;
    }
    
    private RepositoryService findRepositoryService(String id)
    {
        for (RepositoryService serv : repoServices)
        {
            if (id.equals(serv.getId()))
                return serv;
        }
        return null;
    }
    
    public void connectRepository(RepositoryService service)
    {
        if (processor.getRepository() != null)
        {
            processor.getRepository().disconnect();
        }
        selectedRepoService = service;
        processor.setRepository(service.createRepository());
        if (window != null)
            window.reloadArtifactTree();
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
        
        //restore window geometry and tab states if available
        if (configFile != null && configFile.getLoadedConfig() != null)
        {
            final IdeConfig conf = configFile.getLoadedConfig();
            if (conf.env != null && conf.env.window != null && conf.env.window.length == 4)
            {
                window.setGeometry(conf.env.window);
            }
            if (conf.tabs != null)
            {
                for (TabConfig tconf : conf.tabs)
                {
                    for (BrowserTabState tabState : window.getTabStates())
                    {
                        if (tabState.setTabConfig(tconf))
                            break;
                    }
                }
            }
        }

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
                    browser.init();
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
