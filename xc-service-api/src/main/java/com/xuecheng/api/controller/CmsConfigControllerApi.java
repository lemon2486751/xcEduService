package com.xuecheng.api.controller;

import com.xuecheng.framework.domain.cms.CmsConfig;

/**
 * @author lemon
 * @date 2021/1/27 11:20
 */
public interface CmsConfigControllerApi {
    /**
     * 根据id查询config
     * @param pageId
     * @return
     */
    CmsConfig getModel(String pageId);
}
