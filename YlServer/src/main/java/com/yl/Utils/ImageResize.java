package com.yl.Utils;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import com.yl.webRestful.ScenicWebRestful;

public class ImageResize {
	private static Logger log = Logger.getLogger(ImageResize.class);
	private static int sourceWidth;
	private static int sourceHeight;
	private static int destWidth;
	private static int destHeght;

	// public static String

	public static String getImgeResize(String sourceFilePath, String destFilePath, String type, String fileName) {
		File sourceFile = new File(sourceFilePath + fileName);
		try {
			Image img = ImageIO.read(sourceFile);
			sourceWidth = img.getWidth(null);
			sourceHeight = img.getHeight(null);
			if (type != null) {
				String[] strs = type.split(",");
				switch (strs[0]) {
				case "H":
					destHeght = Integer.parseInt(strs[1]);
					destWidth = (int) (destHeght * sourceWidth / sourceHeight);
					break;
				case "W":
					destWidth = Integer.parseInt(strs[1]);
					destHeght = (int) (destWidth * sourceHeight / sourceWidth);
					break;
				case "HW":
					destHeght = Integer.parseInt(strs[1]);
					destWidth = Integer.parseInt(strs[2]);
					break;
				default:
					;
				}
			}
			resize(destWidth, destHeght, sourceFilePath, destFilePath, fileName);
		} catch (IOException e) {
			log.error("图片初始化失败：" , e);
		}
		return null;
	}

	public static void resize(int w, int h, String sourceFilePath, String destFilePath, String fileName) {
		try {
			File sourceFile1 = new File(sourceFilePath + fileName);// 读入文件
			Image img1 = ImageIO.read(sourceFile1);
			// SCALE_SMOOTH 的缩略算法 生成缩略图片的平滑度的 优先级比速度高 生成的图片质量比较好 但速度慢
			BufferedImage image1 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
			image1.getGraphics().drawImage(img1, 0, 0, w, h, null); // 绘制缩小后的图
			File dfp = new File(destFilePath);
			if (!dfp.exists()) {
				dfp.mkdir();
			}
			ImageIO.write(image1, "jpg", new File(destFilePath + fileName));
			// File dfpu = new File(destFilePath+fileName);
			// FileOutputStream out = new FileOutputStream(dfpu); // 输出到文件流
			// out.write();
			// out.close();
		} catch (Exception e) {
			log.error("图片压缩异常:" , e);
		}
	}

	public static void resize1(String sourceFilePath, String destFilePath, String fileName) {
		try {
			File sourceFile1 = new File(sourceFilePath + fileName);// 读入文件
			Image img1 = ImageIO.read(sourceFile1);
			BufferedImage img2 = ImageIO.read(sourceFile1);
			// SCALE_SMOOTH 的缩略算法 生成缩略图片的平滑度的 优先级比速度高 生成的图片质量比较好 但速度慢
//			BufferedImage image1 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
//			image1.getGraphics().drawImage(img1, 0, 0, w, h, null); // 绘制缩小后的图
			File dfp = new File(destFilePath);
			if (!dfp.exists()) {
				dfp.mkdir();
			}
			ImageIO.write(img2, "jpg", new File(destFilePath + fileName));
			
			
//			FileInputStream in = new FileInputStream(sourceFilePath + fileName);
//			byte[] m_byte = new byte[1024];
//			int m_len = 0;
//			OutputStream out = new FileOutputStream(destFilePath + fileName);
//			while ((m_len = in.read(m_byte)) > 0) {
//				out.write(m_byte);
//			}
//			out.flush();
//			out.close();
			
		} catch (Exception e) {
			log.error("图片压缩异常:" , e);
		}
	}

	public static void main(String[] args) {
		String type1 = GetProperties.getevaluateTypeH();
		String type2 = GetProperties.getevaluateTypeW();
		String type3 = GetProperties.getevaluateTypeW1();
		// String d1 = getImgeResize("E:/usr/local/files/",
		// "E:/usr/local/files/", type2, "2.jpg");
		resize1("E:/usr/local/files/", "E:/usr/local/files/test/", "11.jpg");
		// String d2 = getImgeResize("E:/usr/local/files/",
		// "E:/usr/local/files/Minfile/", type2,"22.jpg");
		 String d3 = getImgeResize("E:/usr/local/files/",
		 "E:/usr/local/files/test/", type3,"11.jpg");
	}

}
