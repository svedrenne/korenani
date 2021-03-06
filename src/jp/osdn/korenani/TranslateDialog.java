package jp.osdn.korenani;

import static jp.osdn.i18n.I18n.gtxt;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class TranslateDialog extends JDialog {

	private Frame parent;

	public TranslateDialog(Frame parent) {
		super(parent, true);
		this.parent = parent;
		initComponents();
		// setResizable(false);
		setTitle(gtxt("Translate this application"));
		pack();
	}

	private void initComponents() {
		URI korenaniURI = null;
		try {
			korenaniURI = new URI("http://sourceforge.net/projects/korenani/forums/forum/1801058");
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}

		JPanel panel = makeInfoPanel(korenaniURI);



		Dimension parentDim = parent.getPreferredSize();
		Dimension dim = new Dimension();
		dim.setSize(parentDim.getHeight() * 1.75, parentDim.getWidth() * 1.25);
		add(panel);
		pack();
		setLocationRelativeTo(parent);
	}

	protected JPanel makeInfoPanel(final URI korenaniURI) {
		JPanel panel = new JPanel(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JPanel feedbackPanel = new JPanel(false);
		String feedbackStr = "<html>"
				+ "<table border=\"0\">"
				+ "<tr>"
				+ "</tr>"
				+ "<tr>"
				+ gtxt("You can easily translate Korenani into your own language!")+"<br/>"
				+"<br/>"
				+ "</tr>"
				+ "<tr>"
				+ gtxt("Propose your help and get information on how to proceed<br/> on the Translators Forum hosted by Sourceforge:")+"<br/>"
				+ "</tr>"
				+ "<tr>"
				+ "</tr>" + "<tr>" + "</tr>" + "</table>" + "</html>";
		JLabel label = new JLabel(feedbackStr);
		feedbackPanel.add(label);

		panel.add(feedbackPanel);

		JPanel linkPanel = new JPanel(false);
		JButton linkButton = new JButton();

		linkButton
				.setText("<HTML><FONT color=\"#000099\"><U>"+gtxt("Korenani Translators Forum")+"</U></FONT></HTML>");
		linkButton.setHorizontalAlignment(SwingConstants.CENTER);
		linkButton.setBorderPainted(false);
		linkButton.setOpaque(false);
		linkButton.setBackground(Color.WHITE);
		linkButton.setToolTipText(korenaniURI.toString());
		linkButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TranslateDialog.this.open(korenaniURI);
			}
		});
		linkButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		linkPanel.add(linkButton);
		panel.add(linkPanel);

		return panel;
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
			// TODO: error handling
		}
	}

}
