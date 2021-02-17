package com.xuecheng.manage_cms_client.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author lemon
 * @date 2021/1/19 22:19
 */
public interface CmsSiteRepository extends MongoRepository<CmsSite,String> {
}
