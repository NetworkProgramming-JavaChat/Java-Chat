package client.util;

import model.FilePath;
import model.Message;
import model.TypeOfMessage;

import java.util.HashMap;

/*
 * 채팅 패널에 출력되는 HTML 태그를 작성합니다.
 * getHTML(String adminMessage) - 입장, 퇴장 메세지 출력
 * getHTML(boolean isMine, Message message) - 일반 메세지 / 이미지 출력
 */
public class HTMLMaker {

	private HashMap<TypeOfMessage, String> userMsg = new HashMap<>();
	private HashMap<Boolean, String> align = new HashMap<>();
	private HashMap<Boolean, String> profile = new HashMap<>();
	private HashMap<Boolean, String> background = new HashMap<>();
	private String customFontName;

	public HTMLMaker() {
		align.put(true, "right");
		align.put(false, "left");
		background.put(true, "#79E278");
		background.put(false, "white");

		// FontManager로부터 폰트 이름 가져오기
		customFontName = FontManager.getCustomFont(12f).getFamily();
	}

	public String getHTML(String adminMessage) {
		String htmlResult = "<div style=\"text-align:center;padding: 3px; margin: 3px;\">"
				+ "				<span style=\" color: white; font-size:12px; font-family: '" + customFontName + "';\">"
				+ adminMessage
				+ "				</span></div>";
		return htmlResult;
	}

	public String getHTML(boolean isMine, Message message, boolean isWhisper) {
		TypeOfMessage messageType = message.getType();
		String userName = message.getName();
		String userMessage = message.getMessage();
		int profileNum = UserList.getUser(userName).getProfileNum();
		setProfile(isMine, userName, profileNum);
		setUserMsg(messageType, userMessage);
		userMessage = getMsgBox(userMessage);

		// 귓속말을 받을 경우 배경색을 변경
		String bgColor = isWhisper ? "#FFACB7" : background.get(isMine);

		String htmlResult =
				"<div style=\"text-align:" + align.get(isMine) + ";\">"
						+ "    <table style=\"font-size:12px; font-family: '" + customFontName + "';\">"
						+ "        <tr>"
						+ profile.get(isMine)
						+ "        </tr>"
						+ "        <tr>"
						+ "            <td style=\"text-align:left;\">"
						+ "                <div style=\"background-color: " + bgColor + "; padding: 5px;\">"
						+ userMsg.getOrDefault(messageType, userMessage)
						+ "                </div>"
						+ "            </td>"
						+ "        </tr>"
						+ "    </table>"
						+ "</div>";

		return htmlResult;
	}

	private void setProfile(boolean isMine, String userName, int profileNum) {
		String right =
				"	    	<th style=\"text-align:" + align.get(isMine) + ";\">"
						+ userName
						+ "			</th>"
						+ "   	    <th rowspan=\"2\">"
						+ "				<img src=\"file:" + FilePath.PROFILEPATH + "profile" + profileNum + ".png\" height=\"40\" width=\"40\">"
						+ "			</th>";
		profile.put(true, right);

		String left =
				"   	    <th rowspan=\"2\">"
						+ "				<img src=\"file:" + FilePath.PROFILEPATH + "profile" + profileNum + ".png\" height=\"40\" width=\"40\">"
						+ "			</th>"
						+ "	    	<th style=\"text-align:" + align.get(isMine) + ";\">"
						+ userName
						+ "			</th>";
		profile.put(false, left);
	}

	private void setUserMsg(TypeOfMessage messageType, String userMessage) {
		// 절대 경로를 사용하여 이미지 경로 설정
		String imgPath = String.format("file:%s/download/%s", System.getProperty("user.dir"), userMessage);
		// 이미지 태그 생성
		String imgTag = String.format("<img src=\"%s\">", imgPath);
		userMsg.put(TypeOfMessage.IMAGE, imgTag);
	}

	private String getMsgBox(String userMessage) {
		StringBuilder ret = new StringBuilder();
		for (int i = 0; i < userMessage.length(); i++) {
			ret.append(userMessage.charAt(i));
			if (i != 0 && i % 15 == 0) {
				ret.append("<br>");
			}
		}
		return ret.toString();
	}

}
