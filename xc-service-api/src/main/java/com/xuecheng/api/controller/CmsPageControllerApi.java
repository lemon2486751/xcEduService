package com.xuecheng.api.controller;

import com.xuecheng.framework.domain.request.QueryPageRequest;
import com.xuecheng.framework.model.response.ResponseResult;
import org.springframework.data.querydsl.QPageRequest;

/**
 * @author lemon
 * @date 2021/1/19 21:47
 */
public interface CmsPageControllerApi {
    /**
     * 分页查询页面信息
     * @param page
     * @param size
     * @param request
     * @return
     */
    ResponseResult findPage(int page, int size, QueryPageRequest request);
}
