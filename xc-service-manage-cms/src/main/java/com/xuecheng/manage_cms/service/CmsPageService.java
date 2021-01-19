package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.request.QueryPageRequest;
import com.xuecheng.framework.model.response.*;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lemon
 * @date 2021/1/19 22:16
 */
@Service
public class CmsPageService {
    @Autowired
    private CmsPageRepository repository;

    /**
     * @param page 当前页码，从1开始
     * @param size
     * @param request
     * @return
     */
    public QueryResponseResult findPage(int page, int size, QueryPageRequest request) {
        if (page <= 0) {
            page = 1;
        }
        page = page - 1;
        if (size <= 0) {
            size = 10;
        }
        Pageable pageable = new PageRequest(page, size);
        Page<CmsPage> all = repository.findAll(pageable);
        QueryResult<CmsPage> result = new QueryResult<>();
        result.setList(all.getContent());
        result.setTotal(all.getTotalElements());
        return new QueryResponseResult(CommonCode.SUCCESS, result);
    }
}
