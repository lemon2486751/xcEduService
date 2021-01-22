package com.xuecheng.api.controller;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.request.QueryPageRequest;
import com.xuecheng.framework.model.response.ResponseResult;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.web.bind.annotation.RequestBody;

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

    /**
     * 新增页面
     * @param cmsPage
     * @return
     */
    CmsPageResult add(@RequestBody CmsPage cmsPage);

    /**
     * 根据id查询页面
     * @param id
     * @return
     */
    CmsPage get(String id);

    /**
     * 修改页面
     * @param id
     * @param cmsPage
     * @return
     */
    CmsPageResult update(String id,CmsPage cmsPage);

    /**
     * 删除页面
     * @param id
     * @return
     */
    ResponseResult delete(String id);
}
