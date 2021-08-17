package cn.edu.zjut.mq;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

/**
 * @author zett0n
 * @date 2021/8/17 21:43
 */
@Component
public class MQProducer {
    private DefaultMQProducer producer;

    @Value("${mq.nameserver.address}")
    private String namesrvAddr;

    @Value("${mq.topic-name}")
    private String topicName;

    // @PostConstruct 会在bean被初始化后调用
    // producer初始化
    @PostConstruct
    public void init() throws MQClientException {
        this.producer = new DefaultMQProducer("producerGroup");
        this.producer.setNamesrvAddr(this.namesrvAddr);
        this.producer.start();
    }

    // mq同步库存扣减消息
    public boolean asyncReduceStock(Integer itemId, Integer amount) {
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("itemId", itemId);
        bodyMap.put("amount", amount);
        Message message =
            new Message(this.topicName, "increase", JSON.toJSON(bodyMap).toString().getBytes(StandardCharsets.UTF_8));
        try {
            this.producer.send(message);
        } catch (MQBrokerException | RemotingException | InterruptedException | MQClientException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
