package com.gitlab.neton.module.system.controller.app.notify;

import com.gitlab.neton.framework.apilog.core.annotation.ApiAccessLog;
import com.gitlab.neton.framework.common.enums.UserTypeEnum;
import com.gitlab.neton.framework.common.pojo.CommonResult;
import com.gitlab.neton.framework.common.pojo.PageResult;
import com.gitlab.neton.framework.common.util.object.BeanUtils;
import com.gitlab.neton.module.system.controller.admin.notify.vo.message.NotifyMessageMyPageReqVO;
import com.gitlab.neton.module.system.controller.admin.notify.vo.message.NotifyMessageRespVO;
import com.gitlab.neton.module.system.dal.dataobject.notify.NotifyMessageDO;
import com.gitlab.neton.module.system.service.notify.NotifyMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;

import static com.gitlab.neton.framework.common.pojo.CommonResult.success;
import static com.gitlab.neton.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - 站内信")
@RestController
@RequestMapping("/member/notify-message")
@Validated
public class AppNotifyMessageController {

    @Resource
    private NotifyMessageService notifyMessageService;

    @GetMapping("/my-page")
    @Operation(summary = "获得我的站内信分页")
    public CommonResult<PageResult<NotifyMessageRespVO>> getMyNotifyMessagePage(@Valid NotifyMessageMyPageReqVO pageVO) {
        PageResult<NotifyMessageDO> pageResult = notifyMessageService.getMyMyNotifyMessagePage(pageVO,
                getLoginUserId(), UserTypeEnum.MEMBER.getValue());
        return success(BeanUtils.toBean(pageResult, NotifyMessageRespVO.class));
    }

    @PutMapping("/update-read")
    @Operation(summary = "标记站内信为已读")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<Boolean> updateNotifyMessageRead(@RequestParam("ids") List<Long> ids) {
        notifyMessageService.updateNotifyMessageRead(ids, getLoginUserId(), UserTypeEnum.MEMBER.getValue());
        return success(Boolean.TRUE);
    }

    @PutMapping("/update-all-read")
    @Operation(summary = "标记所有站内信为已读")
    public CommonResult<Boolean> updateAllNotifyMessageRead() {
        notifyMessageService.updateAllNotifyMessageRead(getLoginUserId(), UserTypeEnum.MEMBER.getValue());
        return success(Boolean.TRUE);
    }

    @GetMapping("/get-unread-list")
    @Operation(summary = "获取当前用户的最新站内信列表，默认 10 条")
    @Parameter(name = "size", description = "10")
    public CommonResult<List<NotifyMessageRespVO>> getUnreadNotifyMessageList(
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        List<NotifyMessageDO> list = notifyMessageService.getUnreadNotifyMessageList(
                getLoginUserId(), UserTypeEnum.MEMBER.getValue(), size);
        return success(BeanUtils.toBean(list, NotifyMessageRespVO.class));
    }

    @GetMapping("/get-unread-count")
    @Operation(summary = "获得当前用户的未读站内信数量")
    @ApiAccessLog(enable = false)
    public CommonResult<Long> getUnreadNotifyMessageCount() {
        return success(notifyMessageService.getUnreadNotifyMessageCount(
                getLoginUserId(), UserTypeEnum.MEMBER.getValue()));
    }

}
