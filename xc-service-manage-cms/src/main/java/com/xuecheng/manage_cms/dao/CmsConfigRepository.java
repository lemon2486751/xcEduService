package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author lemon
 * @date 2021/1/27 11:22
 */
public interface CmsConfigRepository extends MongoRepository<CmsConfig,String> {
}
