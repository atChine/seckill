package com.atgao.seckill.vo;


import com.atgao.seckill.annotation.IsMobile;
import com.atgao.seckill.utils.ValidatorUtil;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 *
 * @since 1.0.0
 */
public class IsMobileValidator implements ConstraintValidator<IsMobile,String> {

	private boolean required = false;
	//重写初始化方法
	@Override
	public void initialize(IsMobile constraintAnnotation) {
		required = constraintAnnotation.required();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (required){
			return ValidatorUtil.isMobile(value);
		}else {
			if (StringUtils.isEmpty(value)){
				return true;
			}else {
				return ValidatorUtil.isMobile(value);
			}
		}
	}
}