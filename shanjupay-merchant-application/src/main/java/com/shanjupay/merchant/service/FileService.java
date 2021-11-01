package com.shanjupay.merchant.service;

import java.sql.BatchUpdateException;

/**
 * <p>文件服务</p>
 */
public interface FileService {
    /**
     * 上传文件
     * @param bytes 文件字节
     * @param fileName  文件名称
     * @return  文件下载 url
     * @throws BatchUpdateException
     */
    public String upload(byte[] bytes, String fileName) throws BatchUpdateException;
}
