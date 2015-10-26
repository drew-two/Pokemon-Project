import java.io.*;
import java.util.*;

class Pokemon{
//Declarations-----------------------------------------------------------------------------------------------------
	public int maxhp, hp,nrg,dis,atkn; //health, energy, disable check, num of attacks
	public boolean stun; //check if pokemon is stunned
	public String name,type,res,weak; //name, typing, type resistance, type weakness
	public String[][] atk;//string for storing attack characteristics
	
//File IO Method---------------------------------------------------------------------------------------------------
	public static Scanner FileIO(){//method to grab Pokemon data from a text file 
		Scanner infile = null;
		try{
			infile = new Scanner ( new File ("pokemon.txt"));
		}
		catch (IOException ex){ 
			System.out.println("Pokemon Data file not found.");
		}
		return infile;//returns scanner of file if it exists
	}
	
//Generates Pokemon and outputs them-------------------------------------------------------------------------------	
	public static ArrayList<Pokemon> createPokemon(ArrayList<Pokemon> allPoke,Scanner infile,int numOfPokemon){
		//returns arrayList of Pokemon taking in scanner and number of pokemon in text file
		for (int i=0; i<numOfPokemon; i++){
			String line = infile.nextLine();
			allPoke.add(new Pokemon(line));
			String[] name = line.split(",");
			System.out.println((i+1)+". "+name[0]);//Outputs list so user can pick tehir team
		}
		return allPoke;//returns arraylist of pokemon
	}
	
//Pokemon Constructor----------------------------------------------------------------------------------------------
	public Pokemon(String line){//create a pokemon object using line of data from text file
		nrg=50;//energy stat starts at 50
		dis=0;//pokemon starts not disabled
		stun=false;//pokemon starts not stunned
		String dex[]=line.split(",");//"pokedex entry" from constructor String
		name=dex[0];
		maxhp=hp=Integer.parseInt(dex[1]);//current hp and max hp of pokemon
		type=dex[2];
		res=dex[3];
		weak=dex[4];
		atkn=Integer.parseInt(dex[5]);//number of attacks pokemon has
		atk=new String[atkn][4];//2D list for attacks{{name, cost, damage, special}...}
		int count=-1;//counter for list
		for(int i=6;i<dex.length;i+=4){
			count++;
			for(int j=0;j<4;j++){
				atk[count][j]=dex[i+j];
			}
		}
		//fills attack list
	}

//Method for picking team------------------------------------------------------------------------------------------
	public static void chooseTeam(ArrayList<Pokemon> allPoke,ArrayList<Pokemon> userPoke){
		//method needs arraylist for all pokemon and user's team, and returns arraylist of users team
		System.out.println("Choose four Pokemon; type the number then hit enter.");
		Scanner kb = new Scanner(System.in);
		int count=0;//counter for picked pokemon
		
		while(userPoke.size()<4){//while loop to repeat despite errors
			System.out.println((4-count)+" left to choose.");
			String inputCheck=kb.nextLine();
			int validInput;

			if(Pokemon.isInt(inputCheck,1,allPoke.size())==false){
				continue;
			}//method to make sure user inputs something valid, otherwise it restarts loop
			
			else{
				validInput = Integer.parseInt(inputCheck)-1;
			}//if it works, it moves index back to work in list
			
			if(userPoke.contains(allPoke.get(validInput))==true){
				System.out.println("You've already chosen that Pokemon.");
				continue;
			}//check so user does not pick a pokemon twice
			
			else{
				userPoke.add(allPoke.get(validInput));
				System.out.println("You chose: "+allPoke.get(validInput).name);
				count++;//counter for every successfully picked pokemon
			}
		}
	}
	
//Main game mechanics------------------------------------------------------------------------------------------
	public void attack(Pokemon target,int atkArrayIndex){
		//Attack takes enemy health, given a target, and the attacker's attack array index for used attack 
		Random rand=new Random();
		double dmgMult=1;//damage multiplier for type advantages
		if(this.atk[atkArrayIndex][3].equals("stun")){if((rand.nextInt(2)+0)==0){target.stun=true;}}
		//stuns other pokemon if possible
		if(this.atk[atkArrayIndex][3].equals("disable")){target.dis=10;}//disables other pokemon if applicable
		if(this.atk[atkArrayIndex][3].equals("recharge")){this.nrg=Math.min(50,nrg+=20);}//recharges if it is applicable
		if(this.type.equals(target.res)){dmgMult=0.5;}//checks for type resistance
		if(this.type.equals(target.weak)){dmgMult=2;}//checks for type weakness
		
		//check for stun is done when enemy/user picks move, so move is already valid
		if(this.atk[atkArrayIndex][3].equals("wild card")){//check if move has wild card
			System.out.println("\n"+this.name+" used "+atk[atkArrayIndex][0]+"!");//outputs attack used
			
			if((rand.nextInt(2) + 0)==0){ //wild card only works half of the time
				target.hp=Math.max((int)(target.hp-((Integer.parseInt(this.atk[atkArrayIndex][2])*dmgMult)-this.dis)),0);//Attacks
				//removes health from second pokemon; attack power times type multiplier
				this.nrg=Math.max(0,nrg-Integer.parseInt(atk[atkArrayIndex][1]));
				//subtracts energy accordingly, never going below zero		
				if(this.type.equals(target.weak)){System.out.println("It was super effective!");}//only if type advantage bonus
				if(this.type.equals(target.res)){System.out.println("It wasn't very effective...");}//only if type disadvantage
				if(target.stun==true){System.out.println(target.name+" was stunned!");}//tells player that pokemon was stunned
			}	
				
			else{//if wild card fails
				this.nrg=Math.max(0,nrg-Integer.parseInt(atk[atkArrayIndex][1]));//wild card fails but energy is taken
				target.stun=false; //since wild card failed, the pokemon should not be stunned
				System.out.println("...but nothing happened!");//if attack is unsuccessful
			}
		}
		else{//if they do not have wild card
			System.out.println("\n"+this.name+" used "+atk[atkArrayIndex][0]+"!");
			
			if(this.atk[atkArrayIndex][3].equals("wild storm")){//if pokemon has wild storm
				int count=1;//counter to tell user how many times attack hit
				int wildStorm = rand.nextInt(2) + 0;
				while(wildStorm==0&&target.hp>0){
					target.hp=Math.max((int)(target.hp-((Integer.parseInt(this.atk[atkArrayIndex][2])*dmgMult)-this.dis)),0);
					System.out.println("Hit "+count+" times!");
					wildStorm = rand.nextInt(2) + 0;
					count++;
				}
				//when wild storm fails
				this.nrg=Math.max(0,nrg-Integer.parseInt(atk[atkArrayIndex][1]));
				if(this.type.equals(target.weak)){System.out.println("It was super effective!");}
				if(this.type.equals(target.res)){System.out.println("It wasn't very effective...");}
				if(target.stun==true){System.out.println(target.name+" was stunned!");}
			}
			else{//if pokemon does not have wild storm
				target.hp=Math.max((int)(target.hp-((Integer.parseInt(this.atk[atkArrayIndex][2])*dmgMult)-this.dis)),0);
				this.nrg=Math.max(0,nrg-Integer.parseInt(atk[atkArrayIndex][1]));
				if(this.type.equals(target.weak)){System.out.println("It was super effective!");}
				if(this.type.equals(target.res)){System.out.println("It wasn't very effective...");}
				if(target.stun==true){System.out.println(target.name+" was stunned!");}
			}
		}
		
		if(target.hp>0){//outsputs both stats of both pokemon, if the target is still alive
			System.out.println("\n"+target.name+" has "+target.hp+" hp and "+target.nrg+" energy.");
			System.out.println(this.name+" has "+this.hp+" hp and "+this.nrg+" energy.");
		}
		
		else{
			System.out.println(this.name+" has "+this.hp+" hp and "+this.nrg+" energy.");
		}
	}
	
