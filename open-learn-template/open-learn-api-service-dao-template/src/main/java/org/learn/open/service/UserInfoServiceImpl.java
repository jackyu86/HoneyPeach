package org.learn.open.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import javassist.NotFoundException;
import org.learn.open.api.UserInfoService;
import org.learn.open.entity.UserInfo;
import org.learn.open.mapper.UserInfoMapper;
import org.learn.open.repository.UserInfoRepository;
import org.learn.open.utils.BeanMapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(value="userinfoservice-0.0.1")
public class UserInfoServiceImpl implements UserInfoService{
	
	private static final String CACHE_KEY = "userInfo";
	private static final String DEMO_CACHE_NAME = "demo";
	@Autowired
	private UserInfoRepository userInfoRepository;
	
	@Autowired
	private UserInfoMapper userInfoMapper;
	
	@Cacheable(value=DEMO_CACHE_NAME,key="'userInfo_'+#username")
	public UserInfo findByUsername(String username) {
//		System.out.println("UserInfoServiceImpl.findByUsername()");
//		return userInfoRepository.findByUsername(username);
		return BeanMapUtils.transMap2Bean(userInfoMapper.findByUsername(username), UserInfo.class);
	}
	
	@Override
	@CacheEvict(value = DEMO_CACHE_NAME,key = "'demoInfo_'+#id")
	public void delete(Long id) {
		userInfoRepository.delete(id);
	}

	@Override
	@CachePut(value = DEMO_CACHE_NAME,key = "'userInfo_'+#updated.getUid()")
	@Transactional(rollbackFor=Exception.class)
	public UserInfo update(UserInfo updated) throws NotFoundException {
		// TODO Auto-generated method stub
		UserInfo ui = userInfoRepository.findOne(updated.getUid());
		if(ui==null){
			throw new NotFoundException("not find "+updated.getUid());
		}
		ui.setName(updated.getName());
		ui.setState(updated.getState());
		userInfoRepository.save(ui);
//		if(true){
//			throw new NotFoundException("故意外抛异常测试事务");
//		}
		return ui;
	}

	@Override
	@Cacheable(value=DEMO_CACHE_NAME,key="'userInfo_'+#id")
	public UserInfo findById(Long id) {
		// TODO Auto-generated method stub
		return userInfoRepository.findOne(id);
	}

	@Override
	@CacheEvict(value=DEMO_CACHE_NAME,key=CACHE_KEY)
	public UserInfo save(UserInfo userInfo) {
		// TODO Auto-generated method stub
		return userInfoRepository.save(userInfo);
	}

	@Override
	public PageInfo<UserInfo> searchName(String username) {
		PageHelper.startPage(10, 1);
		return BeanMapUtils.transListMap2Bean(userInfoMapper.pageList(username), UserInfo.class);
	}
	
}
