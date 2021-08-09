package cn.edu.zjut;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import cn.edu.zjut.dao.UserInfoMapper;
import cn.edu.zjut.entity.UserInfo;

/**
 * @author zett0n
 * @date 2021/8/8 14:57
 */
@SpringBootTest
public class UserDAOTest {
    private static final Logger log = LoggerFactory.getLogger(UserDAOTest.class);

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Test
    public void test() {
        UserInfo userInfo = this.userInfoMapper.selectByPrimaryKey(1);
        if (userInfo == null) {
            log.debug("用户不存在");
        } else {
            log.debug("user name: {}", userInfo.getName());
        }
    }
}