	public Pokemon choosePokemon(ArrayList<Pokemon> userPoke,boolean ifRetreating){
		//method for switching pokemon before and during battle, taking in pokemon and user team
		Scanner kb = new Scanner(System.in);
		
		System.out.println("\nChoose your Pokemon; type the number then hit enter.");
		for(int i=0;i<userPoke.size();i++){
			System.out.println((i+1)+". "+userPoke.get(i).name);
		}
		if(ifRetreating==true){
			System.out.println((userPoke.size()+1)+". Return to move selection.");
		}//gives option to return to picking moves if they are doing this during battle
		
		while(true){
			String inputCheck=kb.nextLine();
			int validInput;

			if(isInt(inputCheck,1,userPoke.size()+1)==false){
				continue;
			}//checks if valid input
			
			if(ifRetreating==true){
				if(Integer.parseInt(inputCheck)==userPoke.size()+1){return this;}
			}//if they choose to retreat, arbitrarily return current pokemon
			
			validInput = Integer.parseInt(inputCheck)-1;
			if(this.hp<=0&&userPoke.size()<4){userPoke.add(this);}
			//adds to team arraylist if pokemon is not fainted and team size is less than 4
			System.out.println("\n"+userPoke.get(validInput).name+", I choose you!\n");
			//otherwise sends out new pokemon
			Pokemon newGuy = userPoke.get(validInput);
			userPoke.remove(validInput);//removes from team arraylist
			return newGuy;//returns new pokemon
		}
	}
	
