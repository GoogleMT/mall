package top.gumt.common.exception;

/**
 * 错误枚举类
 * 1. 错误码定义规则为5位数字
 * 2. 前两位表示业务场景，最后三位表示错误码, 如： 100001, 10：通用 001: 系统未知异常
 * 3. 维护错误码后需要维护错误描述，将他们定义为枚举形式
 * 错误码列表：
 *  10： 通用
 *      001 参数格式错误
 *  11： 商品
 *  12： 订单
 *  14： 物流
 */
public enum BizCodeEnum {
    UNKNOW_EXEPTION(10000,"系统未知异常"),
    VALID_EXCEPTION( 10001,"参数格式校验失败");
    private int code;
    private String msg;
    BizCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    public int getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }
}
