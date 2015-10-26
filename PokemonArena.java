import java.io.*;
import java.util.*;

public class PokemonArena{
//Declarations------------------------------------------------------------------------
	private static ArrayList<Pokemon> allPoke = new ArrayList<Pokemon>();
	private static ArrayList<Pokemon> userPoke = new ArrayList<Pokemon>();
	//arraylists for all pokemon (and afterwards enemies) and users team
	
	public static void main (String[] args){

		Scanner infile=Pokemon.FileIO();//Scanner is returned from File IO method
		String numOfPokemon=infile.nextLine();//number of pokemon from scanner

		allPoke=Pokemon.createPokemon(allPoke,infile,Integer.parseInt(numOfPokemon));
		//arraylist is returned from method to create all pokemon
		
		Pokemon.chooseTeam(allPoke,userPoke);
		//userPoke is filled with player choice
		
		for(int i=0;i<4;i++){allPoke.remove(userPoke.get(i));}
		Collections.shuffle(allPoke);
		//removes pokemons user chose and randomizes order of enemies
		
		Pokemon.game(allPoke,userPoke);
		//runs game
	}
}