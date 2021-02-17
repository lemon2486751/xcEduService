package com.xuecheng.manage_cms;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.manage_cms.service.PageService;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author lemon
 * @date 2021/1/29 15:14
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class TestFreemarker {
    @Autowired
    GridFsTemplate gridFsTemplate;

    @Autowired
    GridFSBucket gridFSBucket;

    @Autowired
    PageService pageService;

    @Test
    public void testGridFs() throws FileNotFoundException {
        //要存储的文件
        File file = new File("D:/xcEdu/xcEduService/xc-service-manage-cms/src/test/resources/templates/index_banner.ftl");
        //定义文件输入流
        FileInputStream inputStream = new FileInputStream(file);
        //向gridFs中存储文件
        ObjectId objectId = gridFsTemplate.store(inputStream, "轮播图测试文件01", "");
        //得到文件id
        String fileId = objectId.toString();
        System.out.println(fileId);
    }

    @Test
    public void queryFile() throws IOException {
        String fileId = "6013eaa291a51f3c702bc48a";
        //获取模板文件
        GridFSFile file = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));
        //打开下载流对象
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(file.getObjectId());
        //获取流对象
        GridFsResource gridFsResource = new GridFsResource(file, gridFSDownloadStream);
        //获取流中的数据
        String s = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
        System.out.println(s);
    }

    @Test
    public void getPageHtmlTest(){
        String html = pageService.getPageHtml("6013cfcb91a51f04ec1f5aaf");
        System.out.println(html);
    }
}
