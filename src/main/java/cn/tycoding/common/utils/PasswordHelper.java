package cn.tycoding.common.utils;

import cn.tycoding.common.dto.SysConstant;
import cn.tycoding.system.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.springframework.stereotype.Component;

/**
 * @author tycoding
 * @date 2019-03-15
 */
@Component
public class PasswordHelper {

    //实例化RandomNumberGenerator对象，用于生成一个随机数
    @Setter
    private RandomNumberGenerator randomNumberGenerator = new SecureRandomNumberGenerator();
    //散列算法名称
    @Getter
    @Setter
    private String algorithName = SysConstant.ALGORITHNAME;
    //散列迭代次数
    @Getter
    @Setter
    private int hashInterations = SysConstant.HASHNUM;

    public RandomNumberGenerator getRandomNumberGenerator() {
        return randomNumberGenerator;
    }

    //加密算法
    public void encryptPassword(User user) {
        if (user.getPassword() != null) {
            // 如果没有盐值就进行随机生成盐值，但是Shiro进行密码校验并不会再次生成盐值，因为是随机盐，Shiro会根据数据库中储存的盐值以及你注入的加密方式进行校验，而不是使用这个工具类进行校验的。
            //对user对象设置盐：salt；这个盐值是randomNumberGenerator生成的随机数，所以盐值并不需要我们指定
            user.setSalt(randomNumberGenerator.nextBytes().toHex());

            //调用SimpleHash指定散列算法参数：1、算法名称；2、用户输入的密码；3、盐值（随机生成的）；4、迭代次数
            String newPassword = new SimpleHash(
                    algorithName,
                    user.getPassword(),
                    ByteSource.Util.bytes(user.getSalt()),
                    hashInterations).toHex();
            user.setPassword(newPassword);
        }
    }
}
