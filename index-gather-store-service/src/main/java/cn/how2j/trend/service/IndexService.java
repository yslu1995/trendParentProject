package cn.how2j.trend.service;
 
import cn.how2j.trend.pojo.Index;
import org.springframework.beans.factory.annotation.Autowired;
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
public class IndexService {
    private List<Index> indexes;
    @Autowired RestTemplate restTemplate;
 
    public List<Index> fetch_indexes_from_third_part(){
        List<Map> temp= restTemplate.getForObject("http://127.0.0.1:8090/indexes/codes.json",List.class);
        return map2Index(temp);
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