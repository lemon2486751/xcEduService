package com.xuecheng.manage_cms.controller;

import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_cms.service.CmsPageService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author lemon
 * @date 2021/1/29 18:15
 */
@Controller
public class CmsPreviewController extends BaseController {
    @Autowired
    private CmsPageService cmsPageService;

    @GetMapping("/cms/preview/{pageId}")
    public void preview(@PathVariable("pageId") String pageId) {
        String html = cmsPageService.getPageHtml(pageId);
        if (!StringUtils.isEmpty(html)) {
            try {
                ServletOutputStream outputStream = response.getOutputStream();
                outputStream.write(html.getBytes("utf-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
