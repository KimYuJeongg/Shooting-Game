import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class Enemy {
	
	//적 기본 좌표 설정
	private double x;
	private double y;
	private int r;		//+적 크기
	
	//적 이동 좌표 설정
	private double dx;
	private double dy;
	private double rad;
	private double speed;
	
	//적 기본 정보 설정
	private int health;
	private int type;
	private int rank;
	
	//적 캐릭터 색 설정
	private Color c1;
	
	//적 준비 및 사망 처리 설정
	private boolean ready;
	private boolean dead;
	
	//적 공격 맞은 판정
	private boolean hit;
	private long hitTimer;
	
	//아이템 느려지기 판정
	private boolean slow;
	
	
	//적 타입 및 적 정보
	public Enemy (int type, int rank) {
		this.type = type;
		this.rank = rank;
		
		//기본 적
		if (type == 1) {
			c1 = Color.LIGHT_GRAY;
			if(rank == 1) {
				speed = 5; // 적 이동속도
				r = 7; // 적 크기
				health = 1; // 적 체력
			}
			if(rank == 2) {
				speed = 5;
				r = 10;
				health = 2;
			}
			if(rank == 3) {
				speed = 2.5;
				r = 20;
				health = 3;
			}
			if(rank == 4) {
				speed = 1.5;
				r = 30;
				health = 4;
			}
		}
		
		//빠른 적
		if (type == 2) {
			c1 = Color.RED;
			if(rank == 1) {
				speed = 7;
				r = 7;
				health = 2;
			}
			if(rank == 2) {
				speed = 7;
				r = 10;
				health = 3;
			}
			if(rank == 3) {
				speed = 5;
				r = 20;
				health = 3;
			}
			if(rank == 4) {
				speed = 2.5;
				r = 30;
				health = 5;
			}
		}
		
		//느리지만 어려운 적
		if (type == 3) {
			c1 = Color.GREEN;
			if(rank == 1) {
				speed = 3.5;
				r = 7;
				health = 5;
			}
			if(rank == 2) {
				speed = 3.5;
				r = 10;
				health = 6;
			}
			if(rank == 3) {
				speed = 3.5;
				r = 25;
				health = 7;
			}
			if(rank == 4) {
				speed = 3.5;
				r = 40;
				health = 8;
			}
		}
		
		x = Math.random() * GamePanel.WIDTH / 2 + GamePanel.WIDTH / 4;
		y = -r;
		
		double angle = Math.random() * 140 + 20;
		rad = Math.toRadians(angle);
		
		dx = Math.cos(rad) * speed;
		dy = Math.sin(rad) * speed;
		
		ready = false;
		dead = false;
		
		hit = false;
		hitTimer = 0;
	}
	
	public double getx() { return x; }
	public double gety() { return y; }
	public int getr() { return r; }
	
	public int getType() { return type; }
	public int getRank() { return rank; }
	
	public void setSlow(boolean b) { slow = b; }
	
	public boolean isDead () { return dead; }
	
	//총알에 맞췄을 시 정보
	public void hit() {
		health--;
		if(health <= 0) {
			dead = true;
		}
		hit = true;
		hitTimer = System.nanoTime();
	}
	
	//적 폭발 정보
	public void explode() {
		int amount = 0;
		if(rank > 1) {
			
			if (type == 1) {
				amount = 3;
			}
			if (type == 2) {
				amount = 3;
			}
			if (type == 3) {
				amount = 4;
			}

		}
		for (int i = 0; i < amount; i++) {
			Enemy e = new Enemy(getType(), getRank() - 1);
			e.setSlow(slow);
			e.x = this.x;
			e.y = this.y;
			double angle = 0;
			if(!ready) {
				angle = Math.random() * 140 + 20;
			}
			else {
				angle = Math.random() * 360;
			}
			e.rad = Math.toRadians(angle);
			GamePanel.enemies.add(e);
		}
	}
	
	//적 위치 좌표 업데이트 정보
	public void update() {
		if (slow) {
			x += dx * 0.1;
			y += dy * 0.1;
		} else {
			x += dx;
			y += dy;
		}
		
		if(!ready) {
			if (x > r && x < GamePanel.WIDTH - r &&
				y > r && y < GamePanel.HEIGHT - r) {
				ready = true;
			}
		}
		
		if(x < r && dx < 0) dx = -dx;
		if(y < r && dy < 0) dy = -dy;
		if(x > GamePanel.WIDTH - r && dx > 0) dx = -dx;
		if(y > GamePanel.HEIGHT - r && dy > 0) dy = -dy;
		
		if(hit) {
			long elapsed = (System.nanoTime() - hitTimer) / 1000000;
			if (elapsed > 50) {
				hit = false;
				hitTimer = 0;
			}
		}
	}
	
	//적 그래픽 구현
	public void draw (Graphics2D g) {
		
		if (hit) {
			g.setColor(Color.WHITE);
			g.fillOval((int) (x - r), (int) (y - r), 2 * r, 2 * r);
			g.setStroke(new BasicStroke(3));
			
			g.setColor(c1.WHITE.darker());
			g.fillOval((int) (x - r), (int) (y - r), 2 * r, 2 * r);
			g.setStroke(new BasicStroke(1));
		}
		else {
		g.setColor(c1);
		g.fillOval((int) (x - r), (int) (y - r), 2 * r, 2 * r);
		g.setStroke(new BasicStroke(3));
		
		g.setColor(c1.darker());
		g.fillOval((int) (x - r), (int) (y - r), 2 * r, 2 * r);
		g.setStroke(new BasicStroke(1));
		}
	}

}
