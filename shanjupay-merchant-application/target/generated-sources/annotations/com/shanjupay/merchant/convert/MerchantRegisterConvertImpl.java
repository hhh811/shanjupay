package com.shanjupay.merchant.convert;

import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.vo.MerchantRegisterVO;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-10-31T08:38:40+0800",
    comments = "version: 1.3.0.Final, compiler: javac, environment: Java 15.0.1 (Oracle Corporation)"
)
public class MerchantRegisterConvertImpl implements MerchantRegisterConvert {

    @Override
    public MerchantDTO vo2dto(MerchantRegisterVO vo) {
        if ( vo == null ) {
            return null;
        }

        MerchantDTO merchantDTO = new MerchantDTO();

        merchantDTO.setUserName( vo.getUserName() );
        merchantDTO.setPassword( vo.getPassword() );
        merchantDTO.setMobile( vo.getMobile() );

        return merchantDTO;
    }

    @Override
    public MerchantRegisterVO dto2vo(MerchantDTO dto) {
        if ( dto == null ) {
            return null;
        }

        MerchantRegisterVO merchantRegisterVO = new MerchantRegisterVO();

        merchantRegisterVO.setMobile( dto.getMobile() );
        merchantRegisterVO.setUserName( dto.getUserName() );
        merchantRegisterVO.setPassword( dto.getPassword() );

        return merchantRegisterVO;
    }
}
