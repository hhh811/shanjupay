package com.shanjupay.merchant.convert;

import com.shanjupay.merchant.api.dto.StaffDTO;
import com.shanjupay.merchant.entity.Staff;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StaffConvert {
    StaffConvert INSTANCE = Mappers.getMapper(StaffConvert.class);

    StaffDTO entity2dto(Staff store);

    Staff dto2entity(StaffDTO storeDTO);
}
