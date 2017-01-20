package OpinionDynamics.dbhelper;

public class Comment {
	private String commentID;
	private String source_twitterID;
	private String reviewerID;
	private int topicID;
	private double opinion;
	private String time;
	
	private String allReviewer;
	
	
	private int twitter_Allcomment; //统计一条博文对应的总评论数
	private int user_Allcomment;	//一个用户的总评论数
	
	public String getAllReviewer() {
		return allReviewer;
	}
	public void setAllReviewer(String allReviewer) {
		this.allReviewer = allReviewer;
	}
	public String getCommentID() {
		return commentID;
	}
	public void setCommentID(String commentID) {
		this.commentID = commentID;
	}
	public String getSource_twitterID() {
		return source_twitterID;
	}
	public void setSource_twitterID(String source_twitterID) {
		this.source_twitterID = source_twitterID;
	}
	public String getReviewerID() {
		return reviewerID;
	}
	public void setReviewerID(String reviewerID) {
		this.reviewerID = reviewerID;
	}
	public int getTopicID() {
		return topicID;
	}
	public void setTopicID(int topicID) {
		this.topicID = topicID;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public int getTwitter_Allcomment() {
		return twitter_Allcomment;
	}
	public void setTwitter_Allcomment(int twitter_Allcomment) {
		this.twitter_Allcomment = twitter_Allcomment;
	}
	public int getUser_Allcomment() {
		return user_Allcomment;
	}
	public void setUser_Allcomment(int user_Allcomment) {
		this.user_Allcomment = user_Allcomment;
	}
	public double getRelOpin() {
		return opinion;
	}
	public void setRelOpin(double relOpin) {
		this.opinion = relOpin;
	}
	
	
	
	
}
