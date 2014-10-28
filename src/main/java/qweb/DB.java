package qweb;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mchange.v2.c3p0.PooledDataSource;


public class DB {

	static PooledDataSource pds;
	
	public static Connection getConn() throws SQLException {
		return pds.getConnection();
	}

	public static String getJsonFromResults(ResultSet rs, String string) throws SQLException {
		StringBuilder sb = new StringBuilder(string);
		int openB = 0, closeB = 0;
		while(sb.indexOf("[", openB) != -1) {
			openB = sb.indexOf("[", openB);
			closeB = sb.indexOf("]", closeB);
			
			String val = sb.substring(openB+1, closeB);
			int typeIndicator = val.indexOf(":");
			
			String type = null;
			if (typeIndicator != -1) {
				type = val.substring(typeIndicator+1);
				val = val.substring(0, typeIndicator);
			}
			
			int beforeLen = sb.length();
			
			if (type == null) sb.replace(openB, closeB+1, "\"" + rs.getString(val) + "\"");
			else if ("bool".equals(type)) sb.replace(openB, closeB+1, String.valueOf(rs.getBoolean(val)));
			else if ("num".equals(type)) sb.replace(openB, closeB+1, String.valueOf(rs.getLong(val)));
			else if ("date".equals(type)) sb.replace(openB, closeB+1, "\"" + String.format("%1$tY-%1$tm-%1$tdT%1$tH:%1$tM:%1$tS.%1$tLZ", rs.getTimestamp(val)) + "\"");
			
			int afterLen = sb.length();
			
			int offset = afterLen - beforeLen;
			openB += offset;
			closeB += offset;
		}	
		
		return sb.toString();
	}
}
