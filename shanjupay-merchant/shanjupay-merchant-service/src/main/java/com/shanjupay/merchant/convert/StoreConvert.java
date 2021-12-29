package com.shanjupay.merchant.convert;

import com.shanjupay.merchant.api.dto.AppDTO;
import com.shanjupay.merchant.api.dto.StoreDTO;
import com.shanjupay.merchant.entity.Store;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface StoreConvert {
    StoreConvert INSTANCE = Mappers.getMapper(StoreConvert.class);

    StoreDTO entity2dto(Store store);

    Store dto2entity(StoreDTO storeDTO);

    List<StoreDTO> listentity2dto(List<Store> staff);
}
