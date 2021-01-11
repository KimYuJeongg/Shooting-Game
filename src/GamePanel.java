import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable, KeyListener {

	public static int WIDTH = 800; // 높이
	public static int HEIGHT = 600; // 넓이

	private Thread thread; // Thread 생성
	private boolean running; // 실행 여부 파악을 위해 running 설정

	private BufferedImage image; // 이미지 가져오기
	private Graphics2D g; // 이미지 랜더링, 2D 그래픽을 구현하기 위한 설정

	private int FPS = 30; // 게임의 프레임 수치 설정
	private int averageFPS; // 평균 FPS 수치

	public static Player player; // Player 클래스 상속

	public static ArrayList<Bullet> bullets; // 총알을 배열로 통해 불러오기
	public static ArrayList<Enemy> enemies; // 적을 배열로 통해 불러오기
	public static ArrayList<PowerUp> powerups; // 파워 업을 배열로 통해 불러오기
	public static ArrayList<Explosion> explosions; // 폭팔을 배열로 통해 불러오기
	public static ArrayList<Text> texts; // 텍스트를 배열로 통해 불러오기

	private long waveStartTimer; // 스테이지 웨이브 표시 시간
	private long waveStartTimerDiff; // 웨이브 표시 기본 시간
	private int waveNumber; // 스테이지 웨이브 수
	private boolean waveStart; // 웨이브 글씨 표시 스타트
	private int waveDelay = 2000; // 웨이브 표시 딜레이

	private long slowDownTimer; // 느려지는 아이템 타이머
	private long slowDownTimerDiff; // 느려지는 아이템 기본 시간
	private int slowDownLength = 6000; // 느려지는 아이템 시간 길이

	private long pauseTimer; // 일시정지 시간
	private long pauseTimerDiff; // 일시정지 평균 시간
	private boolean pauseStart; // 일시정지 시작

	public GamePanel() {
		super(); // 상위 클래스 생성
		setPreferredSize(new Dimension(WIDTH, HEIGHT)); // 패널의 사이즈 설정
		setFocusable(true); // 패널에 포커스 활성화
		requestFocus(); // 패널에 포커스를 요청
	}

	public void addNotify() {
		super.addNotify(); // addNotify는 프레임을 보여주게 하는 역할
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}

		addKeyListener(this); // 키 이벤트 활성화
	}

	public void run() {
		running = true; // 실행 될 경우 running을 true로 활성화

		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB); // image에 길이, 높이, RGB타입 설정
		g = (Graphics2D) image.getGraphics(); // g에 image 좌표 집어넣기
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		player = new Player(); // Player 클래스 객체 생성
		bullets = new ArrayList<Bullet>(); // 총알 클래스 객체 생성
		enemies = new ArrayList<Enemy>(); // 적 클래스 객체 생성
		powerups = new ArrayList<PowerUp>(); // 파워 업 클래스 객체 생성
		explosions = new ArrayList<Explosion>(); // 폭발 클래스 객체 생성
		texts = new ArrayList<Text>(); // 텍스트 클래스 객체 생성

		waveStartTimer = 0;
		waveStartTimerDiff = 0;
		waveStart = true;
		waveNumber = 0;

		// 평군 FPS (Frame Per Second)를 구하기 위한 변수들
		long startTime;
		long URDTimeMillis;
		long waitTime;
		long totalTime = 0;
		long targetTime = 1000 / FPS;

		// 최소 프레임과 최대 프레임 표시
		int frameCount = 0;
		int maxFrameCount = 30;

		// 게임 실행 시 반복
		while (running) {
			startTime = System.nanoTime(); // FPS 출력을 위한 정보 수집

			// game에 관련된 업데이트, 렌더, 그림 불러오기
			gameUpdate();
			gameRender();
			gameDraw();

			URDTimeMillis = (System.nanoTime() - startTime) / 1000000; // FPS 계산
			waitTime = targetTime - URDTimeMillis; // 대기시간

			// 대기중이 아닐 경우의 예외처리
			try {
				Thread.sleep(waitTime); // 작동 정지
			} catch (Exception e) {

			}

			totalTime += System.nanoTime() - startTime; // 최종 시간값
			frameCount++; // 프레임 카운터 증가
			if (frameCount == maxFrameCount) {
				averageFPS = (int) (1000 / ((totalTime / frameCount) / 1000000)); // 평균 프레임 계산
				frameCount = 0;
				totalTime = 0;
			}

		}
		// 게임 종료시
		g.setColor(new Color(0, 0, 0));
		g.fillRect(0, 0, WIDTH, HEIGHT);
		g.setColor(Color.WHITE);
		g.setFont(new Font("Century Gothic", Font.PLAIN, 16));
		String s = "G A M E   O V E R";
		int length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
		g.drawString(s, (WIDTH - length) / 2, HEIGHT / 2);
		s = "Score : " + player.getScore();
		length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
		g.drawString(s, (WIDTH - length) / 2, HEIGHT / 2 + 30);
		gameDraw();
	}

	// 게임에서 상시적으로 변화 되는 부분
	private void gameUpdate() {

		// 새로운 웨이브
		if (waveStartTimer == 0 && enemies.size() == 0) {
			waveNumber++;
			waveStart = false;
			waveStartTimer = System.nanoTime();
		} else {
			waveStartTimerDiff = (System.nanoTime() - waveStartTimer) / 1000000;
			if (waveStartTimerDiff > waveDelay) {
				waveStart = true;
				waveStartTimer = 0;
				waveStartTimerDiff = 0;
			}
		}

		// 적 생성
		if (waveStart && enemies.size() == 0) {
			createNewEnemies();
		}
		// 플레이어 업데이트
		player.update();

		// 총알 업데이트
		for (int i = 0; i < bullets.size(); i++) {
			boolean remove = bullets.get(i).update();
			if (remove) {
				bullets.remove(i);
				i--;
			}
		}

		// 적 업데이트
		for (int i = 0; i < enemies.size(); i++) {
			enemies.get(i).update();

		}

		// 파워 업 업데이트
		for (int i = 0; i < powerups.size(); i++) {
			boolean remove = powerups.get(i).update();
			if (remove) {
				powerups.remove(i);
				i--;

			}
		}

		// 폭발 업데이트
		for (int i = 0; i < explosions.size(); i++) {
			boolean remove = explosions.get(i).update();
			if (remove) {
				explosions.remove(i);
				i--;
			}
		}

		// 텍스트 업데이트
		for (int i = 0; i < texts.size(); i++) {
			boolean remove = texts.get(i).update();
			if (remove) {
				texts.remove(i);
				i--;
			}
		}

		// 총알에 맞은 적
		for (int i = 0; i < bullets.size(); i++) {
			// 총알의 좌표
			Bullet b = bullets.get(i);
			double bx = b.getx();
			double by = b.gety();
			double br = b.getr();
			for (int j = 0; j < enemies.size(); j++) {
				// 적의 좌표
				Enemy e = enemies.get(j);
				double ex = e.getx();
				double ey = e.gety();
				double er = e.getr();

				double dx = bx - ex;
				double dy = by - ey;
				double dist = Math.sqrt(dx * dx + dy * dy);

				// 적의 좌표와 총알의 좌표가 일치할 경우
				if (dist < br + er) {
					e.hit();
					bullets.remove(i);
					i--;
					break;
				}

			}
		}

		// 적 제거 체크
		for (int i = 0; i < enemies.size(); i++) {
			if (enemies.get(i).isDead()) {
				Enemy e = enemies.get(i);

				// 파워 업
				double rand = Math.random(); // 랜덤으로 아이템 드랍
				if (rand < 0.005)
					powerups.add(new PowerUp(1, e.getx(), e.gety()));
				else if (rand < 0.020)
					powerups.add(new PowerUp(3, e.getx(), e.gety()));
				else if (rand < 0.120)
					powerups.add(new PowerUp(2, e.getx(), e.gety()));
				else if (rand < 0.130)
					powerups.add(new PowerUp(4, e.getx(), e.gety()));

				player.addScore(e.getType() * 50 + e.getRank() * 50);
				enemies.remove(i);
				i--;

				e.explode();
				explosions.add(new Explosion(e.getx(), e.gety(), e.getr(), e.getr() + 20));
			}
		}

		// 플레이어 죽음 체크
		if (player.isDead()) {
			running = false;
		}

		// 플레이어 - 적 충돌
		if (!player.isRecovering()) {
			int px = player.getx();
			int py = player.gety();
			int pr = player.getr();
			for (int i = 0; i < enemies.size(); i++) {

				Enemy e = enemies.get(i);

				double ex = e.getx();
				double ey = e.gety();
				double er = e.getr();

				double dx = px - ex;
				double dy = py - ey;
				double dist = Math.sqrt(dx * dx + dy * dy);

				if (dist < pr + er) {
					player.lostLife();
				}

			}
		}

		// 플레이어 파워 업
		int px = player.getx();
		int py = player.gety();
		int pr = player.getr();
		for (int i = 0; i < powerups.size(); i++) {
			PowerUp p = powerups.get(i);
			double x = p.getx();
			double y = p.gety();
			double r = p.getr();
			double dx = px - x;
			double dy = py - y;
			double dist = Math.sqrt(dx * dx + dy * dy);

			// 파워 업 효과
			if (dist < pr + r) {
				int type = p.getType();

				if (type == 1) {
					player.gainLife();
					texts.add(new Text(player.getx(), player.gety(), 2000, "Life+"));
				}
				if (type == 2) {
					player.increasePower(1);
					texts.add(new Text(player.getx(), player.gety(), 2000, "Power Up +1"));
				}
				if (type == 3) {
					player.increasePower(2);
					texts.add(new Text(player.getx(), player.gety(), 2000, "Power Up +2"));
				}
				if (type == 4) {
					slowDownTimer = System.nanoTime();
					for (int j = 0; j < enemies.size(); j++) {
						enemies.get(j).setSlow(true);
					}
					texts.add(new Text(player.getx(), player.gety(), 2000, "Slow Down"));
				}
				powerups.remove(i);
				i--;
			}
		}

		// 느려지기 아이템 업데이트
		if (slowDownTimer != 0) {
			slowDownTimerDiff = (System.nanoTime() - slowDownTimer) / 1000000;
			if (slowDownTimerDiff > slowDownLength) {
				slowDownTimer = 0;
				for (int j = 0; j < enemies.size(); j++) {
					enemies.get(j).setSlow(false);
				}
			}
		}
	}

	// 화면 상에 표시
	private void gameRender() {
		// 배경 이미지 구현
		g.setColor(new Color(0, 0, 0));
		g.fillRect(0, 0, WIDTH, HEIGHT);
		g.setColor(Color.WHITE);

		// 느려지는 아이템 화면 구성
		if (slowDownTimer != 0) {
			g.setColor(new Color(255, 255, 255, 64));
			g.fillRect(0, 0, WIDTH, HEIGHT);
		}
		// 플레이어 기체 이미지 구현
		player.draw(g);
		;

		// 총알 이미지 구현
		for (int i = 0; i < bullets.size(); i++) {
			bullets.get(i).draw(g);
		}

		// 적 이미지 구현
		for (int i = 0; i < enemies.size(); i++) {
			enemies.get(i).draw(g);
		}

		// 파워 업 이미지 구현
		for (int i = 0; i < powerups.size(); i++) {
			powerups.get(i).draw(g);
		}

		// 폭발 이미지 구현
		for (int i = 0; i < explosions.size(); i++) {
			explosions.get(i).draw(g);
		}

		// 텍스트 표시
		for (int i = 0; i < texts.size(); i++) {
			texts.get(i).draw(g);
		}

		// 웨이브 숫자 표시
		if (waveStartTimer != 0) {
			g.setFont(new Font("Century Gothic", Font.PLAIN, 18));
			String s = " - W A V E   " + waveNumber + "   -";
			int length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
			int alpha = (int) (255 * Math.sin(3.14 * waveStartTimerDiff / waveDelay));
			if (alpha > 255)
				alpha = 255;
			g.setColor(new Color(255, 255, 255, alpha));
			g.drawString(s, WIDTH / 2 - length / 2, HEIGHT / 2);
		}

		// 플레이어 목숨 표시
		for (int i = 0; i < player.getLifes(); i++) {
			g.setColor(Color.WHITE);
			g.fillOval(20 + (20 * i), 20, player.getr() * 2, player.getr() * 2);
			g.setStroke(new BasicStroke(3));
			g.setColor(Color.WHITE.darker());
			g.fillOval(20 + (20 * i), 20, player.getr() * 2, player.getr() * 2);
			g.setStroke(new BasicStroke(1));
		}

		// 플레이어 파워 표시
		g.setColor(Color.YELLOW);
		g.fillRect(20, 40, player.getPower() * 8, 8);
		g.setColor(Color.YELLOW.darker());
		g.setStroke(new BasicStroke(3));
		for (int i = 0; i < player.getRequiredPower(); i++) {
			g.drawRect(20 + 8 * i, 40, 8, 8);
		}
		g.setStroke(new BasicStroke(1));

		// 점수 표시
		g.setColor(Color.WHITE);
		g.setFont(new Font("Century Gothic", Font.PLAIN, 14));
		g.drawString("Score : " + player.getScore(), WIDTH - 100, 30);

		// 느려지는 아이템 표시
		if (slowDownTimer != 0) {
			g.setColor(Color.WHITE);
			g.drawRect(20, 60, 100, 8);
			g.fillRect(20, 60, (int) (100 - 100 * slowDownTimerDiff / slowDownLength), 8);
		}
	}

	// 이미지 그리기
	private void gameDraw() {
		Graphics g2 = this.getGraphics();
		g2.drawImage(image, 0, 0, null);
		g2.dispose();
	}

	// 적 생성
	private void createNewEnemies() {
		enemies.clear();
		Enemy e;

		// 1탄
		if (waveNumber == 1) {
			for (int i = 0; i < 5; i++) {
				enemies.add(new Enemy(1, 1));
			}
		}
		// 2탄
		if (waveNumber == 2) {
			for (int i = 0; i < 10; i++) {
				enemies.add(new Enemy(1, 1));
			}
		}
		// 3탄
		if (waveNumber == 3) {
			for (int i = 0; i < 5; i++) {
				enemies.add(new Enemy(1, 1));
			}
			enemies.add(new Enemy(1, 2));
			enemies.add(new Enemy(1, 2));
		}
		// 4탄
		if (waveNumber == 4) {
			enemies.add(new Enemy(1, 3));
			enemies.add(new Enemy(1, 4));
			for (int i = 0; i < 5; i++) {
				enemies.add(new Enemy(2, 1));
			}
		}
		// 5탄
		if (waveNumber == 5) {
			enemies.add(new Enemy(1, 4));
			enemies.add(new Enemy(1, 3));
			enemies.add(new Enemy(2, 3));
		}
		// 6탄
		if (waveNumber == 6) {
			enemies.add(new Enemy(1, 3));
			for (int i = 0; i < 5; i++) {
				enemies.add(new Enemy(2, 1));
				enemies.add(new Enemy(3, 1));
			}
		}
		// 7탄
		if (waveNumber == 7) {
			enemies.add(new Enemy(1, 3));
			enemies.add(new Enemy(2, 3));
			enemies.add(new Enemy(3, 3));
		}
		// 8탄
		if (waveNumber == 8) {
			enemies.add(new Enemy(1, 4));
			enemies.add(new Enemy(2, 4));
			enemies.add(new Enemy(3, 4));
		}
		// 9탄
		if (waveNumber == 9) {
			for (int i = 0; i < 10; i++) {
				enemies.add(new Enemy(1, 1));
				enemies.add(new Enemy(2, 1));
				enemies.add(new Enemy(3, 1));
			}
		}
		if (waveNumber == 10) {
			running = false;
		}
	}

	// 키 입력 이벤트
	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		// 좌 우 상 하 기체 움직임 입력시
		if (keyCode == KeyEvent.VK_LEFT) {
			player.setLeft(true);
		}
		if (keyCode == KeyEvent.VK_RIGHT) {
			player.setRight(true);
		}
		if (keyCode == KeyEvent.VK_UP) {
			player.setUp(true);
		}
		if (keyCode == KeyEvent.VK_DOWN) {
			player.setDown(true);
		}
		// 공격버튼 (스페이스바)을 눌렀을 시
		if (keyCode == KeyEvent.VK_SPACE) {
			player.setFiring(true);
		}
		// ESC 버튼 눌렀을 시 게임 종료
		if (keyCode == KeyEvent.VK_ESCAPE) {
			System.exit(0);
		}

	}

	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		// 좌 우 상 하 기체 움직임 입력 종료시
		if (keyCode == KeyEvent.VK_LEFT) {
			player.setLeft(false);
		}
		if (keyCode == KeyEvent.VK_RIGHT) {
			player.setRight(false);
		}
		if (keyCode == KeyEvent.VK_UP) {
			player.setUp(false);
		}
		if (keyCode == KeyEvent.VK_DOWN) {
			player.setDown(false);
		}
		// 공격버튼 (스페이스바)을 그만 눌렀을 시
		if (keyCode == KeyEvent.VK_SPACE) {
			player.setFiring(false);
		}
	}

}
