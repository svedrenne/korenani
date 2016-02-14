package jp.osdn.korenani;

import static jp.osdn.i18n.I18n.gtxt;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import jp.osdn.korenani.Version;

/**
 * CheckUpdateDialog.java
 * 
 * @author svedrenne
 */
@SuppressWarnings("serial")
public class CheckUpdateDialog extends JDialog {

	private static final String VERSION_FILE_DOWNLOAD_WEB_SITE = "http://sourceforge.net/projects/korenani/files/korenani/1.2/LATEST/download";

	private JFrame parent;

	final CheckUpdateAction checkUpdateAction;

	private int result = -1; // to access from the EDT only

	private final SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {

		@Override
		protected String doInBackground() throws Exception {
			/* Executed in a background thread */
			final String str = getHttpLatestVersionString();

			return str;
		}

		@Override
		protected void done() {
			/* Executed in the EDT */
			result = -1;
			try {
				String httpVersionString = get();

				if (httpVersionString.equals(Version.versionString)) {
					result = 0;
				} else if (httpVersionString.startsWith("Korenani")) {
					checkUpdateAction.notifyNewVersionFound();
					result = 1;
				}
			} catch (InterruptedException e) {
			} catch (ExecutionException e) {
			} catch (CancellationException e) {
				result = -2;
			} finally {
				CheckUpdateDialog.this.dispose();
			}
		}

		private String getHttpLatestVersionString() {
			String line = null;
			BufferedReader dis = null;
			try {
				URL url;
				URLConnection urlConn;

				url = new URL(VERSION_FILE_DOWNLOAD_WEB_SITE);

				urlConn = url.openConnection();
				urlConn.setDoInput(true);
				urlConn.setUseCaches(false);

				dis = new BufferedReader(new InputStreamReader(
						urlConn.getInputStream()));

				line = dis.readLine();
			} catch (MalformedURLException mue) {
				mue.printStackTrace();
			} catch (UnknownHostException uhe) {
			} catch (IOException ioe) {
			} finally {
				try {
				    if (dis != null) {
				        dis.close();
				    }
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			String versionString = "";
			if (line != null) {
				String[] strs = line.split(" is the latest version.");
				if (strs.length >= 1) {
					versionString = strs[0];
				}
			}
			return versionString;
		}
	};

	public CheckUpdateDialog(JFrame parent,
			final CheckUpdateAction checkUpdateAction) {
		super(parent, true);
		this.checkUpdateAction = checkUpdateAction;
		this.parent = parent;
		initComponents();
		setResizable(false);
		worker.execute();
	}

	private void initComponents() {

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

		Container pane = getContentPane();
		GridLayout layout = new GridLayout(3, 1);
		pane.setLayout(layout);

		JLabel messageLbl1 = new JLabel("<html>" + "<table border=\"0\">"
				+ "<tr>" + "<td align=\"center\">"
				+ gtxt("Checking for available updates") + ".</td>"
				+ "</tr><html>");
		JLabel messageLbl2 = new JLabel("<html>" + "<table border=\"0\">"
				+ "<tr>" + "<td align=\"center\">" + gtxt("Please wait...")
				+ "</td>" + "</tr><html>");
		JButton cancelBtn = new JButton(gtxt("Cancel"));
		cancelBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		cancelBtn.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cancelButtonClicked();
			}
		});

		pane.add(messageLbl1);
		pane.add(messageLbl2);

		FlowLayout btnLayout = new FlowLayout(1);
		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(btnLayout);
		btnPanel.add(cancelBtn);
		pane.add(btnPanel);

		pack();
		setLocationRelativeTo(parent);
	}

	private void cancelButtonClicked() {
		worker.cancel(true);
	}

	/**
	 * @return <ul>
	 *         <li>0 if the version is up-to-date</li>
	 *         <li>1 if a new version is available</li>
	 *         <li>-1 if an error occurred</li>
	 *         <li>-2 if canceled by the user</li>
	 *         </ul>
	 * 
	 *         Must be executed in the EDT only (because it accesses
	 *         <i>result</i>).
	 */
	int getResult() {
		return result;
	}

}