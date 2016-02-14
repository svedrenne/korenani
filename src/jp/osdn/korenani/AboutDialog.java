/*
 * Korenani - essential sudoku game
 * Copyright (C) 2007-2013 Sylvain Vedrenne
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jp.osdn.korenani;

import static jp.osdn.i18n.I18n.gtxt;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import jp.osdn.korenani.resources.UIResources;

@SuppressWarnings("serial")
public class AboutDialog extends JDialog {

	private Frame parent;

	private final String ABOUT_KORENANI = gtxt("About Korenani...");
	private final String INFORMATION = gtxt("Information");
	private final String CONTRIBUTORS = gtxt("Contributors");
	private final String TRANSFER = gtxt("Transfer");
	private final String LICENSE = gtxt("License");

	public AboutDialog(Frame parent) {
		super(parent, true);
		this.parent = parent;
		initComponents();
		setTitle(ABOUT_KORENANI);
		pack();
	}

	private void initComponents() {
		URI KorenaniURI = null;
		try {
			KorenaniURI = new URI("http://Korenani.sourceforge.net");
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab(INFORMATION, null, makeInfoPanel(KorenaniURI),
				INFORMATION);

		tabbedPane.addTab(CONTRIBUTORS, null, makeTextPanel("<html><body>"
				+ "<table border=\"0\"><tr></tr>"
				
				+ "<tr><td align=\"right\"><b>"
				+ gtxt("Sylvain Vedrenne")
				+ "</b></td><td></td><td align=\"left\">"
				+ gtxt("Lead developer")
				
				+ "</td>" + "</tr>" + "</table>"
				+ "</body></html>"), CONTRIBUTORS);

		tabbedPane
				.addTab(TRANSFER,
						null,
						makeTextPanel("<html><body>"
								+ "<p>"
								+ gtxt("<b>Korenani</b> is released under the terms of the GNU General Public License version 3 or later (GPL v3+).")
								+ "<br/>"
								+ "</p>"
								+ "<p>"
								+ gtxt("The full license text is available in the file called COPYING that must be included in every copy of the program.")
								+ "<br/>"
								+ "</p>"
								+ "<p>"
								+ gtxt("This program is Free Software (\"Free\" as in \"Freedom\") developped during the author's free-time in the hope that some users will find it useful, but WITHOUT ANY WARRANTY of any kind.")
								+ "<br/>"
								+ "</p>"
								+ "<p>"
								+ gtxt("You are welcome to transfer this program to other people as long as you respect the license terms. Read the GNU General Public License for more details.")
								+ "<br/>" + "</p>" + "<p>"
								+ gtxt("Copyright (C) 2007-2013 Sylvain Vedrenne")
								+ "<br/>" + "</p>" + "</body></html>"),
						TRANSFER); // tooltip text

		String license_html = "<html> <table border=\"0\"> <tr> <td>Please visit:</td> </tr><tr> <td>http://www.gnu.org/licenses/gpl.html</td> </tr>";
		try {
			license_html = readTextFile("text/COPYING.html");
		} catch (IOException e) {
		}
		JEditorPane editPane = new JEditorPane("text/html", license_html);
		editPane.setEditable(false);
		{
			Color bgColor = new JPanel().getBackground();
			editPane.setBackground(new Color(bgColor.getRed(), bgColor
					.getGreen(), bgColor.getBlue()));
		}
		editPane.setCaretPosition(0);
		JScrollPane scrollPane = new JScrollPane(editPane);
		tabbedPane.addTab(LICENSE, null, scrollPane, LICENSE);

		Dimension parentDim = parent.getPreferredSize();
		Dimension dim = new Dimension();
		double width = parentDim.getHeight() * 1.2;
		double height = parentDim.getWidth() * 1.3;
		dim.setSize(width, height);
		tabbedPane.setPreferredSize(dim);
		add(tabbedPane);
		pack();
		setLocationRelativeTo(parent);
	}

	protected JComponent makeTextPanel(String text) {
		JEditorPane editPane = new JEditorPane("text/html", text);
		{
			Color bgColor = new JPanel().getBackground();
			editPane.setBackground(new Color(bgColor.getRed(), bgColor
					.getGreen(), bgColor.getBlue()));
		}
		editPane.setEditable(false);
		editPane.setCaretPosition(0);
		JScrollPane scrollPane = new JScrollPane(editPane);
		return scrollPane;
	}

	protected JComponent makeInfoPanel(final URI KorenaniURI) {
		JPanel textPanel = new JPanel();
		JEditorPane infoPane = new JEditorPane("text/html", "<html>"
				+ "<table border=\"0\">" + "<tr>" + "<td align=\"center\"><b>"
				+ Version.versionString + "</b></td>" + "</tr>" + "<tr>"
				+ "<td align=\"center\">"
				+ gtxt("Copyright (C) 2013-2016 Sylvain Vedrenne") + "</td>"
				+ "</tr>" + "</table>" + "</html>");
		{
			Color bgColor = new JPanel().getBackground();
			infoPane.setBackground(new Color(bgColor.getRed(), bgColor
					.getGreen(), bgColor.getBlue()));
		}
		infoPane.setEditable(false);
		infoPane.setCaretPosition(0);

		JPanel panel = new JPanel(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JPanel iconPanel = new JPanel(false);
		ImageIcon icon = Images.ICON_APPLICATION_LOGO;
		JLabel iconLabel = new JLabel(icon);
		iconPanel.add(iconLabel);
		panel.add(iconPanel);

		textPanel.add(infoPane);
		panel.add(textPanel);

		JPanel linkPanel = new JPanel(false);
		JButton linkButton = new JButton();

		linkButton.setText("<HTML><FONT color=\"#000099\"><U>Korenani "
				+ gtxt("on the Web") + "</U></FONT></HTML>");
		linkButton.setHorizontalAlignment(SwingConstants.CENTER);
		linkButton.setBorderPainted(false);
		linkButton.setOpaque(false);
		linkButton.setToolTipText(KorenaniURI.toString());
		linkButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AboutDialog.this.open(KorenaniURI);
			}
		});
		linkButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		linkPanel.add(linkButton);
		panel.add(linkPanel);

		JScrollPane scrollPaneFeedback = new JScrollPane(panel);
		return scrollPaneFeedback;
	}

	protected JComponent makeFeedbackPanel(final URI forumURI) {
		JPanel panel = new JPanel(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		String feedbackStr = "<html>"
				+ "<body><p></p><p>"
				+ gtxt("Feel free to request features and report bugs<br/>on the Korenani Forums hosted by Sourceforge!")
				+ "</p></body>" + "</html>";
		JEditorPane feedbackPane = new JEditorPane("text/html", feedbackStr);
		feedbackPane.setEditable(false);
		feedbackPane.setCaretPosition(0);
		Color bgColor = panel.getBackground();
		feedbackPane.setBackground(new Color(bgColor.getRed(), bgColor
				.getGreen(), bgColor.getBlue()));

		JPanel feedbackPanel = new JPanel(false);
		feedbackPanel.add(feedbackPane);

		panel.add(feedbackPanel);

		JPanel linkPanel = new JPanel(false);
		JButton linkButton = new JButton();

		linkButton.setText("<HTML><FONT color=\"#000099\"><U>"
				+ gtxt("Korenani Forums") + "</U></FONT></HTML>");
		linkButton.setHorizontalAlignment(SwingConstants.CENTER);
		linkButton.setBorderPainted(false);
		linkButton.setOpaque(false);
		linkButton.setToolTipText(forumURI.toString());
		linkButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AboutDialog.this.open(forumURI);
			}
		});
		linkButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		linkPanel.add(linkButton);
		panel.add(linkPanel);

		JScrollPane scrollPaneFeedback = new JScrollPane(panel);
		return scrollPaneFeedback;
	}

	private void open(URI uri) {
		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			try {
				desktop.browse(uri);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Desktop.isDesktopSupported() returned false");
		}
	}

	private static String readTextFile(String path) throws IOException {

		InputStream is = UIResources.class.getResourceAsStream(path);
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		StringBuffer sb = new StringBuffer();
		String line;
		while ((line = br.readLine()) != null) {
			sb.append(line + "\n");
		}
		br.close();
		isr.close();
		is.close();
		return sb.toString();
	}

}
