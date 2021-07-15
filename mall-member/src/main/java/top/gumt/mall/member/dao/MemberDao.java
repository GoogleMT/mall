package top.gumt.mall.member.dao;

import top.gumt.mall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author zhaoming
 * @email gumt0310@gmail.com
 * @date 2021-07-15 21:14:31
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
