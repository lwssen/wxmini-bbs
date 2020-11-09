package com.sss.sssforum.wxclient.dto;

import com.sss.sssforum.constant.CommonConstant;
import com.sss.sssforum.utils.ServletUtils;
import com.sss.sssforum.utils.SqlUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author lws
 * @date 2020-08-07 20:54
 **/
@Data
public class PageDTO {
    /** 当前记录起始索引 */
    private Integer pageNum;
    /** 每页显示记录数 */
    private Integer pageSize;
    /** 排序列 */
    private String orderByColumn;
    /** 排序的方向 "desc" 或者 "asc". */
    private String isAsc;

    public String getOrderBy()
    {
        if (StringUtils.isEmpty(orderByColumn))
        {
            return "";
        }
        return SqlUtil.toUnderScoreCase(orderByColumn) + " " + isAsc;
    }
    public static PageDTO getPageDTO(){
        PageDTO pageDomain = new PageDTO();
        pageDomain.setPageNum(ServletUtils.getParameterToInt(CommonConstant.PAGE_NUM));
        pageDomain.setPageSize(ServletUtils.getParameterToInt(CommonConstant.PAGE_SIZE));
        pageDomain.setOrderByColumn("create_time");
        pageDomain.setIsAsc(" desc");
        return pageDomain;

    }

    public static PageDTO buildPageRequest()
    {
        return getPageDTO();
    }
}
