package cn.edu.zjut.validater;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * @author zett0n
 * @date 2021/8/9 16:20
 */
@Component
public class ValidatorImpl implements InitializingBean {
    private Validator validator;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    public ValidationResult validate(Object bean) {
        ValidationResult result = new ValidationResult();
        Set<ConstraintViolation<Object>> constraintViolations = this.validator.validate(bean);
        if (constraintViolations.size() > 0) {
            result.setHasErrors(true);
            constraintViolations.forEach(constraintViolation -> {
                String propertyName = constraintViolation.getPropertyPath().toString();
                String errMsg = constraintViolation.getMessage();
                result.getErrorMsgMap().put(propertyName, errMsg);
            });
        }
        return result;
    }
}
