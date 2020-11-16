/**
 * ArtifactProviderPanel.java
 *
 * Created on 23. 5. 2020, 18:13:25 by burgetr
 */
package cz.vutbr.fit.layout.ide.views;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.eclipse.rdf4j.model.IRI;

import cz.vutbr.fit.layout.api.ArtifactService;
import cz.vutbr.fit.layout.api.ParametrizedOperation;
import cz.vutbr.fit.layout.ide.Browser;
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
    
    private Browser browser;
    
    private JComboBox<ArtifactService> serviceCombo;
    private ParamsPanel serviceParamsPanel;
    

    public ArtifactProviderPanel(Browser browser, IRI artifactType)
    {
        super();
        this.browser = browser;
        this.artifactType = artifactType;
    }

    public Browser getBrowser()
    {
        return browser;
    }

    public IRI getArtifactType()
    {
        return artifactType;
    }

    public ArtifactService getSelectedProvider()
    {
        int i = getServiceCombo().getSelectedIndex();
        if (i != -1)
            return getServiceCombo().getItemAt(i);
        else
            return null;
    }
    
    public boolean setSelectedProviderId(String id)
    {
        for (int i = 0; i < getServiceCombo().getItemCount(); i++)
        {
            ArtifactService op = getServiceCombo().getItemAt(i);
            if (op.getId().equals(id))
            {
                getServiceCombo().setSelectedIndex(i);
                return true;
            }
        }
        return false;
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
            var model = createServiceComboModel(providerMap);
            serviceCombo.setModel(model);
        }
        return serviceCombo;
    }

    protected ComboBoxModel<ArtifactService> createServiceComboModel(Map<String, ArtifactService> providerMap)
    {
        var providers = new Vector<ArtifactService>(providerMap.values());
        return new DefaultComboBoxModel<ArtifactService>(providers);
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
