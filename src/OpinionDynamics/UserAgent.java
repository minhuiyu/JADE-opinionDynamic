package OpinionDynamics;



import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.Vector;

import javafx.util.converter.PercentageStringConverter;





import com.sun.javafx.collections.MappingChange.Map;
import com.sun.org.apache.bcel.internal.generic.NEW;

import FIPA.DateTime;
import OpinionDynamics.dbhelper.DAO;
import OpinionDynamics.dbhelper.Helper;
import OpinionDynamics.dbhelper.Neighbor;
import OpinionDynamics.dbhelper.Opinion;
import OpinionDynamics.dbhelper.Twitter;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.sam.DifferentialCounterValueProvider;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;


public class UserAgent extends Agent{
	
	private AID aid = null;
	private String userID;
	
	private double vitality = 0.0;	//话题活跃度
	private double fit = 0.0;  //话题适应度
	private double confidence = 0.0;	//话题自信度
	private double peerConformity = 0.0; //社交度，依赖邻居节点的程度
	private double homoplasy = 0.0; //趋同性，话题社区的一致性程度
	
	private double countOpinion = 0.0;	//统计观点值
	private int count_one_topic = 0;	//统计用户参与同一话题次数
//	private static int count_all_topic = 0; //统计用户参与所有话题的次数  
	
	private int count_join = 0;	//用户对同一话题的评论数
//	private static int count_retwitter = 0; //用户对同一话题的转发次数
	private int count_twitter = 0;  //用户对同一话题发表原创博文次数
	
	private int count_join_friends = 0;//评论关注好友的次数
	private int count_join_faimilar = 0;//评论交互好友的次数
	
	
	private String lastTime;
	private double lastOpin = -1;
	
	private int topicID;	//话题ID
	private String twitterID;	//消息ID
	private double senderOpin; //发送者的观点值
	private String source_twitterID;
	private String senderID;
	private String sendTime;
	private String commentID;
	private String commentTime;
	private int opinType;
	private double realOpinion;
	
	private double theta0;
	private double thetaU;  //特征权值
	private double thetaN;
	private double thetaC;
	
	private Opinion opinion = new Opinion(); 
		
	//protected Vector<Integer> neighborsList = new Vector<Integer>();
	protected HashMap<AID,Friends> friends = new HashMap<>() ; //好友关系
//	protected HashMap<AID,Friends> familiars = new HashMap<>(); //交互非好友关系
	
	
//	protected List<Opinion> modeOpList = new HashMap<>();
	
	protected List<Opinion> opinionList = new ArrayList<Opinion>(); //观点库
	protected List<Neighbor> neighborList = new ArrayList<Neighbor>(); //关注关系列表
	
	private List<Opinion> userOpList = new ArrayList<Opinion>();    //自信度
	private List<Opinion> modeOpList = new ArrayList<Opinion>();    //社会趋同度
	private List <Opinion> friendOpList = new ArrayList<Opinion>(); //邻居偏好度list:twitterID,meanOpinion，time
	
	
	private static final String TYPE_COMMMSG= "msg1"; //博文评论者接收消息
	private static final String TYPE_SENDERMSG= "msg2";//博文发表者接收消息
	private static final double INVALID_VALUE = 0xffffffffL;
	
	private static final double VITALITY_THRESHOLD = 0.1;
	private static final double SIMILARITY_THRESHOLD = 0.3;
	private static final double   CONFIDENCE_THRESHOLD = 0.1;
	
 
	
	private class ReplyBehaviour extends CyclicBehaviour 
	{
		public ReplyBehaviour(Agent a) {
			super(a);
		}

