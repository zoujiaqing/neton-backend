package com.gitlab.neton.module.platform.controller.admin.client.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import com.gitlab.neton.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static com.gitlab.neton.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 开放平台客户端分页 Request VO")
@Data
public class ClientPageReqVO extends PageParam {

    @Schema(description = "客户端唯一标识（公开）", example = "3569")
    private String clientId;

    @Schema(description = "客户端名称", example = "张三")
    private String clientName;

    @Schema(description = "客户端编码（英文标识）")
    private String clientCode;

    @Schema(description = "公司名称", example = "王五")
    private String companyName;

    @Schema(description = "营业执照号")
    private String businessLicense;

    @Schema(description = "联系人姓名", example = "王五")
    private String contactName;

    @Schema(description = "联系人邮箱")
    private String contactEmail;

    @Schema(description = "联系人电话")
    private String contactPhone;

    @Schema(description = "状态", example = "2")
    private Integer status;

}