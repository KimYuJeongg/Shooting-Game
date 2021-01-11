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

	public static int WIDTH = 800; // ����
	public static int HEIGHT = 600; // ����

	private Thread thread; // Thread ����
	private boolean running; // ���� ���� �ľ��� ���� running ����

	private BufferedImage image; // �̹��� ��������
	private Graphics2D g; // �̹��� ������, 2D �׷����� �����ϱ� ���� ����

	private int FPS = 30; // ������ ������ ��ġ ����
	private int averageFPS; // ��� FPS ��ġ

	public static Player player; // Player Ŭ���� ���

	public static ArrayList<Bullet> bullets; // �Ѿ��� �迭�� ���� �ҷ�����
	public static ArrayList<Enemy> enemies; // ���� �迭�� ���� �ҷ�����
	public static ArrayList<PowerUp> powerups; // �Ŀ� ���� �迭�� ���� �ҷ�����
	public static ArrayList<Explosion> explosions; // ������ �迭�� ���� �ҷ�����
	public static ArrayList<Text> texts; // �ؽ�Ʈ�� �迭�� ���� �ҷ�����

	private long waveStartTimer; // �������� ���̺� ǥ�� �ð�
	private long waveStartTimerDiff; // ���̺� ǥ�� �⺻ �ð�
	private int waveNumber; // �������� ���̺� ��
	private boolean waveStart; // ���̺� �۾� ǥ�� ��ŸƮ
	private int waveDelay = 2000; // ���̺� ǥ�� ������

	private long slowDownTimer; // �������� ������ Ÿ�̸�
	private long slowDownTimerDiff; // �������� ������ �⺻ �ð�
	private int slowDownLength = 6000; // �������� ������ �ð� ����

	private long pauseTimer; // �Ͻ����� �ð�
	private long pauseTimerDiff; // �Ͻ����� ��� �ð�
	private boolean pauseStart; // �Ͻ����� ����

	public GamePanel() {
		super(); // ���� Ŭ���� ����
		setPreferredSize(new Dimension(WIDTH, HEIGHT)); // �г��� ������ ����
		setFocusable(true); // �гο� ��Ŀ�� Ȱ��ȭ
		requestFocus(); // �гο� ��Ŀ���� ��û
	}

	public void addNotify() {
		super.addNotify(); // addNotify�� �������� �����ְ� �ϴ� ����
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}

		addKeyListener(this); // Ű �̺�Ʈ Ȱ��ȭ
	}

	public void run() {
		running = true; // ���� �� ��� running�� true�� Ȱ��ȭ

		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB); // image�� ����, ����, RGBŸ�� ����
		g = (Graphics2D) image.getGraphics(); // g�� image ��ǥ ����ֱ�
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		player = new Player(); // Player Ŭ���� ��ü ����
		bullets = new ArrayList<Bullet>(); // �Ѿ� Ŭ���� ��ü ����
		enemies = new ArrayList<Enemy>(); // �� Ŭ���� ��ü ����
		powerups = new ArrayList<PowerUp>(); // �Ŀ� �� Ŭ���� ��ü ����
		explosions = new ArrayList<Explosion>(); // ���� Ŭ���� ��ü ����
		texts = new ArrayList<Text>(); // �ؽ�Ʈ Ŭ���� ��ü ����

		waveStartTimer = 0;
		waveStartTimerDiff = 0;
		waveStart = true;
		waveNumber = 0;

		// �� FPS (Frame Per Second)�� ���ϱ� ���� ������
		long startTime;
		long URDTimeMillis;
		long waitTime;
		long totalTime = 0;
		long targetTime = 1000 / FPS;

		// �ּ� �����Ӱ� �ִ� ������ ǥ��
		int frameCount = 0;
		int maxFrameCount = 30;

		// ���� ���� �� �ݺ�
		while (running) {
			startTime = System.nanoTime(); // FPS ����� ���� ���� ����

			// game�� ���õ� ������Ʈ, ����, �׸� �ҷ�����
			gameUpdate();
			gameRender();
			gameDraw();

			URDTimeMillis = (System.nanoTime() - startTime) / 1000000; // FPS ���
			waitTime = targetTime - URDTimeMillis; // ���ð�

			// ������� �ƴ� ����� ����ó��
			try {
				Thread.sleep(waitTime); // �۵� ����
			} catch (Exception e) {

			}

			totalTime += System.nanoTime() - startTime; // ���� �ð���
			frameCount++; // ������ ī���� ����
			if (frameCount == maxFrameCount) {
				averageFPS = (int) (1000 / ((totalTime / frameCount) / 1000000)); // ��� ������ ���
				frameCount = 0;
				totalTime = 0;
			}

		}
		// ���� �����
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

	// ���ӿ��� ��������� ��ȭ �Ǵ� �κ�
	private void gameUpdate() {

		// ���ο� ���̺�
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

		// �� ����
		if (waveStart && enemies.size() == 0) {
			createNewEnemies();
		}
		// �÷��̾� ������Ʈ
		player.update();

		// �Ѿ� ������Ʈ
		for (int i = 0; i < bullets.size(); i++) {
			boolean remove = bullets.get(i).update();
			if (remove) {
				bullets.remove(i);
				i--;
			}
		}

		// �� ������Ʈ
		for (int i = 0; i < enemies.size(); i++) {
			enemies.get(i).update();

		}

		// �Ŀ� �� ������Ʈ
		for (int i = 0; i < powerups.size(); i++) {
			boolean remove = powerups.get(i).update();
			if (remove) {
				powerups.remove(i);
				i--;

			}
		}

		// ���� ������Ʈ
		for (int i = 0; i < explosions.size(); i++) {
			boolean remove = explosions.get(i).update();
			if (remove) {
				explosions.remove(i);
				i--;
			}
		}

		// �ؽ�Ʈ ������Ʈ
		for (int i = 0; i < texts.size(); i++) {
			boolean remove = texts.get(i).update();
			if (remove) {
				texts.remove(i);
				i--;
			}
		}

		// �Ѿ˿� ���� ��
		for (int i = 0; i < bullets.size(); i++) {
			// �Ѿ��� ��ǥ
			Bullet b = bullets.get(i);
			double bx = b.getx();
			double by = b.gety();
			double br = b.getr();
			for (int j = 0; j < enemies.size(); j++) {
				// ���� ��ǥ
				Enemy e = enemies.get(j);
				double ex = e.getx();
				double ey = e.gety();
				double er = e.getr();

				double dx = bx - ex;
				double dy = by - ey;
				double dist = Math.sqrt(dx * dx + dy * dy);

				// ���� ��ǥ�� �Ѿ��� ��ǥ�� ��ġ�� ���
				if (dist < br + er) {
					e.hit();
					bullets.remove(i);
					i--;
					break;
				}

			}
		}

		// �� ���� üũ
		for (int i = 0; i < enemies.size(); i++) {
			if (enemies.get(i).isDead()) {
				Enemy e = enemies.get(i);

				// �Ŀ� ��
				double rand = Math.random(); // �������� ������ ���
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

		// �÷��̾� ���� üũ
		if (player.isDead()) {
			running = false;
		}

		// �÷��̾� - �� �浹
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

		// �÷��̾� �Ŀ� ��
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

			// �Ŀ� �� ȿ��
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

		// �������� ������ ������Ʈ
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

	// ȭ�� �� ǥ��
	private void gameRender() {
		// ��� �̹��� ����
		g.setColor(new Color(0, 0, 0));
		g.fillRect(0, 0, WIDTH, HEIGHT);
		g.setColor(Color.WHITE);

		// �������� ������ ȭ�� ����
		if (slowDownTimer != 0) {
			g.setColor(new Color(255, 255, 255, 64));
			g.fillRect(0, 0, WIDTH, HEIGHT);
		}
		// �÷��̾� ��ü �̹��� ����
		player.draw(g);
		;

		// �Ѿ� �̹��� ����
		for (int i = 0; i < bullets.size(); i++) {
			bullets.get(i).draw(g);
		}

		// �� �̹��� ����
		for (int i = 0; i < enemies.size(); i++) {
			enemies.get(i).draw(g);
		}

		// �Ŀ� �� �̹��� ����
		for (int i = 0; i < powerups.size(); i++) {
			powerups.get(i).draw(g);
		}

		// ���� �̹��� ����
		for (int i = 0; i < explosions.size(); i++) {
			explosions.get(i).draw(g);
		}

		// �ؽ�Ʈ ǥ��
		for (int i = 0; i < texts.size(); i++) {
			texts.get(i).draw(g);
		}

		// ���̺� ���� ǥ��
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

		// �÷��̾� ��� ǥ��
		for (int i = 0; i < player.getLifes(); i++) {
			g.setColor(Color.WHITE);
			g.fillOval(20 + (20 * i), 20, player.getr() * 2, player.getr() * 2);
			g.setStroke(new BasicStroke(3));
			g.setColor(Color.WHITE.darker());
			g.fillOval(20 + (20 * i), 20, player.getr() * 2, player.getr() * 2);
			g.setStroke(new BasicStroke(1));
		}

		// �÷��̾� �Ŀ� ǥ��
		g.setColor(Color.YELLOW);
		g.fillRect(20, 40, player.getPower() * 8, 8);
		g.setColor(Color.YELLOW.darker());
		g.setStroke(new BasicStroke(3));
		for (int i = 0; i < player.getRequiredPower(); i++) {
			g.drawRect(20 + 8 * i, 40, 8, 8);
		}
		g.setStroke(new BasicStroke(1));

		// ���� ǥ��
		g.setColor(Color.WHITE);
		g.setFont(new Font("Century Gothic", Font.PLAIN, 14));
		g.drawString("Score : " + player.getScore(), WIDTH - 100, 30);

		// �������� ������ ǥ��
		if (slowDownTimer != 0) {
			g.setColor(Color.WHITE);
			g.drawRect(20, 60, 100, 8);
			g.fillRect(20, 60, (int) (100 - 100 * slowDownTimerDiff / slowDownLength), 8);
		}
	}

	// �̹��� �׸���
	private void gameDraw() {
		Graphics g2 = this.getGraphics();
		g2.drawImage(image, 0, 0, null);
		g2.dispose();
	}

	// �� ����
	private void createNewEnemies() {
		enemies.clear();
		Enemy e;

		// 1ź
		if (waveNumber == 1) {
			for (int i = 0; i < 5; i++) {
				enemies.add(new Enemy(1, 1));
			}
		}
		// 2ź
		if (waveNumber == 2) {
			for (int i = 0; i < 10; i++) {
				enemies.add(new Enemy(1, 1));
			}
		}
		// 3ź
		if (waveNumber == 3) {
			for (int i = 0; i < 5; i++) {
				enemies.add(new Enemy(1, 1));
			}
			enemies.add(new Enemy(1, 2));
			enemies.add(new Enemy(1, 2));
		}
		// 4ź
		if (waveNumber == 4) {
			enemies.add(new Enemy(1, 3));
			enemies.add(new Enemy(1, 4));
			for (int i = 0; i < 5; i++) {
				enemies.add(new Enemy(2, 1));
			}
		}
		// 5ź
		if (waveNumber == 5) {
			enemies.add(new Enemy(1, 4));
			enemies.add(new Enemy(1, 3));
			enemies.add(new Enemy(2, 3));
		}
		// 6ź
		if (waveNumber == 6) {
			enemies.add(new Enemy(1, 3));
			for (int i = 0; i < 5; i++) {
				enemies.add(new Enemy(2, 1));
				enemies.add(new Enemy(3, 1));
			}
		}
		// 7ź
		if (waveNumber == 7) {
			enemies.add(new Enemy(1, 3));
			enemies.add(new Enemy(2, 3));
			enemies.add(new Enemy(3, 3));
		}
		// 8ź
		if (waveNumber == 8) {
			enemies.add(new Enemy(1, 4));
			enemies.add(new Enemy(2, 4));
			enemies.add(new Enemy(3, 4));
		}
		// 9ź
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

	// Ű �Է� �̺�Ʈ
	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		// �� �� �� �� ��ü ������ �Է½�
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
		// ���ݹ�ư (�����̽���)�� ������ ��
		if (keyCode == KeyEvent.VK_SPACE) {
			player.setFiring(true);
		}
		// ESC ��ư ������ �� ���� ����
		if (keyCode == KeyEvent.VK_ESCAPE) {
			System.exit(0);
		}

	}

	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		// �� �� �� �� ��ü ������ �Է� �����
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
		// ���ݹ�ư (�����̽���)�� �׸� ������ ��
		if (keyCode == KeyEvent.VK_SPACE) {
			player.setFiring(false);
		}
	}

}