		public void action() { //监听消息，执行action
			Date time = null;
			ACLMessage  msg = myAgent.receive();
			if(msg != null){
				ACLMessage reply = msg.createReply(); 
				reply.setContent("ping\n");
				if(msg.getPerformative()== ACLMessage.REQUEST)
				{
					String content = msg.getContent();  //获取消息内容
					//TODO
					System.out.println(getLocalName() +": " + content);
					
					//如果消息前缀是msg1,则表明是接收者为博文评论者
					if ((content != null) && (content.substring(0, 4).equals(TYPE_COMMMSG)))
					{
						count_join++;  //参与话题计数器
						
						String[] strs = content.split("#");
						topicID = Integer.valueOf(strs[1]);
						twitterID = strs[2];
						senderOpin = Double.valueOf(strs[3]);
						source_twitterID = strs[4];
						senderID = strs[5];
						sendTime = strs[6];
						commentID = strs[7];
						commentTime = strs[8];
						realOpinion = Double.valueOf(strs[9]);
						opinType = 3;
	                    
						//判断评论者与发文者间的关系
						
						AID tAid = new AID("agent"+senderID, AID.ISLOCALNAME);
						Friends f=  friends.get(tAid);

						//评论者与博主是好友关系
						if(f != null && f.getRelation_type() == 1)
						{
							count_join_friends++;
							f.setCount(f.getCount()+1);
							int count = f.getCount();
							 
							double weight = (count_join_faimilar==0&&count_join_friends==0)?0:2*count/(2*count_join_friends+count_join_faimilar); //更新权重
							
							f.setWeight(weight); 
							friends.put(tAid, f);
						
							DAO dao = new DAO();
							dao.updateWeight(userID,senderID,weight);
						}
						else if(f != null && f.getRelation_type() == 0)
						{   
							count_join_faimilar++;
							f.setCount(f.getCount()+1);
							int count = f.getCount();
							double weight = f.getWeight();
							weight = (count_join_faimilar==0&&count_join_friends==0)?0:count/(2*count_join_friends+count_join_faimilar); //更新权重
							f.setWeight(weight); 
							
							friends.put(tAid, f);
							
							DAO dao = new DAO();
							dao.updateWeight(userID,senderID,weight);
							
						}
						else
						{
							count_join_faimilar++;
							int count = 1;
							int type = 0;
							double weight = (count_join_faimilar==0&&count_join_friends==0)?0:count/(2*count_join_friends+count_join_faimilar);
							Friends f1 = new Friends();
							f1.setCount(count);
							f1.setFriendID(senderID);
							f1.setUserID(userID);
							f1.setWeight(weight);
							f1.setRelation_type(type);
							
							friends.put(tAid, f1);
							
							
							
							Neighbor nei = new Neighbor();
							nei.setFriendID(senderID);
							nei.setUserID(userID);
							nei.setRelation_type(type);
							nei.setWeight(weight);
							DAO dao = new DAO();
							dao.insertNeighbor(nei);
						}
						
						//如果Agent的opinionLib中参与过该微文或该博文的原微博，则直接输出观点值作为预测值
						if(isInOpinionLib(source_twitterID) != INVALID_VALUE 
								||isInOpinionLib(twitterID) != INVALID_VALUE)
						{
							opinion.setLpcOpin(isInOpinionLib(source_twitterID));
							opinion.setCommentID(commentID);
							opinion.setTopicID(topicID);
							opinion.setTwitterID(twitterID);
							opinion.setUserID(userID);
							opinion.setTime(commentTime);
							opinion.setRealOpin(realOpinion);
							opinion.setOpinType(opinType);//表示预测的是评论
							
							DAO dao = new DAO();
							dao.insertOpin(opinion); //插入新数据到opinion表
							send(reply);
						}
                        //感知器感知消息
						else 
						{
							perceptron(topicID ,twitterID, senderOpin,opinType,commentTime);
						}
					}
					
					//接收者为消息博文的博主
					else if((content != null) && (content.substring(0, 4).equals(TYPE_SENDERMSG)))
					{
						count_join++; //计数器
						
						String[] strs = content.split("#");
						topicID = Integer.valueOf(strs[1]);
						twitterID = strs[2];
//						senderOpin = Double.valueOf(strs[3]);
						source_twitterID = strs[3];
						senderID = strs[4];
						sendTime = strs[5];
						realOpinion = Double.valueOf(strs[6]);
						DAO dao = new DAO();
						
						//博文为转发类型，查看转发者与原博主的关注关系
						if((!source_twitterID.equals(null)) && (!"null".equals(source_twitterID)))
						{
							opinType = 2; 
							//判断转发者userID与发送者senderID是否为同一Agent
							if(senderID!=userID) 
							{
								//若为同一Agent，则判断博文转发者与被转者间的关注关系，若存在关注，则边权重加2
							
								//获取原博文字段
//								System.out.println(source_twitterID);
								List<Twitter> sourceTwitter = dao.executeQueryTwitter(
										"select * from tb_twitter where twitter_realID='"+source_twitterID+"'");
								if(!sourceTwitter.isEmpty()){
								AID tAid = new AID("agent"+sourceTwitter.get(0).getUserID(), AID.ISLOCALNAME);
								Friends f =  friends.get(tAid);
								if(f.getRelation_type() == 1)
								{
									count_join_friends++;
									
									f.setCount(f.getCount()+1);
									int count = f.getCount();
									double weight = 2*count/(2*count_join_friends+count_join_faimilar); //更新权重
									
									f.setWeight(weight); 
									friends.put(tAid, f);
									dao.updateWeight(userID,senderID,weight);
								}
								else if(f.getRelation_type() == 0)
								{   
									count_join_faimilar++;
									
									f.setCount(f.getCount()+1);
									int count = f.getCount();
									double weight = count/(2*count_join_friends+count_join_faimilar); //更新权重
									
									f.setWeight(weight); 
									friends.put(tAid, f);
									dao.updateWeight(userID,senderID,weight);
									
								}
								else
								{
									count_join_faimilar++;
									
									int count = 1;
									int type = 0;
									double weight = count/(2*count_join_friends+count_join_faimilar);
									
									f.setCount(count);
									f.setFriendID(senderID);
									f.setUserID(userID);
									f.setWeight(weight);
									f.setRelation_type(type);
									
									friends.put(tAid, f);
							
									Neighbor nei = new Neighbor();
									nei.setFriendID(senderID);
									nei.setUserID(userID);
									nei.setRelation_type(type);
									nei.setWeight(weight);
									dao.insertNeighbor(nei);
								}
								
							}}
							else //转发自己的博文
							{
								count_twitter++;
								opinion.setLpcOpin(isInOpinionLib(source_twitterID));
								opinion.setTime(sendTime);
								opinion.setTopicID(topicID);
								opinion.setTwitterID(twitterID);
								opinion.setUserID(userID);
								opinion.setSource_twitterID(source_twitterID);
								opinion.setRealOpin(realOpinion);
								opinion.setOpinType(2);//表示预测的是评论

								dao.insertOpin(opinion); //插入新数据到opinion表
								send(reply);
							}
							
							//感知器工作
							perceptron(topicID, twitterID, senderOpin,opinType,sendTime);
						}
						//博文为发送者原创
						else
						{
							count_twitter++;
							count_join++;
							opinType = 1;
							//感知器工作
							perceptron(topicID, twitterID, senderOpin,opinType,sendTime);
						}
						
						
					}
					
				}
				//不是ACLMessage就不做处理
				else {
					
				}
				send(reply);
				
//				System.out.println(source_twitterID );
			}
			else {
				block();
			}		}

		
	} // END of inner class ReplyBehaviour
	 
	
	
