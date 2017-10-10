import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class TankServer {

	public static final int TCP_PORT = 8888;
	public static final int UDP_PORT = 6666;
	private List<Client> clients = new ArrayList<Client>();
	private static int ID = 100;

	public static void main(String[] args) {
		new TankServer().start();
	}

	private void start() {
		new Thread(new UDPThread()).start();
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(TCP_PORT);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		while (true) {
			Socket socket = null;
			try {
				socket = serverSocket.accept();
				DataInputStream dis = new DataInputStream(socket.getInputStream());
				String ipAddress = socket.getInetAddress().getHostAddress();
				int udpPort = dis.readInt();
				System.out.println("A Client Connectioned! Address is " + ipAddress + " UDP Port is" + udpPort);
				Client client = new Client(ipAddress, udpPort);
				clients.add(client);
				DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
				dos.writeInt(ID++);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (socket != null){
					try {
						socket.close();
						socket = null;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private class Client {
		private String IP;
		private int udpPort;

		public Client(String IP, int udpPort) {
			this.IP = IP;
			this.udpPort = udpPort;
		}

	}

	private class UDPThread implements Runnable {

		byte[] buf = new byte[1024];

		@Override
		public void run() {
			DatagramSocket ds = null;
			try {
				ds = new DatagramSocket(UDP_PORT);
			} catch (SocketException e) {
				e.printStackTrace();
			}
			System.out.println("UDP Thread started at port:" + UDP_PORT);
			while (ds != null) {
				DatagramPacket dp = new DatagramPacket(buf, buf.length);
				try {
					ds.receive(dp);
					System.out.println("a packet received!");
					for (int i = 0; i < clients.size(); i++) {
						Client c = clients.get(i);
System.out.println(c.IP + c.udpPort);
						dp.setSocketAddress(new InetSocketAddress(c.IP, c.udpPort));
System.out.println(dp.getAddress().getHostAddress() + "  " + dp.getSocketAddress());
						ds.send(dp);
System.out.println("a packet sended!");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

	}

}
