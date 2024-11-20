package client.frame;

import client.Sender;
import client.util.*;
import model.ChatCommand;
import model.Message;
import model.TypeOfMessage;
import client.util.FontManager;

import javax.swing.*;
import javax.swing.text.html.HTMLDocument;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

/*
 * 실제로 채팅이 이뤄지는 패널
 */
public class ChatPanel extends JPanel {

	JScrollPane chatScrollPane;
	JList userList;
	JTextPane chatTextPane;
	JTextArea txtrMessage;
	HTMLDocument doc;
	DefaultListModel<String> userListModel = new DefaultListModel<>();
	private StringBuffer messageList = new StringBuffer();
	private boolean isOpenList = false;
	private StringBuffer chatLog = new StringBuffer();
	private HTMLMaker htmlMaker = new HTMLMaker();

	public ChatPanel() {
		setLayout(null);

		chatTextPane = new JTextPane();
		chatTextPane.setFont(FontManager.getCustomFont(15f)); // 폰트 적용
		txtrMessage = new JTextArea();
		txtrMessage.setFont(FontManager.getCustomFont(15f)); // 폰트 적용

		JPanel chatBoardPane = new JPanel();
		chatBoardPane.setBackground(Color.decode("#8CABD8"));
		chatBoardPane.setBounds(0, 0, 300, 440);
		add(chatBoardPane);
		chatBoardPane.setLayout(null);

		chatScrollPane = new JScrollPane();
		chatScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		chatScrollPane.setBounds(0, 45, 300, 395);
		chatBoardPane.add(chatScrollPane);

		chatTextPane.setBackground(Color.decode("#8CABD8"));
		chatScrollPane.setViewportView(chatTextPane);
		chatTextPane.setText("");

		userList = new JList(userListModel);
		userList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (isDoubleClicked(e)) {
					setWhisperCommand(userList.getSelectedValue().toString());
				}
			}
		});
		userList.setBackground(Color.WHITE);
		userList.setFont(FontManager.getCustomFont(15f)); // 폰트 적용
		chatScrollPane.setColumnHeaderView(userList);
		userList.setVisible(false);
		userList.setVisibleRowCount(0);
		userList.setAutoscrolls(true);

		ImageIcon userListIcon = new ImageIcon("images/userlist-bar.png");
		Image scaledUserListImage = userListIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
		JLabel lblUserList = new JLabel(new ImageIcon(scaledUserListImage));

		lblUserList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				userListControl();
			}
		});
		lblUserList.setHorizontalAlignment(SwingConstants.CENTER);
		lblUserList.setBounds(12, 0, 40, 40);
		chatBoardPane.add(lblUserList);

		chatTextPane.setContentType("text/html");
		doc = (HTMLDocument) chatTextPane.getStyledDocument();

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 450, 189, 70);
		add(scrollPane);

		txtrMessage.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (isEnter(e)) {
					pressEnter(txtrMessage.getText().replaceAll("\n", ""));
				}
			}
		});
		txtrMessage.setLineWrap(true);
		txtrMessage.setWrapStyleWord(true);
		scrollPane.setViewportView(txtrMessage);

		JButton btnNewButton = new JButton("전송");
		btnNewButton.setFont(FontManager.getCustomFont(15f)); // 폰트 적용
		btnNewButton.setBackground(Color.decode("#8CABD8"));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pressEnter(txtrMessage.getText());
			}
		});
		btnNewButton.setBounds(211, 450, 65, 35);
		add(btnNewButton);

		JLabel lblImage = new JLabel();
		ImageIcon originalIcon = new ImageIcon("images/image.png");
		Image scaledImage = originalIcon.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
		lblImage.setIcon(new ImageIcon(scaledImage));
		lblImage.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				sendImage();
			}
		});
		lblImage.setBounds(211, 490, 30, 30);
		add(lblImage);
	}

	private void setWhisperCommand(String whisperTarget) {
		txtrMessage.setText(ChatCommand.WHISPER + " " + whisperTarget + " ");
	}

	private boolean isDoubleClicked(MouseEvent e) {
		return e.getClickCount() == 2;
	}

	private void userListControl() {
		if (isOpenList) {
			userListClose();
		} else {
			userListOpen();
		}
	}

	private void userListOpen() {
		setUserList();
		userList.setVisible(true);
		userList.setVisibleRowCount(8);
		isOpenList = true;
	}

	private void setUserList() {
		userListModel.clear();
		for (String userName : UserList.getUsernameList()) {
			userListModel.addElement(userName);
		}
	}

	private void userListClose() {
		userList.setVisible(false);
		userList.setVisibleRowCount(0);
		isOpenList = false;
	}

	private boolean isEnter(KeyEvent e) {
		return e.getKeyCode() == KeyEvent.VK_ENTER;
	}

	private void pressEnter(String userMessage) {
		if (isNullString(userMessage)) {
			return;
		} else if (isWhisper(userMessage)) {
			sendWhisper(userMessage);
		} else if (isSearch(userMessage)) {
			sendSearch(userMessage);
		} else {
			sendMessage(userMessage);
		}
		txtrMessage.setText("");
		txtrMessage.setCaretPosition(0);
	}

	private void sendWhisper(String userMessage) {
		String whisperTarget = userMessage.split(" ", 3)[1];
		String sendingMessage = userMessage.replaceAll(ChatCommand.WHISPER + " " + whisperTarget, "");
		Sender.getSender().sendWhisper(sendingMessage, whisperTarget);
	}

	private void sendSearch(String userMessage) {
		Sender.getSender().sendSearch(userMessage);
	}

	private void sendMessage(String userMessage) {
		Sender.getSender().sendMessage(userMessage);
	}

	private boolean isNullString(String userMessage) {
		return userMessage == null || userMessage.equals("");
	}

	private boolean isWhisper(String text) {
		return text.startsWith(ChatCommand.WHISPER.toString());
	}

	private boolean isSearch(String userMessage) {
		return userMessage.startsWith(ChatCommand.SEARCH.toString());
	}

	private void sendImage() {
		String imagePath = FileChooserUtil.getFilePath();
		if (imagePath == null) {
			return;
		} else if (imagePath.endsWith("png") || imagePath.endsWith("jpg")) {
			Sender.getSender().sendImage(imagePath);
		} else {
			JOptionPane.showMessageDialog(null, ".png, .jpg 확장자 파일만 전송 가능합니다.");
		}
	}

	public void addMessage(String adminMessage) {
		messageList.append(htmlMaker.getHTML(adminMessage));
		rewriteChatPane();
		addChatLog(adminMessage);
	}

	public void addMessage(boolean isMine, Message message) {
		boolean isWhisper = !isMine && isWhisperMessage(message);
		boolean isWhisperSent = isMine && isWhisperMessage(message);

		messageList.append(htmlMaker.getHTML(isMine, message, isWhisper || isWhisperSent));
		rewriteChatPane();
		addChatLog(message.getName(), message.getMessage());
	}

	private boolean isWhisperMessage(Message message) {
		return message.getType() == TypeOfMessage.WHISPER;
	}

	private void rewriteChatPane() {
		chatTextPane.setText(messageList.toString());
		chatTextPane.setCaretPosition(doc.getLength());
	}

	private void addChatLog(String adminMessage) {
		chatLog.append(adminMessage + "\r\n");
	}

	private void addChatLog(String userName, String userMsg) {
		chatLog.append(userName + " : " + userMsg + "\r\n");
	}
}