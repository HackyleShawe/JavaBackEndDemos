package com.ks.minio.bean;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * MinIO文件对象：只能含括文件的相关属性
 * 文件完整路径：bucketName + prefix + fileName.fileType
 */
public class MinioFile {
    /** 桶名 */
    private String bucketName;
    /** 文件路径，格式：path1/path2/ */
    private String prefixPath;
    /** 文件对象路径（不含桶名）：/path1/path11/text.txt */
    private String filePath;

    /** 文件类型标识：文本-text/plain；MP3-audio/mpeg */
    private String contentType;
    /** 文件大小 */
    private Long fileSize;
    /** 其他文件属性：StatObjectResponse */
    private String etag;
    private String region;
    private boolean deleteMarker;
    private String versionId;
    private Map<String, String> userMetadata;

    /** 输入流 */
    private InputStream inputStream;
    /** 输出流 */
    private OutputStream outputStream;


    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getPrefixPath() {
        return prefixPath;
    }

    public void setPrefixPath(String prefixPath) {
        this.prefixPath = prefixPath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public boolean isDeleteMarker() {
        return deleteMarker;
    }

    public void setDeleteMarker(boolean deleteMarker) {
        this.deleteMarker = deleteMarker;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public Map<String, String> getUserMetadata() {
        return userMetadata;
    }

    public void setUserMetadata(Map<String, String> userMetadata) {
        this.userMetadata = userMetadata;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }
}
