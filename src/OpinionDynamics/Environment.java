package OpinionDynamics;




import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import OpinionDynamics.dbhelper.Comment;
import OpinionDynamics.dbhelper.DAO;
import OpinionDynamics.dbhelper.Opinion;
import OpinionDynamics.dbhelper.Twitter;

//统计一条博文，所有评论的倾向，寻找大多数人都倾向的一个值
public class Environment {

//	public HashMap<String,Double> modeOpinHash = new HashMap<>();
    public List<Opinion> modeOpLib = new ArrayList<Opinion>();
    public Opinion modeOpinion = new Opinion();
    
    public Opinion getModeOpinList(String tID, double userOpin)
	{
		Environment e = new Environment();
		DAO dao = new DAO();
        double modeOpin;                                                                                 
			List<Comment> comments = dao.executeQueryComment(
					"select * from tb_comment where source_twitterID='"+tID+"' group by opin ORDER BY count(opin) DESC LIMIT 3");
			double[] modeOp = new double[3];
			double[] dis = new double[3];
			int i = 0;
			for(Comment comm : comments)
			{	
				double opin = comm.getRelOpin();
				modeOp[i] = opin;
				dis[i] = Math.abs(opin-userOpin);
				i++;
				if(i == 3) break;
			}
			modeOpin = modeOp[e.getMinDis(dis)];
			modeOpinion.setTwitterID(tID);
			modeOpinion.setModeOpin(modeOpin);
		

		return modeOpinion;
	
	}
	
//	public List<Opinion> getModeOpinList(List<Opinion> userOpinLib,String publishTime, double userLastOpin)
//	{
//		Environment e = new Environment();
//		DAO dao = new DAO();
////		List<Twitter> twitterLib = null;
////      Twitter  t = new Twitter();
//       
//        double modeOpin;
////        String[] str = new String[userOpinLib.size()];
////        int n = 0;
//        
////		for(Opinion op : userOpinLib)
////		{
////			String twitterID = op.getTwitterID();
////			List<Twitter> tw = dao.executeQueryTwitter("select* from tb_twitter where twitter_realID = '"+twitterID+"'");
////			Twitter twi= tw.get(0);
////			twitterLib.add(twi);
////		}
//        for(Opinion op : userOpinLib)
//		{	
//			String twitterID = op.getTwitterID();
//			List<Comment> comments = dao.executeQueryComment(
//					"select * from tb_comment where source_twitterID='"+twitterID+"'and time <'"+publishTime+"' group by opin ORDER BY count(opin) DESC LIMIT 3");
//			double[] modeOp = new double[3];
//			double[] dis = new double[3];
//			int i = 0;
//			for(Comment comm : comments)
//			{	
//				double opin = comm.getRelOpin();
//				modeOp[i] = opin;
//				dis[i] = Math.abs(opin-userLastOpin);
//				i++;
//				if(i == 3) break;
//			}
//			modeOpin = modeOp[e.getMinDis(dis)];
////			twitter.setModeOpin(modeOpin);
////			modeOpinHash.put(twitterID, modeOpin);
//			Opinion opinion = new Opinion();
//			opinion.setTwitterID(twitterID);
//			opinion.setModeOpin(modeOpin);
////			opinion.setTime(lastTime);
//			modeOpLib.add(opinion);
//		}
////		for(Opinion opinion : userOpinLib)
////		{	
////			double userOp = opinion.getRealOpin();
////			double modeOp = getModeOp(opinion.getTopicID(), opinion.getTwitterID(), userOp);
////			
////			Opinion modeOpinion = new Opinion();
////			
////			modeOpinion.setTopicID(opinion.getTopicID());
////			modeOpinion.setTwitterID(opinion.getTwitterID());
////			modeOpinion.setOpinion(modeOp);
////			modeOpinList.add(modeOpinion);
////		}
//		return modeOpLib;
//	
//	}
	
	public int getMinDis(double[] d)
	{
		int minIndex = 0;
		
		for(int i = 0; i < d.length; i++)
		{
			if(d[i] < d[minIndex]) minIndex = i;
		}
		return minIndex;
	}
	
	//一条博文的观点众数值,观点支持人数大于n/3
//	public double getModeOp(int topicID, int twitterID, double userOp)
//	{   
//		double modeOpin = 0;
//		//假设有两个数做存储
//		DAO dao = new DAO();
//		List<Comment> comments = dao.executeQueryComment(
//					"select * from tb_comment where topicID="
//					+topicID+"and source_twitterID="
//					+twitterID+"order by time DESC");
//		
//		double aOp = 0;
//		double bOp = 0;
//		int cnA = 0;
//		int cnB= 0;
//		for(int i = 0; i < comments.size(); i++)
//		{
//			if(cnA == 0 && cnB == 0)
//			{
//				aOp = comments.get(i).getRelOpin();
//			}
//			else if(cnA > 0 && cnB ==0)
//			{
//				if(aOp != comments.get(i).getRelOpin())
//				{
//					bOp = comments.get(i).getRelOpin();
//					cnB++;
//				}
//				else cnA++;
//			}
//			else if(cnA == 0 && cnB >0)
//			{
//				if(bOp != comments.get(i).getRelOpin())
//				{
//					aOp = comments.get(i).getRelOpin();
//					cnA++;
//				}
//				else cnB++;
//			}
//			else
//			{
//				if(aOp == comments.get(i).getRelOpin()) cnA++;
//				else if(bOp == comments.get(i).getRelOpin()) cnB++;
//				else {
//					cnA--;
//					cnB--;
//				}
//			}
//		}
//		
//		if((cnA == cnB) && (cnA >0))
//		{
//			if( (Math.abs(aOp - userOp) > Math.abs(bOp - userOp)))
//			{
//				cnA = 0;
//				for(int i = 0; i < comments.size(); i++)
//				{
//					if(aOp == comments.get(i).getRelOpin()) cnA++;
//					
//				}
//				if(cnA > comments.size()/3) 
//				{
//					modeOpin = aOp;
//				}
//			}
//			else
//			{
//				cnB = 0;
//				for(int i = 0; i < comments.size(); i++)
//				{
//					if(bOp == comments.get(i).getRelOpin()) cnB++;
//					
//				}
//				if(cnB > comments.size()/3) 
//				{
//					modeOpin = bOp;
//				}
//			}
//		}
//		else if((cnA > cnB) && cnA >0)
//		{
//			cnA = 0;
//			for(int i = 0; i < comments.size(); i++)
//			{
//				if(aOp == comments.get(i).getRelOpin()) cnA++;
//				
//			}
//			if(cnA > comments.size()/3) 
//			{
//				modeOpin = aOp;
//			}
//		}
//		
//		else if((cnB > cnA) && cnB >0)
//		{
//			cnB = 0;
//			for(int i = 0; i < comments.size(); i++)
//			{
//				if(bOp == comments.get(i).getRelOpin()) cnB++;
//				
//			}
//			if(cnB > comments.size()/3) 
//			{
//				modeOpin = bOp;
//			}
//		}
//	
//		return modeOpin;
//	
//	}	
	
}
