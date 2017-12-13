/*

import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.lang.StringEscapeUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

*//**
 * @ClassName: ElasticSearchClient
 * @Description: es客户端
 * @author jack-yu
 * @date 2015年11月26日 下午5:23:12
 * 
 *//*
public class ElasticSearchClient {
	public static final String ES_HOST = loadRedisHostConfig();

	public static final int ES_PORT = Integer.parseInt(loadRedisPortConfig());

	static String loadRedisHostConfig() {
		try {
			InputStream is = ElasticSearchClient.class.getClassLoader()
					.getResourceAsStream("es.properties");
			Properties properties = new Properties();
			properties.load(is);
			return properties.getProperty("ES_HOST");

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	static String loadRedisPortConfig() {
		try {
			InputStream is = ElasticSearchClient.class.getClassLoader()
					.getResourceAsStream("es.properties");
			Properties properties = new Properties();
			properties.load(is);
			return properties.getProperty("ES_PORT");

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static class LazyHolder {
		static	Settings settings = ImmutableSettings.settingsBuilder()
				.put("cluster.name", "product ")
				.put("client.transport.sniff", true).build();
		private static final Client CLIENT = new TransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(
						ES_HOST, ES_PORT));
	}

	private ElasticSearchClient() {
	}

	public static final Client  getInstance() {
		return LazyHolder.CLIENT;
	}

	public static void main(String[] args) {
		
		 * Settings settings =
		 * ImmutableSettings.settingsBuilder().put("cluster.name",
		 * "product ").put("client.transport.sniff", true).build(); Client
		 * client = new TransportClient(settings).addTransportAddress(new
		 * InetSocketTransportAddress("192.168.1.220", 9300));
		 
		Client client = ElasticSearchClient.getInstance();
		
		 * GetResponse
		 * getResponse=client.prepareGet("order_v1","match_all","").execute
		 * ().actionGet();
		 	
      QueryBuilder queryBuilder = new BoolQueryBuilder()
       // .must(new QueryStringQueryBuilder(queryString)
                .must(new TermQueryBuilder("phone", "15035278802"))
                .must(new TermQueryBuilder("PromoState", new Random().nextInt(4) + 1))
                .must(new RangeQueryBuilder("shoping-time").
                        from(randomDate("2009-01-10 00:00:00.000", "2010-01-01 00:00:00.000").getTime()).
                        to(randomDate("2010-01-10 00:00:00.000", "2015-01-01 00:00:00.000").getTime()));
        
		
		SearchResponse response = client
				.prepareSearch("order_v1")
				.setTypes("order")
				 .addFields("channel", "phone")
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
				.setQuery(new QueryStringQueryBuilder("channel:"+  StringEscapeUtils.escapeJavaScript("天猫-茵曼旗舰店{}测试店铺不能使用")).defaultField("channel"))
				.execute().actionGet();
		SearchHits hits = response.getHits();
		Iterator<SearchHit> its =  hits.iterator();
		while(its.hasNext()){
			SearchHit hit   =  its.next();
			System.out.println(hit.getFields().get("channel").getValue());
		}
		System.out.println(hits.getHits().length);
		
		client.close();
	}
	

}
*/