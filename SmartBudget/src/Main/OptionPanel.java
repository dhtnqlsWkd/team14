package Main;

import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.GridLayout;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Color;

public class OptionPanel extends JPanel {

   JButton graph1;
   JButton graph2;
   
   public OptionPanel() {
      
      setLayout(null);
      
      graph1 = new JButton("\uC9C0\uCD9C \uADF8\uB798\uD504");
      graph1.setBounds(0, 61, 109, 34);
      add(graph1);
      
      graph2 = new JButton("\uC9C0\uCD9C \uBD84\uC11D");
      graph2.setFont(new Font("����", Font.PLAIN, 15));
      graph2.setBounds(0, 105, 109, 34);
      add(graph2);
      
      JLabel lblNewLabel = new JLabel("mart");
      lblNewLabel.setBackground(Color.LIGHT_GRAY);
      lblNewLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 65));
      lblNewLabel.setBounds(168, 12, 147, 127);
      lblNewLabel.setOpaque(true);
      add(lblNewLabel);
      
      JLabel lblNewLabel_1 = new JLabel("S");
      lblNewLabel_1.setForeground(Color.RED);
      lblNewLabel_1.setBackground(Color.LIGHT_GRAY);
      lblNewLabel_1.setFont(new Font("Comic Sans MS", Font.PLAIN, 65));
      lblNewLabel_1.setBounds(123, 12, 45, 127);
      lblNewLabel_1.setOpaque(true);
      add(lblNewLabel_1);
      
      JLabel lblNewLabel_2 = new JLabel("B");
      lblNewLabel_2.setForeground(Color.BLUE);
      lblNewLabel_2.setFont(new Font("Comic Sans MS", Font.PLAIN, 65));
      lblNewLabel_2.setBackground(Color.LIGHT_GRAY);
      lblNewLabel_2.setBounds(314, 12, 45, 127);
      lblNewLabel_2.setOpaque(true);
      add(lblNewLabel_2);
      
      JLabel lblNewLabel_3 = new JLabel("udget");
      lblNewLabel_3.setFont(new Font("Comic Sans MS", Font.PLAIN, 65));
      lblNewLabel_3.setBackground(Color.LIGHT_GRAY);
      lblNewLabel_3.setBounds(359, 12, 177, 127);
      lblNewLabel_3.setOpaque(true);
      add(lblNewLabel_3);
   }

}