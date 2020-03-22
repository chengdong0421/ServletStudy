package org.wcd.demo.servlet.kuaishou;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

/**
 * Servlet implementation class ParseWorkingListFormatedServlet
 */
@WebServlet(description = "解析作品列表并格式化输出", urlPatterns = { "/parse-kuaishou-formated" })
public class ParseWorkingListFormatedServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ParseWorkingListFormatedServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		
		String pcursor = "";
		int n = 1;
		JsonElement workingJEle = null;
		String workingList = null;
		JsonArray workListJArr = null;
		Iterator<JsonElement> iterator = null;
		String username = ParseUtils.getUserName(request, response);
		
		request.getSession().setAttribute("username", username);
		
		
		//初始化一个页面
		Document doc = Jsoup.parse("");
		doc.title("视频列表-" + username + "-" + ParseUtils.getUserId(request.getParameter("url")));
		doc.head().appendElement("meta").attr("charset", "utf-8");
		doc.head().appendElement("link").attr("rel","stylesheet").attr("href", request.getContextPath() + "/css-kuaishou/table.css");
		Element table = doc.appendElement("table");
		table.append("<tr id='theader'>"
				+ "<td>No.</td>"
				+ "<td>id</td>"
				+ "<td>poster</td>"
				+ "<td>workType</td>"
				+ "<td>type</td>"
				+ "<td>caption</td>"
				+ "<td>download</td>"
				+ "</tr>");
		
		//当获取的视频条目列表中的pcursor不是no_more时，用新获取的pcursor获取后一页视频
		while(!"no_more".equals(pcursor)) {
			workingList = ParseUtils.getWorkListJsonStr(request, response, pcursor);
			//视频列表JsonArray数组
			workListJArr = ParseUtils.parseWorkList(workingList,"private");
			//pcursor
			pcursor = ParseUtils.parseWorkListPcursor(workingList,"private");
			iterator = workListJArr.iterator();
			
			while(iterator.hasNext()) {
				Element tr = table.appendElement("tr");
				workingJEle = iterator.next();
				Map<String, String> workingMap = ParseUtils.parseWorking(workingJEle);
				if(workingMap == null) {
					continue;
				}
				
				String videoUrl = ParseUtils.parseVideoUrl(ParseUtils.getSharePageJsonStr(request, response, workingMap.get("id")));
				System.out.println(videoUrl);
				try {
					System.out.println("停顿中...");
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				tr.appendElement("td").attr("width","50px").appendText("" + (n++));
				//tr.appendElement("td").attr("width","200px").appendElement("a").attr("href",videoUrl).appendText(workingMap.get("id"));
				//点击在新页面播放视频
				Element td_a1 = tr.appendElement("td").attr("width","200px");
					td_a1.appendElement("a").attr("href",request.getContextPath() + "/play-video?url=" + videoUrl).attr("target","_blank")
					.appendText("点击观看");
					td_a1.appendElement("p").appendText("    ");
					td_a1.appendElement("a").attr("href",videoUrl).appendText("下载");
				//tr.appendElement("td").attr("width","180px").appendElement("img").attr("src","").attr("height","150px").attr("width","100px");
				tr.appendElement("td").attr("width","200px").appendElement("img").attr("src",workingMap.get("poster")).attr("height","150px");
				tr.appendElement("td").attr("width","100px").appendText(workingMap.get("workType"));
				tr.appendElement("td").attr("width","100px").appendText(workingMap.get("type"));
				tr.appendElement("td").attr("width","200px").appendText(workingMap.get("caption"));
				tr.appendElement("td").attr("width","200px").appendElement("a").attr("href",request.getContextPath() + "/download-video?vurl=" + videoUrl).attr("target","_blank")
				.appendText("以标题命名下载");;
				
				/*
				Set<Entry<String, String>> entrySet = workingMap.entrySet();
				for (Entry<String, String> entry : entrySet) {
					out.write(entry.getKey() + "-->" + entry.getValue() + "\n");
				}
				out.write("---------------------↑↑↑---" + n++ + "----↑↑↑---------------------\n");
				*/
			}
			
		

		}
		
		Element tr_next = table.appendElement("tr");
		
		out.write(doc.toString());
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		doGet(request, response);
	}

}
