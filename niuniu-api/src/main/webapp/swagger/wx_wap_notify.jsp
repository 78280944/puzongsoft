<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="org.apache.http.HttpResponse" %>
<%@ page import="org.apache.http.client.entity.UrlEncodedFormEntity" %>
<%@ page import="org.apache.http.client.methods.HttpPost" %>
<%@ page import="org.apache.http.impl.client.DefaultHttpClient" %>
<%@ page import="org.apache.http.message.BasicNameValuePair" %>
<%@ page import="org.apache.http.util.EntityUtils" %>
<%@ page import="com.colotnet.util.ConfigUtils" %>
<%@ page import="com.alibaba.fastjson.JSON" %>
<%@ page import="org.apache.http.entity.StringEntity" %>
<%@ page import="com.alibaba.fastjson.JSONObject" %>

{"status":"OK", "remark","Test.."}
<%
	 request.setCharacterEncoding("UTF-8");
	 System.out.println("----------- Payment system --> 异步通知URL，网关接口参数(notifyUrl) .....  ------>  wx_wap_notify.jsp");
	 Enumeration<?> temp = request.getParameterNames();
     String orderNo = "";
     String payNo = "";
     String transAmt = "";
     String orderDate = "";
     String accNo = "";
     String token ="";
     String respCode ="";
     String respDesc = "";
     Map<String, Object> data = new HashMap<String, Object>();
	 if (null != temp) {
	 	 while (temp.hasMoreElements()) {
		 	String key = (String) temp.nextElement();
		 	String value = request.getParameter(key);
		    System.out.println(key + " -> " + value);
		  
	         if (key.equals("orderNo")){
	        	 orderNo = value;
	        	 data.put(key, value);
	         }
	         else if (key.equals("payNo")){
	        	 payNo = value;
	        	 data.put(key, value);
	        	 }
	         else if (key.equals("transAmt")){
	        	 transAmt = value; 
	        	 data.put(key, value);
	        	 }
	         else if (key.equals("orderDate")){
	        	 orderDate = value; 
	        	 data.put(key, value);
	        	 }
	         else if (key.equals("accNo")){
	        	 accNo = value; 
	        	 data.put(key, value);
	        	 }
	         else if (key.equals("token")){
	        	 token = value; 
	        	 data.put(key, value);
	        	 }
	         else if (key.equals("respCode")){
	        	 respCode = value; 
	        	 data.put(key, value);
	        	 }
	         else if (key.equals("respDesc")){
	        	 respDesc = value;	
	        	 data.put(key, value);
	        	 } 
		 
	 	 }
	    System.out.println("----------------------------- 异步通知   end ---------------------------------------------");
	    DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost postMethod = new HttpPost(ConfigUtils.getProperty("resultUrl"));
        JSONObject json = new JSONObject(data);
        StringEntity se = new StringEntity(json.toString());
        se.setContentEncoding("UTF-8");
        se.setContentType("application/json");//发送json数据需要设置contentType
        postMethod.setEntity(se);
        HttpResponse resp = httpClient.execute(postMethod);
        String str = EntityUtils.toString(resp.getEntity(), "UTF-8");
        System.out.println("返回结果："+str);
        JSONObject respJSONObject = JSON.parseObject(str);
        String returnStr = respJSONObject.getString("data");
	 	if (returnStr.equals("success")){
	 		 //成功接收通知数据，返回success
	 		 System.out.println("success");
		     response.getWriter().print("success"); 
	 	}
	 }
	
%>

