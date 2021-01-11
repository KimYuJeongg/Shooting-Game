import java.awt.Color;
import java.awt.Graphics2D;

public class Bullet {
	
	//미사일 발사 좌표 설정
	private double x;
	private double y;
	private int r;
	
	//미사일 발사 이동 좌표 설정
	private double dx;
	private double dy;
	private double rad;
	private double speed;
	
	//미사일 발사 색상 설정
	private Color c1;
	
	//미사일 발사 정보
	public Bullet (double angle, int x, int y) {
		this.x = x;
		this.y = y;
		r = 6;
		
		rad = Math.toRadians(angle);
		speed = 15; //미사일 발사 속도
		dx = Math.cos(rad) * speed;
		dy = Math.sin(rad) * speed;
		
		c1 = Color.YELLOW; // 미사일 발사 색상
	}
	
	public double getx() { return x; }
	public double gety() { return y; }
	public double getr() { return r; }
	
	//미사일 좌표 업데이트 정보
	public boolean update() {
		
		x += dx;
		y += dy;
		
		if( x< -r || x > GamePanel.WIDTH + r ||
			y < -r || y > GamePanel.HEIGHT + r) {
			return true;
		}
		return false;
	}
	
	//미사일 그래픽 구현
	public void draw (Graphics2D g) {
		
		g.setColor(c1);
		g.fillOval((int) (x - r),  (int) (y - r), 2 * r, 2 * r);
	}
}
