package com.gitlab.neton.module.system.job;

import com.gitlab.neton.framework.quartz.core.handler.JobHandler;
import com.gitlab.neton.module.system.dal.dataobject.user.AdminUserDO;
import com.gitlab.neton.module.system.dal.mysql.user.AdminUserMapper;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.List;

@Component
public class DemoJob implements JobHandler {

    @Resource
    private AdminUserMapper adminUserMapper;

    @Override
    public String execute(String param) {
        // 多租户已移除
        List<AdminUserDO> users = adminUserMapper.selectList();
        return "用户数量：" + users.size();
    }

}
