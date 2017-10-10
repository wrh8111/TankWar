import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.Random;

public class Tank {
	private int x, y;

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	private boolean good;

	public void setGood(boolean good) {
		this.good = good;
	}

	public boolean isGood() {
		return good;
	}
	
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public static final int XSPEED = 5;
	public static final int YSPEED = 5;
	public static final int WIDTH = 30;
	public static final int HEIGHT = 30;

	private boolean bl = false, bu = false, br = false, bd = false;

	private TankClient tc = null;

	private boolean live = true;
	
	private Random r = new Random();
	
	private int step = r.nextInt(12)+3;

	public boolean isLive() {
		return live;
	}

	public void setLive(boolean live) {
		this.live = live;
	}

	public Dir dir = Dir.STOP;
	private Dir ptdir = Dir.D;

	public Tank(int x, int y, boolean good) {
		this.x = x;
		this.y = y;
		this.good = good;
	}

	public Tank(int x, int y, boolean good, TankClient tc) {
		this(x, y, good);
		this.tc = tc;
	}
	
	public Tank(int x, int y, boolean good,Dir dir, TankClient tc) {
		this(x, y, good,tc);
		this.dir=dir;
	}

	public void draw(Graphics g) {
		if (!isLive()) {
			if(!good)tc.tanks.remove(this);
			return;
		}
		
			Color c = g.getColor();
			if (good)
				g.setColor(Color.RED);
			else
				g.setColor(Color.YELLOW);
			g.fillOval(x, y, WIDTH, HEIGHT);
			g.drawString(""+id, x, y-10);
			g.setColor(c);

			switch (ptdir) {
			case L:
				g.drawLine(this.x + WIDTH / 2, this.y + HEIGHT / 2, this.x, this.y + HEIGHT / 2);
				break;
			case LU:
				g.drawLine(this.x + WIDTH / 2, this.y + HEIGHT / 2, this.x, this.y);
				break;
			case U:
				g.drawLine(this.x + WIDTH / 2, this.y + HEIGHT / 2, this.x + WIDTH / 2, this.y);
				break;
			case RU:
				g.drawLine(this.x + WIDTH / 2, this.y + HEIGHT / 2, this.x + WIDTH, this.y);
				break;
			case R:
				g.drawLine(this.x + WIDTH / 2, this.y + HEIGHT / 2, this.x + WIDTH, this.y + HEIGHT / 2);
				break;
			case RD:
				g.drawLine(this.x + WIDTH / 2, this.y + HEIGHT / 2, this.x + WIDTH, this.y + HEIGHT);
				break;
			case D:
				g.drawLine(this.x + WIDTH / 2, this.y + HEIGHT / 2, this.x + WIDTH / 2, this.y + HEIGHT);
				break;
			case LD:
				g.drawLine(this.x + WIDTH / 2, this.y + HEIGHT / 2, this.x, this.y + HEIGHT);
				break;
			}
		
		move();
	}

	void move() {
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
		case STOP:
			break;
		}
		if (dir != Dir.STOP)
			this.ptdir = this.dir;
		if (x < 0)
			x = 0;
		if (y < 30)
			y = 30;
		if (x + Tank.WIDTH > TankClient.GAME_WIDTH)
			x = TankClient.GAME_WIDTH - Tank.WIDTH;
		if (y + Tank.HEIGHT > TankClient.GAME_HEIGHT)
			y = TankClient.GAME_HEIGHT - Tank.HEIGHT;
		//对坏坦克产生随机步数和随机方向
		/*if(!good){
			Dir[] directions = Dir.values();
			if(step==0){
				step=r.nextInt(12)+3;//产生随机步数
				dir=directions[r.nextInt(directions.length)];//产生随机方向
			}
			step--;
			if(r.nextInt(40)>36)fire();//随机发射炮弹
		}*/
	}

	void locateDirection() {
		Dir oldDir = this.dir;
		
		if (bl && !bu && !br && !bd)
			dir = Dir.L;
		else if (bl && bu && !br && !bd)
			dir = Dir.LU;
		else if (!bl && bu && !br && !bd)
			dir = Dir.U;
		else if (!bl && bu && br && !bd)
			dir = Dir.RU;
		else if (!bl && !bu && br && !bd)
			dir = Dir.R;
		else if (!bl && !bu && br && bd)
			dir = Dir.RD;
		else if (!bl && !bu && !br && bd)
			dir = Dir.D;
		else if (bl && !bu && !br && bd)
			dir = Dir.LD;
		else if (!bl && !bu && !br && !bd)
			dir = Dir.STOP;
		
		if(dir!=oldDir){
			TankMoveMsg msg = new TankMoveMsg(id,x,y, dir);
			tc.nc.send(msg);
		}
	}

	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		switch (key) {
		case KeyEvent.VK_LEFT:
			bl = true;
			break;
		case KeyEvent.VK_UP:
			bu = true;
			break;
		case KeyEvent.VK_RIGHT:
			br = true;
			break;
		case KeyEvent.VK_DOWN:
			bd = true;
			break;
		}
		locateDirection();
	}

	private Missile fire() {
		if(!live)return null;//如果没有生命，就不产生炮弹
		int x = this.x + Tank.WIDTH / 2 - Missile.WIDTH / 2;
		int y = this.y + Tank.HEIGHT / 2 - Missile.HEIGHT / 2;
		Missile m = new Missile(id,x, y, ptdir, good,this.tc);
		tc.missiles.add(m);
		
		MissileNewMsg msg = new MissileNewMsg(m);
		tc.nc.send(msg);
		
		return m;
	}

	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		switch (key) {
		case KeyEvent.VK_CONTROL:
			fire();
			break;
		case KeyEvent.VK_LEFT:
			bl = false;
			break;
		case KeyEvent.VK_UP:
			bu = false;
			break;
		case KeyEvent.VK_RIGHT:
			br = false;
			break;
		case KeyEvent.VK_DOWN:
			bd = false;
			break;
		}
		locateDirection();
	}

	public Rectangle getRect() {
		return new Rectangle(x, y, WIDTH, HEIGHT);
	}

}
