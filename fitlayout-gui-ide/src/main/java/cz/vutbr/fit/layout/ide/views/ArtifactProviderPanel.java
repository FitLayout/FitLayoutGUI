/**
 * ArtifactProviderPanel.java
 *
 * Created on 23. 5. 2020, 18:13:25 by burgetr
 */
package cz.vutbr.fit.layout.ide.views;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.eclipse.rdf4j.model.IRI;

import cz.vutbr.fit.layout.api.ArtifactService;
import cz.vutbr.fit.layout.api.ParametrizedOperation;
import cz.vutbr.fit.layout.ide.BlockBrowser;
import cz.vutbr.fit.layout.ide.tabs.ParamsPanel;

/**
 * A base class for panels that control a kind of ArtifactService that provides certain type
 * of artifacts. The panel contains the provider selection and parametres.
 * @author burgetr
 */
public class ArtifactProviderPanel extends JPanel
{
    private static final long serialVersionUID = 1L;

    private IRI artifactType;
    
    private BlockBrowser browser;
    
    private JComboBox<ArtifactService> serviceCombo;
    private ParamsPanel serviceParamsPanel;
    

    public ArtifactProviderPanel(BlockBrowser browser, IRI artifactType)
    {
        super();
        this.browser = browser;
        this.artifactType = artifactType;
    }

    public BlockBrowser getBrowser()
    {
        return browser;
    }

    public IRI getArtifactType()
    {
        return artifactType;
    }

    protected JComboBox<ArtifactService> getServiceCombo()
    {
        if (serviceCombo == null)
        {
            serviceCombo = new JComboBox<ArtifactService>();
            serviceCombo.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    updateParamsPanel();
                }
            });
            var providerMap = browser.getProcessor().getArtifactProviders(artifactType);
            var providers = new Vector<ArtifactService>(providerMap.values());
            var model = new DefaultComboBoxModel<ArtifactService>(providers);
            serviceCombo.setModel(model);
        }
        return serviceCombo;
    }

    protected ParamsPanel getServiceParamsPanel()
    {
        if (serviceParamsPanel == null)
        {
            serviceParamsPanel = new ParamsPanel(browser.getProcessor().getServiceManager());
        }
        return serviceParamsPanel;
    }
    
    //=======================================================================================
  
    /**
     * Updates the service params panel according to the current service selection.
     */
    protected void updateParamsPanel()
    {
        ArtifactService ap = (ArtifactService) serviceCombo.getSelectedItem();
        if (ap != null && ap instanceof ParametrizedOperation)
            getServiceParamsPanel().setOperation((ParametrizedOperation) ap, null);
    }
    
    public void reloadServiceParams()
    {
        getServiceParamsPanel().reloadParams();
    }

}
