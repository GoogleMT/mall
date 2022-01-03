package top.gumt.mall.order.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import top.gumt.common.utils.PageUtils;
import top.gumt.common.utils.Query;

import top.gumt.common.vo.MemberResponseVo;
import top.gumt.mall.order.constant.OrderConstant;
import top.gumt.mall.order.dao.OrderDao;
import top.gumt.mall.order.entity.OrderEntity;
import top.gumt.mall.order.feign.CartFeignService;
import top.gumt.mall.order.feign.MemberFeignService;
import top.gumt.mall.order.feign.WareFeignService;
import top.gumt.mall.order.interceptor.LoginInterceptor;
import top.gumt.mall.order.service.OrderService;
import top.gumt.mall.order.vo.MemberAddressVo;
import top.gumt.mall.order.vo.OrderConfirmVo;
import top.gumt.mall.order.vo.OrderItemVo;
import top.gumt.common.vo.SkuHasStockVO;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    private CartFeignService cartFeignService;
    @Autowired
    private WareFeignService wareFeignService;
    @Autowired
    private MemberFeignService memberFeignService;

    @Autowired
    private ThreadPoolExecutor executor;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo confirmOrder() {
        MemberResponseVo memberResponseVo = LoginInterceptor.loginUser.get();
        OrderConfirmVo confirmVo = new OrderConfirmVo();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        CompletableFuture<Void> itemAndStockFuture = CompletableFuture.supplyAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            //1. 查出所有选中购物项
            //List<OrderItemVo> checkedItems = cartFeignService.getCheckedItems();
            List<OrderItemVo> checkedItems = cartFeignService.getCurrentUserCartItems();
            confirmVo.setItems(checkedItems);
            return checkedItems;
        }, executor).thenAcceptAsync((items) -> {
            //4. 库存
            List<Long> skuIds = items.stream().map(OrderItemVo::getSkuId).collect(Collectors.toList());
            Map<Long, Boolean> hasStockMap = wareFeignService.getSkuHasStocks(skuIds).stream().collect(Collectors.toMap(SkuHasStockVO::getSkuId, SkuHasStockVO::getHasStock));
            confirmVo.setStocks(hasStockMap);
        }, executor);

        //2. 查出所有收货地址
        CompletableFuture<Void> addressFuture = CompletableFuture.runAsync(() -> {
            List<MemberAddressVo> addressByUserId = memberFeignService.getAddressByUserId(memberResponseVo.getId());
            confirmVo.setMemberAddressVos(addressByUserId);
        }, executor);

        //3. 积分
        confirmVo.setIntegration(memberResponseVo.getIntegration());

        //5. 总价自动计算
        //6. 防重令牌
        String token = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberResponseVo.getId(), token, 30, TimeUnit.MINUTES);
        confirmVo.setOrderToken(token);
        try {
            CompletableFuture.allOf(itemAndStockFuture, addressFuture).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return confirmVo;
    }
}