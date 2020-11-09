package com.sss.sssforum.wxclient.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 敏感词实体类
 *
 * @author lws
 * @date 2020-08-16 13:56
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SensitiveWordDTO {

    private String sensitiveWord;

    private Long index;

    private String changeValue;
}
