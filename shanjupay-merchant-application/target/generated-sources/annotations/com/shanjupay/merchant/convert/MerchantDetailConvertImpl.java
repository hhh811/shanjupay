package com.shanjupay.merchant.convert;

import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.vo.MerchantDetailVO;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-12-28T21:15:05+0800",
    comments = "version: 1.3.0.Final, compiler: javac, environment: Java 15.0.1 (Oracle Corporation)"
)
public class MerchantDetailConvertImpl implements MerchantDetailConvert {

    @Override
    public MerchantDetailVO dto2vo(MerchantDTO merchantDTO) {
        if ( merchantDTO == null ) {
            return null;
        }

        MerchantDetailVO merchantDetailVO = new MerchantDetailVO();

        merchantDetailVO.setMerchantName( merchantDTO.getMerchantName() );
        merchantDetailVO.setMerchantNo( merchantDTO.getMerchantNo() );
        merchantDetailVO.setMerchantAddress( merchantDTO.getMerchantAddress() );
        merchantDetailVO.setMerchantType( merchantDTO.getMerchantType() );
        merchantDetailVO.setBusinessLicensesImg( merchantDTO.getBusinessLicensesImg() );
        merchantDetailVO.setIdCardFrontImg( merchantDTO.getIdCardFrontImg() );
        merchantDetailVO.setIdCardAfterImg( merchantDTO.getIdCardAfterImg() );
        merchantDetailVO.setUserName( merchantDTO.getUserName() );
        merchantDetailVO.setContactAddress( merchantDTO.getContactAddress() );

        return merchantDetailVO;
    }

    @Override
    public MerchantDTO vo2dto(MerchantDetailVO merchantDetailVO) {
        if ( merchantDetailVO == null ) {
            return null;
        }

        MerchantDTO merchantDTO = new MerchantDTO();

        merchantDTO.setMerchantName( merchantDetailVO.getMerchantName() );
        merchantDTO.setMerchantNo( merchantDetailVO.getMerchantNo() );
        merchantDTO.setMerchantAddress( merchantDetailVO.getMerchantAddress() );
        merchantDTO.setMerchantType( merchantDetailVO.getMerchantType() );
        merchantDTO.setBusinessLicensesImg( merchantDetailVO.getBusinessLicensesImg() );
        merchantDTO.setIdCardFrontImg( merchantDetailVO.getIdCardFrontImg() );
        merchantDTO.setIdCardAfterImg( merchantDetailVO.getIdCardAfterImg() );
        merchantDTO.setUserName( merchantDetailVO.getUserName() );
        merchantDTO.setContactAddress( merchantDetailVO.getContactAddress() );

        return merchantDTO;
    }
}
