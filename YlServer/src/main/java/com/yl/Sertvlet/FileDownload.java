package com.yl.Sertvlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yl.Utils.CodeUtils;
import com.yl.Utils.GetProperties;

@WebServlet(urlPatterns = { "/download/*" })
public class FileDownload extends HttpServlet {
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
			if ("png".equals(postfix) || "jpg".equals(postfix)||"mp3".equals(postfix)) {
				filepath = filepath.substring(filepath.lastIndexOf("download/") + 9);
				String filename = CodeUtils.getfileName();
				response.setHeader("Content-Disposition", "attachment;filename="+filename+"."+postfix);  
				File file = new File(ImgUrl  + filepath);
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
			}

		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static void main(String[] args) {

	}

}
