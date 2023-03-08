package com.study.media.model.dto;

import com.study.media.model.po.MediaFiles;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 上传图像成功后给前端返回的对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UploadFileResultDto extends MediaFiles {

}
