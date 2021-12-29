package com.shanjupay.merchant.convert;

import com.shanjupay.merchant.api.dto.StaffDTO;
import com.shanjupay.merchant.entity.Staff;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-12-28T21:22:00+0800",
    comments = "version: 1.3.0.Final, compiler: javac, environment: Java 15.0.1 (Oracle Corporation)"
)
public class StaffConvertImpl implements StaffConvert {

    @Override
    public StaffDTO entity2dto(Staff store) {
        if ( store == null ) {
            return null;
        }

        StaffDTO staffDTO = new StaffDTO();

        staffDTO.setId( store.getId() );
        staffDTO.setMerchantId( store.getMerchantId() );
        staffDTO.setFullName( store.getFullName() );
        staffDTO.setPosition( store.getPosition() );
        staffDTO.setMobile( store.getMobile() );
        staffDTO.setStoreId( store.getStoreId() );
        staffDTO.setLastLoginTime( store.getLastLoginTime() );
        staffDTO.setStaffStatus( store.getStaffStatus() );

        return staffDTO;
    }

    @Override
    public Staff dto2entity(StaffDTO storeDTO) {
        if ( storeDTO == null ) {
            return null;
        }

        Staff staff = new Staff();

        staff.setId( storeDTO.getId() );
        staff.setMerchantId( storeDTO.getMerchantId() );
        staff.setFullName( storeDTO.getFullName() );
        staff.setPosition( storeDTO.getPosition() );
        staff.setMobile( storeDTO.getMobile() );
        staff.setStoreId( storeDTO.getStoreId() );
        staff.setLastLoginTime( storeDTO.getLastLoginTime() );
        staff.setStaffStatus( storeDTO.getStaffStatus() );

        return staff;
    }
}
