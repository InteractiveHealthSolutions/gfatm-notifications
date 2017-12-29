package com.ihsinformatics.gfatmnotifications.ui;

import java.awt.*;
import java.awt.event.*;
import java.util.Stack;

import javax.swing.*;

import com.ihsinformatics.gfatmnotifications.model.Email;
import com.ihsinformatics.gfatmnotifications.model.UtilityCollection;

public class SwingControl {

	
	private JFrame mainFrame;
	   private JLabel headerLabel;
	   private JLabel statusLabel;
	   private JPanel controlPanel;

	   public SwingControl(){
	      prepareGUI();
	   }
	   public static void main(String[] args){
	      SwingControl  swingControlDemo = new SwingControl();      
	      swingControlDemo.showLabelDemo();
	   }
	   private void prepareGUI(){
	      mainFrame = new JFrame("Java Swing Examples");
	      mainFrame.setSize(400,400);
	      mainFrame.setLayout(new GridLayout(3, 1));
	      
	      mainFrame.addWindowListener(new WindowAdapter() {
	         public void windowClosing(WindowEvent windowEvent){
	            System.exit(0);
	         }        
	      });    
	      headerLabel = new JLabel("", JLabel.CENTER);        
	      statusLabel = new JLabel("",JLabel.CENTER);    
	      statusLabel.setSize(350,100);
	      controlPanel = new JPanel();
	      controlPanel.setLayout(new FlowLayout());

	      mainFrame.add(headerLabel);
	      mainFrame.add(controlPanel);
	      mainFrame.add(statusLabel);
	      mainFrame.setVisible(true);  
	   }
	   public void showLabelDemo(){
		   headerLabel.setText("GFATM Email Notifications");      
		      JLabel label  = new JLabel("", JLabel.CENTER);  
		      label.setText("Running");
		      label.setOpaque(true);
		      label.setBackground(Color.GRAY);
		      label.setForeground(Color.WHITE);
		      controlPanel.add(label);
		      mainFrame.setVisible(true); 
		   /*   String[] emails = new String[UtilityCollection.getEmailList().size()];
		      	for (int i = 0; i < UtilityCollection.getEmailList().size(); i++) {
					emails[i]= UtilityCollection.getEmailList().get(i).getEmailAdress();
				}
		      mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		      mainFrame.add(new JList(emails));
		      mainFrame.pack();
		      mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		      mainFrame.setLocationRelativeTo(null);
		      mainFrame.setVisible(true);*/
	   }  
	   
	   public void showLabelDemoAfter(){
		   headerLabel.setText("GFATM LIST OF EMAILS");      
		   
		      String[] emails = new String[UtilityCollection.getEmailList().size()];
		      	for (int i = 0; i < UtilityCollection.getEmailList().size(); i++) {
					emails[i]= UtilityCollection.getEmailList().get(i).getEmailAdress();
				}
		      mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		      mainFrame.add(new JList(emails));
		      mainFrame.pack();
		      mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		      mainFrame.setLocationRelativeTo(null);
		      mainFrame.setVisible(true);
	   }  

}
