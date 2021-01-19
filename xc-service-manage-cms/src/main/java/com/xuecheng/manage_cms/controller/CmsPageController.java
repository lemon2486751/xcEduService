package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.controller.CmsPageControllerApi;
import com.xuecheng.framework.domain.request.QueryPageRequest;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.service.CmsPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lemon
 * @date 2021/1/19 22:15
 */
@RestController
@RequestMapping("/cms/page")
public class CmsPageController implements CmsPageControllerApi {
    @Autowired
    private CmsPageService service;

    @Override
    @GetMapping("/findPage/{page}/{size}")
    public QueryResponseResult findPage(@PathVariable("page") int page, @PathVariable("size") int size, QueryPageRequest request) {
        return service.findPage(page,size,request);
    }
}
