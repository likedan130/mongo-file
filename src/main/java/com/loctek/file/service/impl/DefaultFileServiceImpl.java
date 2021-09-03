package com.loctek.file.service.impl;

import com.loctek.file.service.constant.MongoConstant;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.loctek.file.service.IfileService;
import com.loctek.file.util.FileUtil;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.gridfs.GridFsUpload;
import org.springframework.data.mongodb.gridfs.GridFsUpload.GridFsUploadBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * @author wneck130@gmail.com
 * @Description:
 * @date 2021/5/1310:29
 */
@Service("file")
public class DefaultFileServiceImpl implements IfileService {

    @Autowired
    GridFsTemplate gridFsTemplate;

    @Override
    public String save(File file) throws Exception {
        String contentType = Optional.ofNullable(FileUtil.getExtension(file.getName()))
                .map(this::switchType).orElse("");
        return gridFsTemplate.store(new FileInputStream(file), file.getName(), contentType).toHexString();
    }

    @Override
    public String save(File file, String filename) throws Exception {
        String contentType = Optional.ofNullable(FileUtil.getExtension(filename)).map(this::switchType).orElse("");
        return gridFsTemplate.store(new FileInputStream(file), filename, contentType).toHexString();
    }

    @Override
    public String save(File file, String filename, Map<String, Object> metaData) throws Exception {
        Document document = new Document();
        metaData.forEach(document::append);
        String contentType = Optional.ofNullable(FileUtil.getExtension(filename)).map(this::switchType).orElse("");
        return gridFsTemplate.store(new FileInputStream(file), filename, contentType, document).toHexString();
    }

    @Override
    public String save(InputStream inputStream, String filename) {
        String contentType = Optional.ofNullable(FileUtil.getExtension(filename)).map(this::switchType).orElse("");
        return gridFsTemplate.store(inputStream, filename, contentType).toHexString();
    }

    @Override
    public String save(InputStream inputStream, String filename, Map<String, Object> metaData) {
        Document document = new Document();
        metaData.forEach(document::append);
        String contentType = Optional.ofNullable(FileUtil.getExtension(filename)).map(this::switchType).orElse("");
        return gridFsTemplate.store(inputStream, filename, contentType, document).toHexString();
    }

    @Override
    public GridFSFile findOne(String filename) {
        return gridFsTemplate.findOne(Query.query(Criteria.where("filename").is(filename)));
    }

    @Override
    public GridFSFile findOneById(String id) {
        return gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(id)));
    }

    @Override
    public GridFSFile findOne(Map<String, Object> metaData) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        metaData.forEach((key, value) -> criteria.and(key).is(value));
        query.addCriteria(criteria);
        return gridFsTemplate.findOne(query);
    }

    @Override
    public List<GridFSFile> find(Map<String, Object> metaData) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        metaData.forEach((key, value) -> criteria.and(key).is(value));
        query.addCriteria(criteria);
        List<GridFSFile> result = new LinkedList<>();
        for (GridFSFile gridFsFile : gridFsTemplate.find(query)) {
            result.add(gridFsFile);
        }
        return result;
    }

    @Override
    public List<GridFSFile> find(Map<String, Object> metaData, int pageNum, int pageSize) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        metaData.forEach((key, value) -> criteria.and(key).is(value));
        query.addCriteria(criteria);
        long skip = (long) (pageNum - 1) * pageSize;
        query.skip(skip).limit(pageSize);
        List<GridFSFile> result = new LinkedList<>();
        for (GridFSFile gridFsFile : gridFsTemplate.find(query)) {
            result.add(gridFsFile);
        }
        return result;
    }

    @Override
    public List<GridFSFile> find(Map<String, Object> metaData, String orderBy, String orderType) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        metaData.forEach((key, value) -> criteria.and(key).is(value));
        query.addCriteria(criteria);
        if (StringUtils.hasText(orderBy) || StringUtils.hasText(orderType)) {
            return null;
        }
        if (MongoConstant.ORDER_DESC.equalsIgnoreCase(orderType)) {
            query.with(Sort.by(Sort.Order.desc(orderBy)));
        } else {
            query.with(Sort.by(Sort.Order.asc(orderBy)));
        }
        List<GridFSFile> result = new LinkedList<>();
        for (GridFSFile gridFsFile : gridFsTemplate.find(query)) {
            result.add(gridFsFile);
        }
        return result;
    }

    @Override
    public List<GridFSFile> find(Map<String, Object> metaData, String orderBy,
                                 String orderType, int pageNum, int pageSize) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        metaData.forEach((key, value) -> criteria.and(key).is(value));
        query.addCriteria(criteria);
        long skip = (long) (pageNum - 1) * pageSize;
        query.skip(skip).limit(pageSize);
        if (MongoConstant.ORDER_DESC.equalsIgnoreCase(orderType)) {
            query.with(Sort.by(Sort.Order.desc(orderBy)));
        } else {
            query.with(Sort.by(Sort.Order.asc(orderBy)));
        }
        List<GridFSFile> result = new LinkedList<>();
        for (GridFSFile gridFsFile : gridFsTemplate.find(query)) {
            result.add(gridFsFile);
        }
        return result;
    }

    @Override
    public void rename(String oldName, String newName) throws Exception {
        GridFSFile gridFSFile = this.findOne(oldName);
        GridFsUploadBuilder<ObjectId> uploadBuilder =
                GridFsUpload.fromStream(new GridFsResource(gridFSFile).getInputStream());
        uploadBuilder.filename(newName);
        uploadBuilder.gridFsFile(gridFSFile);
        gridFsTemplate.store(uploadBuilder.build());
    }

    @Override
    public void renameById(String id, String newName) throws Exception {
        GridFSFile gridFSFile = this.findOneById(id);
        GridFsUploadBuilder<ObjectId> uploadBuilder =
                GridFsUpload.fromStream(new GridFsResource(gridFSFile).getInputStream());
        uploadBuilder.gridFsFile(gridFSFile);
        uploadBuilder.filename(newName);
        gridFsTemplate.store(uploadBuilder.build());
    }

    @Override
    public void removeById(String id) {
        gridFsTemplate.delete(Query.query(Criteria.where("_id").is(id)));
    }

    @Override
    public void remove(String filename) {
        gridFsTemplate.delete(Query.query(Criteria.where("filename").is(filename)));
    }

    @Override
    public void remove(Map<String, Object> metaData) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        metaData.entrySet().stream().forEach(stringObjectEntry ->
                criteria.and(stringObjectEntry.getKey()).is(stringObjectEntry.getValue()));
        query.addCriteria(criteria);
        gridFsTemplate.delete(query);
    }

    @Override
    public GridFsResource getStream(GridFSFile file) {
        return gridFsTemplate.getResource(file);
    }

    @Override
    public GridFsResource getStream(String id) {
        return gridFsTemplate.getResource(this.findOneById(id));
    }

    /**
     * jpg图片类型在http头contentType中对应"image/jpeg"或"application/x-jpg"，为了下载统一格式，将jpg转成jpeg存储
     * @param suffix
     * @return
     */
    public String switchType(String suffix) {
        if (Objects.equals(suffix, FileUtil.JPG_TYPE)) {
            return FileUtil.JPEG_TYPE;
        } else {
            return suffix;
        }
    }
}
