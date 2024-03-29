package cn.tycoding.system.mapper;

import cn.tycoding.common.config.MyMapper;
import cn.tycoding.system.entity.User;
import cn.tycoding.system.entity.UserWithRole;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author tycoding
 * @date 2019-01-19
 */
@Repository
public interface UserMapper extends MyMapper<User> {

    List<User> queryList(User user);

    List<UserWithRole> findById(Long id);

    void updateUser(User user);
}
