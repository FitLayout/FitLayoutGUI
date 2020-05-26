package cz.vutbr.fit.layout.ide.tabs;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cz.vutbr.fit.layout.ide.BlockBrowser;
import cz.vutbr.fit.layout.ide.views.ArtifactProviderPanel;
import cz.vutbr.fit.layout.ontology.SEGM;

public class LogicalPanel extends ArtifactProviderPanel
{
    private static final long serialVersionUID = 1L;

    private JPanel logicalChoicePanel;
    private JLabel lblLogicalBuilder;
    private JButton logicalRunButton;
    private JCheckBox logicalAutorunCheckbox;

    public LogicalPanel(BlockBrowser browser)
    {
        super(browser, SEGM.LogicalAreaTree);
        
        GridBagConstraints gbc_logicalChoicePanel = new GridBagConstraints();
        gbc_logicalChoicePanel.insets = new Insets(0, 0, 1, 0);
        gbc_logicalChoicePanel.fill = GridBagConstraints.BOTH;
        gbc_logicalChoicePanel.gridx = 0;
        gbc_logicalChoicePanel.gridy = 4;
        add(getLogicalChoicePanel(), gbc_logicalChoicePanel);
        
        GridBagConstraints gbc_logicalParamsPanel = new GridBagConstraints();
        gbc_logicalParamsPanel.fill = GridBagConstraints.BOTH;
        gbc_logicalParamsPanel.gridx = 0;
        gbc_logicalParamsPanel.gridy = 5;
        add(getServiceParamsPanel(), gbc_logicalParamsPanel);
        
        updateParamsPanel();
    }

    //====================================================================================================
    
    private void buildLogicalTree()
    {
        if (getServiceCombo().getSelectedIndex() != -1)
        {
            var provider = getServiceCombo().getItemAt(getServiceCombo().getSelectedIndex());
            //browser.buildLogicalTree(provider);
        }
    }

    //====================================================================================================

    private JPanel getLogicalChoicePanel()
    {
        if (logicalChoicePanel == null)
        {
            logicalChoicePanel = new JPanel();
            FlowLayout flowLayout = (FlowLayout) logicalChoicePanel.getLayout();
            flowLayout.setAlignment(FlowLayout.LEFT);
            logicalChoicePanel.add(getLblLogicalBuilder());
            logicalChoicePanel.add(getServiceCombo());
            logicalChoicePanel.add(getLogicalRunButton());
            logicalChoicePanel.add(getLogicalAutorunCheckbox());
            if (getServiceCombo().getModel().getSize() == 0)
                logicalChoicePanel.setVisible(false);
        }
        return logicalChoicePanel;
    }

    private JLabel getLblLogicalBuilder()
    {
        if (lblLogicalBuilder == null)
        {
            lblLogicalBuilder = new JLabel("Logical builder");
        }
        return lblLogicalBuilder;
    }

    private JButton getLogicalRunButton()
    {
        if (logicalRunButton == null)
        {
            logicalRunButton = new JButton("Run");
            logicalRunButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) 
                {
                    buildLogicalTree();
                }
            });
        }
        return logicalRunButton;
    }

    protected JCheckBox getLogicalAutorunCheckbox()
    {
        if (logicalAutorunCheckbox == null)
        {
            logicalAutorunCheckbox = new JCheckBox("Run automatically");
        }
        return logicalAutorunCheckbox;
    }

    
}
