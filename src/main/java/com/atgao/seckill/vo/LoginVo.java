package com.atgao.seckill.vo;

import com.atgao.seckill.annotation.IsMobile;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * @author ：atGaoJu
 * @date ：Created in 2022/3/24 4:02 PM
 * @GetHub：https://github.com/atChine
 * @Blog：https://blog.csdn.net/qq_43649569?spm=1000.2115.3001.5343
 */
@Data
public class LoginVo {
    @NotNull
    @IsMobile
    private String mobile;
    @NotNull
    @Length(min=32)
    private String password;
}