	@Override
	protected void setup() {
		// TODO Auto-generated method stub
		super.setup();
        // create the agent descrption of itself
        DFAgentDescription dfd = new DFAgentDescription();
        aid = getAID();
        String name = aid.getLocalName();
        userID = name.substring(5, name.length()) ;
        dfd.setName( aid );
        
        friends(aid, userID);

        String initialTime = "2012-07-25 10:51:49";
        opinionList = opinionLib(aid, userID, 1, initialTime);
        System.out.println("SETUP"+opinionList.size());

        DAO dao = new DAO();
        
        for(int i = 0; i<opinionList.size();i++)
        {
        	count_join++;
        	if (i==0){
        		//赋初始值
        		Opinion opin = new Opinion();
        		opin.setTwitterID(opinionList.get(i).getTwitterID());
        		opin.setRealOpin(0.5);
        		userOpList.add(opin);
        		friendOpList.add(opin);
        		modeOpList.add(opin);
        	}
        	else
        	{
        		//
        		List<Opinion> opList = new ArrayList<Opinion>();
        		List<Opinion> mopList = new ArrayList<Opinion>();
        		String time = opinionList.get(i).getTime();
        		String tID = opinionList.get(i).getTwitterID();
        		double all_frOpin = 0.0;
         		int count_usable = 0;
        		for(int j = 0;j<i;j++)
        		{
        			String tid = opinionList.get(j).getTwitterID();
        			Environment environment = new Environment();
        			Opinion eopin = environment.getModeOpinList(tid, opinionList.get(j).getRealOpin());
        	        mopList.add(eopin);    
        	        
        	        opList.add(opinionList.get(j));
       
        		}
        		
        		Opinion eopinion = new Opinion();  //社会趋同度
        		double proHomoplasy = mopList.get(mopList.size()-1).getRealOpin()*getSimilarity(mopList);    
    	        eopinion.setTwitterID(tID);
    	        eopinion.setRealOpin(proHomoplasy); 
    	        modeOpList.add(eopinion);
        	    
    	        Opinion uopinion = new Opinion();  //个体自信度
    			double proconfin = getConfidence(opList);
    			uopinion.setTwitterID(tID);
    			if(proconfin < CONFIDENCE_THRESHOLD) uopinion.setRealOpin(0.0); 
    		    else uopinion.setRealOpin(lastOpin);
    			userOpList.add(uopinion);
        			
        		Opinion fopinion = new Opinion();  //好友依赖度
	         	Friends f = new Friends();
    	        List<Opinion> friendsOpinList = f.getFriendsOpinList(tID, neighborList, time);
    	        Iterator<Opinion> friendsOpinListIterator = friendsOpinList.iterator();
    	        while(friendsOpinListIterator.hasNext())
 	        	{
 	        		Opinion op = friendsOpinListIterator.next();
 	        		if(op.getTwitterID() == tID ) 
 	        		{
 	        			
 	        			String fId = op.getUserID();
 	        			AID t = new AID("agent" + fId, AID.ISLOCALNAME);
 	        			Friends friend = friends.get(t);
 	        			
 	        			if(friend.getRelation_type() ==1){
 	        				count_join_friends++;
 	        				friend.setCount(friend.getCount()+1);
 	 	        			friend.setWeight(2*friend.getCount()/(2*count_join_friends+count_join_faimilar));
 	 	        			System.out.println("SETUP GETWEIGHT"+friend.getWeight());
 	 	        			dao.updateWeight(userID,fId,friend.getWeight());
 	        			}
 	        			else{
 	        				count_join_faimilar++;
 	        				friend.setCount(friend.getCount()+1);
 	 	        			friend.setWeight(friend.getCount()/(2*count_join_friends+count_join_faimilar));
 	        			}
 	        			
 	        			if(friend.isVitality(t,tID, friend.getLastTime(), time))
 	                	{
 	                		//判断好友观点相似度
 	                		if(friend.getSimilarity() > SIMILARITY_THRESHOLD)
 	                		{
 	                			count_usable++;
 	                			all_frOpin += friend.getLastOpin()*friend.getSimilarity();     //没有乘权重
 	                		}	
 	                	}
 	        		}
 	        		
 	        	}
    	        double propeerConformity = 0;
	         	if(count_usable != 0){
	        		 propeerConformity = all_frOpin/count_usable;
	        	}    
	        	fopinion.setTwitterID(tID);
	         	fopinion.setRealOpin(propeerConformity);
	         	friendOpList.add(opinion);
	        }
        	
        	
        }
        
        System.out.println("###"+userID+" , "+userOpList.size()+" , "+friendOpList.size());
     
//        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
//
//        send(msg);
        
        
        try {
			DFService.register(this,dfd);
			addBehaviour(new ReplyBehaviour(this));
			
		} catch (FIPAException e) {
			
			doDelete();
		}
        
	}
	
	

