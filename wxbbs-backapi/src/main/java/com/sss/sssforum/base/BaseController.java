package com.sss.sssforum.base;

import com.github.pagehelper.PageHelper;
import com.sss.sssforum.wxclient.dto.PageDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

import java.util.Objects;

/**
 * @author lws
 * @date 2020-08-07 20:53
 **/
public class BaseController {

    /**
     * 设置请求分页数据
     */
    protected void startPage()
    {
        PageDTO pageDomain = PageDTO.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
//         String sortMode="desc";
//        String  orderByColumn="create_time";
//        String orderBy =orderByColumn+" "+sortMode;
//        if (StringUtils.isBlank(orderBy)) {
//            orderBy= Strings.EMPTY;
//        }
        if (Objects.nonNull(pageNum) && Objects.nonNull(pageSize))
        {
            PageHelper.startPage(pageNum, pageSize);
        }
    }

    protected void startPage(String orderByColumn,String sortMode) {

        PageDTO pageDomain = PageDTO.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum()==null?1:pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize()==null?10:pageDomain.getPageSize();
            String orderBy =orderByColumn+" "+sortMode;
            if (StringUtils.isBlank(orderBy)) {
                orderBy= Strings.EMPTY;
            }
            PageHelper.startPage(pageNum, pageSize, orderBy);

    }
}
