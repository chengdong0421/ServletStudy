package org.wcd.demo.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 练习request对象的方法
 * @author Administrator
 *
 */
@WebServlet("/httpservlet")
public class MyHttpServlet extends HttpServlet{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//获取request请求行数据(GET /demo1/demo?username=wcd HTTP/1.1)
		PrintWriter writer = resp.getWriter();
		
		writer.write("\n" + req.toString());
		writer.write("\n" + req.getMethod());
		writer.write("\n" + req.getContextPath());
		writer.write("\n" + req.getRequestURI());
		writer.write("\n" + req.getRequestURL().toString());
		writer.write("\n" + req.getQueryString());
		writer.write("\n" + req.getProtocol());
		writer.write("\n" + req.getServletPath());
		writer.write("\n" + req.getRemoteAddr());
		writer.write("\n\n");
		
		//获取request请求头数据
		//getHeaderNames()
		//getHeader(String name)
		//getHeaders(String name)
		//获取所有请求头的名称
		Enumeration<String> headerNames = req.getHeaderNames();
		while(headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			writer.write(headerName + "::" + req.getHeader(headerName) + "\n");
		}
		
		//获取请求体数据
		//req.getParameter(String name)
		//req.getParameterMap()
		//req.getParameterNames()
		//req.getParameterValues(String name)

		
		
	}
}
