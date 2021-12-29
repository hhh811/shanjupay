package com.shanjupay.merchant.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.domain.PageVO;
import com.shanjupay.common.util.PhoneUtil;
import com.shanjupay.merchant.api.MerchantService;
import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.api.dto.StaffDTO;
import com.shanjupay.merchant.api.dto.StoreDTO;
import com.shanjupay.merchant.convert.MerchantConvert;
import com.shanjupay.merchant.convert.StaffConvert;
import com.shanjupay.merchant.convert.StoreConvert;
import com.shanjupay.merchant.entity.Merchant;
import com.shanjupay.merchant.entity.Staff;
import com.shanjupay.merchant.entity.Store;
import com.shanjupay.merchant.entity.StoreStaff;
import com.shanjupay.merchant.mapper.MerchantMapper;
import com.shanjupay.merchant.mapper.StaffMapper;
import com.shanjupay.merchant.mapper.StoreMapper;
import com.shanjupay.merchant.mapper.StoreStaffMapper;
import com.shanjupay.user.api.TenantService;
import com.shanjupay.user.api.dto.tenant.CreateTenantRequestDTO;
import com.shanjupay.user.api.dto.tenant.TenantDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class MerchantServiceImpl implements MerchantService {

    @Autowired
    MerchantMapper merchantMapper;

    @Autowired
    StoreMapper storemapper;

    @Autowired
    StaffMapper staffMapper;

    @Autowired
    StoreStaffMapper storeStaffMapper;

    @Reference
    TenantService tenantService;

    /**
     * 根据ID查询详细信息
     *
     * @param merchantId
     * @return
     * @throws
     */
    @Override
    public MerchantDTO queryMerchantById(Long merchantId) {
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null) {
            return null;
        }
        MerchantDTO merchantDTO = MerchantConvert.INSTANCE.entity2dto(merchant);
        return merchantDTO;
    }

    /**
     * 商户注册
     *
     * @param merchantDTO
     * @return
     */
    @Override
    @Transactional
    public MerchantDTO createMerchant(MerchantDTO merchantDTO) throws BusinessException {

        // 校验
        if (merchantDTO == null) {
            throw new BusinessException(CommonErrorCode.E_100108);  // 传入对象为空
        }
        if (StringUtils.isBlank(merchantDTO.getMobile())) {
            throw new BusinessException(CommonErrorCode.E_100112);  // 手机号为空
        }
        if (!PhoneUtil.isMatches(merchantDTO.getMobile())) {
            throw new BusinessException(CommonErrorCode.E_100109);  // 手机号格式不合法
        }
        if (StringUtils.isBlank(merchantDTO.getUserName())) {
            throw new BusinessException(CommonErrorCode.E_100110);  // 用户名为空
        }
        if (StringUtils.isBlank(merchantDTO.getPassword())) {
            throw new BusinessException(CommonErrorCode.E_100111);  // 密码为空
        }
        // 手机号唯一性
        LambdaQueryWrapper<Merchant> lambdaQueryWrapper = new LambdaQueryWrapper<Merchant>()
                .eq(Merchant::getMobile, merchantDTO.getMobile());
        Integer count = merchantMapper.selectCount(lambdaQueryWrapper);
        if (count > 0) {
            throw new BusinessException(CommonErrorCode.E_100113);  // 手机号已存在
        }

        //调用SaaS接口
        //构建调用参数
        /**
         1、手机号
         2、账号
         3、密码
         4、租户类型：shanju-merchant
         5、默认套餐：shanju-merchant
         6、租户名称，同账号名
         */
        CreateTenantRequestDTO createTenantRequestDTO = new CreateTenantRequestDTO();
        createTenantRequestDTO.setMobile(merchantDTO.getMobile());
        createTenantRequestDTO.setUsername(merchantDTO.getUserName());
        createTenantRequestDTO.setPassword(merchantDTO.getPassword());
        createTenantRequestDTO.setTenantTypeCode("shanju-merchant");//租户类型
        createTenantRequestDTO.setBundleCode("shanju-merchant");//套餐，根据套餐进行分配权限
        createTenantRequestDTO.setName(merchantDTO.getUserName());//租户名称，和账号名一样
        log.info("商户中心调用同一账号服务，新增租户和账号");
        TenantDTO tenantDTO = tenantService.createTenantAndAccount(createTenantRequestDTO);
        if (tenantDTO == null || tenantDTO.getId() == null) {
            throw new BusinessException(CommonErrorCode.E_200012);
        }

        // 如果租户在Saas已经存在，SaaS直接返回此租户的信息，否则进行添加
        TenantDTO tenantAndAccount = tenantService.createTenantAndAccount(createTenantRequestDTO);
        // 获取租户id
        if (tenantAndAccount == null || tenantAndAccount.getId() == null) {
            throw new BusinessException(CommonErrorCode.E_200017);
        }
        Long tenantId = tenantAndAccount.getId();

        // DTO 转换成 entity, 用 mapstruct
        Merchant merchant = MerchantConvert.INSTANCE.dto2entity(merchantDTO);
        // 设置对应的租户Id
        merchant.setTenantId(tenantId);
        // 设置审核状态 0-未申请
        merchant.setAuditStatus("0");
        // 调用mapper向数据库写入记录
        merchantMapper.insert(merchant);

        // 新增门店
        StoreDTO storeDTO = new StoreDTO();
        storeDTO.setStoreName("跟门店");
        storeDTO.setMerchantId(merchant.getId());
        StoreDTO store = createStore(storeDTO);

        // 新增员工
        StaffDTO staffDTO = new StaffDTO();
        staffDTO.setMobile(merchantDTO.getMobile());//手机号
        staffDTO.setUsername(merchantDTO.getUserName());//账号
        staffDTO.setStoreId(store.getId());//员所属门店id
        staffDTO.setMerchantId(merchant.getId());//商户id

        StaffDTO staff = createStaff(staffDTO);

        // 为门店设置管理员
        bindStaffToStore(store.getId(), staff.getId());

        // 保存商户信息到商户表, 插入过程中会生成id
        merchantMapper.insert(merchant);

        // 再将 entity 转成 dto, 插入过程会增加 id
        return MerchantConvert.INSTANCE.entity2dto(merchant);
    }

    /**
     * 资质申请
     *
     * @param merchantId  商户 id
     * @param merchantDTO 资质申请信息
     * @throws BusinessException
     */
    @Override
    @Transactional
    public void applyMerchant(Long merchantId, MerchantDTO merchantDTO) throws BusinessException {
        // 接受资质申请信息，更新到商户列表
        if (merchantDTO == null || merchantId == null) {
            throw new BusinessException(CommonErrorCode.E_100108);
        }
        // 根据 id 查询商户
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null) {
            throw new BusinessException(CommonErrorCode.E_200002);
        }
        Merchant merchant_update = MerchantConvert.INSTANCE.dto2entity(merchantDTO);
        merchant_update.setAuditStatus("1");    // 已申请，待审核
        merchant_update.setTenantId(merchant.getTenantId());    // 租户 id
        // 更新
        merchantMapper.updateById(merchant_update);
    }

    @Override
    public StoreDTO createStore(StoreDTO storeDTO) throws BusinessException {
        Store store = StoreConvert.INSTANCE.dto2entity(storeDTO);
        log.info("商户下新增门店" + JSON.toJSONString(store));
        storemapper.insert(store);
        return StoreConvert.INSTANCE.entity2dto(store);
    }

    @Override
    public StaffDTO createStaff(StaffDTO staffDTO) throws BusinessException {
        //参数合法性校验
        if(staffDTO ==  null || StringUtils.isBlank(staffDTO.getMobile())
                || StringUtils.isBlank(staffDTO.getUsername())
                || staffDTO.getStoreId() == null){
            throw new BusinessException(CommonErrorCode.E_300009);
        }
        //1.校验手机号是否已经存在
        String mobile = staffDTO.getMobile();

        if (StringUtils.isBlank(mobile)) {
            throw new BusinessException(CommonErrorCode.E_100112);
        }
        // 根据商户id和手机号校验唯一性
        if (isExistStaffByMobile(mobile, staffDTO.getMerchantId())) {
            throw new BusinessException(CommonErrorCode.E_100113);
        }
        //2.校验用户名是否为空
        String username = staffDTO.getUsername();
        if (StringUtils.isBlank(username)) {
            throw new BusinessException(CommonErrorCode.E_100110);
        }
        // 根据商户id和账号校验唯一性
        if (isExistStaffByUserName(username, staffDTO.getMerchantId())) {
            throw new BusinessException(CommonErrorCode.E_100114);
        }
        Staff entity = StaffConvert.INSTANCE.dto2entity(staffDTO);
        log.info("商户下新增员工");
        staffMapper.insert(entity);
        return StaffConvert.INSTANCE.entity2dto(entity);
    }

    private boolean isExistStaffByMobile(String mobile, Long merchantId) {
        LambdaQueryWrapper<Staff> lambdaQueryWrapper = new LambdaQueryWrapper<Staff>();
        lambdaQueryWrapper.eq(Staff::getMobile, mobile).eq(Staff::getMerchantId, merchantId);
        int i = staffMapper.selectCount(lambdaQueryWrapper);
        return i > 0;
    }

    private boolean isExistStaffByUserName(String userName, Long merchantId) {
        LambdaQueryWrapper<Staff> lambdaQueryWrapper = new LambdaQueryWrapper<Staff>();
        lambdaQueryWrapper.eq(Staff::getUserName, userName).eq(Staff::getMerchantId, merchantId);
        int i = staffMapper.selectCount(lambdaQueryWrapper);
        return i > 0;
    }

    @Override
    public void bindStaffToStore(Long storeId, Long staffId) throws BusinessException {
        StoreStaff storeStaff = new StoreStaff();
        storeStaff.setStoreId(storeId);
        storeStaff.setStaffId(staffId);
        storeStaffMapper.insert(storeStaff);
    }

    @Override
    public PageVO<StoreDTO> queryStoreByPage(StoreDTO storeDTO, Integer pageNo, Integer pageSize) {
        // 分页条件
        Page<Store> page = new Page<>(pageNo, pageSize);
        // 查询条件拼装
        LambdaQueryWrapper<Store> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 如果传入商户id，此时要拼装查询条件
        if (storeDTO != null && storeDTO.getMerchantId() != null) {
            lambdaQueryWrapper.eq(Store::getMerchantId, storeDTO.getMerchantId());
        }
        // 再拼装其他查询条件，比如：门店名称
        if (storeDTO != null && StringUtils.isNotEmpty(storeDTO.getStoreName())) {
            lambdaQueryWrapper.eq(Store::getStoreName, storeDTO.getStoreName());
        }

        // 分页查询数据库
        IPage<Store> storeIPage = storemapper.selectPage(page, lambdaQueryWrapper);
        // 查询列表
        List<Store> records = storeIPage.getRecords();
        // 将包含entity的list转成包含dto的list
        List<StoreDTO> storeDTOS = StoreConvert.INSTANCE.listentity2dto(records);
        return new PageVO<>(storeDTOS, storeIPage.getTotal(), pageNo, pageSize);
    }

    @Override
    public Boolean queryStoreInMerchant(Long StoreId, Long merchantId) {
        Integer count = storemapper.selectCount(new LambdaQueryWrapper<Store>().eq(Store::getId, StoreId)
                .eq(Store::getMerchantId, merchantId));
        return count > 0;
    }
}
