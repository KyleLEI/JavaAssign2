package world;

import java.util.Scanner;
import java.util.StringTokenizer;

public class Main {

	public static void main(String[] args) {
		Scanner sc=new Scanner(System.in);
		StringTokenizer tknzr;
		
		String firstLine=sc.nextLine();
		tknzr=new StringTokenizer(firstLine," ");
		World world=new World(Integer.parseInt(tknzr.nextToken()),
				Integer.parseInt(tknzr.nextToken()),
				Integer.parseInt(tknzr.nextToken()));
		
		String secondLine=sc.nextLine();
		tknzr=new StringTokenizer(secondLine," ");
		world.setHP(Integer.parseInt(tknzr.nextToken()), 
				Integer.parseInt(tknzr.nextToken()), 
				Integer.parseInt(tknzr.nextToken()), 
				Integer.parseInt(tknzr.nextToken()), 
				Integer.parseInt(tknzr.nextToken()));
		
		String thirdLine=sc.nextLine();
		tknzr=new StringTokenizer(thirdLine," ");
		world.setAttack(Integer.parseInt(tknzr.nextToken()), 
				Integer.parseInt(tknzr.nextToken()), 
				Integer.parseInt(tknzr.nextToken()), 
				Integer.parseInt(tknzr.nextToken()), 
				Integer.parseInt(tknzr.nextToken()));
		
		sc.close();
		//reading input complete
		world.run();
	}

}