	/**
	 * 博文消息处理，感知器
	 * @param topicID
	 */

	public void perceptron(int topicID, String twitterID, double senderOpin,int opinType,String publishTime) 
	{
		
        if(opinionList.isEmpty())
		{
			effector(0.5,0.3,0.3,0.4,0,0,0); //若第一次参与，则预测观点值为0.5
		}
		else if(userOpList.size() == modeOpList.size() && userOpList.size() == friendOpList.size())
		{
			System.out.println(opinionList.size());
			controller( userOpList,  modeOpList,  friendOpList);
	    }
	}
	
	/**
	 * 博文消息处理，控制器，调整参数，学习参数
	 * @param topicID
	 */
	public void controller(List<Opinion> userOpList,List<Opinion> modeOpList,List<Opinion> friendOpList)
	{
		// TODO Auto-generated method stub
		double alpha = 0.001;
		int iteration = 100000;
		LinearRegression linerRegression = new LinearRegression(userOpList,modeOpList,friendOpList,opinionList, alpha, iteration); 
		linerRegression.trainTheta();
		System.out.println("controller"+linerRegression.getTheta()[0]+" "+linerRegression.getTheta()[1]+" "+linerRegression.getTheta()[2]+" "+linerRegression.getTheta()[3]);
	    theta0 = linerRegression.getTheta()[0];
	    thetaU = linerRegression.getTheta()[1];
	    thetaN = linerRegression.getTheta()[2];
	    thetaC = linerRegression.getTheta()[3];
		effector(theta0,thetaU,thetaN,thetaC
	        		,userOpList.get(userOpList.size()-1).getRealOpin()
	        		,friendOpList.get(friendOpList.size()-1).getRealOpin()
	        		,modeOpList.get(modeOpList.size()-1).getRealOpin());
		
	}
	
