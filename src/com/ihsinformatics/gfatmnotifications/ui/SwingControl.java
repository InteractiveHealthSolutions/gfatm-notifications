package com.ihsinformatics.gfatmnotifications.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

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
	      headerLabel.setText("GFATM Notifications");      
	      JLabel label  = new JLabel("", JLabel.CENTER);        
	      label.setText("Running");
	      label.setOpaque(true);
	      label.setBackground(Color.GRAY);
	      label.setForeground(Color.WHITE);
	      controlPanel.add(label);
	      
	      mainFrame.setVisible(true);  
	   }
	
	
	
}
