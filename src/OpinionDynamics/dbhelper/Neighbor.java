package OpinionDynamics.dbhelper;

import java.util.Vector;

public class Neighbor {
	private String userID;
	private String friendID;
	private double weight;
	private int relation_type;
	private int count;
	
	
	
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getFriendID() {
		return friendID;
	}
	public void setFriendID(String friendID) {
		this.friendID = friendID;
	}
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
	public int getRelation_type() {
		return relation_type;
	}
	public void setRelation_type(int relation_type) {
		this.relation_type = relation_type;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	
}
