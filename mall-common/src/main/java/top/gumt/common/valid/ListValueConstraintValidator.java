package top.gumt.common.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

public class ListValueConstraintValidator implements ConstraintValidator<ListValue,Integer> {// <注解,校验值类型>
    // 存储所有可能的值
    private Set<Integer> set=new HashSet<>();
    @Override  // 初始化，你可以获取注解上的内容并进行处理
    public void initialize(ListValue constraintAnnotation) {
        // 获取后端写好限制 这个value就是ListValue里的Value, 如: @ListValue(value={0, 1})
        int[] value = constraintAnnotation.value();
        for (int i : value) {
            set.add(i);
        }
    }

    @Override  // 重写验证逻辑
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        // 看是否在限制的值里
        return  set.contains(value);
    }
}
