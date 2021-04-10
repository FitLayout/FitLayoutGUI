package cz.vutbr.fit.layout.ide.tabs;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cz.vutbr.fit.layout.api.ArtifactService;
import cz.vutbr.fit.layout.ide.Browser;
import cz.vutbr.fit.layout.ide.views.ArtifactProviderPanel;
import cz.vutbr.fit.layout.model.AreaTree;
import cz.vutbr.fit.layout.model.Artifact;
import cz.vutbr.fit.layout.ontology.SEGM;
import cz.vutbr.fit.layout.provider.OperatorApplicationProvider;

public class SegmentationPanel extends ArtifactProviderPanel
{
    private static final long serialVersionUID = 1L;

    private OperatorApplicationProvider opProvider;
    
    private JPanel segmChoicePanel;
    private JLabel lblSegmentator;
    private JButton segmRunButton;
    private JCheckBox segmAutorunCheckbox;
    private JButton btnOperators;

    protected OperatorConfigWindow operatorWindow;
    private JButton btnApply;

    public SegmentationPanel(Browser browser)
    {
        super(browser, SEGM.AreaTree);
        
        GridBagLayout gbl_sourcesTab = new GridBagLayout();
        gbl_sourcesTab.columnWeights = new double[] { 1.0 };
        gbl_sourcesTab.rowWeights = new double[] { 0.0, 0.0, 0.0 };
        setLayout(gbl_sourcesTab);
        
        GridBagConstraints gbc_segmChoicePanel = new GridBagConstraints();
        gbc_segmChoicePanel.weightx = 1.0;
        gbc_segmChoicePanel.anchor = GridBagConstraints.EAST;
        gbc_segmChoicePanel.fill = GridBagConstraints.BOTH;
        gbc_segmChoicePanel.insets = new Insets(0, 0, 1, 0);
        gbc_segmChoicePanel.gridx = 0;
        gbc_segmChoicePanel.gridy = 2;
        add(getSegmChoicePanel(), gbc_segmChoicePanel);
        
        GridBagConstraints gbc_segmParamsPanel = new GridBagConstraints();
        gbc_segmParamsPanel.insets = new Insets(0, 0, 2, 0);
        gbc_segmParamsPanel.weightx = 1.0;
        gbc_segmParamsPanel.fill = GridBagConstraints.BOTH;
        gbc_segmParamsPanel.gridx = 0;
        gbc_segmParamsPanel.gridy = 3;
        add(getServiceParamsPanel(), gbc_segmParamsPanel);
        
        updateParamsPanel();
    }

    //====================================================================================================
    
    public void segmentPage()
    {
        if (getServiceCombo().getSelectedIndex() != -1)
        {
            // create the area tree using the selected provider
            ArtifactService provider = getServiceCombo().getItemAt(getServiceCombo().getSelectedIndex());
            Artifact a = getBrowser().getProcessor().createArtifact(getBrowser().getWindow().getSelectedArtifact(), provider);
            // apply operators when auto-apply is on
            if (getSegmAutorunCheckbox().isSelected())
                getBrowser().getProcessor().applyOperators((AreaTree) a);
            // add the new artifact
            getBrowser().addArtifact(a);
        }
    }
    
    public boolean isAutoRun()
    {
        return getSegmAutorunCheckbox().isSelected();
    }
    
    public void setAutoRun(boolean state)
    {
        getSegmAutorunCheckbox().setSelected(state);
    }
    
    //====================================================================================================
    
    @Override
    protected ComboBoxModel<ArtifactService> createServiceComboModel(Map<String, ArtifactService> providerMap)
    {
        //filter out the OperatorApplicationProvider from the list of providers since it is activated
        //separately using the Apply button
        Vector<ArtifactService> providers = new Vector<>();
        for (ArtifactService serv : providerMap.values())
        {
            if ("FitLayout.ApplyOperators".equals(serv.getId()) && serv instanceof OperatorApplicationProvider)
            {
                opProvider = (OperatorApplicationProvider) serv;
            }
            else
            {
                providers.add(serv);
            }
        }
        return new DefaultComboBoxModel<ArtifactService>(providers);
    }

    private JPanel getSegmChoicePanel()
    {
        if (segmChoicePanel == null)
        {
            segmChoicePanel = new JPanel();
            FlowLayout flowLayout = (FlowLayout) segmChoicePanel.getLayout();
            flowLayout.setAlignment(FlowLayout.LEFT);
            segmChoicePanel.add(getLblSegmentator());
            segmChoicePanel.add(getServiceCombo());
            segmChoicePanel.add(getSegmRunButton());
            segmChoicePanel.add(getSegmAutorunCheckbox());
            segmChoicePanel.add(getBtnOperators());
            segmChoicePanel.add(getBtnApply());
        }
        return segmChoicePanel;
    }

    private JLabel getLblSegmentator()
    {
        if (lblSegmentator == null)
        {
            lblSegmentator = new JLabel("Segmentator");
        }
        return lblSegmentator;
    }

    private JButton getSegmRunButton()
    {
        if (segmRunButton == null)
        {
            segmRunButton = new JButton("Run");
            segmRunButton.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    segmentPage();
                }
            });
        }
        return segmRunButton;
    }
    
    protected JCheckBox getSegmAutorunCheckbox()
    {
        if (segmAutorunCheckbox == null)
        {
            segmAutorunCheckbox = new JCheckBox("Apply automatically");
        }
        return segmAutorunCheckbox;
    }
    
    private JButton getBtnOperators()
    {
        if (btnOperators == null)
        {
            btnOperators = new JButton("Operators...");
            btnOperators.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    if (operatorWindow == null)
                        operatorWindow = new OperatorConfigWindow(getBrowser().getProcessor());
                    operatorWindow.pack();
                    operatorWindow.setVisible(true);
                }
            });
        }
        return btnOperators;
    }
    
    private JButton getBtnApply() {
        if (btnApply == null) {
        	btnApply = new JButton("Apply");
        	btnApply.addActionListener(new ActionListener() {
        	    public void actionPerformed(ActionEvent e)
        	    {
        	        if (opProvider != null)
        	        {
        	            Artifact art = getBrowser().getProcessor()
        	                    .applyOperatorProvider(getBrowser().getWindow().getSelectedArtifact(), opProvider);
        	            getBrowser().addArtifact(art);
        	        }
        	    }
        	});
        }
        return btnApply;
    }
}
