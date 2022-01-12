package top.gumt.mall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import top.gumt.mall.order.vo.PayVo;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {
    //在支付宝创建的应用的id
    private   String app_id = "2021000118694183";

    /**
     * 商户私钥，您的PKCS8格式RSA2私钥
     */
    private String merchant_private_key = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQCVSpDyULFfQ4IcIue+S1PMlaE0rxXl9xtw0DqVit1L4fHlEV7nRW012penRQ0LveHNAhm8BJl6IUDo+BW1/QPHkPupyd8Es6QXqpPWnWe9W/DbTN0oWPckE+Nls1V4RAoMOpnCeApc4+0rx1m9tfJAwjbxz3AQFfhu+py3j0revOPvmepaBWbPB2OCvwFE8F19756a6kvdRXiNnh+SfWns2/9rPGjgyf2X8ZJ9wgsLANVCtnHpKoroSoJrJ13CB0spPvSro4kGuaB8ZG+zP63FtWL4TqxCwWZbCivfGvrFap9v4X0zBhxgfuXZFUEtBazP6hYNAYxmKIwAnCo60u33AgMBAAECggEAc7Rz/GnP3p5qs7XV2GV3UCNT6oocNbw710Z7cTVL6Cj4mmnJHQNw7gE58lkREF2dKI+NNGx4KvCk5yqHYSH0kPJsKh6cK3+zGOiKZhZetaMRXFYJPeEdwidr4YGJJ4nMI8gzLUZt+appQbnicqcWV1xuyyDToJP2lMTFO3riMxgNLQjwcmPsZwtAFwFN4cSWvAcxRPWUmSJpz9nGenHfnP2IYxpPgCeBKCEUbSqRfPPlxv5v73RzsZft+RCIHKWccEWc3sKszfVrr4UycESMyKwtRUS3mJQ1oMAal69vt7y6oKvhd/gG6CrRkg2sRJUYkcUSN35tTg1hsbpLWVNXsQKBgQDoDz1iKAuIOrHMcHut+zdqFXjch12V1RcObrr3c5HFnRJGFjHW9wSaOqSzscSfoaHJiMBoqrzW5xoGm6N2dPv9LgpX5wy60K1Nth34i56UwfH/Aicce24DLjO3/IYs889T1nZNUhiAtr+ATZG5u9EpbHjIjkIUgxVnwldeAjFIXwKBgQCksWR7mTMivc5EzRN3KGKWNtMdpLwI0k2Or1uaPzSSP68qe8VKjwJxIXajud2p834HSBhIoNV2UvisEbqkjKf9XbQHj1ppoN8FHhQtBzoNpzzpaP4Sj892cYpJFf6sbW7ezrb+cklB2SAphLk9Sp8hR3tsWPgqPg33ehG0yrghaQKBgQCdrfVEo/cqCs/THT88ua0unQZxul0BuTgga2cJSvLq9s565wvYiCINdfmBzDHRUe9v1EcBF7qWv5M0mD6a9+RdZRJMJkfFDJQlcLCuuJf5dksrvle7B7d528PyqVi2ZLgkvF3ILhdOJl67HHn/XTpDTlxH0jneXC+IQEpDK3Q3OwKBgQCAH8VlZnzitTkIS2auV8vCwuog8KycTEesDn48FJnogQTcDyggZftnWbo7i3iII/bS4WKK9hy7DbeJ3c0PAKIcOU66ZurWrCF0kL7vfOTMBGQdzeGDPUAQDCqhyyuypvwO+FBiyTR2tt0Pj8KwjSIZOMwrMVf3PQmOkve9Z8wxqQKBgQCfpR89tug1GMablAu+0AFeRC8k0oHj7lEYYgGzB3X21tZkZtWnWc1hNyyv3sXDlFhPBo98Iw6wPit6TFf3BgNFoXzdW++r2JIxeRTGMZNtBphGXnXzwmG/sx019diyhdc+S7Pe171xXX7cg6Skrlc3uWG9GVRTTOnlDLZydXe/5A==";

    /**
     * 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
     */
    private String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmmcBXBylMnBTbakIT9OsAjtsscOH/qB6vMqJF4rwIverOEiMr4ya86mJktV17JV9nF3vGXb62KggoFzr5XtsLfOmyxj2oNof91nf7bQGtbDz+X4bpYERz1qZeAp8sg+6EXlVFZ55tTqDNXnYFFLBrmKiuzVrtgH4vknvuZHHwel3g3K8ure64PwzxjBcko/fgb4kXzt2Cn5pV269r9NgdHUXJYMuDhcrZgKZn4dDRqtYQ4TfxKWIszSNi2cBSk4n6ApElrNpUODertji/3Ft0ZUG6rUx62OsfB1bix9vp/uzycdUG3P3tsilYngeT1R7gwzgnZUk4fVZHKJVWSYjBwIDAQAB";

    /**
     * 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
     * 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
     */
    private  String notify_url="http://nxz9r8.natappfree.cc/payed/notify";

    /**
     * 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
     * 同步通知，支付成功，一般跳转到成功页
     */
    private  String return_url="http://order.mall.com/memberOrder.html";

    /**
     * 签名方式
     */
    private  String sign_type = "RSA2";

    /**
     * 字符编码格式
     */
    private  String charset = "utf-8";

    /**
     * 支付宝网关； https://openapi.alipaydev.com/gateway.do
     */
    private  String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    public  String pay(PayVo vo) throws AlipayApiException {

        /**
         *AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
         * 1、根据支付宝的配置生成一个支付客户端
         */
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        /**
         * 2、创建一个支付请求 //设置请求参数
         */
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        /**
         * 商户订单号，商户网站订单系统中唯一订单号，必填
         */
        String out_trade_no = vo.getOut_trade_no();
        /**
         * 付款金额，必填
         */
        String total_amount = vo.getTotal_amount();
        /**
         * 订单名称，必填
         */
        String subject = vo.getSubject();
        /**
         * 商品描述，可空
         */
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                +"\"timeout_express\":\"1m\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        /**
         * 会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
         */
        System.out.println("支付宝的响应："+result);

        return result;

    }
}
