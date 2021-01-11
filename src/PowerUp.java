import java.awt.*;

public class PowerUp {
	
	//파워 업 좌표
	private double x;
	private double y;
	private int r;
	
	private int type;
	private Color color1;
	//파워 업 정보
	public PowerUp (int type, double x, double y) {
		this.type = type;
		this.x = x;
		this.y = y;
		
		if(type == 1) {
			color1 = Color.PINK;
			r = 5;
		}
		if(type == 2) {
			color1 = Color.YELLOW;
			r = 5;
		}
		if(type == 3) {
			color1 = Color.GREEN;
			r = 5;
		}
		if(type == 4) {
			color1 = Color.WHITE;
			r = 5;
		}
	}
	
	//파워 업 함수
	public double getx() { return x; }
	public double gety() { return y; }
	public double getr() { return r; }

	public int getType() { return type; }
	
	//파워 업 업데이트
	public boolean update() {
		
		y += 2;
		if(y > GamePanel.HEIGHT + r) {
			return true;
		}
		
		return false;
	}
	
	//파워 업 그래픽 구현
	public void draw(Graphics2D g) {
		
		g.setColor(color1);
		g.fillRect((int) (x - r), (int) (y - r), 2 * r, 2 * r);
		g.setStroke(new BasicStroke(3));
		
		g.setColor(color1.darker());
		g.fillRect((int) (x - r), (int) (y - r), 2 * r, 2 * r);
		g.setStroke(new BasicStroke(1));
		
		
	}
}
