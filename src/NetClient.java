import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class NetClient {
	private static int UDP_PORT_START = 2226;
	private int udpPort;

	public int getUdpPort() {
		return udpPort;
	}

	public void setUdpPort(int udpPort) {
		this.udpPort = udpPort;
	}

	TankClient tc = null;
	DatagramSocket ds = null;

	public NetClient(TankClient tc) {
		this.tc = tc;
	}

	public void connection(String ipAddress, int port) {
		try {
			ds = new DatagramSocket(udpPort);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		Socket socket = null;
		try {
			socket = new Socket(ipAddress, port);
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			dos.writeInt(udpPort);
			System.out.println("Connectioned to Server!Address is " + socket.getInetAddress() + ":" + socket.getPort());
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			int id = dis.readInt();
			tc.myTank.setId(id);
			if (id % 2 == 0)
				tc.myTank.setGood(true);
			else
				tc.myTank.setGood(false);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (socket != null)
				try {
					socket.close();
					socket = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

		TankNewMsg msg = new TankNewMsg(tc.myTank);
		send(msg);
		new Thread(new UDPRecvThread()).start();
	}

	public void send(Msg msg) {
		msg.send(ds, "127.0.0.1", TankServer.UDP_PORT);
	}

	private class UDPRecvThread implements Runnable {
		byte[] buf = new byte[1024];

		@Override
		public void run() {
			System.out.println("UDPRecvThread started!");
			while (ds != null) {
				System.out.println("ds");
				DatagramPacket dp = new DatagramPacket(buf, buf.length);
				try {
					System.out.println("before receive!");
					ds.receive(dp);
					parse(dp);
					System.out.println("a packet received from server!");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		private void parse(DatagramPacket dp) {
			ByteArrayInputStream bais = new ByteArrayInputStream(buf, 0, dp.getLength());
			DataInputStream dis = new DataInputStream(bais);
			try {
				int msgType = dis.readInt();
				Msg msg = null;
				switch (msgType) {
				case Msg.TANK_NEW_MSG:
					msg = new TankNewMsg(tc);
					// 或写成TankNewMsg msg = new TankNewMsg(NetClient.this.tc);
					msg.parse(dis);
					break;
				case Msg.TANK_MOVE_MSG:
					msg = new TankMoveMsg(tc);
					msg.parse(dis);
					break;
				case Msg.MISSILE_NEW_MSG:
					msg = new MissileNewMsg(tc);
					msg.parse(dis);
					break;
				case Msg.TANK_DEAD_MSG:
					msg = new TankDeadMsg(tc);
					msg.parse(dis);
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
