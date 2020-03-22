package org.wcd.demo.servlet.test;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;

public class TestUrl {

	public static void main(String[] args) {
		try {
			URL url = new URL("https://jsmov2.a.yximgs.com/upic/2020/03/21/12/BMjAyMDAzMjExMjQyMTBfMTU3MTc1MTUxMV8yNTMxNjk1OTA5NV8yXzM=_b_B45a911cdca3de518c5dfdfd393b0f5f9.mp4");
			BufferedInputStream bis = new BufferedInputStream(url.openStream());
			//ServletOutputStream os = response.getOutputStream();
			byte[] b = new byte[1024*100];
			while(bis.read(b) != 0) {
				System.out.println(new String(b));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
