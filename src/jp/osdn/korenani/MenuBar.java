package jp.osdn.korenani;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

@SuppressWarnings("serial")
public class MenuBar extends JMenuBar {

	private final ActionsRepository actions = new ActionsRepository();
	
	private final EditMenu editMenu;
	
	public EditMenu getEditMenu() {
		return editMenu;
	}
	
	MenuBar(JFrame parent) {
		add(new FileMenu(parent, actions));
		editMenu = new EditMenu(actions, parent);
		add(editMenu);
		add(new HelpMenu(actions, parent));
	}

	final ActionsRepository getActions() {
		return actions;
	}

}