	public Pokemon userMove(Pokemon enemy, ArrayList<Pokemon> userPoke){
		//method for user's ingame options, attacking, retreating, and passing
		if(this.stun==false){//makes sure pokemon can move
			while(true){
				Scanner kb = new Scanner(System.in);
				System.out.println("\nEnter the number to pick an action:\n 1. Attack\n 2. Retreat\n 3. Pass");
				//User's options
				
				String inputCheck=kb.nextLine();
				int validInput;
				if(isInt(inputCheck,1,3)==false){//check for valid options
					continue;
				}
				validInput=Integer.parseInt(inputCheck);
				
				if(validInput==1){//1st option, to attack
					System.out.println("\nAvailable moves.");//prints move selections
					for(int i=0;i<atkn;i++){System.out.println((i+1)+". "+atk[i][0]);}
					System.out.println(atkn+1+". Return to move selection.");//option to go back
					while(true){
						inputCheck=kb.nextLine();
						if(isInt(inputCheck,1,atkn+1)==false){
							continue;
						}
						validInput=Integer.parseInt(inputCheck)-1;
						if(validInput==atkn){break;}//go back to selction
						if(this.nrg<Integer.parseInt(atk[validInput][1])){
							System.out.println(this.name+" does not have enough energy.");
							continue;
						}//check if pokemon has enough energy		
						this.attack(enemy,validInput);
						return this;//catch so method can actually return switched out pokemon
					}
				}
				if(validInput==2){//2nd option, to retreat
					Pokemon nextToBattle=choosePokemon(userPoke,true);
					if(this==nextToBattle){continue;}//if same pokemon is returned, turn is not used
					System.out.println(nextToBattle.name);
					return nextToBattle;//returns switched pokemon
				}
				if(validInput==3){//3rd option, to pass
					System.out.println("\n"+this.name+" passed.");
					return this;//catch so method can actually return switched out pokemon
				}
			}
		}
		else{//skips turn if Pokemon is stunned
			System.out.println("\n"+"Pokemon is stunned.\n"+this.name+" had to pass!");
			this.stun=false;
			return this;//catch so method can actually return switched out pokemon
		}
	}
	
	public void enemyMove(Pokemon user){//chooses random move for enemy
		ArrayList<Integer> viableAtks = new ArrayList<Integer>();
		int outcome=this.atkn;
		for(int i=0;i<this.atkn;i++){
			if(this.nrg>(Integer.parseInt(atk[i][1]))){
				viableAtks.add(i);//Enters array index of viable attack into arraylist
			}
		}
		if(viableAtks.size()>0){ //if there are any usable attacks
			Collections.shuffle(viableAtks); //shuffles usable attacks arraylist
			outcome=viableAtks.get(0);//returns randomized first index
		}
		else{System.out.println(this.name+" does not have enough energy.");}
		if(outcome<this.atkn&&this.stun==false){this.attack(user,outcome);}//if there are viable attacks, enemy will attack
		if(this.stun==true){System.out.println("\n"+"Pokemon is stunned.\n"+this.name+" had to pass!");}
	}	//also checks if enemy is stunned or not
	
	public static Pokemon chooseEnemy(ArrayList<Pokemon> allPoke){
		//method for switching out new enemy
		Pokemon newGuy=allPoke.get(0);
		allPoke.remove(0);
		return newGuy;
	}
	
