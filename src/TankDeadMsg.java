import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class TankDeadMsg implements Msg {
	
	private int tankId;
	private TankClient tc = null;
	private int msgType =Msg.TANK_DEAD_MSG; 

	public TankDeadMsg(TankClient tc) {
		this.tc = tc;
	}

	public TankDeadMsg(int tankId){
		this.tankId=tankId;
	}
	
	
	
	@Override
	public void send(DatagramSocket ds, String ip, int port) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			dos.writeInt(msgType);
			dos.writeInt(tankId);
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
			if(id==tc.myTank.getId()){
				return;
			}
			boolean exist = false;
			Tank tank = null;
			for(int i=0;i<tc.tanks.size();i++){
				tank=tc.tanks.get(i);
				if(tank.getId()==id){
					exist = true;
					break;
				}
			}
			if(exist){
				tank.setLive(false);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
