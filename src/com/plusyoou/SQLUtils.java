package com.plusyoou;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;


public class SQLUtils {
	static Logger logger = Logger.getLogger(SQLUtils.class.getName());
	
		
	public static DataSource getWebDataSource(){
		try {
			Context iniCTX = new InitialContext();
			String DSName = "jdbc/WebDB";
			Context context = (Context) iniCTX.lookup("java:comp/env");
			return (DataSource) context.lookup(DSName);			
		} catch (NamingException e) {
			logger.fatal("Data Source is not available! error message as:" + e.getExplanation());
			e.printStackTrace();
			return null;
		}
	}
	
	public static DataSource getDemoDataSource(){
		try {
			Context iniCTX = new InitialContext();
			String DSName = "jdbc/demoDB";
			Context context = (Context) iniCTX.lookup("java:comp/env");
			return (DataSource) context.lookup(DSName);			
		} catch (NamingException e) {
			logger.fatal("Data Source is not available! error message as:" + e.getExplanation());
			e.printStackTrace();
			return null;
		}
	}

	
	public static String executeInsertSQL(String[] strSQLBatch, boolean autoIncrease ,DataSource ds) {

		Connection conn = null;
		Statement stmt = null;
		
		try {
			// 建立数据库连接
//			DataSource ds = getDataSource();
			conn = ds.getConnection();
			stmt = conn.createStatement();
			
			for (int i = 0; i < strSQLBatch.length; i++) {
				stmt.addBatch(strSQLBatch[i]);
				logger.debug(strSQLBatch[i]);
			}
			
			int[] batchResult =null;
			// 执行sql语句
			try {
				conn.setAutoCommit(false);
//				stmt.execute("START TRANSACTION");
				batchResult = stmt.executeBatch();
//				stmt.execute("COMMIT");
				try {
					conn.commit();
				} catch (Exception SQLException) {
					conn.rollback();
					logger.error("批量提交插入语句时错误：" + SQLException.getMessage());
					conn.setAutoCommit(true);
					if (!stmt.isClosed()) {stmt.close();};
					if (!conn.isClosed()) {conn.close();};
					return null;
				};
			} catch (Exception SQLException) {
				logger.error("SQL语句错误：" + SQLException.getMessage());
				conn.setAutoCommit(true);
				if (!stmt.isClosed()) {stmt.close();};
				if (!conn.isClosed()) {conn.close();};
				return null;				
			};
			for (int i =0; i < batchResult.length; i++) {
				if (batchResult[i] !=1) {
					logger.error("第" + (i+1) + "条插入语句执行结果错误：" + batchResult[i]);
					conn.rollback();
					conn.setAutoCommit(true);
					stmt.close();
					conn.close();
					return null;
				}
			};
			conn.setAutoCommit(true);
			int intAffectedRows = batchResult.length;
			String returnStr = "";
			logger.info("本次共插入" + intAffectedRows + "条记录");
			if (autoIncrease && intAffectedRows > 0) {
//				ResultSet rs = stmt.executeQuery("SELECT LAST_INSERT_ID()");
				//jdbc3.0支持getGeneratedKeys方法。
				ResultSet rs = stmt.getGeneratedKeys();
				rs.beforeFirst();
				while (rs.next()) {
					returnStr += String.valueOf(rs.getInt(1)) + ",";
				}
				//处理返回字符串的尾部。
				if (!returnStr.equals("")) returnStr = returnStr.substring(0,returnStr.length()-1);
//				intAffectedRows = rs.getInt(intAffectedRows);	
			} else {
				returnStr = String.valueOf(intAffectedRows);
			};
			// 正常返回结果
			return returnStr;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (stmt != null) try {stmt.close();} catch (Exception ignore) {}
			if (conn != null) try {conn.close();} catch (Exception ignore) {}
		}
	}

	
	public static String executeSQLStmt(String[] strSQLBatch, DataSource ds) {
		Connection conn = null;
		Statement stmt = null;
		
		try {
			// 建立数据库连接
			conn = ds.getConnection();
			stmt = conn.createStatement();	
			for (int i = 0; i < strSQLBatch.length; i++) {
				stmt.addBatch(strSQLBatch[i]);
				logger.debug(strSQLBatch[i]);
			}
			
			int[] batchResult =null;
			// 执行sql语句
			try {
				conn.setAutoCommit(false);
				batchResult = stmt.executeBatch();
				try {
					for (int i =0; i < batchResult.length; i++) {
						if (batchResult[i] == Statement.EXECUTE_FAILED ) {
							logger.error("第" + (i+1) + "条语句执行结果错误：" + batchResult[i]);
							logger.info("开始回退");
							conn.rollback();
							conn.setAutoCommit(true);
							stmt.close();
							conn.close();
							return null;
						}
					};
					logger.info("提交事务");
					conn.commit();
					conn.setAutoCommit(true);
				} catch (Exception SQLException) {
					conn.rollback();
					logger.error("提交SQL语句时错误：" + SQLException.getMessage());
					conn.setAutoCommit(true);
					if (!stmt.isClosed()) {stmt.close();};
					if (!conn.isClosed()) {conn.close();};
					return null;
				};
			} catch (Exception SQLException) {
				conn.rollback();
				logger.error("批量提交SQL语句时错误：" + SQLException.getMessage());
				conn.setAutoCommit(true);
				if (!stmt.isClosed()) {stmt.close();};
				if (!conn.isClosed()) {conn.close();};
				return null;				
			};

			int intAffectedRows = batchResult.length;
			String returnStr = "";
			logger.info("本次共影响了" + intAffectedRows + "条记录");
//				ResultSet rs = stmt.executeQuery("SELECT LAST_INSERT_ID()");
				//jdbc3.0支持getGeneratedKeys方法。
			ResultSet rs = stmt.getGeneratedKeys();
			rs.beforeFirst();
			while (rs.next()) {
				returnStr += String.valueOf(rs.getInt(1)) + ",";					
			}
			//处理返回字符串的尾部。
			if (!returnStr.equals("")) returnStr = "AIPK=" + returnStr.substring(0,returnStr.length()-1);
			returnStr += "%AffectedRows=" + String.valueOf(intAffectedRows);
			// 正常返回结果
			return returnStr;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (stmt != null) try {stmt.close();} catch (Exception ignore) {}
			if (conn != null) try {conn.close();} catch (Exception ignore) {}
		}
	}

		
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List executeSelectSQL(String strSQL,DataSource ds) {

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			// 建立数据库连接
			conn = ds.getConnection();
			stmt = conn.createStatement();

			// 执行sql语句
			rs = stmt.executeQuery(strSQL);

			// 获取resultset的元数据信息
			ResultSetMetaData md = rs.getMetaData();
			// 获取返回结果的列数
			int columns = md.getColumnCount();

			// 最早版本使用这个方式获取，rs总行数，再声明相应size的ArrayList。
			// 现改为动态决定ArrayList的长度。
			// rs.last();
			// int rows = rs.getRow();

			ArrayList listResults = new ArrayList();

			rs.beforeFirst();
			while (rs.next()) {
				// 程序的最早版本是使用
				// HashMap存储，但是HashMap天生的put和get的顺序不一致，造成取值时控制不了需要的列顺序。
				// 控制列顺序在项目自定义的plain数据格式中，是必须的。因此改用LinkedHashMap解决这个问题，但是一定牺牲了性能
				// 如果输出是json格式，就不需要严格的列顺序了，由此也看出了json的好处。
				// 如果今后服务端的效率需要优化，这个是一个可以考虑的点，可弃用项目自定义的plain数据格式，改为json。
				// 2013-12-1 ----Harry
				LinkedHashMap row = new LinkedHashMap(columns);
				for (int i = 1; i <= columns; ++i) {
					// getColumnName取出的是表格中的字段名，本项目中是驼峰方式的字段名
					// row.put(md.getColumnName(i),rs.getObject(i));
					// getColumnLable取出的是 sql语句中字段名后面 as 部分的名字；
					row.put(md.getColumnLabel(i), rs.getObject(i));
				};
				listResults.add(row);
			};
			// 正常返回结果
			return listResults;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (rs != null)	try {rs.close();} catch (Exception ignore) {}
			if (stmt != null) try {stmt.close();} catch (Exception ignore) {}
			if (conn != null) try {conn.close();} catch (Exception ignore) {}
		}
	}

	@SuppressWarnings("static-access")
	public static int executeUpdateSQL(String[] strSQLBatch,DataSource ds) {

//		Context iniCTX;
//		Context context;
//		PropertiesHandler ph = new PropertiesHandler("/");
//		String sourceName = ph.getProperty("source.name");
		Connection conn = null;
		Statement stmt = null;
		
		try {
			// 通过jndi获取在web服务器中注册的数据库连接池
//			iniCTX = new InitialContext();
//			context = (Context) iniCTX.lookup("java:comp/env");
//			DataSource ds = (DataSource) context.lookup(sourceName);
			// 建立数据库连接
			conn = ds.getConnection();
			stmt = conn.createStatement();
			
			for (int i = 0; i < strSQLBatch.length; i++) {
				stmt.addBatch(strSQLBatch[i]);
				logger.debug(strSQLBatch[i]);
			}
			
			int[] batchResult =null;
			// 执行sql语句
			try {
				conn.setAutoCommit(false);
//					stmt.execute("START TRANSACTION");
				batchResult = stmt.executeBatch();
//					stmt.execute("COMMIT");
				try {
					conn.commit();
				} catch (Exception SQLException) {
					conn.rollback();
					logger.error("批量提交更新语句时错误：" + SQLException.getMessage());
					conn.setAutoCommit(true);
					if (!stmt.isClosed()) {stmt.close();};
					if (!conn.isClosed()) {conn.close();};
					return -1;
				};
			} catch (Exception SQLException) {
				logger.error("SQL语句错误：" + SQLException.getMessage());
				conn.setAutoCommit(true);
				if (!stmt.isClosed()) {stmt.close();};
				if (!conn.isClosed()) {conn.close();};
				return -1;				
			};
			conn.setAutoCommit(true);
			int intAffectedRows = 0;
			for (int i =0; i < batchResult.length; i++) {
				if (batchResult[i] == stmt.EXECUTE_FAILED && batchResult[i]==0) {
					logger.error("第" + (i+1) + "条更新语句执行结果错误：" + batchResult[i]);
					conn.rollback();
//						conn.setAutoCommit(false);
					stmt.close();
					conn.close();
					return -1;
				}
				intAffectedRows += batchResult[i];
			};
			logger.info("本次共更新" + intAffectedRows + "条记录");
			// 正常返回结果
			return intAffectedRows;
		}
		catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			if (stmt != null) try {stmt.close();} catch (Exception ignore) {}
			if (conn != null) try {conn.close();} catch (Exception ignore) {}
		}
	}
	
}