package cz.vutbr.fit.layout.ide.tabs;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cz.vutbr.fit.layout.ide.Browser;
import cz.vutbr.fit.layout.ide.views.ArtifactProviderPanel;
import cz.vutbr.fit.layout.model.Artifact;
import cz.vutbr.fit.layout.ontology.BOX;


public class BoxSourcePanel extends ArtifactProviderPanel
{
    private static final long serialVersionUID = 1L;
    
    private JPanel rendererChoicePanel;
    private JLabel rendererLabel;
    private JButton okButton;

    /**
     * Create the panel.
     */
    public BoxSourcePanel(Browser browser)
    {
        super(browser, BOX.Page);
        
        GridBagLayout gbl_sourcesTab = new GridBagLayout();
        gbl_sourcesTab.columnWeights = new double[] { 1.0 };
        gbl_sourcesTab.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0, 1.0 };
        setLayout(gbl_sourcesTab);
        
        GridBagConstraints gbc_rendererChoicePanel = new GridBagConstraints();
        gbc_rendererChoicePanel.weightx = 1.0;
        gbc_rendererChoicePanel.anchor = GridBagConstraints.EAST;
        gbc_rendererChoicePanel.fill = GridBagConstraints.BOTH;
        gbc_rendererChoicePanel.insets = new Insets(0, 0, 1, 0);
        gbc_rendererChoicePanel.gridx = 0;
        gbc_rendererChoicePanel.gridy = 0;
        add(getRendererChoicePanel(), gbc_rendererChoicePanel);
        
        GridBagConstraints gbc_rendererParamsPanel = new GridBagConstraints();
        gbc_rendererParamsPanel.weightx = 1.0;
        gbc_rendererParamsPanel.fill = GridBagConstraints.BOTH;
        gbc_rendererParamsPanel.insets = new Insets(0, 0, 2, 0);
        gbc_rendererParamsPanel.gridx = 0;
        gbc_rendererParamsPanel.gridy = 1;
        add(getServiceParamsPanel(), gbc_rendererParamsPanel);

        updateParamsPanel();
    }
    
    //====================================================================================================
    
    public void setUrl(String url)
    {
        getServiceParamsPanel().setParam("url", url);
    }
    
    public String getUrl()
    {
        return (String) getServiceParamsPanel().getParam("url");
    }
    
    public void displaySelectedURL()
    {
        int i = getServiceCombo().getSelectedIndex();
        if (i != -1)
        {
            var btp = getServiceCombo().getItemAt(i);
            Artifact a = getBrowser().createArtifact(null, btp, getServiceParamsPanel().getParams());
            getBrowser().addArtifact(a);
        }
    }
    
    //====================================================================================================
    
    private JPanel getRendererChoicePanel()
    {
        if (rendererChoicePanel == null)
        {
            rendererChoicePanel = new JPanel();
            FlowLayout flowLayout = (FlowLayout) rendererChoicePanel.getLayout();
            flowLayout.setAlignment(FlowLayout.LEFT);
            rendererChoicePanel.add(getRendererLabel());
            rendererChoicePanel.add(getServiceCombo());
            rendererChoicePanel.add(getOkButton());
        }
        return rendererChoicePanel;
    }

    private JLabel getRendererLabel()
    {
        if (rendererLabel == null)
        {
            rendererLabel = new JLabel("Renderer");
        }
        return rendererLabel;
    }

    private JButton getOkButton()
    {
        if (okButton == null)
        {
            okButton = new JButton();
            okButton.setText("Go!");
            okButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    displaySelectedURL();
                }
            });
        }
        return okButton;
    }
}
