package com.bjpowernode.dataservice.mapper;

import com.bjpowernode.api.model.User;
import com.bjpowernode.api.pojo.UserAccountInfo;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {

    // 获取用户和资金信息
    UserAccountInfo selectUserAccountById(@Param("uid") Integer uid);

    // 更新实名认证信息
    int updateRealname(@Param("phone") String phone, @Param("name") String name, @Param("idCard") String idCard);

    // 登录(查询登录信息)
    User selectLogin(@Param("phone") String phone, @Param("loginPassword") String newPassword);

    // 添加记录，获取主键值
    int insertReturnPrimaryKey(User user);

    // 根据手机号查询数据
    User selectByPhone(@Param("phone") String phone);

    // 统计注册的人数
    int selectCountUser();

    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
}