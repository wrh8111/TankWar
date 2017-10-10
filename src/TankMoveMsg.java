import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class TankMoveMsg implements Msg {

	private int msgType = Msg.TANK_MOVE_MSG;
	private TankClient tc;
	private int id;
	private Dir dir;
	private Dir ptdir;

	private int x, y;

	public TankMoveMsg(int id, int x, int y, Dir dir,Dir ptdir) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.dir = dir;
		this.ptdir=ptdir;
	}

	public TankMoveMsg(TankClient tc) {
		this.tc = tc;
	}

	@Override
	public void send(DatagramSocket ds, String ip, int port) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			dos.writeInt(msgType);
			dos.writeInt(id);
			dos.writeInt(x);
			dos.writeInt(y);
			dos.writeInt(dir.ordinal());
			dos.writeInt(ptdir.ordinal());
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] buf = baos.toByteArray();
		try {
			DatagramPacket dp = new DatagramPacket(buf, buf.length, new InetSocketAddress(ip, port));
			ds.send(dp);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void parse(DataInputStream dis) {
		try {
			int id = dis.readInt();
			if (id == tc.myTank.getId()) {
				return;
			}
			int x = dis.readInt();
			int y = dis.readInt();
			Dir dir = Dir.values()[dis.readInt()];
			Dir ptdir = Dir.values()[dis.readInt()];
			boolean exist = false;
			for (int i = 0; i < tc.tanks.size(); i++) {
				Tank t = tc.tanks.get(i);
				if (t.getId() == id) {
					t.setX(x);
					t.setY(y);
					t.dir = dir;
					t.ptdir=ptdir;
					exist = true;
					break;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
