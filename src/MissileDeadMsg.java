import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class MissileDeadMsg implements Msg {
	
	private int tankId;
	private int id;
	private TankClient tc = null;
	private int msgType =Msg.MISSILE_DEAD_MSG; 

	public MissileDeadMsg(TankClient tc) {
		this.tc = tc;
	}

	public MissileDeadMsg(int tankId,int id){
		this.tankId=tankId;
		this.id=id;
	}
	
	
	
	@Override
	public void send(DatagramSocket ds, String ip, int port) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			dos.writeInt(msgType);
			dos.writeInt(tankId);
			dos.writeInt(id);
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
			int id = dis.readInt();
			
			Missile m = null;
			for(int i=0;i<tc.missiles.size();i++){
				m=tc.missiles.get(i);
				if(m.getTankId()==tankId&&m.getId()==id){
					m.setLive(false);
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
