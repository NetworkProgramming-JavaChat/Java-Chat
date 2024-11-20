package client.frame;

import client.Listener;
import client.util.FontManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class LoginPanel extends JPanel {
	
	private JTextField txtName;
	private int profileNum = 0;
	ImageIcon profileImage = getProfileImage();
	JLabel lblProfile;

	public LoginPanel(final AppFrame frame) {
		
		setBackground(Color.decode("#8CABD8"));
		setLayout(null);
		
		JLabel lblTitle = new JLabel("Java-Chat");
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setFont(FontManager.getCustomFont(34f));
		lblTitle.setBounds(12, 100, 276, 46);
		add(lblTitle);
		
		txtName = new JTextField();
		txtName.setFont(FontManager.getCustomFont(14f));
		txtName.setHorizontalAlignment(SwingConstants.CENTER);
		txtName.setBounds(82, 366, 136, 27);
		add(txtName);
		txtName.setColumns(10);
		
		JButton btnLogin = new JButton("로그인");
		btnLogin.setForeground(Color.WHITE);
		btnLogin.setBackground(Color.DARK_GRAY);
		btnLogin.setFont(FontManager.getCustomFont(14f));
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (txtName.getText() != null && !txtName.getText().equals("")) {
					new Thread(new Listener(frame)).start();
				}
			}
		});
		btnLogin.setBounds(82, 403, 136, 27);
		add(btnLogin);
		
		lblProfile = new JLabel(profileImage);
		lblProfile.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				ProfileFrame profileFrame = new ProfileFrame(frame);
				profileFrame.setVisible(true);
			}
		});
		lblProfile.setBounds(70, 175, 160, 160);
		add(lblProfile);
	}
	
	public void changeProfileImage(int index) {
		profileNum = index;
		profileImage = getProfileImage();
		lblProfile.setIcon(profileImage);
	}
	
	private ImageIcon getProfileImage() {
		return new ImageIcon(new ImageIcon(ProfileFrame.PROFILEPATH + "/profile"+profileNum+".png").getImage().getScaledInstance(160, 160, java.awt.Image.SCALE_SMOOTH));
	}

	public String getTxtName() {
		return txtName.getText();
	}

	public void setTxtName(JTextField txtName) {
		this.txtName = txtName;
	}
	
	public int getProfileNum() {
		return profileNum;
	}
}
