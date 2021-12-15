package top.gumt.mall.product.web;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import top.gumt.mall.product.entity.CategoryEntity;
import top.gumt.mall.product.service.CategoryService;
import top.gumt.mall.product.vo.Catelog2Vo;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Controller
public class IndexController {

    @Autowired
    CategoryService categoryService;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    StringRedisTemplate redisTemplate;


    @GetMapping(value = {"/","/index.html"})
    public String indexPage(Model model){
        //1. 查询出所有的一级分类
        List<CategoryEntity> categoryEntityList=categoryService.getLevel1Categories();
        model.addAttribute("categories",categoryEntityList);
        return  "index";
    }

    @ResponseBody
    @GetMapping("index/catalog.json")
    public Map<String, List<Catelog2Vo>> getCatelogJson(){
        Map<String, List<Catelog2Vo>> map=categoryService.getCatelogJson();
        return map;
    }

    @ResponseBody
    @GetMapping("/hello")
    public String hello() {
        // 获取一把锁，只要锁的名字一样，就是同同一把锁
        RLock lock = redissonClient.getLock("my-lock");
        // 加锁 阻塞式等待直到获取到锁
        lock.lock();
        // 锁的自动续期，如果执行业务时间过长，运行期间会自动给锁续上新的30s，不用单行业务时间长，锁自动过期被删除。
        // 加锁的业务只要运行完成，就不会在给锁续期了，即使不手动解锁，锁默认在30s以后自动删除
        // 执行业务代码
        try {
            System.out.println("加锁成功，执行业务..." + Thread.currentThread().getId());
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 3. 不管业务执行成功与否都解锁
            System.out.println("释放锁..." + Thread.currentThread().getId());
            lock.unlock();
        }
        return "hello";
    }

    @ResponseBody
    @GetMapping("/write")
    public String writeValue() {
        RReadWriteLock lock = redissonClient.getReadWriteLock("rw-lock");
        String uuid = "";
        RLock rLock = lock.writeLock();
        try {
            rLock.lock();
            uuid = UUID.randomUUID().toString();
            Thread.sleep(30000);
            redisTemplate.opsForValue().set("writeValue", uuid);
        } catch (InterruptedException e) {

        } finally {
            rLock.unlock();
        }
        return uuid;
    }

    @ResponseBody
    @GetMapping("/read")
    public String readValue() {
        RReadWriteLock lock = redissonClient.getReadWriteLock("rw-lock");
        String uuid = "";
        RLock rLock = lock.readLock();
        // 加读锁
        rLock.lock();
        try {
            uuid = redisTemplate.opsForValue().get("writeValue");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
        }

        return uuid;
    }

    @ResponseBody
    @GetMapping("/lockDoor")
    public String lockDoor() throws InterruptedException {
        RCountDownLatch door = redissonClient.getCountDownLatch("door");
        door.trySetCount(5);
        door.wait();
        return "放假了....";
    }

    @ResponseBody
    @GetMapping("/gogo/{id}")
    public String gogo(@PathVariable("id") Long id) {
        RCountDownLatch door = redissonClient.getCountDownLatch("door");
        door.countDown();
        return id + "人走了...";
    }

    @GetMapping("/park")
    @ResponseBody
    public String park() {
        RSemaphore park = redissonClient.getSemaphore("park");
        try {
            park.acquire(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "停进2";
    }

    @GetMapping("/go")
    @ResponseBody
    public String go() {
        RSemaphore park = redissonClient.getSemaphore("park");
        park.release(2);
        return "开走2";
    }
}
