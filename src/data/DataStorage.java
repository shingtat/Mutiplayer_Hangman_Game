package data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DataStorage {
	//maps a username to a user object
	protected Map<String, User> usersMap;
	//maps a movie title to a movie object
	protected Map<String, Movie> moviesMap;
	//maps a case insensitive username to a list of case sensitive matches
	private Map<String, Set<User>> usernameToUsers;
	private Map<String, Set<User>> firstNameToUsers;
	private Map<String, Set<User>> lastNameToUsers;
	//maps a username to a list of followers
	private Map<String, Set<String>> usernameToFollowers;
	//maps an actor to a list of movies
	private Map<String, Set<Movie>> actorToMovies;
	//maps a genre to a list of movies
	private Map<String, Set<Movie>> genreToMovies;
	//maps a case insensitive movie title to a list of case sensitive matches
	private Map<String, Set<Movie>> titleToMovies;
	//genres
	private List<String> genresList;
	//actions
	private List<String> actionsList;
	//all actors
	protected Vector<Actor> allActors;
	protected Vector<String> wordBank;
	protected Vector<String> finalWordBank; //word bank with no duplicates
	protected Random random;
	private Document doc;
	//user currently logged in
	private User loggedInUser;
	
	private String file;
	
	public DataStorage(String filepath) throws CinemateException{
		usersMap = new HashMap<>();
		moviesMap = new HashMap<>();
		genresList = new ArrayList<>();
		actionsList = new ArrayList<>();
		usernameToUsers = new HashMap<>();
		firstNameToUsers = new HashMap<>();
		lastNameToUsers = new HashMap<>();
		usernameToFollowers = new HashMap<>();
		actorToMovies = new HashMap<>();
		genreToMovies = new HashMap<>();
		titleToMovies = new HashMap<>();
		allActors= new Vector<Actor>();
		wordBank = new Vector<String>();
		finalWordBank = new Vector<String>();
		random = new Random();
		file=filepath;
		

		try{
			//create the document object and parse it
			DocumentBuilder dBuilder= DocumentBuilderFactory.newInstance().newDocumentBuilder();
			doc = dBuilder.parse(new File(filepath));
			parse();
			
			for (User user : usersMap.values()) {
				Set<String> followers = usernameToFollowers.get(user.getUsername());
				Set<String> following = user.getFollowing();
				
				//check that all usernames in following list are valid
				for (String follow : following){
					if (!usersMap.containsKey(follow)){
						throw new CinemateException("Invalid username in following list");
					}
				}
				
				if (followers != null) {
					
					user.setFollowers(followers);
				}
			}
			
			//Putting data into the word bank
			for(Map.Entry<String,Movie> entry: moviesMap.entrySet()){
				wordBank.add(entry.getKey());
				List<String> writers = entry.getValue().getWriters();
				for(String writer : writers){
					wordBank.add(writer);
				}
			}
			for(Actor temp: allActors){
				String actorName = temp.getFirstName() + " " + temp.getLastName();
				wordBank.add(actorName);
			}
			
			for(String word: wordBank){
				if(!finalWordBank.contains(word)){
					finalWordBank.add(word);
				}
			}
	
			
			//TEST MOVIES
//			System.out.println("all movies");
//			Set keys = moviesMap.keySet();
//			for(Iterator i = keys.iterator(); i.hasNext();){
//				String key = (String)i.next();
//				System.out.println(key);
//				Vector<Actor> temp = (Vector<Actor>)moviesMap.get(key).getActorObjects();
//				for(Actor a:temp){
//					System.out.println(a.getFirstName());
//				}
//				System.out.println();
//			}
			
			//TEST ACTORS
//			System.out.println("ACTORS!!!!!");
//			for(Actor temp : allActors){
//				System.out.println(temp.getFirstName());
//			}
			
			
		}
		catch (SAXException | IOException | ParserConfigurationException e){
			throw new CinemateException(e.getMessage());
		}
		
	}
	
	//search methods
	
	public Set<Movie> searchByGenre(String genre){
		return genreToMovies.get(genre.toLowerCase());
	}
	
	public Set<Movie> searchByTitle(String title){
		return titleToMovies.get(title.toLowerCase());
	}
	
	public Set<Movie> searchByActor(String actor){
		return actorToMovies.get(actor.toLowerCase());
	}
	
	public User getUser(String usernanme){
		return usersMap.get(usernanme);
	}
	
	public Movie getMovie(String title){
		return moviesMap.get(title);
	}
	
	public void setLoggedInUser(String username){
		loggedInUser = usersMap.get(username);
	}
	
	public User getLoggedInUser(){
		return loggedInUser;
	}
	
	public Vector<Actor> getAllActorObjects(){
		return allActors;
	}
	
	//check if a username is valid
	public Boolean validUsername(String username){
		return usersMap.containsKey(username);
	}
	//check if a password is correct
	public Boolean correctPassword(String username, String password){
		return usersMap.get(username).getPassword().equals(password);
	}
	//retrieve all users with matching username, fname or lname
	public Set<User> searchForUser(String username){
		Set<User> userSets = new HashSet<>();
		Set<User> usernames = usernameToUsers.get(username.toLowerCase());
		Set<User> fnames = firstNameToUsers.get(username.toLowerCase());
		Set<User> lnames = lastNameToUsers.get(username.toLowerCase());
		
		if (usernames != null) userSets.addAll(usernames);
		if(fnames != null) userSets.addAll(fnames);
		if (lnames != null) userSets.addAll(lnames);
		
		return userSets;
	}
	
	//takes a NodeList and either the list of actions or list of genres 
	private void addToGenresOrActions(List<String> toAddTo, NodeList startNode){
		//children of the parent tag (either <actions> or <genres>)
		NodeList children = startNode.item(0).getChildNodes();
		//iterate through the children and get their text content, add it to the list
		for (int i = 0; i<children.getLength(); i++){

			if (children.item(i).hasChildNodes()){

				toAddTo.add(children.item(i).getFirstChild().getTextContent());
			}
		}
	}
	
	private void parse() throws CinemateException{
		
		NodeList genresNodeList = doc.getElementsByTagName("genres");
		NodeList actionsNodeList = doc.getElementsByTagName("actions");
		NodeList moviesNodeList = doc.getElementsByTagName("movies");
		NodeList usersNodeList = doc.getElementsByTagName("users");
		//parsing actions and genres
		addToGenresOrActions(genresList, genresNodeList);
		addToGenresOrActions(actionsList, actionsNodeList);
		//parsing movies and users (parsing movies first so we will have the movie objects ready when parsing a user's feed)
		parseObjects(moviesNodeList, true);
		parseObjects(usersNodeList, false);
	}
	
	//the same logic is used for iterating through movie and user nodes. So, we pass in a boolean to determine the helper method to call
	private void parseObjects(NodeList rootNode, Boolean isMovie) throws CinemateException{
		NodeList children = rootNode.item(0).getChildNodes();
		
		int count = 0;
		
		for (int i = 0; i<children.getLength(); i++){
			Node node = children.item(i);
			//some of the elements show up as text elements, so we need this check before we choose to parse
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				count++;
				if (isMovie) parseMovie(node);
				else parseUser(node);
			}
		}
		//if we are parsing movies and there were none in the file, throw an exception
		if (isMovie && count == 0){
			throw new CinemateException("No movies found in file.");
		}
	}
	
	//PARSE MOVIES
	//parsing one movie object
	private void parseMovie(Node rootNode) throws CinemateException{
		//get the fields of the movie
		NodeList movieFields = rootNode.getChildNodes();
		Movie movie = new Movie();
		
		for (int i = 0; i<movieFields.getLength(); i++){
			//get the current field of the movie
			Node movieField = movieFields.item(i);
			String nodeName = movieField.getNodeName();
			
			switch (nodeName){
			
				case "director":
					movie.setDirector(movieField.getFirstChild().getTextContent());
					break;
				case StringConstants.WRITERS:
					//parse the writers in a helper method
					parseMovieSubList(StringConstants.WRITERS, movieField, movie);
					break;
				case "year":
					//try to parse year, if we get NumberFormatException, we know we weren't given an integer
					try{
						movie.setYear(Integer.parseInt(movieField.getFirstChild().getTextContent()));
					}
					catch (NumberFormatException nfe){
						throw new CinemateException("Movie year is not an int value");
					}
					
					break;
				case StringConstants.ACTORS:
					//parse the actors in a helper method
					parseMovieSubList(StringConstants.ACTORS, movieField, movie);
					parseActors("actors", movieField,movie);
					break;
					
				case StringConstants.GENRE:
					movie.setGenre(movieField.getFirstChild().getTextContent());
					
					if (!genresList.contains(movie.getGenre())){ throw new CinemateException("Movie object with an invalid genre found");}
					break;
				case StringConstants.TITLE:
					movie.setTitle(movieField.getFirstChild().getTextContent());
					break;
					
				case StringConstants.IMAGE:
					movie.setImage(movieField.getFirstChild().getTextContent());
					break;
					
				case "description":
					movie.setDescription(movieField.getFirstChild().getTextContent());
					break;
				case StringConstants.RATINGTOTAL:
					//check to see if there is a non-empty rating valued
					try{
						//try to parse rating, if we get NumberFormatException, we know we weren't given a double
						if (movieField.getFirstChild() != null) {
							Double rating = Double.parseDouble(movieField.getFirstChild().getTextContent());
							movie.setRatingTotal(rating);
							//System.out.println("rating-total: " + rating);

						}
					}
					catch (NumberFormatException nfe){
						throw new CinemateException("Movie rating is not a double value");
					}
					break;
					
				case StringConstants.RATINGCOUNT:
					//check to see if there is a non-empty rating valued
					try{
						//try to parse rating, if we get NumberFormatException, we know we weren't given a double
						if (movieField.getFirstChild() != null) {
							Double rating = Double.parseDouble(movieField.getFirstChild().getTextContent());
							movie.setRatingCount(rating);
							//System.out.println("rating-count: " + rating);

						}
					}
					catch (NumberFormatException nfe){
						throw new CinemateException("Movie rating is not a double value");
					}
					break;
			}
		}
		//add the movie to the movies map
		moviesMap.put(movie.getTitle(), movie);
		//add the movie to the correct genre's set
		addObjectToMap(genreToMovies, movie.getGenre().toLowerCase(), movie);
		//add the movie to the correct title's set
		addObjectToMap(titleToMovies, movie.getTitle().toLowerCase(), movie);
	}
	
	//used to parse actors and writers
	private void parseMovieSubList(String key, Node movieNode, Movie movie){
		NodeList children = movieNode.getChildNodes();
		
		for (int j = 0; j<children.getLength(); j++) {
			
			Node child = children.item(j);
			//if the current child is of the appropriate Node type
			if (child.getNodeType() == Node.ELEMENT_NODE) {
		       
		        String value = children.item(j).getFirstChild().getTextContent();
		        //if we are parsing the actors
				if (key.equals(StringConstants.ACTORS)){
					//add the actor to the movie, then add the movie to the map from actors to movies
					movie.addActor(value);
					addObjectToMap(actorToMovies, value.toLowerCase(), movie);
				}
				//if we are parsing the writers, add the value to the movie's list of writers
				else movie.addWriter(children.item(j).getFirstChild().getTextContent());
		    }
		}
		
	}
	
	private void parseActors (String key, Node movieNode, Movie movie){
		Vector<Actor> temp = new Vector<Actor>();
		String fName="";
		String lName="";
		String image="";
		NodeList childrenActors = movieNode.getChildNodes();
		for(int i=0; i<childrenActors.getLength(); i++){
			Node a = childrenActors.item(i);
			if(a.getNodeType()==Node.ELEMENT_NODE){
				Element actor = (Element)a;
				fName=actor.getElementsByTagName("fname").item(0).getTextContent();
				lName=actor.getElementsByTagName("lname").item(0).getTextContent();
				image=actor.getElementsByTagName("image").item(0).getTextContent();
				Actor temp2 = new Actor(fName, lName, image);
				temp.add(temp2);
				movie.setActorObjects(temp);
				if(!allActors.contains(temp2)){
					allActors.add(temp2);
				}
				
			}
			
		}
	}
		
	//PARSE USERS
	//parse a user object
	private void parseUser(Node rootNode) throws CinemateException{
		NodeList children = rootNode.getChildNodes();
		User user = new User();
		
		for (int i = 0; i<children.getLength(); i++){
			Node child = children.item(i);
			String nodeName = child.getNodeName();
	
			switch(nodeName){
				case StringConstants.USERNAME:
					user.setUsername(child.getFirstChild().getTextContent());
					break;
				case "fname":
					user.setFName(child.getFirstChild().getTextContent());
					break;
				case "lname":
					user.setLName(child.getFirstChild().getTextContent());
					break;
				case "image":
					user.setImage(child.getFirstChild().getTextContent());
					break;
				case StringConstants.PASSWORD:
					user.setPassword(child.getFirstChild().getTextContent());
					break;
				//parse the following list
				case StringConstants.FOLLOWING:
					parseUserSubList(StringConstants.FOLLOWING, child, user);
					break;
				//parse the feed
				case StringConstants.FEED:
					parseUserSubList(StringConstants.FEED, child, user);
					break;
			}
		}
		//add the user to the users map
		usersMap.put(user.getUsername(), user);
		//add the user with their lowercased username to the usernameToUsers map
		addObjectToMap(usernameToUsers, user.getUsername().toLowerCase(), user);
		addObjectToMap(firstNameToUsers, user.getFName().toLowerCase(), user);
		addObjectToMap(lastNameToUsers, user.getLName().toLowerCase(), user);
	}
	
	//used to parse following and feed
	private void parseUserSubList(String key, Node userNode, User user) throws CinemateException{
		NodeList following = userNode.getChildNodes();
		
		for (int j = 0; j<following.getLength(); j++) {
			
			Node node = following.item(j);
			//if the current child is of the appropriate Node type
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				//if we are parsing the feed, parse this event
		        if (key.equals(StringConstants.FEED)) parseEvent(user, node);
		        //else add the username to the user's following set, then add the relationship to the usernameToFollowers map
		        else{
		        	String username = following.item(j).getFirstChild().getTextContent();
		        	user.addFollowing(username);
		        	addObjectToMap(usernameToFollowers, username, user.getUsername());
		        }
				
		    }
			
		}
	}
	
	//parse an event object
	private void parseEvent(User user, Node eventNode) throws CinemateException{
		NodeList eventFields = eventNode.getChildNodes();
		Event event = new Event();
		event.setUsername(user.getUsername());
		
		for (int i = 0; i<eventFields.getLength(); i++){
			Node eventField = eventFields.item(i);
			String nodeName = eventField.getNodeName();
			
			switch(nodeName){
				//set the movie object of the event.
				//this is why in parse() we made sure to parse the movies node list before the users node list: so this movie value wouldn't be null
				case "movie":
					event.setMovie(moviesMap.get(eventField.getFirstChild().getTextContent()));
					break;
				case StringConstants.ACTION:
					String eventTemp = eventField.getFirstChild().getTextContent();
					event.setAction(eventTemp);
					event.setImage(eventTemp);
					//if we are given an invalid action
					if (!actionsList.contains(event.getAction())) { throw new CinemateException("Invalid action in an event in user's feed");}
					break;
				case StringConstants.RATING:
					try{
						
						if (eventField.getFirstChild() != null) {
							event.setRating(Double.parseDouble(eventField.getFirstChild().getTextContent()));
						}
					}
					catch (NumberFormatException nfe){
						throw new CinemateException("Event rating provided is not a double value");
					}
					
					break;
			}
		}
		//if the action is rated but there is no rating value
		if (event.getAction().equals("Rated") && (event.getRating()==-1)){
			throw new CinemateException("found an event with Rated action but no rating");
		}
		//if the action is not rated but we are given a rating value
		if (!event.getAction().equals("Rated") && (event.getRating() != -1)){
			throw new CinemateException("found an event without Rated action but with a non-empty rating value");
		}
		
		user.addEvent(event);
	}
	
	public void addUserToXML(String username, String password, String firstName, String lastName, String imageURL){
		try{
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(file);
			NodeList usersList = doc.getElementsByTagName("users");
			Node users = usersList.item(0);
			
			Element userElement = doc.createElement("user");
			Element usernameElement = doc.createElement("username");
			Element passwordElement = doc.createElement("password");
			Element fnameElement = doc.createElement("fname");
			Element lnameElement = doc.createElement("lname");
			Element imageElement = doc.createElement("image");
			Element followingElement = doc.createElement("following");
			Element feedElement = doc.createElement("feed");

			
			//creating username aspect
			usernameElement.appendChild(doc.createTextNode(username));
			userElement.appendChild(usernameElement);
			
			passwordElement.appendChild(doc.createTextNode(password));
			userElement.appendChild(passwordElement);
			
			fnameElement.appendChild(doc.createTextNode(firstName));
			userElement.appendChild(fnameElement);
			
			lnameElement.appendChild(doc.createTextNode(lastName));
			userElement.appendChild(lnameElement);
			
			imageElement.appendChild(doc.createTextNode(username));
			userElement.appendChild(imageElement);
			
			followingElement.appendChild(doc.createTextNode(""));
			userElement.appendChild(followingElement);
			
			feedElement.appendChild(doc.createTextNode(""));
			userElement.appendChild(feedElement);
			
			users.appendChild(userElement);
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(file));
			transformer.transform(source, result);
			
		}  catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		   } catch (TransformerException tfe) {
			tfe.printStackTrace();
		   } catch (IOException ioe) {
			ioe.printStackTrace();
		   } catch (SAXException sae) {
			sae.printStackTrace();
		   }
		

		
	}
	
	public void addFeedToXML(String action, String movieTitle, String rating){
		

		
		try{
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(file);
			NodeList userList = doc.getElementsByTagName("user");
			String username = this.getLoggedInUser().getUsername();
			
			Node desiredUserNode = null;
			
			//Loops through all users and tries to find the correct user node with the particular user name
			for(int i=0; i<userList.getLength(); i++){
				Node user = userList.item(i);
				NodeList userChild = user.getChildNodes();
				for(int j=0; j<userChild.getLength(); j++){
					if("username".equals(userChild.item(j).getNodeName())){
						if(username.equals(userChild.item(j).getTextContent())){
							desiredUserNode = user;
						}
						break;
					}
				}
				
			}
			
			//System.out.println("NODE TEXT CONTENT: " + desiredUserNode.getTextContent());
			
			//Once user node is found, it loops to the feed node and adds and event child
			NodeList desiredUserChildren = desiredUserNode.getChildNodes();
			for(int i=0; i<desiredUserChildren.getLength(); i++){
				if("feed".equals(desiredUserChildren.item(i).getNodeName())){
					Node feedAttribute = desiredUserChildren.item(i);
					Element event = doc.createElement("event");
					
					Element actionTemp = doc.createElement("action");
					actionTemp.appendChild(doc.createTextNode(action));
					
					System.out.println(movieTitle);
					Element movieTemp = doc.createElement("movie");
					movieTemp.appendChild(doc.createTextNode(movieTitle));
					
					Element ratingTemp = doc.createElement("rating");
					ratingTemp.appendChild(doc.createTextNode(rating));
					
					//Event appends the child 
					event.appendChild(actionTemp);
					event.appendChild(movieTemp);
					event.appendChild(ratingTemp);
					//Feed appends new event
					feedAttribute.appendChild(event);
				}
				
			} //END OF FORLOOP
			
			//TESTS WHETHER THIS IS A RATING EVENT
			if(rating.equals("")){
				
			}
			else{
				NodeList moviesList = doc.getElementsByTagName("movie");
				String newRatingTotal = Double.toString(this.getMovie(movieTitle).getRatingTotal());
				String newRatingCount = Double.toString(this.getMovie(movieTitle).getRatingCount());
				Node desiredMovieNode = null;
				for(int i=0; i<moviesList.getLength(); i++){
					Node movie = moviesList.item(i);
					NodeList movieChildren = movie.getChildNodes();
					for(int j=0; j<movieChildren.getLength(); j++){
						if(("title").equals(movieChildren.item(j).getNodeName())){
							if(movieTitle.equals(movieChildren.item(j).getTextContent())){
								desiredMovieNode = movie;
								break;
							}
						}
					} //END OF SECOND FORLOOP
				}//END OF FIRST FORLOOP TO FIND THE RIGHT MOVIE	
				
				NodeList desiredMovieNodeChildren = desiredMovieNode.getChildNodes();
				for(int i=0; i<desiredMovieNodeChildren.getLength(); i++){
					if(("rating-total").equals(desiredMovieNodeChildren.item(i).getNodeName())){
						Node ratingTotal = desiredMovieNodeChildren.item(i);
						ratingTotal.setTextContent(newRatingTotal);
					}
					
					if(("rating-count").equals(desiredMovieNodeChildren.item(i).getNodeName())){
						Node ratingCount = desiredMovieNodeChildren.item(i);
						ratingCount.setTextContent(newRatingCount);
					}
				}
					
			}
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(file));
			transformer.transform(source, result);
			
		}catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		   } catch (TransformerException tfe) {
			tfe.printStackTrace();
		   } catch (IOException ioe) {
			ioe.printStackTrace();
		   } catch (SAXException sae) {
			sae.printStackTrace();
		   }

	}
	
	public void writeFollowToXML(String usernameToFollowUnfollow, String action){
		try{
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(file);
			NodeList userList = doc.getElementsByTagName("user");
			String username = this.getLoggedInUser().getUsername();
			
			Node desiredUserNode = null;
			
			//Loops through all users and tries to find the correct user node with the particular user name
			for(int i=0; i<userList.getLength(); i++){
				Node user = userList.item(i);
				NodeList userChild = user.getChildNodes();
				for(int j=0; j<userChild.getLength(); j++){
					if("username".equals(userChild.item(j).getNodeName())){
						if(username.equals(userChild.item(j).getTextContent())){
							desiredUserNode = user;
							break;
						}

					}
				} //END OF SECOND FOR LOOP
				
			} //END OF LOOPING THROUGH ALL USERS
			
			NodeList desiredUserChildren = desiredUserNode.getChildNodes();
			for(int i=0; i<desiredUserChildren.getLength(); i++){
				if("following".equals(desiredUserChildren.item(i).getNodeName())){
					Node followingAttribute = desiredUserChildren.item(i);
					if(action.equals("follow")){
						Element newUsername = doc.createElement("username");
						newUsername.appendChild(doc.createTextNode(usernameToFollowUnfollow));
						followingAttribute.appendChild(newUsername);
						System.out.println("follow written");
						
					} //END OF IF
					else if(action.equals("unfollow")){
						NodeList followingAttributeChildren = followingAttribute.getChildNodes();
						for(int j=0; j<followingAttributeChildren.getLength(); j++){
							if(usernameToFollowUnfollow.equals(followingAttributeChildren.item(j).getTextContent())){
								followingAttribute.removeChild(followingAttributeChildren.item(j));
								System.out.println("unfollow written");
							}
						}
					} //END OF ELSE IF

				} 
			} //END OF LOOP
		
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(file));
			transformer.transform(source, result);
			
		}catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		   } catch (TransformerException tfe) {
			tfe.printStackTrace();
		   } catch (IOException ioe) {
			ioe.printStackTrace();
		   } catch (SAXException sae) {
			sae.printStackTrace();
		   }
		

		}
	
	public String getRandom(){
		//Generate Random Word
		int index = random.nextInt(finalWordBank.size());
		String word = finalWordBank.get(index);
		return word;
	}
	
	
	//SHARED HELPER METHODS
	
	//more generics!! Yay!!!! They are so helpful in times like these!!!
	//adds a new object to the appropriate set
	private <T> void addObjectToMap(Map<String, Set<T>> map, String key, T object){
		//if the map already contains the provided key, retrieve the value (which is a set) and add the new object
		if (map.containsKey(key)) map.get(key).add(object);
		//else create a new set with the object, and add the provided key and created set to the map
		else{
			Set<T> objects = new HashSet<>();
			objects.add(object);
			map.put(key, objects);
		}
	}
}