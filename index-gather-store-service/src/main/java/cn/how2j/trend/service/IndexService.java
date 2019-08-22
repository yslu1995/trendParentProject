package cn.how2j.trend.service;
 
import cn.how2j.trend.pojo.Index;
import cn.hutool.core.collection.CollectionUtil;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
 
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * IndexService 服务类，使用工具类RestTemplate 来获取如下地址：
 * http://127.0.0.1:8090/indexes/codes.json
 */
@Service
//保存到 redis 就会以 indexes 命名
@CacheConfig(cacheNames="indexes")
public class IndexService {
    private List<Index> indexes;
    @Autowired RestTemplate restTemplate;

    /**
     * 如果抛出异常，则执行断路器方法 third_part_not_connected
     * @return
     * test addresss
     * http://127.0.0.1:8001/getCodes
     */
    @HystrixCommand(fallbackMethod = "third_part_not_connected")
    //表示保存到 redis 用的 key 就会使 all_codes.
    @Cacheable(key="'all_codes'")
    public List<Index> fetch_indexes_from_third_part(){
        List<Map> temp= restTemplate.getForObject("http://127.0.0.1:8090/indexes/codes.json",List.class);
        return map2Index(temp);
    }

    /**
     * 断路器执行的方法
     * @return
     */
    public List<Index> third_part_not_connected(){
        System.out.println("third_part_not_connected()");
        Index index= new Index();
        index.setCode("000000");
        index.setName("无效指数代码");
        return CollectionUtil.toList(index);
    }

    private List<Index> map2Index(List<Map> temp) {
        List<Index> indexes = new ArrayList<>();
        for (Map map : temp) {
            String code = map.get("code").toString();
            String name = map.get("name").toString();
            Index index= new Index();
            index.setCode(code);
            index.setName(name);
            indexes.add(index);
        }
 
        return indexes;
    }
 
}