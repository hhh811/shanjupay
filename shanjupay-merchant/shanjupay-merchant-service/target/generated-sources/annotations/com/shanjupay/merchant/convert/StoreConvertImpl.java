package com.shanjupay.merchant.convert;

import com.shanjupay.merchant.api.dto.StoreDTO;
import com.shanjupay.merchant.entity.Store;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-12-28T21:22:00+0800",
    comments = "version: 1.3.0.Final, compiler: javac, environment: Java 15.0.1 (Oracle Corporation)"
)
public class StoreConvertImpl implements StoreConvert {

    @Override
    public StoreDTO entity2dto(Store store) {
        if ( store == null ) {
            return null;
        }

        StoreDTO storeDTO = new StoreDTO();

        storeDTO.setId( store.getId() );
        storeDTO.setStoreName( store.getStoreName() );
        storeDTO.setStoreNumber( store.getStoreNumber() );
        storeDTO.setMerchantId( store.getMerchantId() );
        storeDTO.setParentId( store.getParentId() );
        storeDTO.setStoreStatus( store.getStoreStatus() );
        storeDTO.setStoreAddress( store.getStoreAddress() );

        return storeDTO;
    }

    @Override
    public Store dto2entity(StoreDTO storeDTO) {
        if ( storeDTO == null ) {
            return null;
        }

        Store store = new Store();

        store.setId( storeDTO.getId() );
        store.setStoreName( storeDTO.getStoreName() );
        store.setStoreNumber( storeDTO.getStoreNumber() );
        store.setMerchantId( storeDTO.getMerchantId() );
        store.setParentId( storeDTO.getParentId() );
        store.setStoreStatus( storeDTO.getStoreStatus() );
        store.setStoreAddress( storeDTO.getStoreAddress() );

        return store;
    }

    @Override
    public List<StoreDTO> listentity2dto(List<Store> staff) {
        if ( staff == null ) {
            return null;
        }

        List<StoreDTO> list = new ArrayList<StoreDTO>( staff.size() );
        for ( Store store : staff ) {
            list.add( entity2dto( store ) );
        }

        return list;
    }
}
