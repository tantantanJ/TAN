package com.plusyoou;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
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
public class LoginCheck extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5921593463673904185L;
	static Logger logger = Logger.getLogger(LoginCheck.class.getName());

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//不提供doGet服务
		//this.doPost(request, response);
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		HttpSession session = request.getSession();
		response.addHeader( "Cache-Control", "no-cache" );  
		response.setCharacterEncoding("utf-8");
		String clientIP = Utils.getIpAddr(request);
		String action = request.getParameter("action");
		//request.getHeader("X-Requested-With")为 null，则为传统同步请求；为 XMLHttpRequest，则为 Ajax 异步请求
		boolean reqFromAjax =  request.getHeader("X-Requested-With")!=null && request.getHeader("X-Requested-With").equals("XMLHttpRequest");
		String strLoginCode = ""; 
		String strPwd = request.getParameter("password");
		
		String sqlStr="SELECT YiYanZheng, ZhuCeYouXiang, XingMing, MiMaLingPai, DengLuLingPai FROM webuser WHERE ";
		
		Cookie[] cookies = request.getCookies();
		String userCodeInCookie = "";
		String loginTokenInCookie = "";
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				if (cookies[i].getName().equals("UserCode")) {
					userCodeInCookie = URLDecoder.decode(cookies[i].getValue(),"UTF-8");
					continue;
				}
				if (cookies[i].getName().equals("LoginToken")) {
					loginTokenInCookie = cookies[i].getValue();
					continue;
				}
			}
		}
		session.removeAttribute("Me");
		if (action != null && action.equals("exit")) {
			Cookie cookieForUserCode = new Cookie("UserCode", null);
			cookieForUserCode.setMaxAge(0);
			response.addCookie(cookieForUserCode); 
			Cookie cookieForUserName = new Cookie("UserName", null);
			cookieForUserName.setMaxAge(0);
			response.addCookie(cookieForUserName); 
			Cookie cookieForPwdToken = new Cookie("PwdToken", null);
			cookieForPwdToken.setMaxAge(0);
			response.addCookie(cookieForPwdToken); 
			Cookie cookieForLoginToken = new Cookie("LoginToken", null);
			cookieForLoginToken.setMaxAge(0);
			response.addCookie(cookieForLoginToken);
			if (reqFromAjax) {
				response.getWriter().write("%notLogIn%");
			} else {
				response.setStatus(302);
				response.sendRedirect("index.html");				
			}
			return;
		}
			
		strLoginCode = request.getParameter("YouXiang");
		if (strLoginCode == null || strLoginCode.equals("")) {
			strLoginCode = userCodeInCookie;
		}
		if (strLoginCode == null || strLoginCode.equals("")) {
			if (reqFromAjax) {
				response.getWriter().write("%notLogIn%");
			} else {
				response.setStatus(302);
				response.sendRedirect(request.getContextPath() + "/denglu.html?result=error");				
			}
			return;
		} else {
			sqlStr += "ZhuCeYouXiang = '" + strLoginCode +"'";
			if (strPwd == null || strPwd.equals("")) {
				if (!loginTokenInCookie.equals("")) {
					sqlStr += " AND DengLuLingPai = '" + loginTokenInCookie + "'";
				} else {
					response.setStatus(302);
					response.sendRedirect(request.getContextPath() + "/denglu.html?result=error");
					return;
				}
			} else {
				sqlStr += " AND MiMa = '" + Utils.get16BitMd5(strPwd) +"'";
			}
		}	
		List listResults=null;
		DataSource webDS = SQLUtils.getWebDataSource();
		listResults = SQLUtils.executeSelectSQL(sqlStr, webDS);
		if(listResults.isEmpty() || listResults.size()!=1 ){
			if (reqFromAjax) {
				response.getWriter().write("%notLogIn%");
			} else {
				response.setStatus(302);
				response.sendRedirect(request.getContextPath() + "/denglu.html?result=error");				
			}
			return;
		} else if(!(Boolean)((HashMap) listResults.get(0)).get("YiYanZheng")) {
			if (reqFromAjax) {
				response.getWriter().write("notLogIn");
			} else {
				response.setStatus(302);
				response.sendRedirect(request.getContextPath() + "/denglu.html?result=noactive");
			}
			return;
		} else {
			String userCode = (String)((HashMap) listResults.get(0)).get("ZhuCeYouXiang");
			String userName = URLEncoder.encode((String)((HashMap) listResults.get(0)).get("XingMing"), "UTF-8");
			String pwdToken = (String)((HashMap) listResults.get(0)).get("MiMaLingPai");
			String loginToken = Utils.get16BitMd5(userCode + clientIP + (new Date()).toString());
			if (strPwd != null) {
				pwdToken = Utils.get16BitMd5(strPwd + (new Date()).toString());
			}
			sqlStr = "UPDATE webuser SET MiMaLingPai = '" + pwdToken + "', DengLuLingPai = '" + loginToken + "' WHERE ZhuCeYouXiang = '" + userCode  + "'";
			String[] strSQLBatch = new String[1];
			strSQLBatch[0]=sqlStr;
			int result = SQLUtils.executeUpdateSQL(strSQLBatch, webDS );
			if (result != 1) {
				logger.error("更新登录令牌错误；" + sqlStr);
				response.setStatus(302);
				response.sendRedirect(request.getContextPath() + "/denglu.html?result=error");
				return;
			}
			if (request.getParameter("rememberMe") == null && !reqFromAjax) {
				Cookie cookieForUserCode = new Cookie("UserCode", URLEncoder.encode(userCode, "UTF-8"));
				cookieForUserCode.setMaxAge(-1);
				response.addCookie(cookieForUserCode); 
				Cookie cookieForUserName = new Cookie("UserName", userName);
				cookieForUserName.setMaxAge(-1);
				response.addCookie(cookieForUserName); 
				Cookie cookieForPwdToken = new Cookie("PwdToken", pwdToken);
				cookieForPwdToken.setMaxAge(-1);
				response.addCookie(cookieForPwdToken); 
				Cookie cookieForLoginToken = new Cookie("LoginToken", loginToken);
				cookieForLoginToken.setMaxAge(-1);
				response.addCookie(cookieForLoginToken); 
			} else {
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
			}
			System.out.println(userCode);
			session.setAttribute("Me", userCode);
		}
		
		if (reqFromAjax) {
			response.getWriter().write("LoginOK");
		} else {
			response.setStatus(302);
			response.sendRedirect(request.getContextPath() + "/index.html");		
		}
	}
}
