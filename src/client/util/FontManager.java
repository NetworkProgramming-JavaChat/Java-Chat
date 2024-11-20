package client.util;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class FontManager {
    private static Font customFont;

    static {
        try {
            // 폰트 파일 경로를 지정하고 폰트를 로드
            customFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/omyu pretty.ttf"));
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            // 폰트를 로드할 수 없는 경우 기본 폰트 사용
            customFont = new Font("Arial", Font.PLAIN, 12);
        }
    }

    public static Font getCustomFont(float size) {
        return customFont.deriveFont(size);
    }
}
