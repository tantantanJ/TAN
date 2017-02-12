package com.plusyoou;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

@SuppressWarnings({ "rawtypes" })
public class RegisterActive extends HttpServlet{
	private static final long serialVersionUID = 1L;
	static Logger logger = Logger.getLogger(RegisterActive.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		request.setCharacterEncoding("utf-8");
		HttpSession session = request.getSession();
		String key = request.getParameter("k"); 
		String clientIP = Utils.getIpAddr(request);
	    if(key == null || key.equals("") ){  
	    	response.setStatus(302);
	    	response.sendRedirect("XinXiXianShi.html?active=noSuchCode");
	    	return ;  
	    } else {
	    	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	    	List listResults=null;
	    	String sqlStr="";
	    	DataSource webDS = SQLUtils.getWebDataSource();
	    	sqlStr="SELECT ZhuCeYouXiang, XingMing, MiMa, MiMaLingPai FROM webuser " +
	    			"WHERE YiYanZheng = FALSE AND YanZhengMa='" + key + "'";
	    	logger.info(sqlStr);
	    	listResults = SQLUtils.executeSelectSQL(sqlStr, webDS);
	    	if(listResults.isEmpty() || listResults.size()!=1){
	    		logger.info("错误验证码，key="+key+";IP="+Utils.getIpAddr(request));
	    		response.setStatus(302);
	    		response.sendRedirect("XinXiXianShi.html?active=noSuchCode");
		    	return ;  
	    	} else {
	    		String userCode = (String)((HashMap) listResults.get(0)).get("ZhuCeYouXiang");
	    		String userName = URLEncoder.encode((String)((HashMap) listResults.get(0)).get("XingMing"), "UTF-8");
	    		String password = (String)((HashMap) listResults.get(0)).get("MiMa");
	    		String pwdToken = (String)((HashMap) listResults.get(0)).get("MiMaLingPai");
	    		String loginToken = Utils.get16BitMd5(userCode + clientIP + (new Date()).toString());
	    		
	    		String[] strSQLBatch= new String[1];
	    		String ZhuCeYouXiang = (String)((HashMap) listResults.get(0)).get("ZhuCeYouXiang");
	    		sqlStr="UPDATE webuser SET YiYanZheng=1, YanZhengMa=NULL, YanZhengRiQi='"
    				+ formatter.format(new Date())+"', DengLuLingPai = '"+ loginToken + "' "
    				+ "WHERE ZhuCeYouXiang = '" + ZhuCeYouXiang + "'";
				strSQLBatch[0]=sqlStr;
				int result = SQLUtils.executeUpdateSQL(strSQLBatch, webDS );
	    		if (result==1){
		    		sqlStr="INSERT INTO renyuanbiao " +
		    				"(RenYuanGongHao, RenYuanXingMing, RenYuanXingBie, GangWeiXingZhi, GangWeiJiBie, ZhuYaoJueSe,KeShenPi, BuMenBiaoShi, CaoZuoMiMa, YunXuDengLu, ZhuoMianPeiZhi) " +
		    				"VALUES ("+
		    				"'"+userName+"', "+
		    				"'"+userName+"', "+
		    				"'男', "+"'注册用户', "+"0, "+"'0Z', "+"1, "+"1, "+
		    				"'"+password+"', "+
		    				"1, "+
		    				"'win12:Report11,win13:Report13,win21:FuncZ02,win22:Report12,win23:BriefingF01,win31:FuncZ03,win33:BriefingA01'); ";
		    		logger.info(sqlStr);
					strSQLBatch[0]=sqlStr;
					DataSource DemoDS = SQLUtils.getDemoDataSource();
					String resultInsert = SQLUtils.executeInsertSQL (strSQLBatch,Boolean.FALSE , DemoDS );
	    			logger.info("resultInsert="+resultInsert);
    				Cookie cookieForUserCode = new Cookie("UserCode", URLEncoder.encode(userCode, "UTF-8"));
    				cookieForUserCode.setMaxAge(30*24*60*60);
    				response.addCookie(cookieForUserCode); 		
    				Cookie cookieForUserName = new Cookie("UserName", userName);
    				cookieForUserName.setMaxAge(-1);
    				response.addCookie(cookieForUserName); 		
    				Cookie cookieForPwdToken = new Cookie("PwdToken", pwdToken);
    				cookieForPwdToken.setMaxAge(30*24*60*60);
    				response.addCookie(cookieForPwdToken); 		
    				Cookie cookieForLoginToken = new Cookie("LoginToken", loginToken);
    				cookieForLoginToken.setMaxAge(30*24*60*60);
    				response.addCookie(cookieForLoginToken); 		
	    			
	    			session.setAttribute("Me", userCode);
	    			response.setCharacterEncoding("utf-8");
				  	response.setStatus(302);
				  	response.sendRedirect("XinXiXianShi.html?active=active");
	    		}else{
	    			logger.info("用户验证更新数据库失败。ZhuCeYouXiang= "+ ZhuCeYouXiang);
		    		response.setStatus(302);
		    		response.sendRedirect("XinXiXianShi.html?active=activeError");
	    		}
	    		
	    	}
	    	
	    }
	}
}