	/**
	 * 计算观点生成
	 * @param topicID
	 */
	public void effector(double s,double su,double sf,double se,double uop,double fop,double eop) 
	{   
		double LPCOpin = s+su*uop+sf*fop+se*eop;
		opinion.setLpcOpin(LPCOpin);
		opinion.setUserID(userID);
		opinion.setTopicID(topicID);
		opinion.setOpinType(opinType);
		opinion.setTwitterID(twitterID);
		opinion.setRealOpin(realOpinion);
//		opinion.setCommentID(commentID);
		//观点生成时间
		if(opinType == 3)
		{
			opinion.setTime(commentTime);//观点生成者为关注者，但是该关注者并未参与评论，则观点生成时间仿真为博文发布时间
		    opinion.setCommentID(commentID);
		    
		}
		else if(opinType == 2)
		{
			opinion.setSource_twitterID(source_twitterID);
			opinion.setTime(sendTime);
		}
		else
		{
			opinion.setTime(sendTime);
		}
		//观点生成
		System.out.println(opinion.getLpcOpin()+"  "+opinion.getTwitterID()+"  "+opinion.getOpinType()+"   "+opinion.getUserID()+"  "+opinion.getTime());

		DAO dao = new DAO();
		dao.insertOpin(opinion);
		
		
		lastOpin = opinion.getRealOpin();
		lastTime = opinion.getTime();
		
		opinionList = opinionLib(aid, userID, topicID, lastTime);
		
		Opinion eopinion = new Opinion();  //社会趋同度
		if(!opinionList.isEmpty())
		{
	    homoplasy = opinionList.get(opinionList.size()-1).getRealOpin()*getSimilarity(opinionList);    
        eopinion.setTwitterID(twitterID);
        eopinion.setRealOpin(homoplasy); 
        modeOpList.add(eopinion);
	    
        Opinion uopinion = new Opinion();  //个体自信度
		confidence = getConfidence(opinionList);
		uopinion.setTwitterID(twitterID);
		if(confidence < CONFIDENCE_THRESHOLD) uopinion.setRealOpin(0.0); 
	    else uopinion.setRealOpin(lastOpin);
		userOpList.add(uopinion);
			
		Opinion fopinion = new Opinion();  //好友依赖度
     	Friends f = new Friends();
        List<Opinion> friendsOpinList = f.getFriendsOpinList(twitterID, neighborList, lastTime);
        Iterator<Opinion> friendsOpinListIterator = friendsOpinList.iterator();
        double all_frOpin = 0.0;
 		int count_usable = 0;
        while(friendsOpinListIterator.hasNext())
     	{
     		Opinion op = friendsOpinListIterator.next();
     		if(op.getTwitterID() == twitterID ) 
     		{
     			String fId = op.getUserID();
     			AID t = new AID("agent" + fId, AID.ISLOCALNAME);
     			Friends friend = friends.get(t);
     			
     			if(friend.isVitality(t,twitterID, friend.getLastTime(), lastTime))
             	{
             		//判断好友观点相似度
             		if(friend.getSimilarity() > SIMILARITY_THRESHOLD)
             		{
             			count_usable++;
             			all_frOpin += friend.getLastOpin()*friend.getSimilarity();     //没有乘权重
             		}	
             	}
     		}
     		
     	}
        double peerConformity = 0;
     	if(count_usable != 0){
    		 peerConformity = all_frOpin/count_usable;
    	}    
    	fopinion.setTwitterID(twitterID);
     	fopinion.setRealOpin(peerConformity);
     	friendOpList.add(opinion);
		}
		
	}
	
