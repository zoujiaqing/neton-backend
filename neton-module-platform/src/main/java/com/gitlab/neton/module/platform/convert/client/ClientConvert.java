package com.gitlab.neton.module.platform.convert.client;

import com.gitlab.neton.module.platform.controller.admin.client.vo.ClientRespVO;
import com.gitlab.neton.module.platform.dal.dataobject.client.ClientDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 客户端 Convert
 *
 * @author Neton
 */
@Mapper
public interface ClientConvert {

    ClientConvert INSTANCE = Mappers.getMapper(ClientConvert.class);

    ClientRespVO convert(ClientDO bean);

}
