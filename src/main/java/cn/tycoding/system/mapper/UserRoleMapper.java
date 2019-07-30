package cn.tycoding.system.mapper;

import cn.tycoding.common.config.MyMapper;
import cn.tycoding.system.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author tycoding
 * @date 2019-02-03
 */
@Repository
public interface UserRoleMapper extends MyMapper<UserRole> {
}
