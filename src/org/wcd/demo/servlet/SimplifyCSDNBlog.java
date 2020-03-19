package org.wcd.demo.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Servlet implementation class SimplifyCSDNBlog
 */
@WebServlet("/simplify-csdn-blog")
public class SimplifyCSDNBlog extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SimplifyCSDNBlog() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
		
		String url2 = "https://blog.csdn.net/qq_19467623/article/details/78675823";
		if(request.getParameter("url") != null) {
			url2 = request.getParameter("url");
		}
		Element simplify = simplify(url2,request);
		
		response.getWriter().write(simplify.toString());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	/**
	 * 简化csdn博文页面
	 * @param url2 博文URL
	 * @return 简化后的页面元素
	 * @throws IOException
	 */
	public Element simplify(String url2,HttpServletRequest req) throws IOException {
		//String url2 = "https://blog.csdn.net/qq_19467623/article/details/78675823";
		Document doc = Jsoup.connect(url2).get();
		//Elements elements = doc.select("div.blog-content-box");
		Elements elements = doc.select("html");
		Element element = elements.get(0);
		
		//////////////////////////
		//获取需要移除的元素
		//Element
		Element toolbar_tpl_scriptId = element.getElementById("toolbar-tpl-scriptId");
		Element share = element.selectFirst("div#share-bg-shadow");
		Element post = element.selectFirst("div#tool-post-common");
		//移除
		removeAllElement(toolbar_tpl_scriptId,share,post);
		
		//Elements
		Elements onerror = element.select("img[onerror]");//img src="" onerror="setTimeout，这个代码让离线网页自动跳转
		Elements recommend = element.select("div.recommend-right.align-items-stretch.clearfix");//div class="recommend-right  align-items-stretch clearfix"
		Elements svgs = element.getElementsByTag("svg");
		Elements hide_text = element.select("div.hide-article-box.hide-article-pos.text-center");//div class="hide-article-box hide-article-pos text-center"
		Elements ad = element.select("div#dmp_ad_58");
		Elements js = element.select("script[src^='//']");
		Elements script_csdnimg = element.select("script[src*=\"csdnimg\"]");
		Elements script_baidu = element.select("script[src*=\"baidu\"]");
		//留言板
		Elements comment_box = element.select("div.comment-box");
		//推荐
		Elements recommend_box = element.select("div.recommend-box");
		Elements template_box = element.select("div.template-box");
		//aside
		Elements blog_container_aside = element.select("aside.blog_container_aside");
		Elements toolbox_vertical = element.select("div.tool-box.vertical");
		Elements csdn_toolbar = element.select("div.csdn-toolbar");
		Elements more_toolbox = element.select("div.more-toolbox");
		Elements person_messagebox = element.select("div.person-messagebox");
		
		//移除
		removeAllElement(onerror,recommend,svgs,
				hide_text,ad,js,script_csdnimg,script_baidu,comment_box,
				recommend_box,template_box,
				blog_container_aside,toolbox_vertical,
				csdn_toolbar,more_toolbox,
				person_messagebox);
		
		//替换css文件路径
		//获取css元素
		Elements css_csdnimg = element.select("link[href*=\"csdnimg\"]");
		Elements link = element.select("link[href*=\"detail-\"]");
		replaceHrefOfCSS(css_csdnimg, req);
		
		//link.attr("href","https://raw.githubusercontent.com/chengdong0421/resources/master/csdn_blog.css");
		link.attr("href",req.getContextPath() + "/css/csdn_blog1.css");
		
		return element;
	}
	
	/**
	 * 移除Element元素
	 * @param elements
	 */
	public void removeAllElement(Element... elements) {
		for (Element element : elements) {
			if(element != null) {
				element.remove();
			}
		}
	}
	
	/**
	 * 移除Elements元素
	 * @param elements
	 */
	public void removeAllElement(Elements... elements) {
		for (Elements elems : elements) {
			for (Element element : elems) {
				//捕获异常，防止前面已经移除的元素被再次remove，因为这样会抛异常
					try{
						element.remove();
					}catch(Exception e) {
						System.err.println("Error: " + e.getMessage());
					}
				
			}
				
		}
	}
	
	/**
	 * 替换css外链为本地服务器上的资源
	 * @param elements
	 * @param req
	 */
	public void replaceHrefOfCSS(Elements elements,HttpServletRequest req) {
		for (Element element : elements) {
			String href = element.attr("href");
			String[] splitedHref = href.split("/");
			String[] splitedHref2 = splitedHref[splitedHref.length - 1].split("-");
			if(splitedHref2.length == 1) {
				element.attr("href",req.getContextPath() + "/css/" + splitedHref2[0]);
			}else {
				element.attr("href",req.getContextPath() + "/css/" + splitedHref2[0] + ".css");
			}
			
		}
	}

}
