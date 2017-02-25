package com.yl.Utils;

import java.io.File;

import org.apache.log4j.Logger;

public class FileUtils {
	private static Logger log = Logger.getLogger(FileUtils.class);

	public static boolean deleteFile(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		try {
			// 路径为文件且不为空则进行删除
			if (file.isFile() && file.exists()) {
				file.delete();
				flag = true;
			}
		} catch (Exception e) {
			log.error("图片删除失败:" , e);
		}
		return flag;
	}

}
