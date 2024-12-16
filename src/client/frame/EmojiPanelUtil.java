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

        File profileDir = new File("images/profile");
        File[] files = profileDir.listFiles((dir, name) -> name.startsWith("profile") && name.endsWith(".png"));

        if (files == null) {
            return emojiPanel;
        }

        ArrayList<File> profileImages = new ArrayList<>();
        for (File f : files) {
            profileImages.add(f);
        }
        profileImages.sort((f1, f2) -> f1.getName().compareTo(f2.getName()));

        int index = 0;
        for (File imageFile : profileImages) {
            ImageIcon icon = new ImageIcon(imageFile.getPath());
            Image img = icon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            icon = new ImageIcon(img);

            JButton emojiButton = new JButton(icon);
            emojiButton.setBounds(index * 60 + 10, 10, 40, 40);
            emojiButton.setContentAreaFilled(false);
            emojiButton.setBorderPainted(false);

            // 파일명에서 숫자 추출
            String fileName = imageFile.getName();
            String numberStr = fileName.replaceAll("[^0-9]", "");

            // 클릭 시 바로 이미지 전송
            emojiButton.addActionListener(e -> {
                String imagePath = "images/profile/profile" + numberStr + ".png";
                // 바로 전송
                Sender.getSender().sendImage(imagePath);
                // 패널을 다시 숨기기
                emojiPanel.setVisible(false);
            });

            emojiPanel.add(emojiButton);
            index++;
        }

        return emojiPanel;
    }
}
