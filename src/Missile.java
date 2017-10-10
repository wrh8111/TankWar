import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;

public class Missile {
	public int x, y;
	Dir dir;

	public static final int XSPEED = 10;
	public static final int YSPEED = 10;
	public static final int WIDTH = 10;
	public static final int HEIGHT = 10;
	private static int ID=1;
	
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	private int tankId;
	public int getTankId() {
		return tankId;
	}

	public void setTankId(int tankId) {
		this.tankId = tankId;
	}

	private boolean live = true;
	public void setLive(boolean live) {
		this.live = live;
	}

	private TankClient tc;
	private boolean good;

	public boolean isGood() {
		return good;
	}

	public void setGood(boolean good) {
		this.good = good;
	}

	public boolean isLive() {
		return live;
	}

	public Missile(int tankId,int x, int y,boolean good, Dir dir) {
		this.tankId=tankId;
		this.x = x;
		this.y = y;
		this.good=good;
		this.dir = dir;
		this.id=ID++;
	}

	public Missile(int tankId,int x, int y, Dir dir,boolean good, TankClient tc) {
		this(tankId,x, y,good, dir);
		this.tc = tc;
	}

	public void draw(Graphics g) {
		if (!isLive()) {
			this.tc.missiles.remove(this);
			return;
		}
		Color c = g.getColor();
		g.setColor(Color.BLACK);
		g.fillOval(x, y, WIDTH, HEIGHT);
		g.setColor(c);

		move();
	}

	private void move() {
		switch (dir) {
		case L:
			x -= XSPEED;
			break;
		case LU:
			x -= XSPEED;
			y -= YSPEED;
			break;
		case U:
			y -= YSPEED;
			break;
		case RU:
			x += XSPEED;
			y -= YSPEED;
			break;
		case R:
			x += YSPEED;
			break;
		case RD:
			x += XSPEED;
			y += YSPEED;
			break;
		case D:
			y += YSPEED;
			break;
		case LD:
			x -= XSPEED;
			y += YSPEED;
			break;
		}
		if (x < 0 || y < 0 || x > TankClient.GAME_WIDTH || y > TankClient.GAME_HEIGHT) {
			live = false;
		}

	}

	public Rectangle getRect() {
		return new Rectangle(x, y, WIDTH, HEIGHT);
	}

	public boolean hitTank(Tank t) {
		if (this.live&&getRect().intersects(t.getRect()) && t.isLive()&&this.good!=t.isGood()) {//如果炮弹和坦克都活着并且炮弹和坦克不同派别
			t.setLive(false);
			this.live = false;
			Explode e = new Explode(x,y,tc);
			tc.explodes.add(e);
			return true;
		}
		return false;
	}

	public boolean hitTanks(List<Tank> tanks){
		for(int i=0;i<tanks.size();i++){
			if(hitTank(tanks.get(i)))
				return true;
		}
		return false;
	}
	
}
