package com.tongji.exam.service.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.IdUtil;
import com.tongji.exam.annotation.MyLog;
import com.tongji.exam.qo.RegisterDTO;
import com.tongji.exam.entity.Action;
import com.tongji.exam.entity.Page;
import com.tongji.exam.entity.Role;
import com.tongji.exam.entity.User;
import com.tongji.exam.enums.LoginTypeEnum;
import com.tongji.exam.enums.RoleEnum;
import com.tongji.exam.qo.LoginQo;

import com.tongji.exam.qo.UserInfoQo;
import com.tongji.exam.repository.ActionRepository;
import com.tongji.exam.repository.PageRepository;
import com.tongji.exam.repository.RoleRepository;
import com.tongji.exam.repository.UserRepository;

import com.tongji.exam.repository.*;

import com.tongji.exam.service.UserService;
import com.tongji.exam.utils.JwtUtils;
import com.tongji.exam.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PageRepository pageRepository;

    @Autowired
    ActionRepository actionRepository;

    @Value("${user.default.avatar}")
    private String defaultAvatar;

    @Value("${user.default.username}")
    private String defaultUsername;

    @Autowired
    UpCodeRepository upCodeRepository;

    /**
     * 用户注册
     * @param registerDTO 用户注册所需的信息类
     * @return 注册成功的用户对象
     */
    @Override
    public User register(RegisterDTO registerDTO) {
        try {
            User user = new User();
            user.setUserId(IdUtil.simpleUUID());
            // 好像还缺少个用户名,用"exam_user_手机号"来注册：需要校验唯一性数据字段已经设置unique了，失败会异常地
            user.setUserUsername(defaultUsername + "_" + registerDTO.getMobile());
            // 初始化昵称和用户名相同
            user.setUserNickname(user.getUserUsername());
            // 这里还需要进行加密处理，后续解密用Base64.decode()
            user.setUserPassword(Base64.encode(registerDTO.getPassword()));
            // 默认设置为学生身份，需要老师和学生身份地话需要管理员修改
            if(registerDTO.getUpup()!=null&&upCodeRepository.findUpCodeEntityByCode(registerDTO.getUpup())!=null)
            {
                user.setUserRoleId(RoleEnum.TEACHER.getId());
                upCodeRepository.deleteById(registerDTO.getUpup());
            }
            else
                user.setUserRoleId(RoleEnum.STUDENT.getId());
            // 设置头像图片地址, 先默认一个地址，后面用户可以自己再改
            user.setUserAvatar(defaultAvatar);
            // 设置描述信息，随便设置段默认的
            user.setUserDescription("welcome to online exam system");
            // 需要验证这个邮箱是不是已经存在：数据字段已经设置unique了，失败会异常地
            if(userRepository.findByUserEmail(registerDTO.getEmail())!=null){
                throw new Exception("邮箱已被注册");
            }
            user.setUserEmail(registerDTO.getEmail());
            // 需要验证手机号是否已经存在：数据字段已经设置unique了，失败会异常地
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            user.setCreateTime(simpleDateFormat.parse(simpleDateFormat.format(new Date())));
            user.setUpdateTime(simpleDateFormat.parse(simpleDateFormat.format(new Date())));
            if(userRepository.findByUserEmail(registerDTO.getMobile())!=null){
                throw new Exception("手机号已被注册");
            }
            user.setUserPhone(registerDTO.getMobile());
            userRepository.save(user);
            System.out.println(user);
            return user;
        } catch (Exception e) {
            e.printStackTrace(); // 用户已经存在或验证码错误
            // 出异常，返回null，表示注册失败
            return null;
        }
    }

    /**
     * 用户登录
     * @param loginQo
     * @return
     */
    @Override
    public String login(LoginQo loginQo) {
        User user;
        if (LoginTypeEnum.USERNAME.getType().equals(loginQo.getLoginType())) {
            // 登陆者用地是用户名
            user = userRepository.findByUserUsername(loginQo.getUserInfo());
        } else {
            // 登陆者用地是邮箱
            user = userRepository.findByUserEmail(loginQo.getUserInfo());
        }
        if (user != null) {
            // 如果user不是null即能找到，才能验证用户名和密码
            // 数据库存的密码
            String passwordDb = Base64.decodeStr(user.getUserPassword());
            // 用户请求参数中的密码
            String passwordQo = loginQo.getPassword();
            System.out.println(passwordDb);
            System.out.println(passwordQo);
            if (passwordQo.equals(passwordDb)) {
                // 如果密码相等地话说明认证成功,返回生成的token，有效期为一天
                return JwtUtils.genJsonWebToken(user);
            }
        }
        return null;
    }

    /**
     * 获取用户信息
     * @param userId 用户id
     * @return 用户Vo
     */
    @Override
    public UserVo getUserInfo(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        UserVo userVo = new UserVo();
        assert user != null;
        BeanUtils.copyProperties(user, userVo);
        return userVo;
    }
    /**
     * 获取用户信息
     * @param userId
     * @return
     */
    @Override
    public UserInfoVo getInfo(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        assert user != null;
        UserInfoVo userInfoVo = new UserInfoVo();
        // 1.尽可能的拷贝属性
        BeanUtils.copyProperties(user, userInfoVo);
        Integer roleId = user.getUserRoleId();
        Role role = roleRepository.findById(roleId).orElse(null);
        assert role != null;
        String roleName = role.getRoleName();

        // 2.设置角色名称
        userInfoVo.setRoleName(roleName);

        // 3.设置当前用户的角色细节
        RoleVo roleVo = new RoleVo();
        BeanUtils.copyProperties(role, roleVo);

        // 4.设置角色的可访问页面
        String rolePageIds = role.getRolePageIds();
        String[] pageIdArr = rolePageIds.split("-");
        List<PageVo> pageVoList = new ArrayList<>();
        for (String pageIdStr : pageIdArr) {
            // 获取页面的id
            Integer pageId = Integer.parseInt(pageIdStr);

            // 4.1 向Role中添加Page
            Page page = pageRepository.findById(pageId).orElse(null);
            PageVo pageVo = new PageVo();
            BeanUtils.copyProperties(page, pageVo);

            // 4.2 向Page中添加action
            List<ActionVo> actionVoList = new ArrayList<>();
            String actionIdsStr = page.getActionIds();
            String[] actionIdArr = actionIdsStr.split("-");
            for (String actionIdStr : actionIdArr) {
                Integer actionId = Integer.parseInt(actionIdStr);
                Action action = actionRepository.findById(actionId).orElse(null);
                ActionVo actionVo = new ActionVo();
                assert action != null;
                BeanUtils.copyProperties(action, actionVo);
                actionVoList.add(actionVo);
            }
            // 设置actionVoList到pageVo中，然后把pageVo加到pageVoList中
            pageVo.setActionVoList(actionVoList);
            // 设置pageVoList，下面再设置到RoleVo中
            pageVoList.add(pageVo);
        }
        // 设置PageVo的集合到RoleVo中
        roleVo.setPageVoList(pageVoList);
        // 最终把PageVo设置到UserInfoVo中，这样就完成了拼接
        userInfoVo.setRoleVo(roleVo);
        return userInfoVo;
    }
    /**
     * 更新个人信息
     * @param userInfoQo 前端传来的要修改的用户信息
     * @param user_id 用户id
     * @return 更新结果，成功返回ok，失败返回null
     */
    @Override
    @MyLog
    public String updateInfo(UserInfoQo userInfoQo,String user_id) {
        User user=userRepository.findByUserId(user_id);
        if(user==null){
            return null;
        }
        if(userInfoQo.getUserNickname()!=null){
            user.setUserNickname(userInfoQo.getUserNickname());
        }
        if(userInfoQo.getUserEmail()!=null) {
            user.setUserEmail(userInfoQo.getUserEmail());
        }
        if(userInfoQo.getUserAvatar()!=null) {
            user.setUserAvatar(userInfoQo.getUserAvatar());
        }
        if(userInfoQo.getUserPassword()!=null){
            user.setUserPassword(Base64.encode(userInfoQo.getUserPassword()));
        }
        if(userInfoQo.getUserDescription()!=null){
            user.setUserDescription(userInfoQo.getUserDescription());
        }
        if(userInfoQo.getUserPhone()!=null) {
            user.setUserPhone(userInfoQo.getUserPhone());
        }
        userRepository.save(user);
        return "ok";
    }
}
