package com.ihsinformatics.gfatmnotifications.ui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.ihsinformatics.gfatmnotifications.util.UtilityCollection;

public class SwingControl {

	private JFrame	mainFrame;
	private JLabel	headerLabel;
	private JLabel	statusLabel;
	private JPanel	controlPanel;

	public SwingControl() {
		prepareGUI();
	}

	public static void main(String[] args) {
		SwingControl swingControlDemo = new SwingControl();
		swingControlDemo.showLabelDemo();
	}

	private void prepareGUI() {
		mainFrame = new JFrame("Java Swing Examples");
		mainFrame.setSize(400, 400);
		mainFrame.setLayout(new GridLayout(3, 1));

		mainFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				System.exit(0);
			}
		});
		headerLabel = new JLabel("", SwingConstants.CENTER);
		statusLabel = new JLabel("", SwingConstants.CENTER);
		statusLabel.setSize(350, 100);
		controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout());

		mainFrame.add(headerLabel);
		mainFrame.add(controlPanel);
		mainFrame.add(statusLabel);
		mainFrame.setVisible(true);
	}

	public void showLabelDemo() {
		headerLabel.setText("GFATM Email Notifications");
		JLabel label = new JLabel("", SwingConstants.CENTER);
		label.setText("Running");
		label.setOpaque(true);
		label.setBackground(Color.GRAY);
		label.setForeground(Color.WHITE);
		controlPanel.add(label);
		mainFrame.setVisible(true);
		/*
		 * String[] emails = new
		 * String[UtilityCollection.getEmailList().size()]; for (int i = 0; i <
		 * UtilityCollection.getEmailList().size(); i++) { emails[i]=
		 * UtilityCollection.getEmailList().get(i).getEmailAdress(); }
		 * mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); mainFrame.add(new
		 * JList(emails)); mainFrame.pack();
		 * mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 * mainFrame.setLocationRelativeTo(null); mainFrame.setVisible(true);
		 */
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void showLabelDemoAfter() {
		headerLabel.setText("GFATM LIST OF EMAILS");

		String[] emails = new String[UtilityCollection.getInstance().getEmailList().size()];
		for (int i = 0; i < UtilityCollection.getInstance().getEmailList().size(); i++) {
			emails[i] = UtilityCollection.getInstance().getEmailList().get(i)
					.getEmailAdress();
		}
		mainFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
		mainFrame.add(new JList(emails));
		mainFrame.pack();
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setVisible(true);
	}

}