	//用户好友列表
	public void friends(AID aid,String userId)
	{
		DAO dao = new DAO();
		List<Neighbor> neighbors = dao.executeQueryRelation(
				"select * from tb_relationship where userID='"+userId+"'");
		
//		int count = neighborList.size();
		
		
//		addBehaviour(new TickerBehaviour(this, 1000) 
//		{
//			protected void onTick() 
//		      { 
				for(Neighbor n:neighbors)
				{
					neighborList.add(n);
					AID t = new AID("agent" + n.getFriendID(), AID.ISLOCALNAME);
					Friends friend = new Friends();
					friend.setWeight(n.getWeight());
					friend.setRelation_type(n.getRelation_type());
					friend.setUserID(userId);
					friend.setFriendID(n.getFriendID());
					List<Opinion> frOpLib = friend.getFriendOpLib(n.getFriendID());
					if(frOpLib.isEmpty())
					{
						friend.setCount(0);
					}
					else
					{
						friend.setFriendOpList(frOpLib);
						friend.setCount(0);
						friend.setLastOpin(frOpLib.get(frOpLib.size()-1).getRealOpin());
						friend.setLastTime(frOpLib.get(frOpLib.size()-1).getTime());
						friend.setSimilarity(getSimilarity(frOpLib));
					}
					friends.put(t, friend);
				}
//		      }
//	     });
	}
	
	
	//用户观点库
	public List<Opinion> opinionLib(AID aid, String userId, int topicId,String publishTime)
	{
		DAO dao = new DAO();
//	    opinionList = dao.executeQueryOpinion("select * from tb_opinion where userID='"+userId+"'and topicID='"+topicId+"'order by time DESC");
	    String opinSql = "select * from tb_opinion where userID='"+userId+"'and time < '"+publishTime+"' and topicID='"+topicId+"'order by time ASC";
		List<Opinion> opinions = dao.executeQueryOpinion(opinSql);
		 System.out.println(opinSql);
		if(!opinions.isEmpty()){
		     lastOpin = opinions.get(opinions.size()-1).getRealOpin(); //获取最新的观点值
			 lastTime = opinions.get(opinions.size()-1).getTime();  //上一次参与时间
		}
		for(Opinion op : opinions)
		{
		    opinionList.add(op);
		}
		//一秒刷新一次，目的是统计用户观点总值、用户参与某一话题的次数
//		addBehaviour(new TickerBehaviour(this, 1000) 
//		{
//			protected void onTick() 
//		      { 
				for(Opinion op : opinions)
				{
				    opinionList.add(op);
				}
//		      }
//		
//	     });
//		
		return opinionList;
	}
	
