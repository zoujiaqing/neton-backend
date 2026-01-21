package com.gitlab.neton.module.member.dal.mysql.tag;

import com.gitlab.neton.framework.common.pojo.PageResult;
import com.gitlab.neton.framework.mybatis.core.mapper.BaseMapperX;
import com.gitlab.neton.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.gitlab.neton.module.member.controller.admin.tag.vo.MemberTagPageReqVO;
import com.gitlab.neton.module.member.dal.dataobject.tag.MemberTagDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员标签 Mapper
 *
 * @author Neton
 */
@Mapper
public interface MemberTagMapper extends BaseMapperX<MemberTagDO> {

    default PageResult<MemberTagDO> selectPage(MemberTagPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MemberTagDO>()
                .likeIfPresent(MemberTagDO::getName, reqVO.getName())
                .betweenIfPresent(MemberTagDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(MemberTagDO::getId));
    }

    default MemberTagDO selelctByName(String name) {
        return selectOne(MemberTagDO::getName, name);
    }
}
