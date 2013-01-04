package hmi.animationui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import lombok.Getter;

/**
 * A user interface to set and update joint rotations
 * @author hvanwelbergen
 */
public class JointView
{
    private final RotationsController controller;
    
    @Getter
    private JPanel panel = new JPanel();
    
    
    private List<JointRotationPanel> rotPanels = new ArrayList<>();
    
    public JointView(RotationsController controller, Collection<String> joints)
    {
        this.controller = controller;
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        for(String vj:joints)
        {
            JointRotationPanel rp = new JointRotationPanel(vj, this); 
            rotPanels.add(rp);
            panel.add(rp.getPanel());
        }
        panel.add(Box.createVerticalGlue());        
    }    
    
    public void update()
    {
        List<JointRotationConfiguration> jrcList = new ArrayList<>();
        for(JointRotationPanel rp: rotPanels)
        {
            jrcList.add(rp.getRotationConfiguration());
        }        
        controller.setJointRotations(jrcList);
    }    
}
