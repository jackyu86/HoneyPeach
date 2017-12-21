package com.open.configs.web.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketClient {
	private Socket socket;

	public SocketClient(String ip, int port) throws UnknownHostException, IOException {
		this.socket = new Socket(ip, port);
		this.socket.setSoTimeout(120000);
	}

	public String send(String msg) throws IOException {
		BufferedReader in = null;
		PrintWriter out = null;
		StringBuffer sb = new StringBuffer();
		try {
			in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

			out = new PrintWriter(this.socket.getOutputStream(), true);

			out.println(msg);
			out.flush();

			char[] cbuf = new char[409600];
			int end = 0;
			while ((end = in.read(cbuf)) != -1) {
				sb.append(cbuf, 0, end);
			}
			String str = sb.toString();
			System.out.println("======================================="  + sb.length());
			return str;
		} catch (IOException e) {
			System.out.println("======================================="+ sb.length() + "\r\n"  + sb.toString());
			throw e;
		}finally {
			if (out != null) {
				out.close();
			}
			if (in != null)
				in.close();
		}
	}

	public void close() {
		try {
			if ((this.socket != null) && (!this.socket.isClosed()))
				this.socket.close();
		} catch (IOException e) {
		}
	}

	public static void main(String[] args) {
		//String[] cmds = { "conf", "cons", "dump", "envi", "reqs", "ruok", "stat", "wchs\t", "wchc", "wchp" };
		String[] cmds = { "conf", "cons", "envi", "reqs", "ruok", "stat", "wchs\t","wchc", "wchp" };

		SocketClient client = null;
		try {
			long time0 = System.currentTimeMillis();
			for (int i = 0; i < cmds.length; i++) {
				System.out.println("=====>命令:" + cmds[i]);
				client = new SocketClient("192.168.229.55", 2181);
				String res = client.send(cmds[i]);
				System.out.println("命令:" + cmds[i] + "\n返回：" + res);
			}
			long time1 = System.currentTimeMillis();
			System.out.println("耗时：" + (time1 - time0) + "ms");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (client != null)
				client.close();
		}
	}
}