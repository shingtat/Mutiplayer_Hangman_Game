package data;

public class Event {

	private String action;
	private Movie movie;
	private String movieTitle;
	private String username;
	private double rating;
	private String image;
	
	public Event(){
		rating = -1;
	}
	
	//SETTERS
	public void setUsername(String username){
		this.username = username;
	}
	
	public void setAction(String action){
		this.action = action;
	}
	public void setMovieTitle(String title){
		movieTitle = title;
	}
	
	public void setRating(double rating){
		this.rating = rating;
	}
	
	public void setMovie(Movie movie){
		this.movie = movie;
		this.movieTitle = movie.getTitle();
	}
	
	public void setImage(String event){
		if(event.equals("Liked")){
			image="../img/resources/liked.png";
		}
		else if(event.equals("Disliked")){
			image="../img/resources/disliked.png";
		}
		else if(event.equals("Watched")){
			image="../img/resources/watched.png";
		}
		else if(event.equals("Rated")){
			image="../img/resources/rating5.png";
		}
	}
	
	//GETTERS
	
	public String getImage(){
		return image;
	}
	
	public String getAction() {
		return action;
	}

	public Movie getMovie() {
		return movie;
	}

	public String getUsername() {
		return username;
	}
	
	public double getRating(){
		return rating;
	}
	
	public String getMovieTitle(){
		return movieTitle;
	}
	
}
