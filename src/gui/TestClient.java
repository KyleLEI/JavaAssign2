package gui;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import warriors.Warrior;
import world.Team;

public class TestClient{
	static ObjectInputStream in;
	public static void main(String[] args) {
		try {
			Socket socket = new Socket("localhost", Def.portNo);
			System.out.println("Server Connected");
			in = new ObjectInputStream(socket.getInputStream());
			System.out.println("Input Stream Generated");
			ObjectOutputStream out=new ObjectOutputStream(socket.getOutputStream());
			System.out.println("Output Stream Generated");
			for(;;){
				decode(in.readInt());
			}
			
		} catch (Exception e) {
			System.out.println("Connection Error: "+e.getMessage());
		}
	}
	static void decode(int header) throws IOException, ClassNotFoundException{
		switch(header){
		case Def.updateLE://int
			System.out.println(in.readInt());
			break;
		case Def.updateTime://UTF
			System.out.println(in.readUTF());
			break;
		case Def.updateEnd://UTF, update end
			System.out.println(in.readUTF());
			break;
		case Def.updateRedSpawn://Warrior
			System.out.println((Warrior)in.readObject());
			break;
		case Def.updateMap://City[]
			System.out.println("Start receiving map");
			for(int i=0;i<10;i++){
				Warrior w= (Warrior) in.readObject();
//				if(w!=null){
//					System.out.println(w.getSteps());
//					System.out.println(w.getAttackV());
//					System.out.println(w.getHP());
//				}
			}
			System.out.println("Map received");
			break;
		case Def.updateFlag://int city, Team flag
			System.out.println("Flag raised in city "+in.readInt());
			System.out.println("Color is "+((Team)in.readObject()));
			break;
		case Def.updateRedOccu://int
			System.out.println(in.readInt());
			break;
		case Def.updateBlueOccu://int
			System.out.println(in.readInt());
		}
	}

}
