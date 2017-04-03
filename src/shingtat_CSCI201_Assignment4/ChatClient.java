package shingtat_CSCI201_Assignment4;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ChatClient extends Thread {
	
	private BufferedReader br;
	private PrintWriter pw;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private String username;
	private Socket s;
	private Scanner scan;
	private boolean isEliminated;
	
	public ChatClient(Socket s){
	System.out.println("Congragulations! You have connected to the Cineman server!");
	this.s = s;
	String finalGameName="";
		try{
			isEliminated = false;
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
			scan = new Scanner(System.in);
			int input = printIntroduction();
			int players = 0;
			if(input==1){
				System.out.println("Please enter your username");
				String username = scan.nextLine();
				Message usernameMessage = new Message (username, "username");
				clientSendMessage(usernameMessage);
				String gameName="";
				boolean gamenameCondition = true;
				while(gamenameCondition){
					System.out.println("Please enter a unique name for your game");
					gameName = scan.nextLine();
					Message message = new Message(gameName, "isValidName");
					clientSendMessage(message);
					boolean isValidName = (boolean)ois.readObject();
					if(isValidName){
						finalGameName = gameName;
						gamenameCondition=false;
					}
					else{
						System.out.print("this name has already been chosen by another game. ");
					}
				} // end of while loop
				
				boolean playerCondition=true;
				while(playerCondition){
					try{
						System.out.println("Please enter the number of players (1-4) in the game");
						players = scan.nextInt();
						scan.nextLine();
						while(players<1 || players>4){
							System.out.print("Invalid number of players.Please enter the number of players (1-4) in the game");
							players = scan.nextInt();
							scan.nextLine();
						}
						playerCondition=false;
					} catch(InputMismatchException ime){
						System.out.println("ime exception in ChatClient: " + ime.getMessage());
					}
				}
				Message message = new Message(players,"createGame");
				clientSendMessage(message);
				
				System.out.println("Waiting for " + (players-1) + " players to join..");
				Message clientConnectMessage = null;
				while((clientConnectMessage=(Message)ois.readObject())!=null){
					String clientMessage = (String)clientConnectMessage.getObject();
					if(clientMessage.equals("Waiting for 0 players to join..")){
						Message startGame = new Message(gameName, "startGame");
						clientSendMessage(startGame);
						break;
					}
					System.out.println(clientMessage);
				}
	
			}
			
			else if(input==2){
				String gameName="";
				String username="";
				boolean condition = true;
				while(condition){
					System.out.println("Please enter the name of the game you wish to join");
					gameName = scan.nextLine();
					Message message = new Message(gameName, "gameExist");
					clientSendMessage(message);
					boolean checkGameExistence = (boolean)ois.readObject();
					boolean isFullCapacity = true;
					if(checkGameExistence){
						isFullCapacity = (boolean)ois.readObject();
					}
					else{
						isFullCapacity = false;
					}
					if(checkGameExistence && !isFullCapacity){
						String hostname = (String)ois.readObject();
						System.out.println("Congragulations! You have joined " + hostname +"'s game!" );
						
						boolean usernameCondition = true;
						while(usernameCondition){
							System.out.println("Please enter your username");
							username = scan.nextLine();
							Message validateUsername = new Message(username, "isValidUsername");
							clientSendMessage(validateUsername);
							boolean isValidUsername = (boolean)ois.readObject();
							if(isValidUsername){
								usernameCondition=false;
								condition=false;
							}
							else{
								System.out.print("this username has already been chosen by other players. ");
							}
						}
					}
					else{
						System.out.println("The game does not exist or has already reached the maximum number of players.");
					}
				}
				finalGameName=gameName;
				String fullMessage = gameName + ":" + username;
				Message usernameMessage = new Message(fullMessage, "clientConnect");
				clientSendMessage(usernameMessage);
				condition=false;
				System.out.println("waiting for other players to join...");
				String closeMessage="";
				while((closeMessage=(String)ois.readObject())!=null){
					if(closeMessage.equals("all has joined")){
						String welcome = (String)ois.readObject();
						System.out.println(welcome);
						break;
					}
				}

			}
			
			Message getWordMessage = new Message(finalGameName, "getWord");
			clientSendMessage(getWordMessage);
			String word = (String)ois.readObject();
			printInterface(8,word," ");
			this.start(); //Run method constantly receives messages from server
			
			//Following while loop reads the next line and sends it to the server;
			boolean active = true;
			String line="";
			while(active&&line!=null){
				if(isEliminated){
					line=scan.nextLine()+":"+finalGameName;
					Message message = new Message(line,"eliminatedMessage");
					clientSendMessage(message);
				}
				else{
					line = scan.nextLine()+":"+finalGameName;
					Message message = new Message(line, "gameMessage");
					clientSendMessage(message);
				}

			}
									
			} catch(IOException ioe){
				System.out.println("ioe in ChatClient: " + ioe.getMessage());
			} catch(ClassNotFoundException cnfe){
				System.out.println("cnfe in ChatClient: " + cnfe.getMessage());
			}
		
	}
	
	public void clientSendMessage(Object obj){
		try{
			oos.writeObject(obj);
			oos.flush();
		} catch(IOException ioe){
			System.out.println("ioe in ChatClient: " + ioe.getMessage());
		}

	}
	
	public int printIntroduction(){
		boolean condition = true;
		int input=0;
		while(condition){
			System.out.println("Please choose from the following:");
			System.out.println("1. Start Game");
			System.out.println("2. Join Game");
			try{
				input = scan.nextInt();
				scan.nextLine();
				while(input>2 || input<1 || input==0){
					System.out.println("here Invalid Command. Please choose from the following:");
					System.out.println("1. Start Game");
					System.out.println("2. Join Game");
					input=scan.nextInt();
					scan.nextLine();
				}
				condition=false;
			} catch(InputMismatchException e){
				System.out.print("Invalid Command. ");
			
			}
		}
		return input;
	}
	
	public void run(){ //prints the lines to console
		GameMessage gameMessage = null;
		try {
			while((gameMessage = (GameMessage)ois.readObject())!=null){
				int x = gameMessage.getLives();
				String word = gameMessage.getWord();
				String guess = gameMessage.getGuess();
				if(x==-1){
					if(guess.equals("Your hangman has been completed. You lose :(")){
						isEliminated = true;
					}
					System.out.println(guess);
				}
				else if(x==-2){
					System.exit(0);
				}
				else{
					printInterface(x,word,guess);
				}
			}
		} catch (ClassNotFoundException cnfe) {
			System.out.println("cnfe in ChatClient run: " + cnfe.getMessage());
		} catch (IOException ioe) {
			System.out.println("ioe in ChatClient run: " + ioe.getMessage());
		}
		
	}
	
	public void printInterface(int x, String word, String guesses){
		switch(x){
		case 8:
			System.out.println("  -----");
			System.out.println("  |                 Cineman");
			System.out.println("  |");
			System.out.println("  |");
			System.out.println("  |                  " + word);
			System.out.println("  |");
			System.out.println(" -|-----");
			System.out.println(guesses);
			break;
		case 7:
			System.out.println("  -----");
			System.out.println("  |                 Cineman");
			System.out.println("  |   @");
			System.out.println("  |");
			System.out.println("  |                  " + word);
			System.out.println("  |");
			System.out.println(" -|-----");
			System.out.println(guesses);
			break;
		case 6:
			System.out.println("  -----");
			System.out.println("  |                 Cineman");
			System.out.println("  |   @");
			System.out.println("  |   |");
			System.out.println("  |                  " + word);
			System.out.println("  |");
			System.out.println(" -|-----");
			System.out.println(guesses);
			break;
		case 5:
			System.out.println("  -----");
			System.out.println("  |                 Cineman");
			System.out.println("  |   @");
			System.out.println("  |  /|");
			System.out.println("  |                  " + word);
			System.out.println("  |");
			System.out.println(" -|-----");
			System.out.println(guesses);
			break;
		case 4:
			System.out.println("  -----");
			System.out.println("  |                 Cineman");
			System.out.println("  |   @");
			System.out.println("  |  /|\\");
			System.out.println("  |                  " + word);
			System.out.println("  |");
			System.out.println(" -|-----");
			System.out.println(guesses);
			break;
		case 3:
			System.out.println("  -----");
			System.out.println("  |                 Cineman");
			System.out.println("  |   @");
			System.out.println("  |  /|\\");
			System.out.println("  |  /               " + word);
			System.out.println("  |");
			System.out.println(" -|-----");
			System.out.println(guesses);
			break;
		case 2:
			System.out.println("  -----");
			System.out.println("  |                 Cineman");
			System.out.println("  |   @");
			System.out.println("  |  /|\\");
			System.out.println("  |  / \\            " + word);
			System.out.println("  |");
			System.out.println(" -|-----");
			System.out.println(guesses);
			break;
		case 1:
			System.out.println("  -----");
			System.out.println("  |   |             Cineman");
			System.out.println("  |   @");
			System.out.println("  |  /|\\");
			System.out.println("  |  / \\            " + word);
			System.out.println("  |");
			System.out.println(" -|-----");
			System.out.println(guesses);
			break;
		}
	}
	
	public static void main(String [] args){
		Scanner scan = new Scanner(System.in);
		System.out.println("Welcome to Cineman!");
		System.out.println();
		int port=0;
		boolean condition=true;
		while(condition){
			try{
				System.out.println("Please enter the ipaddress");
				String ipaddress = scan.nextLine();
				System.out.println("Please enter the port");
				port=scan.nextInt();
				scan.nextLine();
				while(port<=0){
					System.out.println("Invalid port. Please enter the port");
					port = scan.nextInt();
					scan.nextLine();
				}
				Socket s = new Socket(ipaddress, port);
				condition=false;
				new ChatClient(s);
			}catch (IOException ioe){
				System.out.println("Unable to connect to server with provided fields");
			}catch (InputMismatchException e){
				System.out.println("Invalid port. Please enter the port");
			}
		}
		
		

	}
}
