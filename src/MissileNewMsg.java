import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class MissileNewMsg implements Msg {

	private Missile m = null;
	private TankClient tc = null;

	private int msgType;

	public MissileNewMsg(TankClient tc) {
		this.tc = tc;
	}

	public MissileNewMsg(Missile m) {
		this.m = m;
	}

	@Override
	public void send(DatagramSocket ds, String ip, int port) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			dos.writeInt(Msg.MISSILE_NEW_MSG);
			dos.writeInt(m.getTankId());
			dos.writeInt(m.x);
			dos.writeInt(m.y);
			dos.writeInt(m.dir.ordinal());
			dos.writeBoolean(m.isGood());
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
			int tankId = dis.readInt();
			if (tankId == tc.myTank.getId())
				return;
			int x = dis.readInt();
			int y = dis.readInt();
			Dir dir = Dir.values()[dis.readInt()];
			boolean good = dis.readBoolean();
			Missile m = new Missile(tankId, x, y, dir, good, tc);
			tc.missiles.add(m);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
