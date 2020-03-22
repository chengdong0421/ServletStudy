package org.wcd.demo.servlet.kuaishou;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Servlet implementation class PlayVideoServlet
 */
@WebServlet("/play-video")
public class PlayVideoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PlayVideoServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String videourl = request.getParameter("url");
		Document doc = Jsoup.parse("");
		doc.head().appendElement("meta").attr("charset", "utf-8");
		doc.head().appendElement("link").attr("rel","stylesheet").attr("href", request.getContextPath() + "/css-kuaishou/video.css");
		
		Element video_div = doc.appendElement("div").attr("id", "video");
		Element videodoc = video_div.appendElement("video")
				.attr("height", "600px")
				.attr("controls","controls")
				.attr("id", "v");
		videodoc.appendElement("source")
			.attr("src",videourl)
			.attr("type","video/mp4");
		
		video_div.appendElement("script").attr("type","text/javascript").attr("src", request.getContextPath() + "/js-kuaishou/video.js");
		
		response.getWriter().write(doc.toString());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
