package db;

import java.sql.*;

public class PostgreWrapper {
	public static final int POSTGRESQL_DEFAULT_PORT = 5432;
	public static final int CONN_KEY_HOST = 0;
	public static final int CONN_KEY_HOSTADDR = 1;
	public static final int CONN_KEY_PORT = 2;
	public static final int CONN_KEY_DBNAME = 3;
	public static final int CONN_KEY_USER = 4;
	public static final int CONN_KEY_PASSWORD = 5;
	public static final int CONN_KEY_LAST = 6;
	//public static final String[] s_connKeyList={"host","hostaddr","port","dbname","user","password"};
	private int m_NofSupportedKeys;
	private static String url;
	private String[] m_pConnInfo;
	
	
	public PostgreWrapper(){
		m_NofSupportedKeys = CONN_KEY_LAST;
		m_pConnInfo = new String[m_NofSupportedKeys];
		//PGconn =null;
		for(int i =0; i<CONN_KEY_LAST;i++){
			m_pConnInfo[i]=null;
		}
	}
	
	public boolean addConnectionKey(int key_id,String val){
		if(key_id<0||key_id>=m_NofSupportedKeys)	return false;
		if(val==null){
			if(m_pConnInfo[key_id]!=null)	m_pConnInfo[key_id]=null;				
		}
		else{
			m_pConnInfo[key_id]= val;
		}
		return true;
	}
	
	public boolean addConnectionKey(int key_id,int val){
		return addConnectionKey(key_id, Integer.toString(val));

	}
	
	public Connection connect(){
		Connection conn = null;
		try {
//			try {
//				Class.forName("postgresql.Driver.class");
//			} catch (ClassNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			conn = DriverManager.getConnection(url, m_pConnInfo[CONN_KEY_USER], m_pConnInfo[CONN_KEY_PASSWORD]);
            System.out.println("Connected to the PostgreSQL server successfully.");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
            System.out.println("ERROR connecting to the PostgreSQL server.");
			e.printStackTrace();
		}
		return conn;	
	}
	
	public void disconnect(Connection conn){
		try {
			conn.close();
			System.out.println("Disconnected to the PostgreSQL server successfully.");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR closing the PostgreSQL server connection.");
			e.printStackTrace();
		}
	}
	
	public boolean insert(Connection con, String query){
		Statement st =null;
		try {
			st = con.createStatement();
			st.executeQuery(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR INSERT");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public ResultSet insert(Connection con, String query, String keyName){
		PreparedStatement stmt = null;
		query = query+"RETURNING "+keyName+";";
		try {
			stmt = con.prepareStatement(query);
			stmt.executeUpdate();
			return stmt.getResultSet();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR INSERT");
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean notify(Connection con, String query){
		Statement st = null;
		try {
			st = con.createStatement();
			return st.execute(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR NOTIFY");
			e.printStackTrace();
			return false;
		}
	}
	
	public ResultSet select(Connection con, String query){
		Statement st = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			st = con.createStatement();
			st.execute("BEGIN");
			st.execute("DECLARE myportal CURSOR FOR "+query);
			stmt = con.prepareStatement("FETCH ALL in myportal");
			stmt.executeQuery();
			rs = stmt.getResultSet();
			st.execute("CLOSE myportal");
			st.execute("END");
			return rs;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR INSERT");
			e.printStackTrace();
			return null;
		}
		
	}
	
	public boolean setDbInfo(String name, String user, String password){
		addConnectionKey(CONN_KEY_DBNAME, name);
		addConnectionKey(CONN_KEY_USER, user);
		addConnectionKey(CONN_KEY_PASSWORD, password);
		
		return true;
	}
	
 	public boolean setHostInfo(String hostname, String hostaddr, int port /*=-1*/){
		if(port ==-1)				port = POSTGRESQL_DEFAULT_PORT;
		if(hostname.isEmpty())		hostname = null;
		if(hostaddr.isEmpty())		hostaddr = null;
		addConnectionKey(CONN_KEY_HOST, hostname);
		addConnectionKey(CONN_KEY_HOSTADDR, hostaddr);
		addConnectionKey(CONN_KEY_PORT, port);
		
		return true;
	}
	
	public boolean setURLString(){
		url = "jdbc:postgresql://"+m_pConnInfo[CONN_KEY_HOST]+":"+m_pConnInfo[CONN_KEY_PORT]+"/"+
	m_pConnInfo[CONN_KEY_DBNAME];
		return true;
	}
	
}
