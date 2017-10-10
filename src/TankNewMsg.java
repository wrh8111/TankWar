import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class TankNewMsg implements Msg {
	private Tank tank;
	private TankClient tc;
	private int msgType = Msg.TANK_NEW_MSG;
	
	public TankNewMsg(Tank tank) {
		this.tank = tank;
	}
	public TankNewMsg(TankClient tc) {
		this.tc=tc;
	}

	public void send(DatagramSocket ds, String ip, int port) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			dos.writeInt(msgType);
			dos.writeInt(tank.getId());
			dos.writeInt(tank.getX());
			dos.writeInt(tank.getY());
			dos.writeInt(tank.dir.ordinal());
			dos.writeBoolean(tank.isGood());
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
	public void parse(DataInputStream dis) {
		try {
			int id = dis.readInt();
			if(id==tc.myTank.getId()){
				return;
			}
			int x=dis.readInt();
			int y=dis.readInt();
			Dir dir = Dir.values()[ dis.readInt()];
			boolean good = dis.readBoolean();
			boolean exist = false;
			for (int i=0;i<tc.tanks.size();i++){
				if(tc.tanks.get(i).getId()==id){
					exist = true;
					break;
				}
			}
			if(!exist){
				TankNewMsg tnMsg = new TankNewMsg(tc.myTank);
				tc.nc.send(tnMsg);
				Tank t = new Tank(x,y,good,dir,tc);
				t.setId(id);
				tc.tanks.add(t);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
