package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author lemon
 * @date 2021/1/19 22:19
 */
public interface CmsPageRepository extends MongoRepository<CmsPage,String> {
}
