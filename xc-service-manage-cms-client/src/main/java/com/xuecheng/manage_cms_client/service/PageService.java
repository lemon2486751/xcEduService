package com.xuecheng.manage_cms_client.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.manage_cms_client.dao.CmsPageRepository;
import com.xuecheng.manage_cms_client.dao.CmsSiteRepository;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * @author lemon
 * @date 2021/2/17 13:37
 */
@Service
public class PageService {
    @Autowired
    private CmsPageRepository cmsPageRepository;
    @Autowired
    private CmsSiteRepository cmsSiteRepository;
    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    private GridFSBucket gridFSBucket;

    private static final Logger LOGGER = LoggerFactory.getLogger(PageService.class);

    public void saveFileToServerPath(String pageId) {
        //获取cmsPage
        CmsPage cmsPage = this.findByPageId(pageId);
        if (cmsPage == null) {
            LOGGER.error("saveFileToServerPath cmsPage is null,pageId:{}", pageId);
            return;
        }
        //从cmsPage中获取siteId
        String siteId = cmsPage.getSiteId();
        if (siteId == null) {
            LOGGER.error("saveFileToServerPath siteId is null,pageId:{}", pageId);
            return;
        }
        //根据sitId获取cmsSite
        CmsSite cmsSite = this.findBySiteId(siteId);
        if (cmsSite == null) {
            LOGGER.error("saveFileToServerPath cmsSite is null,siteId:{}", siteId);
            return;
        }
        //从cmsSite获取站点物理路径
        String sitePhysicalPath = cmsSite.getSitePhysicalPath();
        if (sitePhysicalPath == null) {
            LOGGER.error("saveFileToServerPath sitePhysicalPath is null,siteId:{}", siteId);
        }
        //页面物理路径=站点物理路径+页面物理路径+页面名称
        String pagePath = sitePhysicalPath + cmsPage.getPagePhysicalPath() + cmsPage.getPageName();
        //下载文件到指定物理路径
        //从cmsPage中获取fileId
        String fileId = cmsPage.getHtmlFileId();
        if (fileId == null) {
            LOGGER.error("saveFileToServerPath htmlFileId is null,pageId:{}", pageId);
            return;
        }
        //根据fileId从gridFs中获取文件输入流
        InputStream inputStream = this.findByFileId(fileId);
        if (inputStream == null) {
            LOGGER.error("saveFileToServerPath inputStream is null,htmlFileId:{}", fileId);
            return;
        }
        //根据页面物理路径定义文件输出流
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(new File(pagePath));
            IOUtils.copy(inputStream, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关闭输入输出流
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private CmsPage findByPageId(String pageId) {
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    private InputStream findByFileId(String fileId) {
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        try {
            return new GridFsResource(gridFSFile, gridFSDownloadStream).getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private CmsSite findBySiteId(String siteId) {
        Optional<CmsSite> optional = cmsSiteRepository.findById(siteId);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }


}
