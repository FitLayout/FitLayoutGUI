package cz.vutbr.fit.layout.ide.tabs;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cz.vutbr.fit.layout.ide.Browser;
import cz.vutbr.fit.layout.ide.service.RepositoryService;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class RepositoryConfigDialog extends JDialog
{
    private static final long serialVersionUID = 1L;
    
    private ParamsPanel paramsPanel;
    private JComboBox<RepositoryService> serviceSelectionCombo;

    
    public RepositoryConfigDialog(Frame parent, Browser browser, List<RepositoryService> services, RepositoryService selected)
    {
        super(parent);
        setTitle("Artifact repository");
        setModal(true);
        setBounds(100, 100, 462, 307);
        GridBagLayout gridBagLayout = new GridBagLayout();
        //gridBagLayout.columnWidths = new int[]{442, 0};
        //gridBagLayout.rowHeights = new int[]{100, 207, 34, 0};
        gridBagLayout.columnWeights = new double[]{1.0};
        gridBagLayout.rowWeights = new double[]{0.0, 0.0, 1.0, 0.0};
        getContentPane().setLayout(gridBagLayout);
        final Vector<RepositoryService> servList = new Vector<>(services);
        
        JLabel lblRepository = new JLabel("Repository service");
        GridBagConstraints gbc_lblRepository = new GridBagConstraints();
        gbc_lblRepository.insets = new Insets(10, 10, 0, 0);
        gbc_lblRepository.anchor = GridBagConstraints.WEST;
        gbc_lblRepository.gridx = 0;
        gbc_lblRepository.gridy = 0;
        getContentPane().add(lblRepository, gbc_lblRepository);
        
        JPanel serviceSelectionPanel = new JPanel();
        GridBagConstraints gbc_serviceSelectionPanel = new GridBagConstraints();
        gbc_serviceSelectionPanel.anchor = GridBagConstraints.WEST;
        gbc_serviceSelectionPanel.insets = new Insets(0, 0, 5, 0);
        gbc_serviceSelectionPanel.gridx = 0;
        gbc_serviceSelectionPanel.gridy = 1;
        getContentPane().add(serviceSelectionPanel, gbc_serviceSelectionPanel);
        
        paramsPanel = new ParamsPanel();
        GridBagConstraints gbc_paramsPanel = new GridBagConstraints();
        gbc_paramsPanel.fill = GridBagConstraints.BOTH;
        gbc_paramsPanel.insets = new Insets(0, 0, 5, 0);
        gbc_paramsPanel.gridx = 0;
        gbc_paramsPanel.gridy = 2;
        getContentPane().add(paramsPanel, gbc_paramsPanel);
        
        serviceSelectionCombo = new JComboBox<>();
        serviceSelectionCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) 
            {
                int i = serviceSelectionCombo.getSelectedIndex();
                if (i != -1)
                {
                    var sel = serviceSelectionCombo.getItemAt(i);
                    paramsPanel.setOperation(sel, null);
                }
            }
        });
        serviceSelectionCombo.setModel(new DefaultComboBoxModel<RepositoryService>(servList));
        if (selected != null)
            serviceSelectionCombo.setSelectedItem(selected);
        serviceSelectionPanel.add(serviceSelectionCombo);
        
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        GridBagConstraints gbc_buttonPane = new GridBagConstraints();
        gbc_buttonPane.insets = new Insets(0, 0, 5, 0);
        gbc_buttonPane.anchor = GridBagConstraints.NORTH;
        gbc_buttonPane.fill = GridBagConstraints.HORIZONTAL;
        gbc_buttonPane.gridx = 0;
        gbc_buttonPane.gridy = 3;
        getContentPane().add(buttonPane, gbc_buttonPane);
        
        JButton okButton = new JButton("Connect");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int i = serviceSelectionCombo.getSelectedIndex();
                if (i != -1)
                {
                    var sel = serviceSelectionCombo.getItemAt(i);
                    browser.connectRepository(sel);
                    dispose();
                }
            }
        });
        buttonPane.add(okButton);
        getRootPane().setDefaultButton(okButton);
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        buttonPane.add(cancelButton);
        
    }

}
