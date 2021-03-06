package cn.edu.zjut.controller;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.alibaba.druid.util.StringUtils;

import cn.edu.zjut.controller.vo.UserVO;
import cn.edu.zjut.error.BusinessException;
import cn.edu.zjut.error.EmBusinessError;
import cn.edu.zjut.response.CommonReturnType;
import cn.edu.zjut.service.UserService;
import cn.edu.zjut.service.model.UserModel;

/**
 * @author zett0n
 * @date 2021/8/8 15:12
 */
@RestController
@RequestMapping("/user")
@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*")
public class UserController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    private UserVO convertFromModel(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userModel, userVO);
        return userVO;
    }

    @GetMapping("{id}")
    public CommonReturnType getUserById(@PathVariable("id") Integer id) throws BusinessException {
        if (id == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "未输入用户id");
        }

        UserModel userModel = this.userService.getUserById(id);
        if (userModel == null) {
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }
        UserVO userVO = convertFromModel(userModel);
        return CommonReturnType.create(userVO);
    }

    @PostMapping("/otp")
    public CommonReturnType otp(@RequestParam(name = "telephone") String telephone) throws BusinessException {
        if (telephone == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "未输入手机号");
        }

        // 按一定规则生成验证码
        Random random = new Random();
        int randomInt = 10000 + random.nextInt(99999);
        String otpCode = String.valueOf(randomInt);

        // 将验证码与用户手机号相关联
        this.httpServletRequest.getSession().setAttribute(telephone, otpCode);

        log.debug("telephone: {}, otpCode: {}", telephone, otpCode);
        return CommonReturnType.create(null);
    }

    // 用户注册接口
    @PostMapping("/register")
    public CommonReturnType register(@RequestParam(name = "telephone") String telephone,
        @RequestParam(name = "otpCode") String otpCode, @RequestParam(name = "name") String name,
        @RequestParam(name = "gender") Integer gender, @RequestParam(name = "age") Integer age,
        @RequestParam(name = "password") String password)
        throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        // 验证手机号和对应的otpCode相符合
        String inSessionOtpCode = (String)this.httpServletRequest.getSession().getAttribute(telephone);
        // 工具类的equals已经进行了判空的处理
        if (!StringUtils.equals(otpCode, inSessionOtpCode)) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "短信验证码不符合！");
        }

        // 用户注册流程
        UserModel userModel = new UserModel();
        userModel.setName(name);
        userModel.setGender(new Byte(String.valueOf(gender.intValue())));
        userModel.setAge(age);
        userModel.setTelephone(telephone);
        userModel.setRegisterMode("by phone");
        userModel.setEncryptedPassword(EncodeByMd5(password));
        this.userService.register(userModel);
        return CommonReturnType.create(null);
    }

    // 用户登录接口
    @PostMapping("/login")
    public CommonReturnType login(@RequestParam(name = "telphone") String telephone,
        @RequestParam(name = "password") String password)
        throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {

        // 入参校验
        if (StringUtils.isEmpty(telephone) || StringUtils.isEmpty(password))
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);

        UserModel userModel = this.userService.validateLogin(telephone, EncodeByMd5(password));

        // 没有任何异常，则加入到用户登录成功的session内。这里不用分布式的处理方式（假设单点登录）
        this.httpServletRequest.getSession().setAttribute("IS_LOGIN", true);
        this.httpServletRequest.getSession().setAttribute("LOGIN_USER", userModel);

        return CommonReturnType.create(null);
    }
}
