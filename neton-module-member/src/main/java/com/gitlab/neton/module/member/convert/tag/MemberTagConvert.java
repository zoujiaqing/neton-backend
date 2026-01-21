package com.gitlab.neton.module.member.convert.tag;

import com.gitlab.neton.framework.common.pojo.PageResult;
import com.gitlab.neton.module.member.controller.admin.tag.vo.MemberTagCreateReqVO;
import com.gitlab.neton.module.member.controller.admin.tag.vo.MemberTagRespVO;
import com.gitlab.neton.module.member.controller.admin.tag.vo.MemberTagUpdateReqVO;
import com.gitlab.neton.module.member.dal.dataobject.tag.MemberTagDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 会员标签 Convert
 *
 * @author Neton
 */
@Mapper
public interface MemberTagConvert {

    MemberTagConvert INSTANCE = Mappers.getMapper(MemberTagConvert.class);

    MemberTagDO convert(MemberTagCreateReqVO bean);

    MemberTagDO convert(MemberTagUpdateReqVO bean);

    MemberTagRespVO convert(MemberTagDO bean);

    List<MemberTagRespVO> convertList(List<MemberTagDO> list);

    PageResult<MemberTagRespVO> convertPage(PageResult<MemberTagDO> page);

}
