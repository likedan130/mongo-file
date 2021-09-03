package com.loctek.file.service;

import com.mongodb.client.gridfs.model.GridFSFile;
import org.springframework.data.mongodb.gridfs.GridFsResource;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @author wneck130@gmail.com
 * @Description:
 * @date 2021/5/139:49
 */
public interface IfileService {



    /**
     * 文件保存类方法
     * @param file
     * @return
     * @throws Exception
     */
    String save(File file) throws Exception;

    /**
     * 文件保存类方法
     * @param file
     * @param filename
     * @return
     * @throws Exception
     */
    String save(File file, String filename) throws Exception;

    /**
     * 文件保存类方法
     * @param file
     * @param filename
     * @param metaData
     * @return
     * @throws Exception
     */
    String save(File file, String filename, Map<String, Object> metaData) throws Exception;

    /**
     * 文件保存类方法
     * @param inputStream
     * @param filename
     * @return
     */
    String save(InputStream inputStream, String filename);

    /**
     * 文件保存类方法
     * @param inputStream
     * @param filename
     * @param metaData
     * @return
     */
    String save(InputStream inputStream, String filename, Map<String, Object> metaData);

    /**
     * 文件查询类方法
     * @param filename
     * @return
     */
    GridFSFile findOne(String filename);

    /**
     * 文件查询类方法
     * @param id
     * @return
     */
    GridFSFile findOneById(String id);

    /**
     * 文件查询类方法
     * @param metaData
     * @return
     */
    GridFSFile findOne(Map<String, Object> metaData);

    /**
     * 文件查询类方法
     * @param metaData
     * @return
     */
    List<GridFSFile> find(Map<String, Object> metaData);

    /**
     * 文件查询类方法
     * @param metaData
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<GridFSFile> find(Map<String, Object> metaData, int pageNum, int pageSize);

    /**
     * 文件查询类方法
     * @param metaData
     * @param orderBy
     * @param orderType
     * @return
     */
    List<GridFSFile> find(Map<String, Object> metaData, String orderBy, String orderType);

    /**
     * 文件查询类方法
     * @param metaData
     * @param orderBy
     * @param orderType
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<GridFSFile> find(Map<String, Object> metaData, String orderBy, String orderType, int pageNum, int pageSize);

    /**
     * 文件重命名
     * @param oldName
     * @param newName
     * @throws Exception
     */
    void rename(String oldName, String newName) throws Exception;

    /**
     * 文件重命名
     * @param id
     * @param newName
     * @throws Exception
     */
    void renameById(String id, String newName) throws Exception;

    /**
     * 文件删除
     * @param id
     */
    void removeById(String id);

    /**
     * 文件删除
     * @param filename
     */
    void remove(String filename);

    /**
     * 文件删除
     * @param metaData
     */
    void remove(Map<String, Object> metaData);

    /**
     * 获取文件实际流内容
     * @param file
     * @return
     */
    GridFsResource getStream(GridFSFile file);

    /**
     * 获取文件实际流内容
     * @param id
     * @return
     */
    GridFsResource getStream(String id);

}
