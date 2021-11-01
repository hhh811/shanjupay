package com.shanjupay.transaction.convert;

import com.shanjupay.transaction.api.dto.PlatformChannelDTO;
import com.shanjupay.transaction.entity.PlatformChannel;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-10-26T21:55:21+0800",
    comments = "version: 1.3.0.Final, compiler: javac, environment: Java 15.0.1 (Oracle Corporation)"
)
public class PlatformChannelConvertImpl implements PlatformChannelConvert {

    @Override
    public PlatformChannelDTO entity2dto(PlatformChannel entity) {
        if ( entity == null ) {
            return null;
        }

        PlatformChannelDTO platformChannelDTO = new PlatformChannelDTO();

        platformChannelDTO.setId( entity.getId() );
        platformChannelDTO.setChannelName( entity.getChannelName() );
        platformChannelDTO.setChannelCode( entity.getChannelCode() );

        return platformChannelDTO;
    }

    @Override
    public PlatformChannel dto2entity(PlatformChannelDTO dto) {
        if ( dto == null ) {
            return null;
        }

        PlatformChannel platformChannel = new PlatformChannel();

        platformChannel.setId( dto.getId() );
        platformChannel.setChannelName( dto.getChannelName() );
        platformChannel.setChannelCode( dto.getChannelCode() );

        return platformChannel;
    }

    @Override
    public List<PlatformChannelDTO> listentity2listdto(List<PlatformChannel> platformChannels) {
        if ( platformChannels == null ) {
            return null;
        }

        List<PlatformChannelDTO> list = new ArrayList<PlatformChannelDTO>( platformChannels.size() );
        for ( PlatformChannel platformChannel : platformChannels ) {
            list.add( entity2dto( platformChannel ) );
        }

        return list;
    }
}
