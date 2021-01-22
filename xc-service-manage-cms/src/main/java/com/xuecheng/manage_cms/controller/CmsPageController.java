package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.controller.CmsPageControllerApi;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.request.QueryPageRequest;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.service.CmsPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    //GetMapping不支持@RequestBody注解
    public QueryResponseResult findPage(@PathVariable("page") int page, @PathVariable("size") int size, QueryPageRequest request) {
        return service.findPage(page, size, request);
    }

    @Override
    @PostMapping("/add")
    public CmsPageResult add(@RequestBody CmsPage cmsPage) {
        return service.add(cmsPage);
    }

    @Override
    @GetMapping("/get/{id}")
    public CmsPage get(@PathVariable("id") String id) {
        return service.get(id);
    }

    @Override
    @PutMapping("/update/{id}")
    public CmsPageResult update(@PathVariable("id") String id, @RequestBody CmsPage cmsPage) {
        return service.update(id,cmsPage);
    }

    @Override
    @DeleteMapping("/delete/{id}")
    public ResponseResult delete(@PathVariable("id") String id) {
        return service.delete(id);
    }
}
