package com.brick.biReport.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
public class CreateHtml {
	public static void writeHtml(String path, String name, String data)throws IOException {
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		FileOutputStream fos = null;
		try { 
			fos = new FileOutputStream(file + File.separator + name);
			OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");
			out.write(data);
			out.flush();
			out.close();	
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	

}
