package com.bloghub.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bloghub.entity.SysLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统日志 Mapper
 */
@Mapper
public interface LogMapper extends BaseMapper<SysLog> {
}
