package client.frame;

import client.util.FontManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ErrorPanel extends JPanel {

	public ErrorPanel(final AppFrame frame, String errorMessage) {
		setBackground(Color.decode("#8CABD8"));
		setLayout(null);
		
		JTextPane textPane = new JTextPane();
		textPane.setFont(FontManager.getCustomFont(18f));
		textPane.setBackground(Color.decode("#8CABD8"));
		textPane.setBounds(12, 161, 276, 94);
		add(textPane);
		textPane.setText(errorMessage);
		
		JButton btnNewButton = new JButton("돌아가기");
		btnNewButton.setFont(FontManager.getCustomFont(12f));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.changeToLogin();
			}
		});
		btnNewButton.setBackground(Color.DARK_GRAY);
		btnNewButton.setForeground(Color.WHITE);
		btnNewButton.setBounds(100, 296, 97, 23);
		add(btnNewButton);
	}
}
