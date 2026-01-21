package com.gitlab.neton.module.product.convert.spu;

import com.gitlab.neton.framework.common.util.collection.CollectionUtils;
import com.gitlab.neton.framework.common.util.object.BeanUtils;
import com.gitlab.neton.module.product.controller.admin.spu.vo.ProductSkuRespVO;
import com.gitlab.neton.module.product.controller.admin.spu.vo.ProductSpuPageReqVO;
import com.gitlab.neton.module.product.controller.admin.spu.vo.ProductSpuRespVO;
import com.gitlab.neton.module.product.controller.app.spu.vo.AppProductSpuPageReqVO;
import com.gitlab.neton.module.product.dal.dataobject.sku.ProductSkuDO;
import com.gitlab.neton.module.product.dal.dataobject.spu.ProductSpuDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

import static com.gitlab.neton.framework.common.util.collection.CollectionUtils.convertMultiMap;

/**
 * 商品 SPU Convert
 *
 * @author Neton
 */
@Mapper
public interface ProductSpuConvert {

    ProductSpuConvert INSTANCE = Mappers.getMapper(ProductSpuConvert.class);

    ProductSpuPageReqVO convert(AppProductSpuPageReqVO bean);

    default ProductSpuRespVO convert(ProductSpuDO spu, List<ProductSkuDO> skus) {
        ProductSpuRespVO spuVO = BeanUtils.toBean(spu, ProductSpuRespVO.class);
        spuVO.setSkus(BeanUtils.toBean(skus, ProductSkuRespVO.class));
        return spuVO;
    }

    default List<ProductSpuRespVO> convertForSpuDetailRespListVO(List<ProductSpuDO> spus, List<ProductSkuDO> skus) {
        Map<Long, List<ProductSkuDO>> skuMultiMap = convertMultiMap(skus, ProductSkuDO::getSpuId);
        return CollectionUtils.convertList(spus, spu -> convert(spu, skuMultiMap.get(spu.getId())));
    }

}
