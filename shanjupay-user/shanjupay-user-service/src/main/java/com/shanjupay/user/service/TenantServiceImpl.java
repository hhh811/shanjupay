package com.shanjupay.user.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.domain.PageVO;
import com.shanjupay.common.util.MD5Util;
import com.shanjupay.common.util.PhoneUtil;
import com.shanjupay.common.util.RandomStringUtil;
import com.shanjupay.user.api.AuthorizationService;
import com.shanjupay.user.api.ResourceService;
import com.shanjupay.user.api.TenantService;
import com.shanjupay.user.api.dto.authorization.AuthorizationInfoDTO;
import com.shanjupay.user.api.dto.authorization.RoleDTO;
import com.shanjupay.user.api.dto.resource.ApplicationDTO;
import com.shanjupay.user.api.dto.tenant.*;
import com.shanjupay.user.convert.*;
import com.shanjupay.user.entity.*;
import com.shanjupay.user.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TenantServiceImpl implements TenantService {

    @Autowired
    private TenantMapper tenantMapper;
    @Autowired
    private BundleMapper bundleMapper;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private TenantAccountMapper tenantAccountMapper;
    @Autowired
    private AccountRoleMapper accountRoleMapper;
    @Autowired
    private AuthorizationRoleMapper roleMapper;
    @Autowired
    private AuthorizationRolePrivilegeMapper rolePrivilegeMapper;
    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private ResourceApplicationMapper resourceApplicationMapper;

    //////////////////////////////////////////租户相关的操作///////////////////////////////////////////////////

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TenantDTO createTenantAndAccount(CreateTenantRequestDTO createTenantRequest) {
        //1.判断手机号、用户名在账号中是否存在
        String mobile = createTenantRequest.getMobile();
        if (isExistAccountByMobile(mobile)) {
            AccountDTO account = getAccountByMobile(mobile);
            Long accountId = account.getId();
            Boolean isAdmin = true;
            TenantDTO tenant = getTenantByAccount(accountId, isAdmin);
            if (tenant == null) {
                //创建租户
                Tenant ten = createTenant(createTenantRequest);
                bindTenantAccount(ten.getId(), accountId, true);
                return TenantConvert.INSTANCE.entity2dto(ten);
            } else {
                return tenant;
            }
        } else {
            //2.新增租户
            Tenant tenant = createTenant(createTenantRequest);
            //3.创建账号并绑定租户
            CreateAccountRequestDTO createAccountRequest = new CreateAccountRequestDTO();
            createAccountRequest.setMobile(mobile);
            createAccountRequest.setUsername(createTenantRequest.getUsername());
            createAccountRequest.setPassword(createTenantRequest.getPassword());
            createAccountInTenant(createAccountRequest, tenant.getId());
            //4.初始化套餐
            if (tenant.getId() == null) {
                throw new BusinessException(CommonErrorCode.E_200012);
            }
            log.info("初始化套餐");
            initBundle(tenant.getId(), createTenantRequest.getBundleCode());
            return TenantConvert.INSTANCE.entity2dto(tenant);
        }
    }

    private Tenant createTenant(CreateTenantRequestDTO createTenantRequest) {
        //新增租户
        Tenant tenant = new Tenant();
        tenant.setName(createTenantRequest.getName() + "_" + RandomStringUtil.getRandomString(6));
        tenant.setTenantTypeCode(createTenantRequest.getTenantTypeCode());
        tenant.setBundleCode(createTenantRequest.getBundleCode());
        tenantMapper.insert(tenant);
        return tenant;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TenantDTO createTenantRelateAccount(CreateTenantRequestDTO createTenantRequest) {
        AccountDTO accountDTO = getAccountByUsername(createTenantRequest.getUsername());
        if (accountDTO == null) {
            throw new BusinessException(CommonErrorCode.E_110001);
        }
        Tenant tenant = new Tenant();
        tenant.setName(createTenantRequest.getName() + "_" + RandomStringUtil.getRandomString(8));
        tenant.setTenantTypeCode(createTenantRequest.getTenantTypeCode());
        tenant.setBundleCode(createTenantRequest.getBundleCode());
        int insert = tenantMapper.insert(tenant);
        bindTenantAccount(tenant.getId(), accountDTO.getId(), true);
        initBundle(tenant.getId(), createTenantRequest.getBundleCode());
        return TenantConvert.INSTANCE.entity2dto(tenant);
    }

    /**
     * 删除租户
     * 解绑租户下所有账号，清除租户下账号-角色信息，清除租户下角色，删除租户
     * @param id 租户id
     */
    @Override
    public void removeTenant(Long id) {
//        tenantMapper.deleteById(id);
    }



    /**
     * 获取租户信息
     * @param id
     * @return
     */
    @Override
    public TenantDTO getTenant(Long id) {
        Tenant entity = tenantMapper.selectById(id);
        return TenantConvert.INSTANCE.entity2dto(entity);
    }

    /**
     * 检索租户
     * @param tenantQuery
     * @param pageNo
     * @param pageSize
     * @param sortBy
     * @param order
     * @return
     */
    @Override
    public PageVO<TenantDTO> queryTenants(TenantQueryDTO tenantQuery, Integer pageNo, Integer pageSize, String sortBy, String order) {
        Page<TenantDTO> page = new Page<>(pageNo, (pageSize == null || pageSize < 1 ? 10 : pageSize));
        List<TenantDTO> tenants = tenantMapper.selectTenantByPage(page, tenantQuery, sortBy, order);
        return  new PageVO<>(tenants, page.getTotal(), pageNo, pageSize);
    }

    /**
     * 查询某租户类型的套餐列表(不包含初始化套餐)
     * @param tenantType
     * @return
     */
    @Override
    public List<BundleDTO> queryBundleByTenantType(String tenantType) {
        QueryWrapper<Bundle> qw = new QueryWrapper<>();
        qw.lambda().eq(Bundle::getTenantTypeCode, tenantType).eq(Bundle::getInitialize, 0);
        List<Bundle> bundles = bundleMapper.selectList(qw);
        return BundleConvert.INSTANCE.entitylist2dto(bundles);
    }

    /**
     * 获取某套餐信息
     * @param bundleCode
     * @return
     */
    @Override
    public BundleDTO getBundle(String bundleCode) {
        QueryWrapper<Bundle> qw = new QueryWrapper<>();
        qw.lambda().eq(Bundle::getCode, bundleCode);
        Bundle entity = bundleMapper.selectOne(qw);
        return BundleConvert.INSTANCE.entity2dto(entity);
    }

    /**
     * 查询所有套餐
     * @return
     */
    @Override
    public List<BundleDTO> queryAllBundle() {
        List<Bundle> bundles = bundleMapper.selectList(null);
        return BundleConvert.INSTANCE.entitylist2dto(bundles);
    }

    /**
     * 切换租户套餐
     * 租户切换套餐操作会清除 原租户内的所有账号-角色关联数据  原套餐产生的角色权限数据,并将限流规则写入sentinel
     * @param tenantId
     * @param bundleCode
     */
    @Override
    public void changeBundle(Long tenantId, String bundleCode) {
        clearTenantRelateInfo(tenantId);
        initBundle(tenantId, bundleCode);
    }

    /**
     * 清除租户下账号-角色信息，清除租户下角色
     * @param tenantId
     */
    private void clearTenantRelateInfo(Long tenantId) {
        //查询租户下的所有角色
        List<RoleDTO> roleDTOS = authorizationService.queryRole(tenantId);
        if (roleDTOS.isEmpty()) {
            throw new BusinessException(CommonErrorCode.E_110007);
        }
        List<Long> roleIds = roleDTOS.stream().map(RoleDTO::getId).collect(Collectors.toList());
        //1.清除租户下所有账号和角色的关系
        accountRoleMapper.update(null, new UpdateWrapper<AccountRole>().lambda()
                .eq(AccountRole::getTenantId, tenantId).set(AccountRole::getRoleCode, null));
        //2.删除租户下所有角色
        roleMapper.delete(new QueryWrapper<AuthorizationRole>().lambda()
                .eq(AuthorizationRole::getTenantId, tenantId));
        //3.删除租户下所有角色对应的权限
        rolePrivilegeMapper.delete(new QueryWrapper<AuthorizationRolePrivilege>().lambda()
                .in(AuthorizationRolePrivilege::getId, roleIds));
    }

    /**
     * 初始化租户套餐
     * @param tenantId
     * @param bundleCode
     */
    @Override
    public void initBundle(Long tenantId, String bundleCode) {
        // 设置套餐
        Bundle bundle = bundleMapper.selectOne(new QueryWrapper<Bundle>().lambda().eq(Bundle::getCode, bundleCode));
        if (bundle != null && StringUtils.isNotBlank(bundle.getAbility())) {
            String ability = bundle.getAbility();
            //处理角色权限json(角色和权限的code),为租户绑定角色和权限
            List<RoleDTO> roleDTOList = JSONObject.parseArray(ability, RoleDTO.class);
            List<AuthorizationRole> roles = AuthorizationRoleConvert.INSTANCE.dtolist2entity(roleDTOList);
            List<String> rCodes = new ArrayList<>();    // 套餐中的角色集合
            roles.forEach(role -> rCodes.add(role.getCode()));
            //1.在指定租户下新增角色, 操作authorization_role表
            roleMapper.createRoles(tenantId, roles);
            //2.为租户绑定角色,操作account_role表
            Account account = accountMapper.selectAccountInfoByTenantId(tenantId);
            if (account != null && StringUtils.isNotBlank(account.getUsername())) {
                String[] rcodeArray = rCodes.toArray(new String[0]);
                authorizationService.bindAccountRole(account.getUsername(), tenantId, rcodeArray);
            }
            //3.为租户下的角色绑定权限，操作authorization_role_privilege表
            for (RoleDTO roleDTO : roleDTOList) {
                String code = roleDTO.getCode();
                List<String> pCodes = roleDTO.getPrivilegeCodes();
                String[] privilegeCodes = pCodes.toArray(new String[0]);
                authorizationService.roleBindPrivilege(tenantId, code, privilegeCodes);
            }
        }
    }

    /**
     * 新增套餐
     * @param bundleDTO
     */
    @Override
    public void createBundle(BundleDTO bundleDTO) {
        Bundle bundle = BundleConvert.INSTANCE.dto2entity(bundleDTO);
        bundleMapper.insert(bundle);
    }

    /**
     * 更新套餐
     * @param bundleDTO
     */
    @Override
    public void modifyBundle(BundleDTO bundleDTO) {
        Bundle bundle = BundleConvert.INSTANCE.dto2entity(bundleDTO);
        bundleMapper.updateById(bundle);
    }

    /**
     * 进件账号并绑定至租户
     * 1.若用户名已存在，禁止创建
     * 2.手机号已存在，禁止创建
     * @param createAccountRequest 创建账号请求
     * @param tenantId 租户id
     */
    @Override
    public void createAccountInTenant(CreateAccountRequestDTO createAccountRequest, Long tenantId) {
        //1.创建账号
        AccountDTO accountDTO = createAccount(createAccountRequest);
        if (accountDTO.getId() == null) {
            throw new BusinessException(CommonErrorCode.E_110001);
        }
        log.info("将创建的账号绑定至租户");
        //2.将账号绑定至租户
        bindTenantAccount(tenantId, accountDTO.getId(), true);
    }

    @Override
    public void bindTenant(Long tenantId, String username) {
        //1.根据用户名获取账号信息
        AccountDTO accountDTO = getAccountByUsername(username);
        //2.如果查询到账号，绑定关系
        if (accountDTO != null && accountDTO.getId() != null) {
            bindTenantAccount(tenantId, accountDTO.getId(), null);
        }
    }

    /**
     * 创建账号
     * @param createAccountRequest 创建账号请求
     * @return
     */
    @Override
    public AccountDTO createAccount(CreateAccountRequestDTO createAccountRequest) {
        log.info("创建账号" + JSON.toJSONString(createAccountRequest));
        //1.校验手机号格式及是否存在
        String mobile = createAccountRequest.getMobile();
        if (StringUtils.isBlank(mobile)) {
            throw new BusinessException(CommonErrorCode.E_100112);
        }
        if (isExistAccountByMobile(mobile)) {
            throw new BusinessException(CommonErrorCode.E_100113);
        }
        //2.校验用户名是否为空及是否存在
        String username = createAccountRequest.getUsername();
        if (StringUtils.isBlank(username)) {
            throw new BusinessException(CommonErrorCode.E_100110);
        }
        if (isExistAccountByUsername(username)) {
            throw new BusinessException(CommonErrorCode.E_100114);
        }
        //3.校验密码是否为空
        String password = createAccountRequest.getPassword();
        if (StringUtils.isBlank(password)) {
            throw new BusinessException(CommonErrorCode.E_100111);
        }
        //4.构造账号数据并保存
        Account account = new Account();
        String salt = RandomStringUtil.getRandomString(5);
        String md5pwd = MD5Util.getMd5(password + salt);
        account.setSalt(salt);
        account.setPassword(md5pwd);
        account.setUsername(username);
        account.setMobile(mobile);
        accountMapper.insert(account);
        return AccountConvert.INSTANCE.entity2dto(account);
    }

    /**
     * 修改账号密码
     * @param accountPwdDTO
     * @return
     */
    @Override
    public boolean accountPassword(ChangeAccountPwdDTO accountPwdDTO) {
        if (accountPwdDTO == null || accountPwdDTO.getAccountId() == null || StringUtils.isBlank(accountPwdDTO.getPassword())) {
            throw new BusinessException(CommonErrorCode.E_100101);
        }
        LambdaUpdateWrapper<Account> qw = new LambdaUpdateWrapper<>();
        qw.eq(Account::getId, accountPwdDTO.getAccountId());
        qw.eq(Account::getUsername, accountPwdDTO.getAccountId());
        Account account = accountMapper.selectOne(qw);
        if (account == null) {
            throw new BusinessException(CommonErrorCode.E_100104);
        }
        qw.set(Account::getPassword, MD5Util.getMd5(account.getPassword()));
        int result = accountMapper.update(null, qw);
        return  result > 0;
    }

    /**
     * 绑定账号和租户关系
     * @param tenantId
     * @param accountId
     * @param isAdmin
     */
    public void bindTenantAccount(Long tenantId, Long accountId, Boolean isAdmin) {
        //设置为管理员,需要在创建租户时传值过来
        isAdmin = (isAdmin != null && isAdmin);
        log.info("绑定账号和租户的关系");
        tenantMapper.insertTenantAccount(tenantId, accountId, isAdmin);
    }

    /**
     * 将某账号从租户内移除,租户管理员不可移除
     * @param tenantId 租户id
     * @param username 用户名
     */
    @Override
    public void unbindTenant(Long tenantId, String username) {
        //1.租户内非管理员账号
        List<TenantAccount> tenantAccounts = accountMapper.selectNoAdmin(tenantId, username);
        for (TenantAccount list : tenantAccounts) {
            //2.删除账号-租户的关系
            Long accountId = list.getAccountId();
            tenantAccountMapper.deleteAccountInTenant(tenantId, accountId);
            //3.账号在本租户下的角色关系
            accountRoleMapper.deleteByUserNameInTenant(tenantId, username);
            //4.判断员工账号是否被其他租户使用
            int i = tenantAccountMapper.selectTenantByUsernameInAccount(username);
            if (i > 0) {
                throw new BusinessException(CommonErrorCode.E_110008);
            }
            //5.如果没有被使用，删除员工对应的账号
            QueryWrapper<Account> qw = new QueryWrapper<>();
            qw.lambda().eq(Account::getUsername, username);
            accountMapper.delete(qw);
        }
    }

    /**
     * 根据用户名判断账户是否存在
     * @param username 用户名
     * @return
     */
    @Override
    public boolean isExistAccountByUsername(String username) {
        int i = accountMapper.selectAccountByName(username);
        return i > 0;
    }

    /**
     * 根据手机号判断是否存在
     * @param mobile 手机号
     * @return
     */
    @Override
    public boolean isExistAccountByMobile(String mobile) {
        log.info("判断手机号在账号是否存在");
        int i = accountMapper.selectAccountByMobile(mobile);
        return i > 0;
    }

    /**
     * 根据用户名获取账号信息
     * @param username 用户名
     * @return
     */
    @Override
    public AccountDTO getAccountByUsername(String username) {
        QueryWrapper<Account> qw = new QueryWrapper<>();
        qw.lambda().eq(Account::getUsername, username);
        Account account = accountMapper.selectOne(qw);
        return AccountConvert.INSTANCE.entity2dto(account);
    }

    /**
     * 根据手机号获取账号信息
     * @param mobile 手机号
     * @return
     */
    @Override
    public AccountDTO getAccountByMobile(String mobile) {
        QueryWrapper<Account> qw = new QueryWrapper<>();
        qw.lambda().eq(Account::getMobile, mobile);
        Account account = accountMapper.selectOne(qw);
        return AccountConvert.INSTANCE.entity2dto(account);
    }

    /**
     * 根据用户名判断账号是否在某租户内
     * @param tenantId 租户id
     * @param username 用户名
     * @return
     */
    @Override
    public boolean isExistAccountInTenantByUsername(Long tenantId, String username) {
        int i = accountMapper.selectAccountInTenantByName(tenantId, username);
        return i > 0;
    }

    /**
     * 根据手机号判断账号是否在某租户内
     * @param tenantId 租户id
     * @param mobile 手机号
     * @return
     */
    @Override
    public boolean isExistAccountInTenantByMobile(Long tenantId, String mobile) {
        int i = accountMapper.selectAccountInTenantByMobile(tenantId, mobile);
        return i > 0;
    }

    /**
     * 检索账号
     * @param accountQuery 账号查询条件
     * @param pageNo 查询页
     * @param pageSize 页记录数
     * @param sortBy 排序字段
     * @param order 顺序
     * @return
     */
    @Override
    public PageVO<AccountDTO> queryAccount(AccountQueryDTO accountQuery, Integer pageNo, Integer pageSize, String sortBy, String order) {
        Page<AccountDTO> page = new Page<>(pageNo, (pageSize == null || pageSize < 1 ? 10 : pageSize));
        List<AccountDTO> tenants = accountMapper.selectAccountByPage(page, accountQuery, sortBy, order);
        return new PageVO<>(tenants, page.getTotal(), pageNo, pageSize);
    }

    /**
     * 查询某账号所属租户列表
     * @param username 用户名
     * @return
     */
    @Override
    public List<TenantDTO> queryAccountInTenant(String username) {
        AccountDTO accountDTO = getAccountByUsername(username);
        Long id = accountDTO.getId();
        return tenantMapper.selectAccountInTenant(id);
    }

    @Autowired
    private RestTemplate restTemplate;
    @Value("${sms.url}")
    private String smsUrl;

    /**
     * 发送短信验证码
     * @param phone
     * @return
     */
    @Override
    public String sendMessage(String phone) {
        log.info("调用短信微服务发送验证码，手机号:{}", phone);
        //验证码过期时间为600秒
        String url = smsUrl + "/generate?name=sms&effectiveTime=600";
        Map<String, Object> params = new HashMap();
        params.put("mobile", phone);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity(params, httpHeaders);
        //执行发送
        ResponseEntity<Map> exchange = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        log.info("验证码，返回值：{}", JSON.toJSONString(exchange));
        //接收返回结果
        Map map = (Map) exchange.getBody();
        Map resultMap = (Map) map.get("result");
        return  resultMap.get("key").toString();
    }

    /**
     * 校验手机验证码
     * @param smsKey
     * @param passwordOrMessage
     * @return
     */
    private Map verificationMessage(String smsKey, String passwordOrMessage) {
        String url = smsUrl + "/verify?name=sms&verificationKey=" + smsKey + "&verificationCode=" + passwordOrMessage;
        log.info("调用短信微服务校验验证码: url: {}", url);
        //验证验证码是否正确
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(params);
        ResponseEntity<Map> exchange = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        log.info("校验验证码: 返回值: {}", JSON.toJSONString(exchange));
        Map map = (Map) exchange.getBody();
        return map;
    }

    /**
     * 用户认证
     * @param authenticationInfo 认证请求信息
     * @return
     */
    @Override
    public AccountDTO authentication(AuthenticationInfo authenticationInfo) {
        //判断用户名密码登录的 还是手机验证码登录的 类型：前端传入
        //短信快捷认证、用户名密码认证、二维码认证等
        String type = authenticationInfo.getAuthenticationType();
        String userOrMobile = authenticationInfo.getPrincipal();
        String passwordOrMessage = authenticationInfo.getCertificate();

        //用户名密码认证
        if ("password".equals(type)) {
            //根据用户名判断是否存在
            AccountDTO account = getAccountByUsername(userOrMobile);
            if (account == null) {
                throw new BusinessException(CommonErrorCode.E_110001);
            }
            String salt = account.getSalt();
            String password1 = account.getPassword();
            String md5 = DigestUtils.md2Hex(passwordOrMessage + salt);
            //DigestUtils
            if (!md5.equals(password1)) {
                throw new BusinessException(CommonErrorCode.E_100115);
            }
            return account;
        } else if ("sms".equals(type)) {    //短信快捷认证
            //校验手机号格式
            if (!PhoneUtil.isMatches(userOrMobile)) {
                throw new BusinessException(CommonErrorCode.E_100109);
            }
            String smsKey = authenticationInfo.getSmsKey();
            //校验手机短信验证码
            Map map = verificationMessage(smsKey, passwordOrMessage);
            if (map.get("result") == null || !(Boolean) map.get("result")) {
                throw new BusinessException(CommonErrorCode.E_100102);
            }
            //根据手机号判断用户是否存在
            boolean byMobile = isExistAccountByMobile(userOrMobile);
            if (!byMobile) {
                throw new BusinessException(CommonErrorCode.E_110001);
            }
            AccountDTO account = getAccountByMobile(userOrMobile);
            return account;
        }
        return null;
    }

    /**
     * 用户登录，被uaa服务调用生成令牌
     * 1.调用authentication(AuthenticationInfo authenticationInfo)认证接口通过认证
     * 2.调用queryAccountInTenant(String username)获取该用户所属租户列表
     * 3.授权，调用AuthorizationService.authorize(String username, Long[] tenantIds)获取该用户在多个租户下的权限
     * 4.获取资源,调用loadResources(Map<Long, AuthorizationInfoDTO> tenantAuthorizationInfMap);，获取该用户在多个租户下的资源
     * @param loginRequest
     * @return
     *
     * 备忘录：
     * 1.由于token设计的可以跨多个租户，当前登入租户，需由应用方(可默认选第一个租户)掌控并传递，设计在有状态接口的参数中，不然无法知道用户当前的登入租户。
     * 2.在uaa生成token前，需要根据client_id获取当前接入应用所属租户ID(ResourceService.queryApplication(String applicationCode))，
     * 并放入token，此tenantId作为限流的limitApp，也是套餐拥有者标识，用于套餐限制
     * 3.token不能跨不同租户开发的应用
     */
    @Override
    public LoginInfoDTO login(LoginRequestDTO loginRequest) {
        AuthenticationInfo auth = new AuthenticationInfo();
        BeanUtils.copyProperties(loginRequest, auth);
        //认证返回
        AccountDTO accountDTO = authentication(auth);
        String username = accountDTO.getUsername();

        LoginInfoDTO loginInfo = new LoginInfoDTO();
        //账号的id username mobile
        loginInfo.setMobile(accountDTO.getMobile());
        loginInfo.setUsername(accountDTO.getUsername());
        loginInfo.setId(accountDTO.getId());

        List<TenantDTO> tenantDTOS = queryAccountInTenant(username);
        if (!CollectionUtils.isEmpty(tenantDTOS)) {
            loginInfo.setTenants(tenantDTOS);
            Long[] tenantIds = new Long[tenantDTOS.size()];
            int i = 0;
            for (TenantDTO t : tenantDTOS) {
                tenantIds[i++] = t.getId();
            }
            //map-authorize key: 租户id value: 权限
            Map<Long, AuthorizationInfoDTO> authorize = authorizationService.authorize(username, tenantIds);
            loginInfo.setTenantAuthorizationInfoMap(authorize);
        }
        return loginInfo;
    }

    /**
     * 根据接入客户端查询应用
     * @param clientId
     * @return
     */
    @Override
    public ApplicationDTO getApplicationDTOByClientId(String clientId) {
        QueryWrapper<ResourceApplication> queryWrapper = new QueryWrapper<ResourceApplication>();
        queryWrapper.lambda().eq(ResourceApplication::getCode, clientId);
        ResourceApplication resourceApplication = resourceApplicationMapper.selectOne(queryWrapper);
        return ResourceApplicationConvert.INSTANCE.entity2dto(resourceApplication);
    }

    @Override
    public TenantDTO getTenantByAccount(Long accountId, Boolean isAdmin) {
        TenantDTO tenant = null;
        //同一个手机号的账号，只能作为一个租户的管理员
        TenantAccount tenantAccount = tenantAccountMapper.selectOne(new QueryWrapper<TenantAccount>().lambda()
                .eq(TenantAccount::getAccountId, accountId).eq(TenantAccount::getIsAdmin, isAdmin));
        if (tenantAccount != null && tenantAccount.getAccountId() != null) {
            tenant = getTenant(tenantAccount.getTenantId());
        }
        return tenant;
    }

    @Override
    public AccountRoleQueryDTO queryAccountRole(String username, String roleCode, Long tenantId) {
        return tenantAccountMapper.selectAccountRole(username, roleCode, tenantId);
    }

    @Override
    public PageVO<AccountRoleQueryDTO> queryAdministratorByPage(AccountRoleDTO query, Integer pageNo, Integer pageSize) {
        if (null == query.getTenantId()) {
            throw new BusinessException(CommonErrorCode.E_200012);
        }
        Page<AccountRoleQueryDTO> page = new Page<>(pageNo, pageSize);
        List<AccountRoleQueryDTO> accountRole = tenantMapper.queryAdministratorByPage(page, query);
        return new PageVO<>(accountRole, page.getTotal(), pageNo, pageSize);
    }

    @Override
    public void checkCreateStaffAccountRole(Long tenantId, CreateAccountRequestDTO accountRequest, String[] roleCodes) {
        boolean byMobile = isExistAccountByMobile(accountRequest.getMobile());
        if (!byMobile) {
            //如果账号不存在，为员工创建账号并绑定角色
            AccountDTO account = createAccount(accountRequest);
            //绑定角色
            authorizationService.bindAccountRole(accountRequest.getUsername(), tenantId, roleCodes);
        } else {
            //如果已存在账号，判断是否绑定了角色（角色可能是多个），如果没有绑定，则绑定
            getAccountRoleBind(accountRequest.getUsername(), tenantId, roleCodes);
        }
        //绑定租户和账号关系
        bindTenant(tenantId, accountRequest.getUsername());
    }

    @Override
    public void getAccountRoleBind(String username, Long tenantId, String[] roleCodes) {
        //如果已存在账号，判断是否绑定了角色（角色可能是多个），如果没有绑定，则绑定
        List<AccountRoleDTO> accountRoleDTOS = authorizationService.queryAccountBindRole(username, tenantId, roleCodes);
        if (accountRoleDTOS.isEmpty()) {
            //为空，则全部绑定
            authorizationService.bindAccountRole(username, tenantId, roleCodes);
        } else {
            //不为空，求差集，将未绑定的绑定到账号
            ArrayList<String> listRoleCodes = new ArrayList<>(Arrays.asList(roleCodes));
            authorizationService.bindAccountRole(username, tenantId, listRoleCodes.stream().filter(item -> !accountRoleDTOS.contains(item)).toArray(String[]::new));
        }
    }
}
