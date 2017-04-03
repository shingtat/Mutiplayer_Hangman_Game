package data;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Movie {
	private String title;
	private String director;
	private String description;
	private int year;
	private String image;
	private List<String> actors;
	private Vector<Actor> actorObjects;
	private List<String> writers;
	private double ratingTotal;
	private double ratingCount;
	private String genre;
	
	public Movie() {
		actors = new ArrayList<>();
		writers = new ArrayList<>();
	}
	
	//METHODS
	public void addActor(String actor){
		actors.add(actor);
	}
	
	public void addActorObject(Actor temp){
		actorObjects.add(temp);
	}
	
	public void addWriter(String actor){
		writers.add(actor);
	}
	
	//GETTERS
	
	public String getImage(){
		return image;
	}
	
	public String getGenre(){
		return genre;
	}

	public String getTitle() {
		return title;
	}

	public String getDirector() {
		return director;
	}

	public String getDescription() {
		return description;
	}

	public int getYear() {
		return year;
	}
	public List<String> getActors() {
		return actors;
	}
	
	public List<String> getWriters(){
		return writers;
	}

	public double getRatingTotal() {
		return ratingTotal;
	}
	
	public double getRatingCount() {
		return ratingCount;
	}
	
	public int calculateRating(){
		return (int)(ratingTotal/ratingCount);
	}
	
	public Vector<Actor> getActorObjects(){
		return actorObjects;
	}
	
	//SETTERS
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setImage(String image){
		this.image= image;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public void setActors(List<String> actors) {
		this.actors = actors;
	}

	public void setWriters(List<String> writers) {
		this.writers = writers;
	}

	public void setRatingTotal(double rating) {
		this.ratingTotal = rating;
	}
	
	public void setRatingCount(double rating) {
		this.ratingCount = rating;
	}
	
	public void incrementRatingCount(){
		this.ratingCount++;
	}
	
	public void addRatingTotal(double rating){
		this.ratingTotal+=rating;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}
	
	public void setActorObjects(Vector<Actor> actors){
		this.actorObjects=actors;
	}
}
