package com.yl.Sertvlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yl.Utils.CodeUtils;
import com.yl.Utils.GetProperties;

/**
 *
 * @author Administrator
 */
//@WebServlet(urlPatterns = { "/files/*" })
@WebServlet(urlPatterns = { "/files1/*" })
public class FileServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private String ImgUrl = GetProperties.getImgUrlPath();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			String filepath = request.getRequestURI();
			String postfix = filepath.substring(filepath.lastIndexOf(".") + 1);
			if ("png".equals(postfix) || "jpg".equals(postfix)) {
//				filepath = filepath.substring(filepath.lastIndexOf("files/") + 6);
				filepath = filepath.substring(filepath.lastIndexOf("files1/") + 6);
				File file = new File(ImgUrl +"/" + filepath);
				FileInputStream m_fis = new FileInputStream(file);
				byte[] m_byte = new byte[1024];
				int m_len = 0;
				OutputStream m_os = response.getOutputStream();
				while ((m_len = m_fis.read(m_byte)) > 0) {
					m_os.write(m_byte);
				}
				m_os.flush();
				m_fis.close();
				m_os.close();
			} else if ("mp3".equals(postfix)) {
//				filepath = filepath.substring(filepath.lastIndexOf("files/") + 6);
				filepath = filepath.substring(filepath.lastIndexOf("files1/") + 6);
				File file = new File(ImgUrl +"/" + filepath);
				String filename = file.getName();
				InputStream fis = new BufferedInputStream(new FileInputStream(ImgUrl +"/" + filepath));
				byte[] buffer = new byte[fis.available()];
				fis.read(buffer);
				// 设置response的Header
				response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes()));
				response.addHeader("Content-Length", "" + file.length());
				response.setContentLength((int) file.length());
				OutputStream toClient = response.getOutputStream();
				toClient.write(buffer);
				toClient.flush();
				fis.close();
				toClient.close();
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static void main(String[] args) {

	}

}
