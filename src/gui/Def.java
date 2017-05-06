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
	final int updateTime=0;
	final int updateEnd=1;
	final int updateRedSpawn=2;
	final int updateMap=3;
	final int updateFlag=4;
	final int updateRedOccu=5;
	final int updateBlueOccu=6;
	final int spawnResponse=7;
	
	/*---To Server---*/
	final int clientDisconnect=-1;
	final int requestResponse=0;
	
	
}
