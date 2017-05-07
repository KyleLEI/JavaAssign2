package gui;

public interface Def {
	/*------Delay between world clock increment------*/
	final int delayMs=2000;
	
	/*------Spawn Messaging------*/
	final int mSpawnSuccess=0;
	final int mNotEnoughLE=1;
	final int mNotRightTime=2;
	
	/*------UI parameters------*/
	final int flagW=40;
	final int cityW=60;
	final String font="Arial";
	
	/*------Multiplayer Messaging------*/
	final int portNo=4095;
	
	/*---To Client---*/
	final int serverShutdown=-1;
	final int updateLE=0;//int
	final int updateTime=1;//UTF
	final int updateEnd=2;//UTF, update end
	final int updateRedSpawn=3;//Warrior
	final int updateMap=4;//10 warriors
	final int updateFlag=5;//int city, Team flag
	final int updateRedOccu=6;//int
	final int updateBlueOccu=7;//int
	final int spawnResponse=8;//int,Warrior
	
	/*---To Server---*/
	final int clientDisconnect=-1;
	final int requestResponse=0;
	
	
}
