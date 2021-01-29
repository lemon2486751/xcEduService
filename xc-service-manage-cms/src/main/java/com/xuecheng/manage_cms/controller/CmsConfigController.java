package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.controller.CmsConfigControllerApi;
import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.manage_cms.service.CmsPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lemon
 * @date 2021/1/27 11:21
 */
@RestController
@RequestMapping("/cms/config")
public class CmsConfigController implements CmsConfigControllerApi {
    @Autowired
    private CmsPageService cmsPageService;

    @Override
    @GetMapping("/getModel/{id}")
    public CmsConfig getModel(@PathVariable("id") String pageId) {
        return cmsPageService.findCmsConfigById(pageId);
    }
}