	//判断是否参与过该博文
	private double isInOpinionLib(String source_twitterID)
	{
		for(Opinion o : opinionList)
		{
			if(o.getTwitterID() == source_twitterID) return o.getRealOpin();
		}
		return INVALID_VALUE;
	}
	
	//判断活跃度
	public boolean isVitality(AID aid,String userID ,String lastTime, String publishTime)
	{
		//判断用户上一次参与话题的时间间隔
		double diff = 0.0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try
		{
			Date last = sdf.parse(lastTime);
			Date now = sdf.parse(publishTime);
			diff = now.getTime() - last.getTime();
		}catch(Exception e){
			System.out.println("时间差计算异常");
		}
		if(diff<VITALITY_THRESHOLD)
		{
			return false;
		}
		return true;
	}
	
	//话题自信度
	public double getConfidence (List<Opinion> opinionList)
	{
		if(opinionList.isEmpty())
		{
			confidence = 0; //如果没有参与过，则自信度为 0
		}
		else
		{
			Iterator<Opinion> myIterator = opinionList.iterator();
			double count = 0.0;
			double sumOp = 0.0;
			double sum_sq = 0.0;
			while(myIterator.hasNext())
			{
				count++;
				Opinion myOpin= myIterator.next();
				double myopin = myOpin.getRealOpin();
				sumOp += myopin;
				sum_sq += Math.pow(sumOp, 2);
			}
			
			confidence = count * sum_sq - Math.pow(sumOp, 2);
		}
		return confidence;
		
	}
	
	//皮尔逊相关系数
	public double getSimilarity(List<Opinion> frOpLib)
	{
		double sim = 0.0;
		double common_item_len = 0.0;
		//观点求和
		double my_sum = 0.0;
		double other_sum = 0.0;
		//观点求平方和
		double my_sum_sq = 0.0;
		double other_sum_sq = 0.0;
		//乘积和
		double p_sum = 0.0;
		
		Iterator<Opinion> myIterator = opinionList.iterator();
		
		while(myIterator.hasNext())
		{
			Opinion myOpin= myIterator.next();
			Iterator<Opinion> otherIterator = frOpLib.iterator();
			
			while(otherIterator.hasNext())
			{
				Opinion otherOpin = otherIterator.next();
				if(otherOpin.getTwitterID() == otherOpin.getTwitterID())
				{
					double myopin = myOpin.getRealOpin();
					double otheropin = otherOpin.getRealOpin();
					my_sum += myopin;
					other_sum += other_sum;
					my_sum_sq += Math.pow(my_sum, 2);
					other_sum_sq += Math.pow(other_sum, 2);
					p_sum += my_sum_sq * other_sum_sq;
					
					common_item_len++;
				}
			}
			
		}
		//如果等于0,则无对相同博文的评论，返回sim = 0即可
		if(common_item_len > 0)
		{
			//协方差
			double num = common_item_len * p_sum - my_sum * other_sum;	
			
			double den = Math.sqrt((common_item_len * my_sum_sq - Math.pow(my_sum, 2)) 
					               * (common_item_len * other_sum_sq - Math.pow(other_sum, 2)));
			sim = (den == 0) ? 1 : Math.abs(num/den);
		}
		
		return sim;
	}
	
	//参数学习
	
}
