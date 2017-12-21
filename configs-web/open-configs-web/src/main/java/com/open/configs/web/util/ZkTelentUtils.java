package com.open.configs.web.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.telnet.TelnetClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.open.configs.domain.ZkPathWatch;
import com.open.configs.domain.ZkServerGroupStat;
import com.open.configs.domain.Zkconn;

public class ZkTelentUtils {

	private static Logger LOG = LoggerFactory.getLogger(ZkTelentUtils.class);

	public static List<String> telentCommond(String ip, int port, String commond) throws IOException {

		TelnetClient client = new TelnetClient();
		client.setConnectTimeout(60000);
		client.setDefaultTimeout(600000);
		client.setSendBufferSize(409600);
		client.setReceiveBufferSize(409600);
		
		InputStream inputStream = null;
		OutputStream outputStream = null;
		List<String> lines = null;
		try {
			client.connect(ip, port);
			inputStream = client.getInputStream();
			outputStream = client.getOutputStream();

			outputStream.write(commond.getBytes());
			outputStream.write("\r".getBytes());
			outputStream.flush();

			lines = IOUtils.readLines(inputStream);
			//LOG.info(ip + ":" + port + "-" + commond + "===>" + lines);
		} catch (SocketException e) {
			LOG.error(ip + ":" + port + "-" + commond, e);
			throw e;
		} catch (IOException e) {
			LOG.error(ip + ":" + port + "-" + commond, e);
			throw e;
		} finally {
			IOUtils.closeQuietly(inputStream);
			IOUtils.closeQuietly(outputStream);
			try {
				client.disconnect();
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			}
		}

		return lines;
	}

	/**
	 * 查看cons
	 * 
	 * @param ip
	 * @param port
	 * @return
	 * @throws IOException
	 */
	public static Map<String, Zkconn> telentCons(String ip, int port) throws IOException {
		Map<String, Zkconn> zkconns = new HashMap<String, Zkconn>();

		List<String> lines = telentCommond(ip, port, "cons");
		Pattern p = Pattern.compile("(.*?)\\[1\\]\\((.*?)sid=(.*?),(.*)\\)");

		for (String line : lines) {
			Matcher m = p.matcher(line);
			if (m.find()) {
				String clientIp = m.group(1);
				String sid = m.group(3);
				zkconns.put(sid, Zkconn.create(clientIp, sid));
			}
		}

		return zkconns;
	}

	/**
	 * 查看wchp
	 * 
	 * @param ip
	 * @param port
	 * @return
	 * @throws IOException
	 */
	public static Map<String, ZkPathWatch> telentWchp(String ip, int port) throws IOException {
		Map<String, ZkPathWatch> zkWatchs = new HashMap<String, ZkPathWatch>();

		List<String> lines = telentCommond(ip, port, "wchp");

		String path = null;
		ZkPathWatch watch = null;

		for (String line : lines) {
			if (line.startsWith("/")) {
				if (StringUtils.isNotEmpty(path)) {
					zkWatchs.put(path, watch);
				}
				watch = new ZkPathWatch();
				path = line;
				watch.path = path;

			} else {
				if (StringUtils.isNotEmpty(line)) {
					watch.addWatchSid(line.trim());
				}
			}
		}

		return zkWatchs;
	}

	/**
	 * 查看ruok
	 * 
	 * @param ip
	 * @param port
	 * @return
	 * @throws IOException
	 */
	public static boolean telentRuok(String ip, int port) throws IOException {
		List<String> lines = telentCommond(ip, port, "ruok");

		for (String line : lines) {
			if ("imok".equals(line.trim())) {
				return true;
			}
		}
		return false;
	}

	public static void main(String[] args) throws IOException {

		String servers = "10.10.225.38:2181,10.10.225.38:2182,10.10.225.38:2183";
		servers = "172.17.3.182:2181,172.17.3.181:2181";
		servers = "192.168.229.53:2181,192.168.229.54:2181,192.168.229.55:2181";
		ZkServerGroupStat zkServerGroup = gropStat(servers);
		
		//System.out.println(zkServerGroup.getShowString());
	}
	
	/**
	 * zookeeper集群状态
	 * @param servers
	 * @return
	 */
	public static ZkServerGroupStat gropStat(String servers) {
		ZkServerGroupStat zkServerGroup = new ZkServerGroupStat();
		zkServerGroup.name = servers;
		zkServerGroup.servers = servers;

		int count = 3;
		for (String server : servers.split(",")) {
			String ip = server.split(":")[0];
			int port = Integer.parseInt(server.split(":")[1]);

			for (int i = 0; i < count; i++) {
				try {
					Map<String, Zkconn> conns = ZkTelentUtils.telentCons(ip, port);
					zkServerGroup.zkconns.put(server, conns);
					break;
				} catch (Exception e) {
					LOG.error(ip + ":" + port , e);
				}
			}
			for (int i = 0; i < count; i++) {
				try {
					Map<String, ZkPathWatch> watches = ZkTelentUtils.telentWchp(ip, port);
					zkServerGroup.zkWatches.put(server, watches);
					break;
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}
			}
		}

		zkServerGroup.gropStat();
		return zkServerGroup;
	}
}
