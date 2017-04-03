package data;

public class Actor {
	private String firstName;
	private String lastName;
	private String image;
	
	public Actor(String fn, String ln, String i){
		firstName=fn;
		lastName=ln;
		image = i;
	}
	
	public String getFirstName(){
		return firstName;
	}
	
	public String getLastName(){
		return lastName;
	}
	
	public String getImage(){
		return image;
	}

}

