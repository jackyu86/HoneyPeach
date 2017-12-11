package org.learn.open.api.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.learn.open.service.TestEhcacheService;
import org.learn.open.StartDubboServicePublish;
import org.learn.open.api.UserInfoService;
import org.learn.open.entity.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.github.pagehelper.PageInfo;
import javassist.NotFoundException;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
////指定我们SpringBoot工程的Application启动类

///由于是Web项目，Junit需要模拟ServletContext，因此我们需要给我们的测试类加上@WebAppConfiguration。
@WebAppConfiguration
@SpringBootTest(classes = StartDubboServicePublish.class)
public class UserInfoServiceTest {
	
	@Resource(name="userinfoservice-0.0.1")
	protected UserInfoService uis;
	
	@Autowired
	protected TestEhcacheService tes;
	
	@Test
//	@Transactional
    public void testCache() throws NotFoundException{
		PageInfo<UserInfo> pi = uis.searchName("admin");
		System.out.println(pi.getSize());
    	//存入两条数据.
    	UserInfo userinfo = uis.findByUsername("admin");
    	userinfo = uis.findByUsername("admin");
    	System.out.println(userinfo.getName());
    	userinfo = uis.findByUsername("admin");
    	System.out.println(userinfo.getName());
    	
    	userinfo = uis.findById(userinfo.getUid());
    	System.out.println(userinfo.getName());
    	userinfo = uis.findById(userinfo.getUid());
    	System.out.println(userinfo.getName());
    	userinfo.setName(userinfo.getName()+"_updated");
    	uis.update(userinfo);
    	userinfo = uis.findById(userinfo.getUid());
    	System.out.println(userinfo.getName());
    }
	
	@Test
	public void testEhcacheService(){
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("abc", "aaaaaaaaaaaaa");
		params.put("def", "bbbbbbbbbbbbb");
		params.put("ghi", "ccccccccccccc");
		
		System.out.println(tes.list(params));
		System.out.println(tes.list(params));
		params.put("abc", "bbbbbbbbbbbbb");
		params.put("def", "ddddddddddddd");
		params.put("ghi", "iiiiiiiiiiiii");
		System.out.println(tes.list(params));
		System.out.println(tes.list(params));
	}
}
