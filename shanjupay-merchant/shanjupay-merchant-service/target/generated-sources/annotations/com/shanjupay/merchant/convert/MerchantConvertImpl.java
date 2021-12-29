package com.shanjupay.merchant.convert;

import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.entity.Merchant;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-12-28T21:22:00+0800",
    comments = "version: 1.3.0.Final, compiler: javac, environment: Java 15.0.1 (Oracle Corporation)"
)
public class MerchantConvertImpl implements MerchantConvert {

    @Override
    public MerchantDTO entity2dto(Merchant entity) {
        if ( entity == null ) {
            return null;
        }

        MerchantDTO merchantDTO = new MerchantDTO();

        merchantDTO.setId( entity.getId() );
        merchantDTO.setMerchantName( entity.getMerchantName() );
        if ( entity.getMerchantNo() != null ) {
            merchantDTO.setMerchantNo( Long.parseLong( entity.getMerchantNo() ) );
        }
        merchantDTO.setMerchantAddress( entity.getMerchantAddress() );
        merchantDTO.setMerchantType( entity.getMerchantType() );
        merchantDTO.setIdCardFrontImg( entity.getIdCardFrontImg() );
        merchantDTO.setIdCardAfterImg( entity.getIdCardAfterImg() );
        merchantDTO.setUserName( entity.getUserName() );
        merchantDTO.setMobile( entity.getMobile() );
        merchantDTO.setContactAddress( entity.getContactAddress() );
        merchantDTO.setAuditStatus( entity.getAuditStatus() );
        merchantDTO.setTenantId( entity.getTenantId() );

        return merchantDTO;
    }

    @Override
    public Merchant dto2entity(MerchantDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Merchant merchant = new Merchant();

        merchant.setId( dto.getId() );
        merchant.setMerchantName( dto.getMerchantName() );
        if ( dto.getMerchantNo() != null ) {
            merchant.setMerchantNo( String.valueOf( dto.getMerchantNo() ) );
        }
        merchant.setMerchantAddress( dto.getMerchantAddress() );
        merchant.setMerchantType( dto.getMerchantType() );
        merchant.setIdCardFrontImg( dto.getIdCardFrontImg() );
        merchant.setIdCardAfterImg( dto.getIdCardAfterImg() );
        merchant.setUserName( dto.getUserName() );
        merchant.setMobile( dto.getMobile() );
        merchant.setContactAddress( dto.getContactAddress() );
        merchant.setAuditStatus( dto.getAuditStatus() );
        merchant.setTenantId( dto.getTenantId() );

        return merchant;
    }
}
