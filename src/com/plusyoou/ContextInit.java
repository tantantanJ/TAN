package com.plusyoou;

import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ContextInit implements ServletContextListener {
    public ContextInit() {
        // TODO Auto-generated constructor stub
    	System.out.println("System started at " + new Date());
    }
    public void contextInitialized(ServletContextEvent arg0) {
    	ServletContext servletCTX = arg0.getServletContext();
    	/**
    	 * 用于外网虚拟服务器上确定Log4j日志文件位置
    	 * 取服务器上应用的真实地址，将之放在系统参数中，供log4j.properties中取用
    	 * log4jdir:/home/plusyoouup8luu6sey0oropu/wwwroot/
    	 */
    	String log4jdir = servletCTX.getRealPath("/");
  	  	System.out.println("Appication root @ " + log4jdir);
  	  	System.setProperty("log4jdir", log4jdir);
  	  	
  	  	System.setProperty("file.encoding","utf-8");
    }  	

    public void contextDestroyed(ServletContextEvent arg0) {
        // TODO Auto-generated method stub
    	System.getProperties().remove("log4jdir");
    }

}

