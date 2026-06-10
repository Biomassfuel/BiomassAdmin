package com.ruoyi.system.domain.space;

import com.ruoyi.common.core.domain.BaseEntity;

public class SpaceImportBatch extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long batchId;
    private String importType;
    private String fileName;
    private String filePath;
    private Integer totalCount;
    private Integer successCount;
    private Integer failCount;
    private String importStatus;
    private String errorMsg;

    public Long getBatchId()
    {
        return batchId;
    }

    public void setBatchId(Long batchId)
    {
        this.batchId = batchId;
    }

    public String getImportType()
    {
        return importType;
    }

    public void setImportType(String importType)
    {
        this.importType = importType;
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public String getFilePath()
    {
        return filePath;
    }

    public void setFilePath(String filePath)
    {
        this.filePath = filePath;
    }

    public Integer getTotalCount()
    {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount)
    {
        this.totalCount = totalCount;
    }

    public Integer getSuccessCount()
    {
        return successCount;
    }

    public void setSuccessCount(Integer successCount)
    {
        this.successCount = successCount;
    }

    public Integer getFailCount()
    {
        return failCount;
    }

    public void setFailCount(Integer failCount)
    {
        this.failCount = failCount;
    }

    public String getImportStatus()
    {
        return importStatus;
    }

    public void setImportStatus(String importStatus)
    {
        this.importStatus = importStatus;
    }

    public String getErrorMsg()
    {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg)
    {
        this.errorMsg = errorMsg;
    }

}