	public static void game(ArrayList<Pokemon> allPoke,ArrayList<Pokemon> userPoke){
		//method that runs game
		Random rand= new Random();
		System.out.println("\nAll Pokemon set! The rest are your enemies. Good luck.");
		
		Pokemon currPoke=userPoke.get(0);
		Pokemon badGuy = Pokemon.chooseEnemy(allPoke);
		System.out.println("\nYour first enemy is "+badGuy.name+"!");
		currPoke = currPoke.choosePokemon(userPoke,false);	
		int pick = rand.nextInt(2) + 0;	//randomizer for order of battle
		while(allPoke.size()>0&&badGuy.hp>0){//loop to keep game playing
			
			if(pick==0){//enemy first then player
				System.out.println(badGuy.name+" goes first!\n");
				System.out.println(badGuy.name+" has "+badGuy.hp+" hp and "+badGuy.nrg+" energy.");
				System.out.println(currPoke.name+" has "+currPoke.hp+" hp and "+currPoke.nrg+" energy.");
				while(true){
					badGuy.enemyMove(currPoke);//enemy can attack player
					if(currPoke.hp<=0){break;}//check if player poke is alive
					currPoke=currPoke.userMove(badGuy,userPoke);//player chooses move
					if(badGuy.hp<=0){break;}//check if enemy poke is alive
					badGuy.nrg=Math.min(50,badGuy.nrg+10);//adds energy to pokemon at the end of round
					currPoke.nrg=Math.min(50,currPoke.nrg+10);
				}
			}
			if(pick==1){//player first then enemy
				System.out.println(currPoke.name+" goes first!\n");
				System.out.println(badGuy.name+" has "+badGuy.hp+" hp and "+badGuy.nrg+" energy.");
				System.out.println(currPoke.name+" has "+currPoke.hp+" hp and "+currPoke.nrg+" energy.");
				while(true){
					currPoke=currPoke.userMove(badGuy,userPoke);
					if(badGuy.hp<=0){break;}
					badGuy.enemyMove(currPoke);
					if(currPoke.hp<=0){break;}
					badGuy.nrg=Math.min(50,badGuy.nrg+10);
					currPoke.nrg=Math.min(50,currPoke.nrg+10);
				}
			}
			
			if(currPoke.hp<=0){//after player poke faints
				System.out.println("\n"+currPoke.name+" has fainted!");
				badGuy.dis=0;//living pokemon is no longer disabled
				if(userPoke.size()>0){//if any pokemon remain,
					badGuy.nrg=Math.min(50,badGuy.nrg+10);//bad guy gets energy
					currPoke.getHP(userPoke);
					currPoke=currPoke.choosePokemon(userPoke,false);//player chooses pokemon
					pick = rand.nextInt(2) + 0;//random order of next battle
				}
				else{//if player has no more pokemon and whites out
					System.out.println("All Pokemon have fainted!\n.\n.\n.\nYou rushed to the Pokemon Centre. Play again?");
				}
				
			}
			
			if(badGuy.hp<=0){//after enemy poke faints
				System.out.println("The opposing "+badGuy.name+" fainted!\n");
				currPoke.dis=0;
				if(allPoke.size()>0){//when there are still other enemies
					currPoke.nrg=Math.min(50,currPoke.nrg+10);//adds energy to player poke
					badGuy = Pokemon.chooseEnemy(allPoke);//chooses new enemy
					System.out.println("Your next enemy is "+badGuy.name+"!");
					userPoke.add(currPoke);//returns pokemon to pokeball
					currPoke=currPoke.choosePokemon(userPoke,false);//allows user to pick pokemon again
					pick = rand.nextInt(2) + 0;
				}
				else{//if there are no other enemies and player wins
					System.out.println("\nYou have defeated them all! You are Trainer Supreme!");
					break;
				}
			}
		}
	}
	
//Utility Methods--------------------------------------------------------------------------------------------------
	public void getHP(ArrayList<Pokemon> userPoke){
		//heals user's pokemon post battle
		this.hp=Math.min(this.hp+20,maxhp);
		for(int i=0;i<userPoke.size();i++){userPoke.get(i).hp=Math.min(userPoke.get(i).hp+20,maxhp);}
		//math.min to makes sure pokemon hp never exceeds total
	}
		
	public static boolean isInt(String check,int min,int max){
		//utility method to make sure user enters in proper selection
		try{ 
	        Integer.parseInt(check); //checks for proper number
	    }catch(NumberFormatException e){ 
	        System.out.println("Enter a valid number.");
	        return false; 
	    }
	    if(Math.min(Integer.parseInt(check),min)!=min){
	    	System.out.println("Enter a valid number.");
	    	return false;
	    }
	    else if(Math.max(Integer.parseInt(check),max)!=max){
	    	System.out.println("Enter a valid number.");
	    	return false;
	    }
	    //check for proper range
	    else{return true;}//otherwise, it works, returns true
	}
}