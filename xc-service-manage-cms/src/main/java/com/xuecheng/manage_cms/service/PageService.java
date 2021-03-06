package com.xuecheng.manage_cms.service;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.request.QueryPageRequest;
import com.xuecheng.framework.exception.CustomException;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.config.RabbitmqConfig;
import com.xuecheng.manage_cms.dao.CmsConfigRepository;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author lemon
 * @date 2021/1/19 22:16
 */
@Service
public class PageService {
    @Autowired
    private CmsPageRepository cmsPageRepository;

    @Autowired
    private CmsConfigRepository cmsConfigRepository;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private CmsTemplateRepository cmsTemplateRepository;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    @Autowired
    private RabbitTemplate rabbitTemplate;

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
        Page<CmsPage> all = cmsPageRepository.findAll(example, pageable);
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
        CmsPage one = cmsPageRepository.findCmsPageByPageNameAndSiteIdAndPageWebPath
                (cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if (one == null) {
            cmsPage.setPageId(null);
            CmsPage save = cmsPageRepository.save(cmsPage);
            return new CmsPageResult(CommonCode.SUCCESS, cmsPage);
        } else {
            throw new CustomException(CmsCode.CMS_ADDPAGE_EXISTS);
        }

    }

    /**
     * 根据id查询页面信息
     *
     * @param pageId
     * @return
     */
    public CmsPage findByPageId(String pageId) {
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
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
        CmsPage one = findByPageId(id);
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
            //更新数据路径
            one.setDataUrl(cmsPage.getDataUrl());
            CmsPage save = cmsPageRepository.save(one);
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
        CmsPage one = findByPageId(id);
        if (one != null) {
            //执行删除
            cmsPageRepository.deleteById(id);
            return new ResponseResult(CommonCode.SUCCESS);
        } else
            return new ResponseResult(CommonCode.FAIL);
    }

    //查询页面配置信息
    public CmsConfig findCmsConfigById(String pageId) {
        Optional<CmsConfig> optional = cmsConfigRepository.findById(pageId);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    //页面静态化
    public String getPageHtml(String pageId) {
        //获取页面模型数据
        Map model = this.getModelByPageId(pageId);
        if (model == null) {
            throw new CustomException(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }
        //获取页面模板
        String template = this.getTemplateByPageId(pageId);
        if (StringUtils.isEmpty(template)) {
            throw new CustomException(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        //执行静态化
        String html = this.generateHtml(template, model);
        if (StringUtils.isEmpty(html)) {
            throw new CustomException(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        }
        return html;
    }

    //页面发布
    public void postPage(String pageId) {
        //执行页面静态化
        String htmlContent = this.getPageHtml(pageId);
        //将页面存储到gridFs中
        CmsPage cmsPage = this.saveHtml(pageId, htmlContent);
        //通知客户端发布页面
        this.sendToClient(pageId);
    }

    //通知客户端发布页面
    private void sendToClient(String pageId) {
        //定义消息体
        Map<String, Object> map = new HashMap<>();
        map.put("pageId", pageId);
        //转json格式
        String msg = JSON.toJSONString(map);
        //通过rabbitMQ通知客户端
        /**
         * 参数说明
         * 1.交换机
         * 2.路由key
         * 3.消息体
         */
        //获取页面信息
        CmsPage cmsPage = this.findByPageId(pageId);
        if (cmsPage == null) {
            throw new CustomException(CmsCode.CMS_GENERATEHTML_PAGEISNULL);
        }
        rabbitTemplate.convertAndSend(RabbitmqConfig.EX_ROUTING_CMS_POSTPAGE, cmsPage.getSiteId(), msg);
    }

    //将页面存储到gridFs中
    private CmsPage saveHtml(String pageId, String content) {
        //将页面内容转为输入流
        InputStream inputStream = IOUtils.toInputStream(content);
        //获取页面信息
        CmsPage cmsPage = this.findByPageId(pageId);
        if (cmsPage == null) {
            throw new CustomException(CmsCode.CMS_GENERATEHTML_PAGEISNULL);
        }
        //获取页面名称
        //将页面存储gridFs
        ObjectId objectId = gridFsTemplate.store(inputStream, cmsPage.getPageName());
        //更新cmsPage中的htmlFileId
        cmsPage.setHtmlFileId(objectId.toHexString());
        cmsPageRepository.save(cmsPage);
        return cmsPage;
    }

    //执行静态化
    private String generateHtml(String template, Map model) {
        try {
            //生成配置类
            Configuration configuration = new Configuration(Configuration.getVersion());
            //模板加载器
            StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
            stringTemplateLoader.putTemplate("template", template);
            //配置模板加载器
            configuration.setTemplateLoader(stringTemplateLoader);
            //获取模板
            Template template1 = configuration.getTemplate("template");
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template1, model);
            return html;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取页面模板
    private String getTemplateByPageId(String pageId) {
        //查询页面信息
        CmsPage cmsPage = this.findByPageId(pageId);
        if (cmsPage == null) {
            throw new CustomException(CmsCode.CMS_GENERATEHTML_PAGEISNULL);
        }
        //取页面模板
        String templateId = cmsPage.getTemplateId();
        if (StringUtils.isEmpty(templateId)) {
            throw new CustomException(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        Optional<CmsTemplate> optional = cmsTemplateRepository.findById(templateId);
        if (optional.isPresent()) {
            CmsTemplate cmsTemplate = optional.get();
            //取出模板文件内容
            GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(cmsTemplate.getTemplateFileId())));
            //打开下载流对象
            GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
            //获取流对象
            GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
            //返回模板内容
            try {
                String content = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
                return content;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //获取页面模型数据
    private Map getModelByPageId(String pageId) {
        //查询页面信息
        CmsPage cmsPage = this.findByPageId(pageId);
        if (cmsPage == null) {
            throw new CustomException(CmsCode.CMS_GENERATEHTML_PAGEISNULL);
        }
        //取dataUrl
        String dataUrl = cmsPage.getDataUrl();
        if (StringUtils.isEmpty(dataUrl)) {
            throw new CustomException(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        }
        //todo...
        ResponseEntity<Map> forEntity = restTemplate.getForEntity(dataUrl, Map.class);
        return forEntity.getBody();
    }
}
