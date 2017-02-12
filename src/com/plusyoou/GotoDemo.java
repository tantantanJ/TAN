package com.plusyoou;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

public class GotoDemo extends HttpServlet{
	static Logger logger = Logger.getLogger(GotoDemo.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO 自动生成的方法存根
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HttpSession session = req.getSession();
		String userCode=(String)session.getAttribute("Me");
		if (userCode==null || userCode.equals("")){
			resp.setStatus(302);
			resp.sendRedirect(req.getContextPath() + "/denglu.html?result=error");				
			return;
		}else{
			String sqlStr = "SELECT ZhuCeYouXiang,XingMing,MiMa,DengLuLingPai FROM webuser WHERE ZhuCeYouXiang='" + userCode  + "'";
			List listResults=null;
			DataSource webDS = SQLUtils.getWebDataSource();
			listResults = SQLUtils.executeSelectSQL(sqlStr, webDS);
			if(listResults.isEmpty() || listResults.size()!=1 ){
				resp.setStatus(302);
				resp.sendRedirect(req.getContextPath() + "/denglu.html?result=error");				
				return;
			} else {
				//直接返回页面进行提交，后发觉如被抓包会暴露参数，先这样修改，有时间再实验RequestDispatcher.forward，forward无法跨应用传参数
				logger.info("登录demo： userCode="+(String)((HashMap) listResults.get(0)).get("XingMing")+" password="+(String)((HashMap) listResults.get(0)).get("MiMa")+" PwdToken="+(String)((HashMap) listResults.get(0)).get("DengLuLingPai"));
				PrintWriter out = resp.getWriter();
				String htmlStr="<HTML> <HEAD> <TITLE>Login</TITLE></HEAD> " +
						"<BODY> <FORM METHOD=POST id=\"myForm\" ACTION=\"demo/loginCheck.do\"> " +
						"<INPUT TYPE=\"hidden\" NAME=\"userCode\" value=\""+ (String)((HashMap) listResults.get(0)).get("XingMing") + "\"> " +
						"<INPUT TYPE=\"hidden\" NAME=\"password\" value=\""+ (String)((HashMap) listResults.get(0)).get("MiMa") + "\"> " +
						"<INPUT TYPE=\"hidden\" NAME=\"LoginToken\" value=\""+ (String)((HashMap) listResults.get(0)).get("DengLuLingPai") + "\"> " +
						"</FORM> <script type=\"text/javascript\"> " +
						"window.onload = function(){document.getElementById(\"myForm\").submit();} </script> </BODY> </HTML>";
		        out.println(htmlStr); 
		        return;
			}
			
		}
		
		
		
		
//		req.setAttribute("userCode","huosm");
//		req.setAttribute("password","123123");
//		req.getRequestDispatcher("/demo/login.jsp").forward(req,resp);

//		String forwardStr="?userCode=huosm&password=123123";
//		resp.sendRedirect("http://211.149.210.226/demo/login.jsp"+forwardStr);
		}


        
}
