package OpinionDynamics;

import java.util.List;

import OpinionDynamics.dbhelper.DAO;
import OpinionDynamics.dbhelper.User;
import jade.core.Agent;
import jade.wrapper.AgentController;
import jade.wrapper.PlatformController;

public class InitAgent extends Agent 
{
	private InitUI jFrame;

	@Override
	protected void setup() 
	{
		// TODO Auto-generated method stub
		super.setup();
		
		setUpUI(this);

	}
	
	public void startAgent(Object args[])
	{
		DAO dao = new DAO();
		List<User> users = dao.executeQueryUser("select * from tb_useragent order by register_time ");

		PlatformController container = getContainerController(); //get a container controller for creating new agents
		
		// create agents
		try {
			for (User u : users) {
				// create a new agent
				String agentName = "agent" + u.getUser_realID();
				
				AgentController agent = container.createNewAgent(agentName, "OpinionDynamics.UserAgent", null);
				agent.start();
			
			}

			AgentController msgAgent = container.createNewAgent("MsgAgent", "OpinionDynamics.MsgAgent", args);
			msgAgent.start();
		} catch (Exception e) {
			System.err.println("Exception while adding guests: " + e);
			e.printStackTrace();
		}		
	}

	private void setUpUI(InitAgent initAgent) { 
		// TODO Auto-generated method stub
		jFrame = new InitUI(this);                         
		jFrame.setSize(400, 300);
		jFrame.setVisible(true);
	}
}
