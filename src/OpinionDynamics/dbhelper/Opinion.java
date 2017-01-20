package OpinionDynamics.dbhelper;



public class Opinion
{
	private double lpcOpin;	//观点值
	private double realOpin;
	private double lastOpin;
	private String lastTime;
	
	private int opinionID;
	private int opinType;
	private int topicID;
	
	private double modeOpin;
	private double fMeanOpin;
	
	private String twitterID;
	private String source_twitterID;
	private String userID;
	private String commentID;	//值为空表示该用户发表了该博文，没有评论
	private String time;
	
	private double countOpinion;	//统计观点值
	
	private int count_one_topic;	//统计用户参与某一话题次数
	private int count_all_topic;    //统计用户参与所有话题的次数
	
	
	
	public int getTopicID() {
		return topicID;
	}
	public void setTopicID(int topicID) {
		this.topicID = topicID;
	}
	public String getTwitterID() {
		return twitterID;
	}
	public void setTwitterID(String messageID) {
		this.twitterID = messageID;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getCommentID() {
		return commentID;
	}
	public void setCommentID(String commentID) {
		this.commentID = commentID;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public double getCountOpinion() {
		return countOpinion;
	}
	public void setCountOpinion(double countOpinion) {
		this.countOpinion = countOpinion;
	}
	public int getCount_one_topic() {
		return count_one_topic;
	}
	public void setCount_one_topic(int count_one_topic) {
		this.count_one_topic = count_one_topic;
	}
	public int getOpinionID() {
		return opinionID;
	}
	public void setOpinionID(int opinionID) {
		this.opinionID = opinionID;
	}
	public int getCount_all_topic() {
		return count_all_topic;
	}
	public void setCount_all_topic(int count_all_topic) {
		this.count_all_topic = count_all_topic;
	}
	public double getRealOpin() {
		return realOpin;
	}
	public void setRealOpin(double realOpin) {
		this.realOpin = realOpin;
	}
	public double getLpcOpin() {
		return lpcOpin;
	}
	public void setLpcOpin(double lpcOpin) {
		this.lpcOpin = lpcOpin;
	}
	public int getOpinType() {
		return opinType;
	}
	public void setOpinType(int opinType) {
		this.opinType = opinType;
	}
	public String getSource_twitterID() {
		return source_twitterID;
	}
	public void setSource_twitterID(String source_twitterID) {
		this.source_twitterID = source_twitterID;
	}
	public double getfMeanOpin() {
		return fMeanOpin;
	}
	public void setfMeanOpin(double fMeanOpin) {
		this.fMeanOpin = fMeanOpin;
	}
	public double getModeOpin() {
		return modeOpin;
	}
	public void setModeOpin(double modeOpin) {
		this.modeOpin = modeOpin;
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
}
