package hmi.faceanimationui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import lombok.Getter;

/**
 * A user interface to set and update morph target deformations
 * 
 * @author hvanwelbergen
 */
public class MorphView {
	@Getter
	private final JPanel panel = new JPanel();
	private final MorphController morphController;
	private Map<String, MorphPanel> morphPanels = new HashMap<>();

	public MorphView(MorphController mc, Collection<String> morphs) {
		this.morphController = mc;
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		for (String morph : morphs) {
			MorphPanel mp = new MorphPanel(morph, this);
			morphPanels.put(morph, mp);
			panel.add(mp.getPanel());
		}
	}

	public void update() {
		List<MorphConfiguration> mc = new ArrayList<>();
		for (MorphPanel mp : morphPanels.values()) {
			mc.add(mp.getMorphConfiguration());
		}
		morphController.update(mc);
	}

	public void setMorphConfiguration(Collection<MorphConfiguration> rotations) {
		for (MorphConfiguration j : rotations) {
			MorphPanel rp = morphPanels.get(j.getName());
			rp.setMorphConfiguration(j);
		}
	}

	public Collection<MorphConfiguration> getMorphConfigurations() {
		Collection<MorphConfiguration> rotationConfigurations = new ArrayList<MorphConfiguration>();
		for (MorphPanel rp : morphPanels.values()) {
			rotationConfigurations.add(rp.getMorphConfiguration());
		}
		return rotationConfigurations;
	}

	public Collection<MorphConfiguration> getSelectedMorphConfigurations() {
		Collection<MorphConfiguration> rotationConfigurations = new ArrayList<MorphConfiguration>();
		for (MorphPanel rp : morphPanels.values()) {
			if (rp.useInKeyFrame()) {
				rotationConfigurations.add(rp.getMorphConfiguration());
			}
		}
		return rotationConfigurations;
	}
}
