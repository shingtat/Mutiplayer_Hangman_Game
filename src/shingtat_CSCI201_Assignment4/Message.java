package shingtat_CSCI201_Assignment4;

import java.io.Serializable;

//Used to send messages to start a game
public class Message implements Serializable {
	private static final long serialVersionUID = -2342323432342L;
	public  String action;
	public  Object obj;
	
	public Message(Object obj, String action){
		this.obj = obj;
		this.action = action;
	}
	
	public String getAction(){
		return action;
	}
	
	public Object getObject(){
		return obj;
	}
}
