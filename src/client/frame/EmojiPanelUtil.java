package client.frame;

import client.Sender;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class EmojiPanelUtil {

    public static JPanel createEmojiPanel(ChatPanel chatPanel) {
        JPanel emojiPanel = new JPanel();
        emojiPanel.setLayout(null);
        emojiPanel.setBounds(0, 0, 300, 60);
        // 배경색은 ChatPanel에서 설정
        emojiPanel.setVisible(false);

        // "emoji-"로 시작하고 ".png"로 끝나는 파일만 읽어옵니다.
        File emojiDir = new File("images");
        File[] files = emojiDir.listFiles((dir, name) -> name.matches("emoji-\\d+\\.png"));

        if (files == null) {
            return emojiPanel;
        }

        // 파일 리스트 정렬 (숫자 순서대로)
        ArrayList<File> emojiImages = new ArrayList<>();
        for (File f : files) {
            emojiImages.add(f);
        }
        emojiImages.sort((f1, f2) -> f1.getName().compareTo(f2.getName()));

        // 이모티콘 버튼 생성 및 추가
        int index = 0;
        for (File emojiFile : emojiImages) {
            ImageIcon icon = new ImageIcon(emojiFile.getPath());
            Image img = icon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            icon = new ImageIcon(img);

            JButton emojiButton = new JButton(icon);
            emojiButton.setBounds(index * 60 + 10, 10, 40, 40);
            emojiButton.setContentAreaFilled(false);
            emojiButton.setBorderPainted(false);

            // 클릭 시 이모티콘 이미지 전송
            emojiButton.addActionListener(e -> {
                String imagePath = "images/" + emojiFile.getName();
                Sender.getSender().sendImage(imagePath);
                emojiPanel.setVisible(false);
            });

            emojiPanel.add(emojiButton);
            index++;
        }

        return emojiPanel;
    }
}
