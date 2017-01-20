package OpinionDynamics;


import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.sun.org.apache.bcel.internal.generic.NEW;


public class InitUI extends JFrame{

	InitAgent owner = null;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	JButton btn_start = new JButton("start");
	JPanel panel = new JPanel();
	JTextField txtStart = new JTextField();
	JTextField txtEnd   = new JTextField();
	JLabel lbNotice     = new JLabel();
	
	public InitUI(InitAgent ower)
	{
		this.owner = ower;
		sdf.setLenient(false);
		
		txtStart.setText("2012-07-25 10:51:49");
		txtEnd.setText("2012-08-02 23:11:32");
		
		btn_start.addActionListener(new java.awt.event.ActionListener() 
		{
            public void actionPerformed(ActionEvent e) 
            {
            	Object[] args = new Object[2];
            	
            	boolean isValid = true;
            	
            	String start = txtStart.getText();
            	String end   = txtEnd.getText();
            	try
            	{
            		sdf.parse(start);
            		sdf.parse(end);
            	}
            	catch(ParseException exception)
            	{
            		isValid = false;
            		lbNotice.setText("date error");
            	}
            	
            	
                if(isValid)
                {
                	args[0] = start;
                	args[1] = end;
                	ower.startAgent(args);
                }
            }
        });
		
		panel.setLayout(new FlowLayout());
		panel.add(lbNotice);
		panel.add(txtStart);
		panel.add(txtEnd);
		panel.add(btn_start);
		add(panel);
	}
	
}
