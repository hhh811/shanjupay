package com.shanjupay.user.convert;

import com.shanjupay.user.api.dto.tenant.AccountDTO;
import com.shanjupay.user.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AccountConvert {

    AccountConvert INSTANCE = Mappers.getMapper(AccountConvert.class);

    AccountDTO entity2dto(Account entity);

    Account dto2entity(AccountDTO dto);

}
