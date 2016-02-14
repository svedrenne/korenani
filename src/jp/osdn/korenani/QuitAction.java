package jp.osdn.korenani;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

@SuppressWarnings("serial")
public class QuitAction extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		System.exit(0);
	}

}
