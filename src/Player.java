import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class Player {
	
	//플레이어 위치
	private int x;
	private int y;
	private int r;
	
	//플레이어 이동 위치
	private int dx;
	private int dy;
	private int speed;
	
	//플레이어 이동 방향
	private boolean left;
	private boolean right;
	private boolean up;
	private boolean down;
	
	//플레이어 공격
	private boolean firing;
	private long firingTimer;
	private long firingDelay;
	
	//충돌 후 회복
	private boolean recovering;
	private long recoveryTimer;
	
	//플레이어 목숨
	private int life;
	
	//플레이어 색상
	private Color c1;
	private Color c2;
	
	//점수
	private int score;
	
	//파워
	private int powerLevel;
	private int power;
	private int[] requiredPower = {
			1,2,3,4,5
	};
	
	//사운드
	private File bulletSound = new File("sounds/bulletSound.wav");
    private InputStream bulletSoundInput;
	
	//플레이어 좌표 위치, 속도, 목숨, 총알 발사 속도 설정
	public Player() {
		x = GamePanel.WIDTH / 2;
		y = GamePanel.HEIGHT / 2;
		r = 5;
		
		dx = 0;
		dy = 0;
		speed = 10;
		
		life = 3;
		
		c1 = Color.WHITE;
		c2 = Color.RED;
		
		firing = false;
		firingTimer = System.nanoTime();
		firingDelay = 250;
		
		recovering = false;
		recoveryTimer = 0;
		
		score = 0;
	}
	
	public int getx() { return x; }
	public int gety() { return y; }
	public int getr() { return r; }
	
	public int getScore() { return score; }
	
	public int getLifes() { return life; }
	
	public boolean isDead() { return life <= 0; }
	public boolean isRecovering() { return recovering; }
	
	public void setLeft(boolean b) { left = b; }
	public void setRight(boolean b) { right = b; }
	public void setUp(boolean b) { up = b; }
	public void setDown(boolean b) { down = b; }
	
	public void setFiring(boolean b) { firing = b; }
	
	public void addScore(int i) { score += i; }
	
	public void gainLife() {
		life++;
	}
	
	public void lostLife(){
		life--;
		recovering = true;
		recoveryTimer = System.nanoTime();
	}
	
	public void increasePower(int i) {
		power += i;
		if (powerLevel == 4){
			if (power > requiredPower [powerLevel]) {
				power = requiredPower [powerLevel];
			}
			return;
		}
		if (power >= requiredPower [powerLevel]) {
			power -= requiredPower [powerLevel];
			powerLevel++;
		}
	}
	
	public int getPowerLevel() { return powerLevel; }
	public int getPower() { return power; }
	public int getRequiredPower() {return requiredPower [powerLevel]; }
	
	//플레이어의 위치 움직이는 좌표 값 업데이트
	public void update(){
		if(left) {
			dx = -speed;
		}
		if(right) {
			dx = speed;
		}
		if(up) {
			dy = -speed;
		}
		if(down) {
			dy = speed;
		}
		
		x += dx;
		y += dy;
		
		if(x < r) x = r;
		if(y < r) y = r;
		if(x > GamePanel.WIDTH - r) x = GamePanel.WIDTH - r;
		if(y > GamePanel.HEIGHT - r) y = GamePanel.HEIGHT - r;
		
		dx = 0;
		dy = 0;
		
		if (firing) {
			long elapsed = (System.nanoTime() - firingTimer) / 1000000;
			
			if (elapsed > firingDelay) {
				firingTimer = System.nanoTime();
				
				if(powerLevel < 2 ) {
					GamePanel.bullets.add(new Bullet(270, x, y));
				}
				else if(powerLevel < 4){
					GamePanel.bullets.add(new Bullet(270, x+5, y));
					GamePanel.bullets.add(new Bullet(270, x-5, y));
				}
				else {
					GamePanel.bullets.add(new Bullet(270, x, y));
					GamePanel.bullets.add(new Bullet(275, x+5, y));
					GamePanel.bullets.add(new Bullet(265, x-5, y));
				}
			}
			
		}
		
		if(recovering) {
			long elapsed = (System.nanoTime() - recoveryTimer) / 1000000;
			if (elapsed > 2000) {
				recovering = false;
				recoveryTimer = 0;
			}
		}
		
		try {
            bulletSoundInput = new FileInputStream(bulletSound);
        } catch (IOException e) {
        }
	}
	
	//플레이어 그래픽 구현
	public void draw(Graphics2D g) {
		
		if(recovering){
			g.setColor(c2);
			g.fillOval(x - r,  y - r, 2 * r,  2 * r);
			
			g.setStroke(new BasicStroke(3));
			g.setColor(c2.darker());
			g.drawOval(x - r, y - r, 2 * r, 2 * r);
			g.setStroke(new BasicStroke(1));
		} else {
			g.setColor(c1);
			g.fillOval(x - r,  y - r, 2 * r,  2 * r);
			
			g.setStroke(new BasicStroke(3));
			g.setColor(c1.darker());
			g.drawOval(x - r, y - r, 2 * r, 2 * r);
			g.setStroke(new BasicStroke(1));
		}
		
	}

}
