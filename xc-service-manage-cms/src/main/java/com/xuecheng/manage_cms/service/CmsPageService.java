package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.request.QueryPageRequest;
import com.xuecheng.framework.model.response.*;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author lemon
 * @date 2021/1/19 22:16
 */
@Service
public class CmsPageService {
    @Autowired
    private CmsPageRepository repository;

    /**
     * @param page    当前页码，从1开始
     * @param size
     * @param request
     * @return
     */
    public QueryResponseResult findPage(int page, int size, QueryPageRequest request) {
        //创建条件匹配器
        //设置页面别名为模糊匹配
        ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());
        //创建匹配值
        CmsPage cmsPage = new CmsPage();
        if (StringUtils.isNotEmpty(request.getSiteId())) {
            //设置站点Id
            cmsPage.setSiteId(request.getSiteId());
        }
        if (StringUtils.isNotEmpty(request.getPageAliase())) {
            //设置页面别名
            cmsPage.setPageAliase(request.getPageAliase());
        }
        //创建条件实例
        Example<CmsPage> example = Example.of(cmsPage, matcher);
        //分页条件
        if (page <= 0) {
            page = 1;
        }
        page = page - 1;
        if (size <= 0) {
            size = 10;
        }
        Pageable pageable = new PageRequest(page, size);
        Page<CmsPage> all = repository.findAll(example, pageable);
        QueryResult<CmsPage> result = new QueryResult<>();
        result.setList(all.getContent());
        result.setTotal(all.getTotalElements());
        return new QueryResponseResult(CommonCode.SUCCESS, result);
    }

    /**
     * 新增页面
     *
     * @param cmsPage
     * @return
     */
    public CmsPageResult add(CmsPage cmsPage) {
        if (cmsPage == null) {
            cmsPage = new CmsPage();
        }
        //根据唯一索引查询页面是否存在
        CmsPage one = repository.findCmsPageByPageNameAndSiteIdAndPageWebPath
                (cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if (one == null) {
            cmsPage.setPageId(null);
            CmsPage save = repository.save(cmsPage);
            return new CmsPageResult(CommonCode.SUCCESS, cmsPage);
        } else {
            return new CmsPageResult(CommonCode.FAIL, null);
        }

    }

    /**
     * 根据id查询页面信息
     *
     * @param id
     * @return
     */
    public CmsPage get(String id) {
        Optional<CmsPage> optional = repository.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        } else
            return null;
    }

    /**
     * 修改页面
     *
     * @param id
     * @param cmsPage
     * @return
     */
    public CmsPageResult update(String id, CmsPage cmsPage) {
        //查询页面是否存在
        CmsPage one = get(id);
        if (one != null) {
            //更新模板id
            one.setTemplateId(cmsPage.getTemplateId());
            //更新所属站点
            one.setSiteId(cmsPage.getSiteId());
            //更新页面别名
            one.setPageAliase(cmsPage.getPageAliase());
            //更新页面名称
            one.setPageName(cmsPage.getPageName());
            //更新访问路径
            one.setPageWebPath(cmsPage.getPageWebPath());
            //更新物理路径
            one.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
            CmsPage save = repository.save(one);
            return new CmsPageResult(CommonCode.SUCCESS, save);
        } else
            return new CmsPageResult(CommonCode.FAIL, null);
    }

    /**
     * 删除页面
     *
     * @param id
     * @return
     */
    public ResponseResult delete(String id) {
        //查询页面是否存在
        CmsPage one = get(id);
        if (one != null) {
            //执行删除
            repository.deleteById(id);
            return new ResponseResult(CommonCode.SUCCESS);
        } else
            return new ResponseResult(CommonCode.FAIL);
    }
}
