package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author lemon
 * @date 2021/1/29 17:11
 */
public interface CmsTemplateRepository extends MongoRepository<CmsTemplate,String> {
}
