package OpinionDynamics;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import OpinionDynamics.dbhelper.Comment;
import OpinionDynamics.dbhelper.DAO;
import OpinionDynamics.dbhelper.Twitter;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;


public class MsgAgent extends Agent{
	
	private AID aid = null;

	@Override
	protected void setup() 
	{
		// TODO Auto-generated method stub
		super.setup();
        // create the agent descrption of itself
        DFAgentDescription dfd = new DFAgentDescription();
        aid = getAID();
        dfd.setName( aid );
        //博文消息读取时间段设定
        Object[] objs = (Object[]) getArguments();
        String start = "";
        String end = "";
        if(objs.length == 2)
        {
        	start = (String) objs[0];
             end = (String) objs[1];
        }
        twitter(start, end);
        try 
        {
			DFService.register(this,dfd);
			
		} catch (FIPAException e) 
        {
			
			doDelete();
		}
        
	}
	
	
	//博文消息
	public void twitter(String start, String end )
	{
		DAO dao = new DAO();
		//List<User> users = dao.executeQueryUser("select * from tb_useragent order by register_time ");
		//List<Comment> reviewer = dao.executeQueryComment(sql)
		//update sql
        
        /**
        Timestamp tStart = Timestamp.valueOf(start);
		Timestamp tEnd = Timestamp.valueOf(end);
		List<Twitter> tw = dao.executeQueryTwitter("select * from tb_twitter order by time where time between "+tStart+" and "+tEnd);
		**/
		
		//将时间转换为Date型
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date dStart = Calendar.getInstance().getTime();
		Date dEnd   = Calendar.getInstance().getTime();
		try {
			dStart = sdf.parse(start);
			dEnd = sdf.parse(end);
			
			if(dStart.compareTo(dEnd) >0)
			{
				throw new Exception("date error");
			}
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e)
		{
			e.getStackTrace();
		}
		//按开始结束时间
		List<Twitter> tw = dao.executeQueryTwitter("select * from tb_twitter where time between '"+start+"' and '"+end+"' order by time");
	  	//定时器一秒刷新一次
//		addBehaviour(new TickerBehaviour(this, 10000) 
//		{ 
//		      protected void onTick() 
//		      {} });
		    	for(Twitter t: tw)
		    	{
		    		String twID=t.getTwitterID();
		    		AID senderAid = new AID("agent"+t.getUserID(), AID.ISLOCALNAME);
		    		ACLMessage twMsg2 = new ACLMessage(ACLMessage.REQUEST);
//		    		String content2 = "msg2#"+t.getTopicID()+"#"+t.getTwitterID()+"#"+t.getOpinion()+"#"+t.getSource_twitterID()+"#"+t.getUserID()+"#"+t.getTime();
		    		
		    		String content2 = String.format("msg2#%d#%s#%s#%s#%s#%f"
								    				,t.getTopicID()
						        				    ,t.getTwitterID()
						        				    ,t.getSource_twitterID()
						        				    ,t.getUserID()
						        				    ,t.getTime()
						        				    ,t.getOpinion());
		        	buildMsg(twMsg2, content2, senderAid);
		        	send(twMsg2);//消息发送给博文发表者
		        	
		    		List<Comment> comments = reviewer(twID);
		    		for(Comment i : comments){
				        	AID aid = new AID("agent"+i.getReviewerID(), AID.ISLOCALNAME);
				        	ACLMessage twMsg1 = new ACLMessage(ACLMessage.REQUEST);
//				        	String content1 = "msg1#"++"#"++"#"++"#"++"#"++"#"++"";
				        	String content1 = String.format("msg1#%d#%s#%f#%s#%s#%s#%s#%s#%f"
								        				   ,t.getTopicID()
								        				   ,t.getTwitterID()
								        				   ,t.getOpinion()
								        				   ,t.getSource_twitterID()
								        				   ,t.getUserID()
								        				   ,t.getTime()
								        				   ,i.getCommentID()
								        				   ,i.getTime()   //评论时间
								        				   ,i.getRelOpin() //评论的真实观点
								        			       );
				        	buildMsg(twMsg1, content1, aid);
				        	send(twMsg1);//消息发送给评论者
		    		}
			        
		    	} 
		      
		
	}
	
	
	//博文消息接收者
		public List<Comment> reviewer(String twitterID){
			DAO dao = new DAO();
//			Vector<Integer> receiverList = new Vector<Integer>();
			List<Comment> comm =dao.executeQueryComment(
					"select * from tb_comment where source_twitterID='"+twitterID+"' order by time ");
//
//			for(Comment c:comm){
//				receiverList.add(c.getReviewerID());//添加消息接收者
//			}
			return comm;
		}
		
		
	public void buildMsg(ACLMessage msg, String content, AID aid)
	{
		msg.addReceiver(aid);
		msg.setContent(content);	
		msg.setPostTimeStamp(new Date().getTime());		//博文发送时间
    	//twMsg.setSender(senderAid);		//发送者AID
		Calendar now = Calendar.getInstance();
		now.add(Calendar.MINUTE, 2880);	//2天后消息过期
		msg.setReplyByDate(now.getTime());
	}
		
}
