package jp.osdn.korenani;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class AboutAction extends AbstractAction {
	
	private JFrame parent;
	
	public AboutAction(JFrame parent) {
		this.parent = parent;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		AboutDialog dlg = new AboutDialog(parent);
		dlg.setVisible(true);
	}


}
