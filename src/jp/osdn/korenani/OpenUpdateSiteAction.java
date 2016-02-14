package jp.osdn.korenani;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class OpenUpdateSiteAction extends AbstractAction {

	private JFrame frame;

	public OpenUpdateSiteAction(JFrame frame) {
		this.frame = frame;
		setEnabled(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		NewVersionFoundDialog nvDlg = new NewVersionFoundDialog(frame);
		nvDlg.setVisible(true);
	}

}
