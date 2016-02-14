package jp.osdn.korenani;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import static jp.osdn.i18n.I18n.gtxt;

@SuppressWarnings("serial")
public class CheckUpdateAction extends AbstractAction {

        private Action openUpdateSiteAction;

        private JFrame frame;

        public CheckUpdateAction(JFrame frame, Action openUpdateSiteAction) {
                this.frame = frame;
                this.openUpdateSiteAction = openUpdateSiteAction;
                performSilentCheck();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
                CheckUpdateDialog dlg = new CheckUpdateDialog(frame, this);
                dlg.setVisible(true);
                int upToDateStatus = dlg.getResult();
                if (upToDateStatus == 0) {
                        JOptionPane.showMessageDialog(frame, "<html>"
                                        + "<table border=\"0\">" + "<tr>"
                                        + gtxt("This version of Korenani is up-to-date.") + "</tr>"
                                        + "</html>", "Korenani", JOptionPane.PLAIN_MESSAGE);
                } else if (upToDateStatus == 1) {
                        openUpdateSiteAction.setEnabled(true);
                        NewVersionFoundDialog nvDlg = new NewVersionFoundDialog(frame);
                        nvDlg.setVisible(true);
                } else if (upToDateStatus == -1) {
                        JOptionPane.showMessageDialog(frame, "<html>"
                                        + "<table border=\"0\">" + "<tr>"
                                        + gtxt("Unable to retrieve update information.<br/><br/>Please check on the following website<br/>if a new version of Korenani is available:<br/><br/>")
                                        + "http://sourceforge.net/projects/korenani/files/korenani"
                                        + "</tr>" + "</html>", "Korenani",
                                        JOptionPane.WARNING_MESSAGE);
                } else {
                        /*
                         * CheckUpdateAction.actionPerformed() CANCELLED");
                         */
                }
        }

        public void performSilentCheck() {
                CheckUpdateDialog dlg = new CheckUpdateDialog(frame, this);
                dlg.setVisible(false);
                // Here I'm supposing that the SwingWorker created by the
                // CheckUpdateDialog will live on and from its done() method will notify
                // this CheckUpdateAction when ready.
        }

        void notifyNewVersionFound() {
        openUpdateSiteAction.setEnabled(true);
        }

}
