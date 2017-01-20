package OpinionDynamics.dbhelper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Helper {
	// open();
	// connect();

	public static final String url = "jdbc:mysql://localhost:3306/opiniondynamic?";
	public static final String name = "com.mysql.jdbc.Driver";
	public static final String user = "root";
	public static final String password = "root";
	private static Connection conn = null;
	public Connection getConn() {
		return conn;
	}

	private static Helper instance = null;

	private Helper() {

	}
	
	public static Helper getInstance()
	{
		if(instance == null)
		{
			instance = new Helper();
			try {
				Class.forName(name); // 指定连接类型
				conn = DriverManager.getConnection(url, user, password);
//				Statement ps =  conn.createStatement();
//				ResultSet rs = ps.executeQuery("select * from tb_useragent");
				System.out.println("成功加载Mysql驱动程序");
			} catch (SQLException e) {
				System.out.println("MySQL操作错误");
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return instance;
	}

	public void close() {
		try {
			Helper.conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
