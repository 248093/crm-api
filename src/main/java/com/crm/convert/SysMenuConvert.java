package com.crm.convert;

import com.crm.entity.SysMenu;
import com.crm.vo.SysMenuVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 *
 */
@Mapper
public interface SysMenuConvert {
    SysMenuConvert INSTANCE = Mappers.getMapper(SysMenuConvert.class);

    SysMenu convert(SysMenuVO vo);

    SysMenuVO convert(SysMenu entity);

    List<SysMenuVO> convertList(List<SysMenu> list);

}
