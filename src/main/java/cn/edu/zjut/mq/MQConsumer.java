package cn.edu.zjut.mq;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

import cn.edu.zjut.dao.ItemStockMapper;

/**
 * @author zett0n
 * @date 2021/8/17 21:43
 */
@Component
public class MQConsumer {
    private DefaultMQPushConsumer consumer;

    @Value("${mq.nameserver.address}")
    private String namesrvAddr;

    @Value("${mq.topic-name}")
    private String topicName;

    @Autowired
    private ItemStockMapper itemStockMapper;

    // @PostConstruct 会在bean被初始化后调用
    // producer初始化
    @PostConstruct
    public void init() throws MQClientException {
        this.consumer = new DefaultMQPushConsumer("stockConsumerGroup");
        this.consumer.setNamesrvAddr(this.namesrvAddr);
        this.consumer.subscribe(this.topicName, "*");

        this.consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list,
                ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                // 实现数据库库存扣减
                Message msg = list.get(0);
                String jsonStr = new String(msg.getBody());
                Map<String, Object> map = JSON.parseObject(jsonStr, Map.class);
                Integer itemId = (Integer)map.get("itemId");
                Integer amount = (Integer)map.get("amount");

                MQConsumer.this.itemStockMapper.decreaseStock(itemId, amount);

                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        this.consumer.start();
    }
}
