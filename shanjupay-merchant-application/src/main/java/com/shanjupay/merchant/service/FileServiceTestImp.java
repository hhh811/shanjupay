package com.shanjupay.merchant.service;

import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.util.FileUtilTest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.BatchUpdateException;

@Slf4j
@Service
public class FileServiceTestImp implements FileService {
    /**
     * 上传文件
     *
     * @param bytes    文件字节
     * @param fileName 文件名称
     * @return 文件下载 url
     * @throws BatchUpdateException
     */
    @Override
    public String upload(byte[] bytes, String fileName) throws BatchUpdateException {
        String fileUrl = "";
        try {
            fileUrl = FileUtilTest.upload();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(CommonErrorCode.E_100106);
        }
        return fileUrl;
    }
}
