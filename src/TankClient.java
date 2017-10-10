import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

public class TankClient extends Frame {

	int x = 50, y = 50;
	Image offScreenImage = null;
	public static final int GAME_WIDTH = 800;
	public static final int GAME_HEIGHT = 600;

	Tank myTank = new Tank(50, 50, true,Dir.STOP, this);
	List<Missile> missiles = new ArrayList<Missile>();
	List<Explode> explodes = new ArrayList<Explode>();
	List<Tank> tanks = new ArrayList<Tank>();
	
	ConnDialog conndialog = new ConnDialog();
	NetClient nc = new NetClient(this);
	
	@Override
	public void paint(Graphics g) {
		g.drawString("Missile Count : " + missiles.size(), 10, 40);
		g.drawString("Explode Count : " + explodes.size(), 10, 55);
		g.drawString("Tank Count : " + tanks.size(), 10, 70);

		Missile missile = null;
		for (int i = 0; i < missiles.size(); i++) {
			missile = missiles.get(i);
			//missile.hitTanks(tanks);
			if(missile.hitTank(myTank)){
				TankDeadMsg msg = new TankDeadMsg(myTank.getId());
				nc.send(msg);
				MissileDeadMsg mdmMsg = new MissileDeadMsg(missile.getTankId(), missile.getId());
				nc.send(mdmMsg);
			}
			missile.draw(g);
		}
		Explode explode = null;
		for(int i=0;i<explodes.size();i++){
			explode = explodes.get(i);
			explode.draw(g);
		}
		Tank t = null;
		for(int i=0;i<tanks.size();i++){
			t=tanks.get(i);
			t.draw(g);
		}
		myTank.draw(g);
	}

	@Override
	public void update(Graphics g) {
		if (offScreenImage == null) {
			offScreenImage = this.createImage(GAME_WIDTH, GAME_HEIGHT);
		}
		Graphics gOffScreen = offScreenImage.getGraphics();
		Color c = gOffScreen.getColor();
		gOffScreen.setColor(Color.GREEN);
		gOffScreen.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
		gOffScreen.setColor(c);
		this.paint(gOffScreen);
		g.drawImage(offScreenImage, 0, 0, null);
	}

	public static void main(String[] args) {
		new TankClient().launchClient();
	}

	private void launchClient() {
		/*for(int i=0;i<10;i++){
			tanks.add(new Tank(150+(i*40),50,false,Tank.Direction.D,this));
		}*/
		
		setLocation(400, 300);
		setSize(GAME_WIDTH, GAME_HEIGHT);
		setBackground(Color.GREEN);
		setVisible(true);
		setResizable(false);
		addKeyListener(new KeyMoniter());
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}

		});
		new Thread(new PaintThread()).start();
		
	//	nc.connection("127.0.0.1", TankServer.TCP_PORT);
	}

	private class PaintThread implements Runnable {

		@Override
		public void run() {
			while (true) {
				repaint();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private class KeyMoniter extends KeyAdapter {

		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode()==KeyEvent.VK_C){
				conndialog.setVisible(true);
			}else{
				myTank.keyPressed(e);
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			myTank.keyReleased(e);
		}

	}
	
	private class ConnDialog extends Dialog{
		
		Label lb_IP = new Label("IP:");
		Label lb_TCP_Port = new Label("TCP Port:");
		Label lb_UDP_Port = new Label("UDP Port:");
		TextField tfIP = new TextField("127.0.0.1",12);
		TextField tfTcpPort = new TextField(""+TankServer.TCP_PORT, 4);
		TextField tfUdpPort = new TextField("2223", 4);
		Button btn_Ok = new Button("OK");
		public ConnDialog(){
			super(TankClient.this,true);
			this.setLayout(new FlowLayout());
			this.add(lb_IP);
			this.add(tfIP);
			this.add(lb_TCP_Port);
			this.add(tfTcpPort);
			this.add(lb_UDP_Port);
			this.add(tfUdpPort);
			this.add(btn_Ok);
			this.pack();
			this.setLocation(600, 500);
			
			btn_Ok.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					String IP = tfIP.getText().trim();
					int tcpPort = Integer.parseInt(tfTcpPort.getText().trim());
					int udpPort = Integer.parseInt(tfUdpPort.getText().trim());
					nc.setUdpPort(udpPort);
					nc.connection(IP,tcpPort);
					setVisible(false);
				}
				
			});
			
			this.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					setVisible(false);
				}
			});
		}
		
	}

}
