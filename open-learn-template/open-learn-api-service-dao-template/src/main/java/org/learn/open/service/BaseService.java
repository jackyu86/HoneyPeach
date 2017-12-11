package org.learn.open.service;

import org.learn.open.utils.LocaleMessageSourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseService {
	@Autowired
	protected LocaleMessageSourceService messageServ;
	
	protected Logger log = LoggerFactory.getLogger(this.getClass().getName());
}
