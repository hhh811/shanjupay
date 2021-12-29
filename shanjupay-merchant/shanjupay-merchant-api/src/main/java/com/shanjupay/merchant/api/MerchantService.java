package com.shanjupay.merchant.api;

import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.PageVO;
import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.api.dto.StaffDTO;
import com.shanjupay.merchant.api.dto.StoreDTO;

public interface MerchantService {

    MerchantDTO queryMerchantById(Long merchantId);

    /**
     * 商户注册
     * @param merchantDTO
     * @return
     */
    MerchantDTO createMerchant(MerchantDTO merchantDTO) throws BusinessException;

    /**
     * 资质申请
     * @param merchantId  商户 id
     * @param merchantDTO  资质申请信息
     * @throws BusinessException
     */
    public void applyMerchant(Long merchantId, MerchantDTO merchantDTO) throws BusinessException;


    /**
     * 新增门店
     * @param storeDTO  新增门店信息
     * @return 新增成功的门店信息
     * @throws BusinessException
     */
    StoreDTO createStore(StoreDTO storeDTO) throws BusinessException;

    /**
     * 新增员工
     * @param staffDTO 员工信息
     * @return 新增成功的员工信息
     * @throws BusinessException
     */
    StaffDTO createStaff(StaffDTO staffDTO) throws BusinessException;


    /**
     * 将员工设置为门店的管理员
     * @param storeId
     * @param staffId
     * @throws BusinessException
     */
    void bindStaffToStore(Long storeId, Long staffId) throws BusinessException;

    /**
     * 门店列表的查询
     * @param storeDTO 查询条件，必要参数：商户id
     * @param pageNo  页码
     * @param pageSize 分页记录数
     * @return
     */
    PageVO<StoreDTO> queryStoreByPage(StoreDTO storeDTO, Integer pageNo, Integer pageSize);

    /**
     * 校验门店是否属于商户
     * @param StoreId
     * @param merchantId
     * @return
     */
    Boolean queryStoreInMerchant(Long StoreId,Long merchantId);
}
