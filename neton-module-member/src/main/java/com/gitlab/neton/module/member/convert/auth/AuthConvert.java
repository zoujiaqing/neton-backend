package com.gitlab.neton.module.member.convert.auth;

import com.gitlab.neton.module.member.controller.app.auth.vo.*;
import com.gitlab.neton.module.member.controller.app.social.vo.AppSocialUserUnbindReqVO;
import com.gitlab.neton.module.member.controller.app.user.vo.AppMemberUserResetPasswordReqVO;
import com.gitlab.neton.framework.common.biz.system.oauth2.dto.OAuth2AccessTokenRespDTO;
import com.gitlab.neton.module.system.api.sms.dto.code.SmsCodeSendReqDTO;
import com.gitlab.neton.module.system.api.sms.dto.code.SmsCodeUseReqDTO;
import com.gitlab.neton.module.system.api.sms.dto.code.SmsCodeValidateReqDTO;
import com.gitlab.neton.module.system.api.social.dto.SocialUserBindReqDTO;
import com.gitlab.neton.module.system.api.social.dto.SocialUserUnbindReqDTO;
import com.gitlab.neton.module.system.api.social.dto.SocialWxJsapiSignatureRespDTO;
import com.gitlab.neton.module.system.enums.sms.SmsSceneEnum;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AuthConvert {

    AuthConvert INSTANCE = Mappers.getMapper(AuthConvert.class);

    SocialUserBindReqDTO convert(Long userId, Integer userType, AppAuthSocialLoginReqVO reqVO);
    SocialUserUnbindReqDTO convert(Long userId, Integer userType, AppSocialUserUnbindReqVO reqVO);

    SmsCodeSendReqDTO convert(AppAuthSmsSendReqVO reqVO);
    SmsCodeUseReqDTO convert(AppMemberUserResetPasswordReqVO reqVO, SmsSceneEnum scene, String usedIp);
    SmsCodeUseReqDTO convert(AppAuthSmsLoginReqVO reqVO, Integer scene, String usedIp);

    AppAuthLoginRespVO convert(OAuth2AccessTokenRespDTO bean, String openid);

    SmsCodeValidateReqDTO convert(AppAuthSmsValidateReqVO bean);

    SocialWxJsapiSignatureRespDTO convert(SocialWxJsapiSignatureRespDTO bean);

}
