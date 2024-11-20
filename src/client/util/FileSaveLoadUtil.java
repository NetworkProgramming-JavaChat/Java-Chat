package client.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.UUID;


/*
 * 전송된 파일의 save, load
 * fileSave() - file을 save한 뒤 저장한 이름을 return
 * fileLoad() - byte 단위로 load 된 file을 return
 */

public class FileSaveLoadUtil {

	public static String fileSave(String extention, String path, byte[] file) {
		String fileName = UUID.randomUUID() + extention;

		// 디렉토리가 존재하지 않으면 생성
		File directory = new File(path);
		if (!directory.exists()) {
			directory.mkdirs();
		}

		try {
			// 바이트 배열을 BufferedImage로 변환
			BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(file));

			// 새로운 크기로 이미지 조정 (예: 100x100)
			BufferedImage resizedImage = new BufferedImage(150, 150, originalImage.getType());
			Graphics2D g = resizedImage.createGraphics();
			g.drawImage(originalImage, 0, 0, 150, 150, null);
			g.dispose();

			// 조정된 이미지를 파일로 저장
			File outputFile = new File(path + fileName);
			ImageIO.write(resizedImage, extention.replace(".", ""), outputFile);

			System.out.println("File saved at: " + path + fileName); // 경로 확인을 위한 디버그 출력
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileName;
	}


	public static byte[] fileLoad(String path) {
		File file = new File(path);
		byte[] loadFile = new byte[(int)file.length()];
		BufferedInputStream bufferedInputStream;
		try {
			bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
			bufferedInputStream.read(loadFile, 0, loadFile.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return loadFile;
	}
}
