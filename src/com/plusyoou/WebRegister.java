package com.plusyoou;

import org.apache.log4j.Logger;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

public class WebRegister extends HttpServlet{

	private static final long serialVersionUID = 2557739855812474286L;

	static Logger logger = Logger.getLogger(WebRegister.class.getName());

		public static final String HOST = "smtp.exmail.qq.com";
		public static final String PROTOCOL = "smtp";
		public static final int PORT = 25;
		public static final String SENDER = "service@plusyoou.com";//
		public static final String SENDERPWD = "abc1234";
		public static final String MAILSUBJECT = "普赖优用户注册";

		protected void doGet(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
//			doPost(req, resp);
		}

		@SuppressWarnings("rawtypes")
		protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			String action = request.getParameter("action");
			PrintWriter out = response.getWriter();

			if ("register".equals(action)) {
				String email = request.getParameter("ZhuCeYouXiang");
			  	String yanzhengma = Utils.get16BitMd5(email+request.getParameter("ZhuCeYouXiang"));//创建加密字符串
				String pwdToken = Utils.get16BitMd5(request.getParameter("MiMa") + (new Date()).toString());
				
				String sqlStr = "Insert into webuser (ZhuCeYouXiang, XingMing, MiMa, YanZhengMa, ShouJi, ZhuCeRiQi, ShengFen, ChengShi, "
					+ "DiZhi, GongSiMingCheng, GongSiGuiMo, YeWuLeiBie, ZhuYaoPinPai, ZhiWu, DianHua, ChuanZhen, MiMaLingPai) VALUES (";
				sqlStr += "'" + request.getParameter("ZhuCeYouXiang")+"', ";
				sqlStr += "'" + request.getParameter("XingMing")+"', ";
				sqlStr += "'" + Utils.get16BitMd5(request.getParameter("MiMa"))+"', ";
				sqlStr += "'" + yanzhengma+"', ";
				sqlStr += "'" + request.getParameter("Mobile")+"', ";
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				sqlStr += "'" + formatter.format(new Date())+"', ";
				if (request.getParameter("ShengFen")==null ||request.getParameter("ShengFen").equals("")){
					sqlStr += "NULL, ";
				} else {
					sqlStr += request.getParameter("ShengFen")+", ";
				}
				if (request.getParameter("ChengShi")==null ||request.getParameter("ChengShi").equals("")){
					sqlStr += "NULL, ";
				} else {
					sqlStr += "'" + request.getParameter("ChengShi")+"', ";
				}
				if (request.getParameter("DiZhi")==null||request.getParameter("DiZhi").equals("")){
					sqlStr += "NULL, ";
				} else {
					sqlStr += "'" + request.getParameter("DiZhi")+"', ";
				}
				if (request.getParameter("GongSiMingCheng")==null ||request.getParameter("GongSiMingCheng").equals("")){
					sqlStr += "NULL, ";
				} else {
					sqlStr += "'" + request.getParameter("GongSiMingCheng")+"', ";
				}
				if (request.getParameter("MenDianGuiMo")==null ||request.getParameter("MenDianGuiMo").equals("")){
					sqlStr += "NULL, ";
				} else {
					sqlStr += request.getParameter("MenDianGuiMo")+", ";
				}
				if (request.getParameter("YeWuLeiBie")==null ||request.getParameter("YeWuLeiBie").equals("")){
					sqlStr += "NULL, ";
				} else {
					sqlStr += "'" + request.getParameter("YeWuLeiBie")+"', ";
				}
				if (request.getParameter("ZhuYaoPinPai")==null || request.getParameter("ZhuYaoPinPai").equals("")){
					sqlStr += "NULL, ";
				} else {
					sqlStr += "'" + request.getParameter("ZhuYaoPinPai")+"', ";
				}
				if (request.getParameter("ZhiWu")==null || request.getParameter("ZhiWu").equals("")){
					sqlStr += "NULL, ";
				} else {
					sqlStr += "'" + request.getParameter("ZhiWu")+"', ";
				}
				if (request.getParameter("DianHua")==null || request.getParameter("DianHua").equals("")){
					sqlStr += "NULL, ";
				} else {
					sqlStr += "'" + request.getParameter("DianHua")+"', ";
				}
				if (request.getParameter("ChuanZhen")==null || request.getParameter("ChuanZhen").equals("")){
					sqlStr += "NULL, ";
				} else {
					sqlStr += "'" + request.getParameter("ChuanZhen")+"', ";
				}
				sqlStr += "'" + pwdToken +"'); ";
				logger.info(sqlStr);
				String[] strSQLBatch= new String[1];
				strSQLBatch[0]=sqlStr;
				DataSource webDS = SQLUtils.getWebDataSource();
				String result = SQLUtils.executeInsertSQL (strSQLBatch,Boolean.FALSE , webDS );
				if (result.equals("")){
					logger.error("添加用户信息出现错误。");
					response.setStatus(302);
					response.sendRedirect("error.jsp");
				} else {
				  	StringBuffer content = new StringBuffer("<h2>尊敬的");
				  	content.append(request.getParameter("XingMing") + "： </h2>");
				  	content.append("感谢您对我们的关注与支持，欢迎您使用我们的产品与服务。<br>");
				  	content.append("您注册的用户名是："+request.getParameter("XingMing") + "   密码是："+request.getParameter("MiMa"));
				  	content.append("<h2>请点击下面的激活地址完成验证，激活地址只能使用一次，请尽快激活！</h2>");
				  	content.append("<a style='font-size:16px;' href="+"'http://www.plusyoou.com/3c8eac13?k=")
				  		.append( yanzhengma +"'>")
				  		.append("http://www.plusyoou.com/3c8eac13?k="+yanzhengma)
				  		.append(""+"</a><br/><br/>")
				  		.append("<span style='color:blue;font-size:16px;font-weight:bold;'>（如果上面的链接点击后无响应，您可以复制激活地址，并粘帖到浏览器的地址栏中访问。）<span>");
				  	sendmail(email, "www.plusyoou.com官网注册确认", content.toString());//开始发送邮件
				  	logger.info("成功添加用户。邮箱："+request.getParameter("ZhuCeYouXiang"));
				  	response.setStatus(302);
				  	response.sendRedirect("XinXiXianShi.html?active=register");
				}
			} else if ("resendRegisterMail".equals(action)) {
				String strSQL = "SELECT ZhuCeYouXiang, XingMing, YiYanZheng, YanZhengMa FROM webuser WHERE ZhuCeYouXiang = '" 
					+ request.getParameter("YouXiang")+"' ";
				logger.info(strSQL);
				DataSource webDS = SQLUtils.getWebDataSource();
				List listResults = SQLUtils.executeSelectSQL(strSQL,webDS);

				if (listResults == null) {
					logger.error("SQL异常，检查语句");
					out.println("对不起，网站异常，请等待我们修复");
					return;
				} else if (listResults.isEmpty() || listResults.size()!=1 ){
					out.println("您输入的邮箱没有注册！");
					return;					
				} else {
					if ((boolean)((HashMap) listResults.get(0)).get("YiYanZheng")) {
						out.println("您的账号已经激活，不需要重发激活邮件！");
					} else {
						String YanZhengMa = (String)((HashMap) listResults.get(0)).get("YanZhengMa");
						StringBuffer content = new StringBuffer("<h2>尊敬的");
					  	content.append((String)((HashMap) listResults.get(0)).get("XingMing") + "： </h2>");
					  	content.append("感谢您对我们的关注与支持，并欢迎您使用我们的产品与服务。<br>");
//					  	content.append("您注册的用户名是：" + (String)((HashMap) listResults.get(0)).get("XingMing"));
					  	content.append("<h2>请点击下面的激活地址完成验证，激活地址只能使用一次，请尽快激活！</h2>");
					  	content.append("<a style='font-size:16px;' href="+"'http://www.plusyoou.com/3c8eac13?k=")
					  		.append( YanZhengMa +"'>")
					  		.append("http://www.plusyoou.com/3c8eac13?k=" + YanZhengMa)
					  		.append(""+"</a><br/><br/>")
					  		.append("<span style='color:blue;font-size:16px;font-weight:bold;'>（如果上面的链接点击后无响应，您可以复制激活地址，并粘帖到浏览器的地址栏中访问。）<span>");
					  	sendmail(request.getParameter("YouXiang"), "www.plusyoou.com官网注册确认", content.toString());//开始发送邮件
					  	out.println("ok");
					};
					return;
				}
			} else if("verifyEmail".equals(action)) {
				String strSQL = "SELECT COUNT(ZhuCeYouXiang) AS CountNum FROM webuser WHERE ZhuCeYouXiang='"+request.getParameter("ZhuCeYouXiang")+"' ";
				logger.info(strSQL);
				DataSource webDS = SQLUtils.getWebDataSource();
				List listResults = SQLUtils.executeSelectSQL(strSQL,webDS);

				if (listResults == null) {
					logger.error("SQL异常，检查语句");
					out.println(-1);
					return;
				}else{
					out.println(Integer.parseInt((((HashMap) listResults.get(0)).get("CountNum")+"")));
					return;
				}
			} else if("verifyName".equals(action)) {
				String strSQL = "SELECT COUNT(XingMing) AS CountNum FROM webuser WHERE XingMing='"+request.getParameter("XingMing")+"' ";
				logger.info(strSQL);
				DataSource webDS = SQLUtils.getWebDataSource();
				List listResults = SQLUtils.executeSelectSQL(strSQL,webDS);

				if (listResults == null) {
					logger.error("SQL异常，检查语句");
					out.println(-1);
					return;
				}else{
					out.println(Integer.parseInt((((HashMap) listResults.get(0)).get("CountNum")+"")));
					return;
				}
			} else if("getAcct".equals(action)) {
				System.out.println(action);
				HttpSession session = request.getSession();
				String strLoginCode = (String) session.getAttribute("Me");
				if (strLoginCode == null || strLoginCode.equals("")) {
					out.println("notLogIn");
					return;					
				}
				String strSQL = "SELECT ShouJi, ShengFen, ChengShi, DiZhi, GongSiMingCheng, "
					+ "MenDianGuiMo, YeWuLeiBie, ZhuYaoPinPai, ZhiWu, DianHua, ChuanZhen FROM webuser WHERE ZhuCeYouXiang='" + strLoginCode +"'";
				logger.info(strSQL);
				DataSource webDS = SQLUtils.getWebDataSource();
				List listResults = SQLUtils.executeSelectSQL(strSQL,webDS);
				
				if (listResults == null) {
					logger.error("SQL异常，检查语句");
					out.println(-1);
					return;
				}else{
					String outString = "";
					outString += "Mobile~" + ((HashMap) listResults.get(0)).get("ShouJi") + "^";
					outString += "GongSiMingCheng~" + ((HashMap) listResults.get(0)).get("GongSiMingCheng") + "^";						
					if (((HashMap) listResults.get(0)).get("DiZhi") != null) {
						outString += "DiZhi~" + ((HashMap) listResults.get(0)).get("DiZhi") + "^";						
					}
					if (((HashMap) listResults.get(0)).get("MenDianGuiMo")!= null) {
						outString += "MenDianGuiMo~" + ((HashMap) listResults.get(0)).get("MenDianGuiMo") + "^";
					}
					if (((HashMap) listResults.get(0)).get("YeWuLeiBie")!= null) {
						outString += "YeWuLeiBie~" + ((HashMap) listResults.get(0)).get("YeWuLeiBie") + "^";
					}
					if (((HashMap) listResults.get(0)).get("ZhuYaoPinPai")!= null) {
						outString += "ZhuYaoPinPai~" + ((HashMap) listResults.get(0)).get("ZhuYaoPinPai") + "^";
					}
					if (((HashMap) listResults.get(0)).get("ZhiWu")!= null) {
						outString += "ZhiWu~" + ((HashMap) listResults.get(0)).get("ZhiWu") + "^";
					}
					if (((HashMap) listResults.get(0)).get("DianHua")!= null) {
						outString += "DianHua~" + ((HashMap) listResults.get(0)).get("DianHua") + "^";
					}
					if (((HashMap) listResults.get(0)).get("ChuanZhen")!= null) {
						outString += "ChuanZhen~" + ((HashMap) listResults.get(0)).get("ChuanZhen") + "^";
					}
					out.println(outString);
					System.out.println(outString);
					return;
				}
			} else if ("updateAcct".equals(action)) {
				HttpSession session = request.getSession();
				String strLoginCode = (String) session.getAttribute("Me");
				if (strLoginCode == null || strLoginCode.equals("")) {
					out.println("notLoggedIn");
					return;					
				}
				String sqlStr = "UPDATE webuser SET ";
				sqlStr += "ShouJi = '" + request.getParameter("Mobile")+"', ";
				sqlStr += "ShengFen = ";
				if (request.getParameter("ShengFen")==null ||request.getParameter("ShengFen").equals("")){
					sqlStr += "NULL, ";
				} else {
					sqlStr += request.getParameter("ShengFen")+", ";
				}
				sqlStr += "ChengShi = ";				
				if (request.getParameter("ChengShi")==null ||request.getParameter("ChengShi").equals("")){
					sqlStr += "NULL, ";
				} else {
					sqlStr += "'" + request.getParameter("ChengShi")+"', ";
				}
				sqlStr += "DiZhi = ";				
				if (request.getParameter("DiZhi")==null||request.getParameter("DiZhi").equals("")){
					sqlStr += "NULL, ";
				} else {
					sqlStr += "'" + request.getParameter("DiZhi")+"', ";
				}
				sqlStr += "GongSiMingCheng = ";				
				if (request.getParameter("GongSiMingCheng")==null ||request.getParameter("GongSiMingCheng").equals("")){
					sqlStr += "NULL, ";
				} else {
					sqlStr += "'" + request.getParameter("GongSiMingCheng")+"', ";
				}
				sqlStr += "MenDianGuiMo = ";		
				if (request.getParameter("MenDianGuiMo")==null ||request.getParameter("MenDianGuiMo").equals("")){
					sqlStr += "NULL, ";
				} else {
					sqlStr += request.getParameter("MenDianGuiMo")+", ";
				}
				sqlStr += "YeWuLeiBie = ";				
				if (request.getParameter("YeWuLeiBie")==null ||request.getParameter("YeWuLeiBie").equals("")){
					sqlStr += "NULL, ";
				} else {
					sqlStr += "'" + request.getParameter("YeWuLeiBie")+"', ";
				}
				sqlStr += "ZhuYaoPinPai = ";				
				if (request.getParameter("ZhuYaoPinPai")==null || request.getParameter("ZhuYaoPinPai").equals("")){
					sqlStr += "NULL, ";
				} else {
					sqlStr += "'" + request.getParameter("ZhuYaoPinPai")+"', ";
				}
				sqlStr += "ZhiWu = ";				
				if (request.getParameter("ZhiWu")==null || request.getParameter("ZhiWu").equals("")){
					sqlStr += "NULL, ";
				} else {
					sqlStr += "'" + request.getParameter("ZhiWu")+"', ";
				}
				sqlStr += "DianHua = ";				
				if (request.getParameter("DianHua")==null || request.getParameter("DianHua").equals("")){
					sqlStr += "NULL, ";
				} else {
					sqlStr += "'" + request.getParameter("DianHua")+"', ";
				}
				sqlStr += "ChuanZhen = ";				
				if (request.getParameter("ChuanZhen")==null || request.getParameter("ChuanZhen").equals("")){
					sqlStr += "NULL ";
				} else {
					sqlStr += "'" + request.getParameter("ChuanZhen")+"' ";
				}				
				sqlStr += "WHERE ZhuCeYouXiang='" + strLoginCode +"'";
				logger.info(sqlStr);
				String[] strSQLBatch= new String[1];
				strSQLBatch[0]=sqlStr;
				DataSource webDS = SQLUtils.getWebDataSource();
				int result = SQLUtils.executeUpdateSQL(strSQLBatch, webDS );
				if (result != 1){
					logger.error("修改用户信息出现错误。");
					response.setStatus(302);
					response.sendRedirect("XinXiXianShi.html?active=updateError");
				} else {
				  	response.setStatus(302);
				  	response.sendRedirect("XinXiXianShi.html?active=updateSuccess");
				}
				//重置密码仅支持通过ajax访问
			} else if ("resetPWD".equals(action)) {
				String strLoginCode = request.getParameter("UserCode");
				if (strLoginCode == null || strLoginCode.equals("")) {
					logger.error("重置密码无登录邮箱。");
					out.println("userError");
					return;					
				}
				String strSQL = "SELECT XingMing FROM webuser WHERE ZhuCeYouXiang='" + strLoginCode +"'";
				logger.info(strSQL);
				DataSource webDS = SQLUtils.getWebDataSource();
				List listResults = SQLUtils.executeSelectSQL(strSQL,webDS);
				String userName = "";
				if (listResults == null || listResults.size() == 0) {
					logger.error("重置密码无用户信息登记。邮箱：" + strLoginCode);
					out.println("userError");
					return;
				} else {
					userName = (String)((HashMap) listResults.get(0)).get("XingMing");						
				}

				Date QingQiuShiJian = new Date();
				Calendar c = Calendar.getInstance(); 
				c.setTime(QingQiuShiJian); 
				c.add(Calendar.DATE, 1);
				Date GuoQiShiJian = c.getTime();
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				String QingQiu = formatter.format(QingQiuShiJian);
				String GuoQi = formatter.format(GuoQiShiJian);
				System.out.println(QingQiu);
				System.out.println(GuoQi);
				String ChongZhiMiMa = Utils.get16BitMd5(strLoginCode + (QingQiuShiJian).toString());
				String[] strSQLBatch= new String[2];
				String sqlStr = "UPDATE pwdreset SET YiShiXiao = TRUE WHERE ZhuCeYouXiang = '" + strLoginCode + "'";			
				logger.info(sqlStr);
				strSQLBatch[0]=sqlStr;
				sqlStr = "INSERT INTO pwdreset (ZhuCeYouXiang, QingQiuShiJian, GuoQiShiJian, ChongZhiMiYao) VALUES ('" + strLoginCode + "', '"
						+ QingQiu + "', '" + GuoQi + "', '" + ChongZhiMiMa + "')";				
				logger.info(sqlStr);
				strSQLBatch[1]=sqlStr;
				String result = SQLUtils.executeSQLStmt (strSQLBatch, webDS );
				if (result.equals("")){
					logger.error("添加重置密码记录出现错误。");
					out.println("DBError");
					return;
				} else {
				  	StringBuffer content = new StringBuffer("<h3>尊敬的");
				  	content.append(userName + "： </h3>")
				  		.append("<p>感谢您对我们的关注与支持，欢迎您使用我们的产品与服务。</p>")
				  		.append("<h4>请点击<a href=\"http://www.plusyoou.com/register?action=reset&e=" + strLoginCode 
			  				+ "&r=" + ChongZhiMiMa	+ "\">这个激活地址</a>完成密码重置，重置地址只能使用一次，且将在24小时后失效！</h4>")
		  				.append("<p style=\"color:blue;font-size:14px\">如果上面的链接点击后无响应，"
	  						+ "您可以复制下面的重置地址，粘帖到浏览器的地址栏中访问。</p>")
		  				.append("<p style=\"font-size:14px;pointer-events: none;cursor:default\">"
	  						+ "http://www.plusyoou.com/register?action=reset&e=" + strLoginCode 
	  						+ "&r=" + ChongZhiMiMa + "</p>");
				  	sendmail(strLoginCode, "www.plusyoou.com账号密码重置", content.toString());//开始发送邮件
				  	logger.info("发送重置链接至邮箱：" + strLoginCode);
					out.println("resetMailOK");
					return;
				}
			} else if ("reset".equals(action)){
				String strLoginCode = request.getParameter("e");
				if (strLoginCode == null || strLoginCode.equals("")) {
					logger.error("重置密码链接不包含登录邮箱。");
					response.setStatus(302);
					response.sendRedirect("XinXiXianShi.html?active=resetError");
					return;					
				}
				String resetKey = request.getParameter("r");
				String strSQL = "SELECT QingQiuShiJian, GuoQiShiJian, ShiYongShiJian, YiShiXiao FROM pwdreset WHERE ZhuCeYouXiang='" + strLoginCode +"' "
					+ "AND ChongZhiMiYao = '" + resetKey + "'";
				logger.info(strSQL);
				DataSource webDS = SQLUtils.getWebDataSource();
				List listResults = SQLUtils.executeSelectSQL(strSQL,webDS);
				if (listResults == null) {
					logger.error("无效的重置链接。邮箱：" + strLoginCode + "，链接码：" + resetKey);
					response.setStatus(302);
					response.sendRedirect("XinXiXianShi.html?active=linkError");
					return;
				} else {
					boolean YiShiXiao = (boolean)((HashMap) listResults.get(0)).get("YiShiXiao");
					if (YiShiXiao) {
						if (((HashMap) listResults.get(0)).get("ShiXiaoShiJian") == null) {
							logger.error("后续又申请了重置密码，该码失效。邮箱：" + strLoginCode + "，链接码：" + resetKey);
							response.setStatus(302);
							response.sendRedirect("XinXiXianShi.html?active=linkError");
						} else {
							logger.error("重置链接码已经使用失效。邮箱：" + strLoginCode + "，链接码：" + resetKey);
							response.setStatus(302);
							response.sendRedirect("XinXiXianShi.html?active=linkError");							
						}
						return;
					} else if (((HashMap) listResults.get(0)).get("ShiYongShiJian") != null) {
						logger.error("已使用，强制回到密码重置界面！邮箱：" + strLoginCode + "，链接码：" + resetKey);
						response.setStatus(302);
						response.sendRedirect("ChongZhiMiMa.html?key=" + resetKey);
						return;
					}
					Date ShiXiaoShiJian = (Date)((HashMap) listResults.get(0)).get("GuoQiShiJian");
					Date nowDate = new Date();
					long diff = ShiXiaoShiJian.getTime() - nowDate.getTime();
			        if (diff < 0) {
						logger.error("重置链接码已经过期。邮箱：" + strLoginCode + "，链接码：" + resetKey);
						response.setStatus(302);
						response.sendRedirect("XinXiXianShi.html?active=overDate");
						return;
			        }
			        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			        String ShiYong = formatter.format(nowDate);
			        strSQL = "UPDATE pwdreset SET ShiYongShiJian = '" + ShiYong + "' WHERE ZhuCeYouXiang = '" + strLoginCode + "' "
			        		+ "AND ChongZhiMiYao = '" + resetKey + "'";
			        logger.info(strSQL);
					String[] strSQLBatch= new String[1];
					strSQLBatch[0]=strSQL;
					int result = SQLUtils.executeUpdateSQL(strSQLBatch, webDS );
					if (result != 1){
						logger.error("重置密码出现错误。");
						response.setStatus(302);
						response.sendRedirect("XinXiXianShi.html?active=DBError");
					} else {
					  	response.setStatus(302);
					  	response.sendRedirect("ChongZhiMiMa.html?key=" + resetKey);
					}
				}
			} else if ("newPWD".equals(action)){
				String strLoginCode = request.getParameter("ZhuCeYouXiang");
				if (strLoginCode == null || strLoginCode.equals("")) {
					logger.error("重置密码链接不包含登录邮箱。");
					response.setStatus(302);
					response.sendRedirect("XinXiXianShi.html?active=newPWDError");
					return;					
				}
				String resetKey = request.getParameter("key");
				String strSQL = "SELECT QingQiuShiJian, GuoQiShiJian, ShiYongShiJian, YiShiXiao FROM pwdreset WHERE ZhuCeYouXiang='" + strLoginCode +"' "
					+ "AND ChongZhiMiYao = '" + resetKey + "'";
				logger.info(strSQL);
				DataSource webDS = SQLUtils.getWebDataSource();
				List listResults = SQLUtils.executeSelectSQL(strSQL,webDS);
				if (listResults == null) {
					logger.error("无效的重置链接。邮箱：" + strLoginCode + "，链接码：" + resetKey);
					response.setStatus(302);
					response.sendRedirect("XinXiXianShi.html?active=linkError");
					return;
				} else {
					boolean YiShiXiao = (boolean)((HashMap) listResults.get(0)).get("YiShiXiao");
					if (YiShiXiao) {
						if (((HashMap) listResults.get(0)).get("ShiXiaoShiJian") == null) {
							logger.error("非法调用，未从邮箱确认重置。邮箱：" + strLoginCode + "，链接码：" + resetKey);
							response.setStatus(302);
							response.sendRedirect("XinXiXianShi.html?active=newPWDError");
						} else {
							logger.error("密码已经重置，不能再次重置。邮箱：" + strLoginCode + "，链接码：" + resetKey);
							response.setStatus(302);
							response.sendRedirect("XinXiXianShi.html?active=linkError");							
						}
						return;
					}
					String[] strSQLBatch= new String[2];
					strSQL = "UPDATE pwdreset SET YiShiXiao = TRUE WHERE ZhuCeYouXiang = '" + strLoginCode + "' "
							+ "AND ChongZhiMiYao = '" + resetKey + "'";		
					logger.info(strSQL);
					strSQLBatch[0] = strSQL;
					strSQL = "UPDATE webuser SET MiMaLingPai = '" 
							+ Utils.get16BitMd5(request.getParameter("MiMa") + (new Date()).toString()) + "', MiMa = '"
							+ Utils.get16BitMd5(request.getParameter("MiMa")) + "' WHERE ZhuCeYouXiang = '" + strLoginCode + "'"; 				
					logger.info(strSQL);
					strSQLBatch[1]=strSQL;
					String result = SQLUtils.executeSQLStmt (strSQLBatch, webDS );
					if (result.equals("")){
						logger.error("更改密码出现错误。邮箱：" + strLoginCode);
						response.setStatus(302);
						response.sendRedirect("XinXiXianShi.html?active=DBError");	
						return;
					} else {
						response.setStatus(302);
						response.sendRedirect("XinXiXianShi.html?active=pwdOK");	
						return;
					}
				}
			} else if ("ShengFen_list4A".equals(action)) {
				String strSQL = "SELECT BiaoShi,ShengFenMingCheng FROM shengfen ";
				DataSource webDS = SQLUtils.getWebDataSource();
				List listResults = SQLUtils.executeSelectSQL(strSQL,webDS);
//				logger.debug(strSQL);
				String jsonstr="{";
				for (int i=0;i<listResults.size();i++){
					jsonstr+="\"BiaoShi\":"+Integer.parseInt((((HashMap) listResults.get(i)).get("BiaoShi")+""))+",";
					jsonstr+="\"ShengFenMingCheng\":\""+(String) ((HashMap) listResults.get(i)).get("ShengFenMingCheng")+"\",";
				}
		        jsonstr=jsonstr.substring(0, jsonstr.length()-1);
		        jsonstr="["+jsonstr+"]";
		        response.setContentType("text/html");
		        out.println(jsonstr);
			} 
		}
		
		/**
		 * 获取用于发送邮件的Session
		 * @return
		 */
		public static Session getSession() {
			Properties props = new Properties();
			props.put("mail.smtp.host", HOST);//设置服务器地址
	        props.put("mail.store.protocol" , PROTOCOL);//设置协议
	        props.put("mail.smtp.port", PORT);//设置端口
	        props.put("mail.smtp.auth" , true);
	        
	        Authenticator authenticator = new Authenticator() {
	        	@Override
	            protected PasswordAuthentication getPasswordAuthentication() {
	                return new PasswordAuthentication(SENDER, SENDERPWD);
	            }
			};
	        Session session = Session.getDefaultInstance(props,authenticator);
	        return session;
		}
		/**
		 * 发送邮件
		 * @param receiver
		 * @param content
		 */
		public static void sendmail(String receiver, String MailTitle, String content) {
			Session session = getSession();
			try {
//				System.out.println("-------开始发送-------");
				Message msg = new MimeMessage(session);
				//设置message属性
				msg.setFrom(new InternetAddress(SENDER));
				InternetAddress[] addrs = {new InternetAddress(receiver)};
				msg.setRecipients(Message.RecipientType.TO, addrs);
				msg.setSubject(MailTitle);
				msg.setSentDate(new Date());
				msg.setContent(content,"text/html;charset=utf-8");
				//开始发送
				Transport.send(msg);
//	                        System.out.println("-------发送完成-------");
			} catch (AddressException e) {
				e.printStackTrace();
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
	
		/**
		 * 将源字符串通过MD5进行加密为字节数组
		 * @param source
		 * @return
		 */
		public static byte[] encodeToBytes(String source) {
			byte[] result  = null;
			try {
				MessageDigest md = MessageDigest.getInstance("MD5");
				md.reset();//重置
				md.update(source.getBytes("UTF-8"));//添加需要加密的源
				result = md.digest();//加密
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return result;
		}
		
		/**
		 * 将源字符串通过MD5加密成32位16进制数
		 * @param source
		 * @return
		 */
		public static String encodeToHex(String source) {
			byte[] data = encodeToBytes(source);//先加密为字节数组
			StringBuffer hexSb = new StringBuffer();
			for (int i = 0; i < data.length; i++) {
				String hex = Integer.toHexString(0xff & data[i]);
				if (hex.length() == 1) {
					hexSb.append("0");
				}
				hexSb.append(hex);
			}
			return hexSb.toString();
		}
		
		/**
		 * 验证字符串是否匹配
		 * @param unknown	待验证的字符串
		 * @param okHex		使用MD5加密后的16进制字符串
		 * @return
		 */
		public static boolean validate(String unknown , String okHex) {
			return okHex.equals(encodeToHex(unknown));
		}
}
