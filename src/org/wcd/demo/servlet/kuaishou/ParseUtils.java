package org.wcd.demo.servlet.kuaishou;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class ParseUtils {
	
	public static String getRequestParams(HttpServletRequest request, HttpServletResponse response, String param) {
		//// 获取参数
		String ret = "";
		switch (param) {
		case "url":
			ret = request.getParameter("url");
			break;
		case "count":
			ret = request.getParameter("count");
			break;
		case "cookie":
			ret = request.getParameter("cookie");
			break;
		default:
			ret = request.getParameter("url");
		}
		return ret;
	}
	
	/**
	 * 根据主播首页URL获取主播id
	 * @param mainPageUrl 主播的首页地址
	 * @return 主播的id
	 */
	public static String getUserId(String mainPageUrl) {
		long count = Arrays.stream(mainPageUrl.split("/")).count();
		return Arrays.stream(mainPageUrl.split("/")).skip(count - 1).findFirst().get();
	}
	
	public static String getUserName(HttpServletRequest request, HttpServletResponse response) {
		String url = request.getParameter("url");
		String userName = "";
		if("".equals(url)) {
			return "";
		}else {
			try {
			userName = Jsoup.connect(url)
					.cookies(buildCookies(getRequestParams(request, response, "cookie")))
					.get().select("p.user-info-name").last().text().split(" ")[0].trim();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return userName;
		}
			
	}
	
	private static String getPhotoId(Map<String,String> workingMap) {
		return workingMap.get("id");
	}
	
	/**从json字符串中解析出视频地址
	 * @param workingJsonStr 从视频播放页解析出的Document
	 * @return 字符串形式的视频地址
	 */
	public static String parseVideoUrl(String workingJsonStr) {
		//解析json
		JsonParser parser = new JsonParser();
		String videoUrl = null;
		JsonObject object = null;
		/*
		GsonBuilder gb = new GsonBuilder(); 
		gb.registerTypeAdapter(String.class, new StringConverter()); 
		Gson gson = gb.create();*/
		
		try {
			JsonElement jsonElement = parser.parse(workingJsonStr)
					.getAsJsonObject()
					.getAsJsonObject("data")
					.getAsJsonObject("feedById")
					.get("currentWork");
			if(!(jsonElement instanceof JsonNull)) {
				videoUrl = jsonElement.getAsJsonObject().get("playUrl").getAsString();
			}
			
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		}
		
		return videoUrl;
	}
	
	
	/**
	 * 根据作品id获取作品视频url的json数据
	 * @param mainPageUrl
	 * @param cookies
	 * @param photoId
	 * @return
	 */
	public static String getSharePageJsonStr(HttpServletRequest request, HttpServletResponse response, String photoId) {

		//请求主播作品的请求地址
		String requestUrl = "https://live.kuaishou.com/m_graphql";
		
		////获取参数
		//url
		String mainPageUrl = request.getParameter("url");
		//count
		String count = request.getParameter("count");
		//cookie
		String cookieStr = request.getParameter("cookie");
				
		//构建请求头
		Map<String,String> headersMap = buildHeaders(mainPageUrl);
				
		//构建cookie
		Map<String, String> cookiesMap = buildCookies(cookieStr);
		
		//构建请求数据
		String data = "{\"operationName\":\"SharePageQuery\",\"variables\":{\"photoId\":\""
		 		+ photoId
		 		+ "\",\"principalId\":\""
		 		+ getUserId(mainPageUrl)
		 		+ "\"},\"query\":\"query SharePageQuery($principalId: String, $photoId: String) {\\n  feedById(principalId: $principalId, photoId: $photoId) {\\n    currentWork {\\n      playUrl\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n\"}";
	 
		Document doc2 = null;
		try {
			doc2 = Jsoup.connect(requestUrl).ignoreContentType(true)
			.headers(headersMap)
			.cookies(cookiesMap)
			.requestBody(data)
			.post();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return doc2.body().text();
	}
	
	
	/**
	 * @param mainPageUrl
	 * @param cookies
	 * @return
	 */
	public static String getWorkListJsonStr(HttpServletRequest request, HttpServletResponse response, String pcursor) {
		
		//请求主播作品列表的请求地址
		String requestUrl = "https://live.kuaishou.com/m_graphql";
		
		////获取参数
		//url
		String mainPageUrl = request.getParameter("url");
		//count
		String count = request.getParameter("count");
		//cookie
		String cookieStr = request.getParameter("cookie");
				
		//构建请求头
		Map<String,String> headersMap = buildHeaders(mainPageUrl);
		//构建cookie
		Map<String, String> cookiesMap = buildCookies(cookieStr);
		//构建请求数据
		 String data = "{\"operationName\":\"privateFeedsQuery\",\"variables\":{\"principalId\":\""
		 		+ getUserId(mainPageUrl)
		 		+ "\",\"pcursor\":\""
		 		+ pcursor
		 		+ "\",\"count\":" + count + "},\"query\":\"query privateFeedsQuery($principalId: String, $pcursor: String, $count: Int) "
		 		+ "{\\n  privateFeeds(principalId: $principalId, pcursor: $pcursor, count: $count) "
		 		+ "{\\n    pcursor\\n    list {\\n      id\\n      thumbnailUrl\\n      poster\\n      workType\\n      type\\n      useVideoPlayer\\n      imgUrls\\n      imgSizes\\n      magicFace\\n      musicName\\n      caption\\n      location\\n      liked\\n      onlyFollowerCanComment\\n      relativeHeight\\n      timestamp\\n      width\\n      height\\n      counts {\\n        displayView\\n        displayLike\\n        displayComment\\n        __typename\\n      }\\n      user {\\n        id\\n        eid\\n        name\\n        avatar\\n        __typename\\n      }\\n      expTag\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n\"}";
		
		 
		String data2 = "{\"operationName\":\"privateFeedsQuery\",\"variables\":{\"principalId\":\""
			 		+ getUserId(mainPageUrl)
			 		+ "\",\"pcursor\":\""
			 		+ pcursor
			 		+ "\",\"count\":" + count + "},\"query\":\"query privateFeedsQuery($principalId: String, $pcursor: String, $count: Int) "
			 		+ "{\\n  privateFeeds(principalId: $principalId, pcursor: $pcursor, count: $count) "
			 		+ "{\\n    pcursor\\n    list {\\nid\\nthumbnailUrl\\nposter\\nworkType\\ntype\\nuseVideoPlayer\\ncaption\\nwidth\\nheight\\nuser {\\nid\\neid\\nname\\navatar\\n__typename\\n}\\nexpTag\\n__typename\\n}\\n__typename\\n}\\n}\\n\"}";
			
		String data3 = buildRequestData("privateFeedsQuery", getUserId(mainPageUrl), pcursor, count);
		System.out.println(data3);
		//装配请求并获取数据
		Document worklist = null;
		try {
			worklist = Jsoup.connect(requestUrl)
					.ignoreContentType(true)
					.headers(headersMap)
					.cookies(cookiesMap)
					.requestBody(data3)
					.post();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return worklist.body().text();
	}
	
	
	/**
	 * 将JsonElement类型的单个作品提取必要信息封装成Map
	 * @param working
	 * @return
	 */
	public static Map<String,String> parseWorking(JsonElement working){
		Map<String, String> workingMap = new HashMap<String, String>();
		
		JsonObject wj = working.getAsJsonObject();
		
		if(wj.get("id") instanceof JsonNull) {
			return null;
		}
		
		String id = wj.get("id").getAsString();
		String thumbnailUrl = wj.get("thumbnailUrl").getAsString();
		String poster = wj.get("poster").getAsString();
		String workType = wj.get("workType").getAsString();
		String type = wj.get("type").getAsString();
		String caption = wj.get("caption").getAsString();
		String user = wj.get("user").getAsJsonObject().toString();
		
		workingMap.put("id", id);
		workingMap.put("thumbnailUrl", thumbnailUrl);
		workingMap.put("poster", poster);
		workingMap.put("workType", workType);
		workingMap.put("type", type);
		workingMap.put("caption", caption);
		workingMap.put("user", user);
		
		return workingMap;
	}
	
	
	/**
	 * 解析作品列表
	 * @param workListJsonStr 作品列表的json字符串
	 * @param type 作品类型  public|private
	 * @return 作品列表(JsonArray)
	 */
	public static JsonArray parseWorkList(String workListJsonStr,String type){
		JsonParser jsonParser = new JsonParser();
		
		if("private".equals(type)) {
			return jsonParser.parse(workListJsonStr).getAsJsonObject()
					.get("data").getAsJsonObject()
					.get("privateFeeds").getAsJsonObject()
					.get("list").getAsJsonArray();
		}else if("public".equals(type)) {
			return jsonParser.parse(workListJsonStr).getAsJsonObject()
				.get("data").getAsJsonObject()
				.get("publicFeeds").getAsJsonObject()
				.get("list").getAsJsonArray();
		}else {
			return null;
		}
		
		
	}
	
	/**
	 * 解析作品列表的PCurser,该参数用于获取下一页作品列表
	 * @param workListJsonStr 作品列表的json字符串
	 * @return pcursor
	 */
	public static String parseWorkListPcursor(String workListJsonStr,String type){
		JsonParser jsonParsor = new JsonParser();
		
		if("private".equals(type)) {
			return jsonParsor.parse(workListJsonStr).getAsJsonObject()
					.get("data").getAsJsonObject()
					.get("privateFeeds").getAsJsonObject()
					.get("pcursor").getAsString();
		}else if("public".equals(type)) {
			return jsonParsor.parse(workListJsonStr).getAsJsonObject()
					.get("data").getAsJsonObject()
					.get("publicFeeds").getAsJsonObject()
					.get("pcursor").getAsString();
		}else {
			return null;
		}
		
		
	}
	
	
	
	/**
	 * 通过主播首页获取主播信息
	 * @param mainPageUrl 主播首页地址
	 * @param cookies cookie字符串
	 * @return String类型的主播信息(json字符串)
	 */
	public static String getUserInfoJsonStr(String mainPageUrl, String cookies) {
		//请求主播信息的请求地址
		String requestUrl = "https://live.kuaishou.com/graphql";
		
		//构建请求头
		Map<String,String> headersMap = buildHeaders(mainPageUrl);
		
		//构建cookie
		Map<String, String> cookiesMap = buildCookies(cookies);
		
		//构建请求数据
		 String data = "{\"operationName\":\"sensitiveUserInfoQuery\",\"variables\":{\"principalId\":\""
		 		+ getUserId(mainPageUrl)
		 		+ "\"},\"query\":\"query sensitiveUserInfoQuery($principalId: String) {\\n  sensitiveUserInfo(principalId: $principalId) {\\n    kwaiId\\n    userId\\n    constellation\\n    cityName\\n    countsInfo {\\n      fan\\n      follow\\n      photo\\n      liked\\n      open\\n      playback\\n      private\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n\"}";
		
		//装配请求并获取数据
		 Document userinfo = null;
		try {
			userinfo = Jsoup.connect(requestUrl)
					.ignoreContentType(true)
					.headers(headersMap)
					.cookies(cookiesMap)
					.requestBody(data)
					.post();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return userinfo.body().text();
	}
	
	
	/**
	 * 从字符串形式的cookieStr构建cookie Map
	 * @param cookieStr 字符串形式的cookie
	 * @return cookie Map
	 */
	public static Map<String,String> buildCookies(String cookieStr){
		//TODO 添加该方法的重载，对输入的字符串可以自定义各cookie之间的分隔符，当前分隔符为换行符\r \n \r\n
		String[] cookieArr = cookieStr.trim().split("[\\s]+");
		Map<String, String> cookieMap = new HashMap<>();
		for (String cookie : cookieArr) {
			cookieMap.put(cookie.trim().split("=")[0], cookie.trim().split("=")[1].trim());
		}
		return cookieMap;
	}
	
	
	/**
	 * 构建请求头信息，主要是设置referer为主播首页url
	 * @param mainPageUrl
	 * @return Map形式的头信息
	 */
	public static Map<String,String> buildHeaders(String mainPageUrl){
		//构建请求头信息
		
		String s = "Host: live.kuaishou.com#"
				+ "Connection: keep-alive#"
				+"Pragma: no-cache#"
				+"Cache-Control: no-cache#"
				+"Sec-Fetch-Dest: empty#"
				+"User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36#"
				+"content-type: application/json#"
				+"Origin: https://live.kuaishou.com#"
				+"Sec-Fetch-Site: same-origin#"
				+"Sec-Fetch-Mode: cors#"
				+"Referer: " + mainPageUrl + "#"
				+"Accept-Encoding: gzip, deflate, br#"
				+"Accept-Language: zh-CN,zh;q=0.9,en-GB;q=0.8,en-US;q=0.7,en;q=0.6";

		Map<String,String> headersMap = new HashMap<>();
		String[] headers = s.split("#");
		for (String header : headers) {
			headersMap.put(header.split(": ")[0].trim(),header.split(": ")[1].trim());
		}
				
		//System.out.println(headersMap);
		return headersMap;
	}
	
	
	
	public static String buildRequestData(String operation, String principalId,String pcursor,String count) {
		
		JsonParser parser = new JsonParser();
		
		JsonObject variables = parser.parse("{}").getAsJsonObject();
		variables.addProperty("principalId", principalId);
		variables.addProperty("pcursor",pcursor);
		variables.addProperty("count",Integer.parseInt(count));
		
		JsonObject requestBody = parser.parse("{}").getAsJsonObject();
		
		switch(operation) {
			case "publicFeedsQuery":
				requestBody.addProperty("operationName","publicFeedsQuery");
				requestBody.add("variables", variables);
				requestBody.addProperty("query","query publicFeedsQuery($principalId: String, $pcursor: String, $count: Int) {\n  publicFeeds(principalId: $principalId, pcursor: $pcursor, count: $count) {\n    pcursor\n    live {\n      user {\n        id\n        avatar\n        name\n        __typename\n      }\n      watchingCount\n      poster\n      coverUrl\n      caption\n      id\n      playUrls {\n        quality\n        url\n        __typename\n      }\n      quality\n      gameInfo {\n        category\n        name\n        pubgSurvival\n        type\n        kingHero\n        __typename\n      }\n      hasRedPack\n      liveGuess\n      expTag\n      __typename\n    }\n    list {\n      id\n      thumbnailUrl\n      poster\n      workType\n      type\n      useVideoPlayer\n      imgUrls\n      imgSizes\n      magicFace\n      musicName\n      caption\n      location\n      liked\n      onlyFollowerCanComment\n      relativeHeight\n      timestamp\n      width\n      height\n      counts {\n        displayView\n        displayLike\n        displayComment\n        __typename\n      }\n      user {\n        id\n        eid\n        name\n        avatar\n        __typename\n      }\n      expTag\n      __typename\n    }\n    __typename\n  }\n}\n");
				break;
			case "privateFeedsQuery":
				requestBody.addProperty("operationName","privateFeedsQuery");
				requestBody.add("variables", variables);
				requestBody.addProperty("query","query privateFeedsQuery($principalId: String, $pcursor: String, $count: Int) {\n  privateFeeds(principalId: $principalId, pcursor: $pcursor, count: $count) {\n    pcursor\n    list {\n      id\n      thumbnailUrl\n      poster\n      workType\n      type\n      useVideoPlayer\n      imgUrls\n      imgSizes\n      magicFace\n      musicName\n      caption\n      location\n      liked\n      onlyFollowerCanComment\n      relativeHeight\n      timestamp\n      width\n      height\n      counts {\n        displayView\n        displayLike\n        displayComment\n        __typename\n      }\n      user {\n        id\n        eid\n        name\n        avatar\n        __typename\n      }\n      expTag\n      __typename\n    }\n    __typename\n  }\n}\n");
				break;
		}
		System.out.println(requestBody.toString());
		return requestBody.toString();
	}

}
