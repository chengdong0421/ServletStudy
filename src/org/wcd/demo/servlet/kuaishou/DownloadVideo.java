package org.wcd.demo.servlet.kuaishou;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class DownloadVideo
 */
@WebServlet("/download-video")
public class DownloadVideo extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DownloadVideo() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userName = request.getSession().getAttribute("username").toString();
		String vurl = request.getParameter("vurl");
		response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode((userName + "-" + vurl.substring(vurl.lastIndexOf("/") + 1)), "UTF-8"));
		
		
		URL url = new URL(vurl);
		BufferedInputStream bis = new BufferedInputStream(url.openStream());
		ServletOutputStream os = response.getOutputStream();
		int len = 0;
		byte[] b = new byte[1024*100];
		while((len = bis.read(b)) != -1) {
			os.write(b,0,len);
		}
		
		os.close();
		bis.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
