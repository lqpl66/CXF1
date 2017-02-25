package com.yl.httpTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


import com.yl.Utils.MD5Utils;

public class HttpUtils {
	public static String sendPost(String urlParam, Map<String, Object> params, String charset) {
		StringBuffer resultBuffer = null;
		// 构建请求参数
		StringBuffer sbParams = new StringBuffer();
		if (params != null && params.size() > 0) {
			for (Entry<String, Object> e : params.entrySet()) {
				sbParams.append(e.getKey());
				sbParams.append("=");
				sbParams.append(e.getValue());
				sbParams.append("&");
			}
		}
		String s = sbParams.substring(0, sbParams.length() - 1);
		HttpURLConnection con = null;
		OutputStreamWriter osw = null;
		BufferedReader br = null;
		// 发送请求
		try {
			URL url = new URL(urlParam);
//			URL url = new URL(urlParam+"?"+s);
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestProperty("accept", "*/*");
			con.setRequestProperty("connection", "Keep-Alive");
			con.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
//			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			if (sbParams != null && sbParams.length() > 0) {
				osw = new OutputStreamWriter(con.getOutputStream(), charset);
				osw.write(sbParams.substring(0, sbParams.length() - 1));
				osw.flush();
			}
			// 读取返回内容
			resultBuffer = new StringBuffer();
//			int contentLength = Integer.parseInt(con.getHeaderField("Content-Length"));
//			if (contentLength > 0) {
				br = new BufferedReader(new InputStreamReader(con.getInputStream(), charset));
				String temp;
				while ((temp = br.readLine()) != null) {
					resultBuffer.append(temp);
				}
//			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (osw != null) {
				try {
					osw.close();
				} catch (IOException e) {
					osw = null;
					throw new RuntimeException(e);
				} finally {
					if (con != null) {
						con.disconnect();
						con = null;
					}
				}
			}
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					br = null;
					throw new RuntimeException(e);
				} finally {
					if (con != null) {
						con.disconnect();
						con = null;
					}
				}
			}
		}
		return resultBuffer.toString();
	}

	public static void main(String args[]) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("mobile", "18501755867");
		params.put("pwd","123123");
		params.put("type", "1");
		params.put("devicetoken", "861189032357625");
		String s =sendPost("http://120.26.247.12:8080/parkingapp/login/login", params, "UTF-8");
		System.out.println(s);
//		sendPost("http://120.26.247.12:8080/parkingapp/api/user/login", params, "UTF-8");
//		 /api/user/login 

	}
}
