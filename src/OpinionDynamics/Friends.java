package OpinionDynamics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import OpinionDynamics.dbhelper.DAO;
import OpinionDynamics.dbhelper.Neighbor;
import OpinionDynamics.dbhelper.Opinion;
import jade.core.behaviours.TickerBehaviour;

public class Friends extends UserAgent {

	private String userID;
	private String friendID;
	private double similarity;
	private double weight;
	private double lastOpin;
	private String lastTime;
	private int count;
	private int relation_type;
	private double similarly;
	protected List<Opinion> friendOpList = new ArrayList<Opinion>();
	
	public List<Opinion> getFriendOpLib(String fId) {
		DAO dao = new DAO();
//		List<Opinion> friendOpList = null;
		List<Opinion> frOpLib = dao.executeQueryOpinion("select * from tb_opinion where userID='" + fId
				+ "' order by time ASC");
		if(frOpLib.isEmpty())
		{
			count = 0;
		}
		else
		{
			count = frOpLib.size();
		    lastOpin = frOpLib.get(frOpLib.size()-1).getRealOpin(); //获取最新的观点值
	        lastTime = frOpLib.get(frOpLib.size()-1).getTime(); //上一次参与时间
		}
//		addBehaviour(new TickerBehaviour(this, 1000) {
//			protected void onTick() {
				for (Opinion op : frOpLib) {
					friendOpList.add(op);
				}
//			}
//
//		});
		return friendOpList;
	}

	public void setFriendOpList(List<Opinion> friendOpList) {
		this.friendOpList = friendOpList;
	}

	public List<Opinion> getFriendOpinLib(String fId, String publishTime) {
		DAO dao = new DAO();
		List<Opinion> friendOpinList = new ArrayList<Opinion>();
		List<Opinion> frOpLib = dao.executeQueryOpinion("select * from tb_opinion where userID='" + fId
				+ "'and time < '" + publishTime + "' order by time ASC");
		// lastOpin = opinions.get(0).getRealOpin(); //获取最新的观点值
		// lastTime = opinions.get(0).getTime(); //上一次参与时间

//		addBehaviour(new TickerBehaviour(this, 1000) {
//			protected void onTick() {
				for (Opinion op : frOpLib) {
					friendOpinList.add(op);
				}
//			}
//
//		});
		return friendOpinList;
	}

	public List<Opinion> getFriendsOpinList(String twitterID, List<Neighbor> userNeiList,String publishTime)
	{
		List<Opinion> friendsOpinList = new ArrayList<Opinion>();
		
		Iterator<Neighbor> neiItertor = userNeiList.iterator(); 
        
        while(neiItertor.hasNext())
        {
        	Neighbor nei = neiItertor.next();
        	String fId = nei.getFriendID();
        	Friends f = new Friends();
        	
        	List<Opinion> frendOpinLib = f.getFriendOpinLib(fId, publishTime);
        	Iterator<Opinion> otherIterator = frendOpinLib.iterator();
  
        		while(otherIterator.hasNext())
        		{
        			Opinion otherOpin = otherIterator.next();
    				if(twitterID == otherOpin.getTwitterID())
    				{
    					friendsOpinList.add(otherOpin);
    				}
        		
        	}
        }
        return friendsOpinList;
	}

	public double getSimilarity() {
		return similarity;
	}

	public void setSimilarity(double similarity) {
		this.similarity = similarity;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getLastOpin() {
		return lastOpin;
	}

	public void setLastOpin(double lastOpin) {
		this.lastOpin = lastOpin;
	}

	public String getLastTime() {
		return lastTime;
	}

	public void setLastTime(String lastTime) {
		this.lastTime = lastTime;
	}

	public String getFriendID() {
		return friendID;
	}

	public void setFriendID(String friendID) {
		this.friendID = friendID;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public double getSimilarly() {
		return similarly;
	}

	public void setSimilarly(double similarly) {
		this.similarly = similarly;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public int getRelation_type() {
		return relation_type;
	}

	public void setRelation_type(int relation_type) {
		this.relation_type = relation_type;
	}

}
