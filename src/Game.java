import javax.swing.JFrame;

public class Game { // 게임 시작 파일

	public static void main(String[] args) {
		
		JFrame jf = new JFrame("Galaxy Shooter"); //jf 프레임 생성
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //닫기 버튼 활성화
		
		jf.setContentPane(new GamePanel()); // GamePanel 클래스를 통한 패널 생성
		
		jf.pack(); // 사이즈 자동 조절
		jf.setVisible(true); // jf 프레임 활성화
	}
}
