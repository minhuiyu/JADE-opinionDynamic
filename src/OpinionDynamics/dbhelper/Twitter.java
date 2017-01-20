package OpinionDynamics.dbhelper;

import FIPA.DateTime;

public class Twitter 
{
	private String twitterID;
	private String source_twitterID;	//��ֵΪ����Ϊԭ��΢��
	private int topicID;
	private String userID;
	private String content;  //�������ݣ��벩����Ϣ���
	private double opinion;	//���Ĺ۵�ֵ
	private double modeOpin;
	private String time;
	
	
	private int topic_AllTwitters;	//һ�������µĲ�������
	private int user_AllTwitters;	//һ���û�����Ĳ���������ͬһ���⣩
	
	public String getTwitterID() {
		return twitterID;
	}
	public void setTwitterID(String twitterID) {
		this.twitterID = twitterID;
	}
	public String getSource_twitterID() {
		return source_twitterID;
	}
	public void setSource_twitterID(String source_twitterID) {
		this.source_twitterID = source_twitterID;
	}
	public int getTopicID() {
		return topicID;
	}
	public void setTopicID(int topicID) {
		this.topicID = topicID;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public int getTopic_AllTwitters() {
		return topic_AllTwitters;
	}
	public void setTopic_AllTwitters(int topic_AllTwitters) {
		this.topic_AllTwitters = topic_AllTwitters;
	}
	public int getUser_AllTwitters() {
		return user_AllTwitters;
	}
	public void setUser_AllTwitters(int user_AllTwitters) {
		this.user_AllTwitters = user_AllTwitters;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public double getOpinion() {
		return opinion;
	}
	public void setOpinion(double opinion) {
		this.opinion = opinion;
	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return this.twitterID == ((Twitter)obj).getTwitterID();
	}
	public double getModeOpin() {
		return modeOpin;
	}
	public void setModeOpin(double modeOpin) {
		this.modeOpin = modeOpin;
	}

	
	
	
	
	
}
