package OpinionDynamics.dbhelper;



import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Statement;

import jdk.nashorn.internal.objects.annotations.Where;
import OpinionDynamics.Friends;

public class DAO 
{  //查询微博表
	public List<Twitter> executeQueryTwitter(String sql)
	{
		List<Twitter> twitterlist = new ArrayList<>();
		
		Helper helper = Helper.getInstance();
		try {
			Statement ps =  helper.getConn().createStatement();
			ResultSet rs = ps.executeQuery(sql);
			while(rs.next())
			{
				Twitter t = new Twitter();
				//微博ID 用户ID 话题ID 博文内容  博文观点值  博文发布时间 
				//t.setContent(rs.getString(1)+" "+rs.getString(4)+" "+rs.getString(5)+" "+rs.getString(6)+" "+rs.getString(7)+" "+rs.getString(8));	
				t.setTwitterID(rs.getString(2));	//博文ID
				t.setSource_twitterID(rs.getString(3));
				t.setUserID(rs.getString(4));	//微博发布者
				t.setTopicID(Integer.valueOf(rs.getString(5)));	  //话题ID
				t.setContent(rs.getString(6));	//微博内容
				t.setOpinion(Double.parseDouble(rs.getString(10)));	//博文观点值
				t.setTime(rs.getString(8));
				twitterlist.add(t);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return twitterlist;
	}
	
	//查询评论表
	public List<Comment> executeQueryComment(String sql){
		List<Comment> commentlist = new ArrayList<>();
		Helper helper = Helper.getInstance();
		try {
			Statement ps =  helper.getConn().createStatement();
			ResultSet rs = ps.executeQuery(sql);
			while(rs.next())
			{
				Comment comment = new Comment();
				comment.setCommentID(rs.getString(2));//评论ID
				comment.setReviewerID(rs.getString(3));//评论者ID
				comment.setSource_twitterID(rs.getString(5));//微博ID
				comment.setTopicID(Integer.valueOf(rs.getString(6)));//话题ID
				comment.setRelOpin(Double.parseDouble(rs.getString(9)));//真实观点值
				comment.setTime(rs.getString(8));
				commentlist.add(comment);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return commentlist;
	}
	
	//查询观点表
	public List<Opinion> executeQueryOpinion(String sql){
		List<Opinion> opinionlist = new ArrayList<>();
		Helper helper = Helper.getInstance();
		try {
			Statement ps =  helper.getConn().createStatement();
			ResultSet rs = ps.executeQuery(sql);
			while(rs.next())
			{
				Opinion op = new Opinion();
				op.setOpinionID(Integer.valueOf(rs.getString(1)));	
				op.setOpinType(Integer.valueOf(rs.getString(2)));
				op.setTwitterID(rs.getString(3));
				op.setSource_twitterID(rs.getString(4));  
				op.setCommentID(rs.getString(5));
				op.setTopicID(Integer.valueOf(rs.getString(6)));
				op.setUserID(rs.getString(7));   
				op.setTime(rs.getString(8));
				op.setRealOpin(Double.parseDouble(rs.getString(9)));	
				opinionlist.add(op);
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return opinionlist;
	}
	
	//好友列表
	public List<Neighbor> executeQueryRelation(String sql){
		List<Neighbor> neighborlist = new ArrayList<>();
		Helper helper = Helper.getInstance();
//		Vector<String>	
		try {
			Statement ps =  helper.getConn().createStatement();
			ResultSet rs = ps.executeQuery(sql);
			while(rs.next())
			{
				Neighbor ng = new Neighbor();
				ng.setUserID(rs.getString(2));
				ng.setFriendID(rs.getString(3));
				ng.setWeight(Double.parseDouble(rs.getString(4)));
				ng.setRelation_type(Integer.valueOf(rs.getString(5)));
				neighborlist.add(ng);
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return neighborlist;
	}
	
	//查询用户表
	public List<User> executeQueryUser(String sql){

		List<User> userlist = new ArrayList<>();
		Helper helper = Helper.getInstance();
		try {
			Statement ps =  helper.getConn().createStatement();
			ResultSet rs = ps.executeQuery(sql);
			while(rs.next())
			{
				 User user = new User();
				 user.setUser_realID(rs.getString(1));
				 user.setRegister_time(rs.getString(2));
				 userlist.add(user);
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return userlist;
	}
	
	public void insertOpin(Opinion opinion)
	{
		Helper helper = Helper.getInstance();
		try {
			Statement ps =  helper.getConn().createStatement();
			String str=String.format("insert into tb_opinion(opin_type, twitterID, source_twitterID, commentID, topicID, userID, time, realOpin, LPCOpin)"
					                + "values(%d, %s, %s, %s, %d, %s, '%s', %f, %f)"
					                , opinion.getOpinType()
									, opinion.getTwitterID()
									, opinion.getSource_twitterID()
									, opinion.getCommentID()
									, opinion.getTopicID()
									, opinion.getUserID()
									, opinion.getTime()
									, opinion.getRealOpin()
									, opinion.getLpcOpin()
									);
			System.out.println(str);
			ps.executeUpdate(str);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void updateWeight(String uid, String fid, double weight)
	{
		Helper helper = Helper.getInstance();
		try {
			Statement ps =  helper.getConn().createStatement();
			ps.executeUpdate("update tb_relationship set weight = '"+weight+"' where userID = '"+ uid +"' and friendID = '"+ fid+"'");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void insertNeighbor(Neighbor neighbor)
	{
		Helper helper = Helper.getInstance();
		try {
			Statement ps = helper.getConn().createStatement();
			String str = String.format("insert into tb_relationship(userID, friendID, weight, relation_type) values(%s, %s, %f, %d)"
					,neighbor.getUserID()
					,neighbor.getFriendID()
					,neighbor.getWeight()
					,neighbor.getRelation_type());  
			ps.executeUpdate(str);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void updateOpin(Opinion opin)
	{
		Helper helper = Helper.getInstance();
		try {
			Statement ps = helper.getConn().createStatement();
			ps.executeUpdate("update tb_opinion set LPCOpin = '"+opin.getLpcOpin()+"' where opinionID = '"+opin.getOpinionID()+"'");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
