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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	// 레이어드 팬 사용
	private JLayeredPane layeredPane;

	// 이모티콘 패널 관련
	private JPanel emojiPanel; // 이모티콘 패널
	private boolean isEmojiPanelVisible = false;

	public ChatPanel() {
		setLayout(null);
		setBackground(Color.WHITE);
		setBounds(0,0,300,600);

		layeredPane = new JLayeredPane();
		layeredPane.setLayout(null);
		layeredPane.setBounds(0,0,300,600);
		add(layeredPane);

		chatTextPane = new JTextPane();
		chatTextPane.setFont(FontManager.getCustomFont(15f));
		txtrMessage = new JTextArea();
		txtrMessage.setFont(FontManager.getCustomFont(15f));

		JPanel chatBoardPane = new JPanel();
		chatBoardPane.setBackground(Color.decode("#8CABD8"));
		chatBoardPane.setBounds(0, 0, 300, 440);
		chatBoardPane.setLayout(null);
		layeredPane.add(chatBoardPane, JLayeredPane.DEFAULT_LAYER);

		chatScrollPane = new JScrollPane();
		chatScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		chatScrollPane.setBounds(0, 45, 300, 395);
		chatBoardPane.add(chatScrollPane);

		chatTextPane.setBackground(Color.decode("#8CABD8"));
		chatScrollPane.setViewportView(chatTextPane);
		chatTextPane.setText("");
		chatTextPane.setContentType("text/html");
		doc = (HTMLDocument) chatTextPane.getStyledDocument();

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
		userList.setFont(FontManager.getCustomFont(15f));
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

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 450, 189, 70);
		layeredPane.add(scrollPane, JLayeredPane.DEFAULT_LAYER);

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
		btnNewButton.setFont(FontManager.getCustomFont(15f));
		btnNewButton.setBackground(Color.decode("#8CABD8"));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pressEnter(txtrMessage.getText());
			}
		});
		btnNewButton.setBounds(211, 450, 65, 35);
		layeredPane.add(btnNewButton, JLayeredPane.DEFAULT_LAYER);

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
		layeredPane.add(lblImage, JLayeredPane.DEFAULT_LAYER);

		ImageIcon emoticonIcon = new ImageIcon("images/emoticon.png");
		Image scaledEmoticonImage = emoticonIcon.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
		JLabel lblImoticon = new JLabel(new ImageIcon(scaledEmoticonImage));
		lblImoticon.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				toggleEmojiPanel();
			}
		});
		lblImoticon.setBounds(246, 490, 30, 30);
		layeredPane.add(lblImoticon, JLayeredPane.DEFAULT_LAYER);

		// 이모티콘 패널 초기화
		emojiPanel = EmojiPanelUtil.createEmojiPanel(this);
		// 매개변수를 txtrMessage 대신 ChatPanel 자신을 넘겨서 EmojiPanelUtil이 이미지 전송 시 ChatPanel 메서드 호출 가능
		emojiPanel.setBounds(0, 390, 300, 60);
		emojiPanel.setBackground(new Color(255,255,255,180)); // 흰색 반투명
		emojiPanel.setOpaque(true);
		layeredPane.add(emojiPanel, JLayeredPane.PALETTE_LAYER);
	}

	private void toggleEmojiPanel() {
		isEmojiPanelVisible = !isEmojiPanelVisible;
		emojiPanel.setVisible(isEmojiPanelVisible);
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

	public void pressEnter(String userMessage) {
		if (isNullString(userMessage)) {
			return;
		}
		// 기존 로직 그대로 유지
		Pattern pattern = Pattern.compile(":profile(\\d+):");
		Matcher matcher = pattern.matcher(userMessage.trim());

		if (matcher.matches()) {
			// 이모티콘 이미지 전송
			String index = matcher.group(1);
			String imagePath = "images/profile/profile" + index + ".png";
			Sender.getSender().sendImage(imagePath);
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
