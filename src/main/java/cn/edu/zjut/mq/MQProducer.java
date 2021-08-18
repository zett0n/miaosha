package cn.edu.zjut.mq;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

import cn.edu.zjut.dao.ItemStockLogInfoMapper;
import cn.edu.zjut.entity.ItemStockLogInfo;
import cn.edu.zjut.error.BusinessException;
import cn.edu.zjut.service.OrderService;

/**
 * @author zett0n
 * @date 2021/8/17 21:43
 */
@Component
public class MQProducer {
    private DefaultMQProducer producer;
    private TransactionMQProducer transactionMQProducer;

    @Value("${mq.nameserver.address}")
    private String namesrvAddr;

    @Value("${mq.topic-name}")
    private String topicName;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ItemStockLogInfoMapper itemStockLogInfoMapper;

    // @PostConstruct 会在bean被初始化后调用
    // producer初始化
    @PostConstruct
    public void init() throws MQClientException {
        this.producer = new DefaultMQProducer("producerGroup");
        this.producer.setNamesrvAddr(this.namesrvAddr);
        this.producer.start();

        this.transactionMQProducer = new TransactionMQProducer("transactionProducerGroup");
        this.transactionMQProducer.setNamesrvAddr(this.namesrvAddr);
        this.transactionMQProducer.start();

        this.transactionMQProducer.setTransactionListener(new TransactionListener() {
            @Override
            public LocalTransactionState executeLocalTransaction(Message message, Object order) {
                // 创建订单
                Map orderMap = (Map)order;
                String itemStockLogId = (String)orderMap.get("itemStockLogId");
                try {
                    MQProducer.this.orderService.createOrder((Integer)orderMap.get("userId"),
                        (Integer)orderMap.get("itemId"), (Integer)orderMap.get("amount"),
                        (Integer)orderMap.get("promoId"), itemStockLogId);
                } catch (BusinessException e) {
                    e.printStackTrace();
                    // 设置对应的对应stockLog为回滚状态
                    ItemStockLogInfo itemStockLogInfo =
                        MQProducer.this.itemStockLogInfoMapper.selectByPrimaryKey(itemStockLogId);
                    itemStockLogInfo.setStatus(3);
                    MQProducer.this.itemStockLogInfoMapper.updateByPrimaryKeySelective(itemStockLogInfo);
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                }
                return LocalTransactionState.COMMIT_MESSAGE;
            }

            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
                // 根据是否扣减库存成功，来判断要返回commit还是rollback还是继续unknown
                String jsonStr = new String(messageExt.getBody());
                Map<String, Object> map = JSON.parseObject(jsonStr, Map.class);
                Integer itemId = (Integer)map.get("itemId");
                Integer amount = (Integer)map.get("amount");
                String itemStockLogId = (String)map.get("itemStockLogId");

                ItemStockLogInfo itemStockLogInfo =
                    MQProducer.this.itemStockLogInfoMapper.selectByPrimaryKey(itemStockLogId);
                if (itemStockLogInfo == null || itemStockLogInfo.getStatus() == 1) {
                    return LocalTransactionState.UNKNOW;
                }
                if (itemStockLogInfo.getStatus() == 3) {
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                }
                return LocalTransactionState.COMMIT_MESSAGE;
            }
        });
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

    // 事务型同步扣减消息
    public boolean transactionAsyncReduceStock(Integer userId, Integer itemId, Integer amount, Integer promoId,
        String itemStockLogId) {
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("itemId", itemId);
        bodyMap.put("amount", amount);
        bodyMap.put("itemStockLogId", itemStockLogId);
        Message message =
            new Message(this.topicName, "increase", JSON.toJSON(bodyMap).toString().getBytes(StandardCharsets.UTF_8));

        Map<String, Object> orderMap = new HashMap<>();
        orderMap.put("userId", userId);
        orderMap.put("itemId", itemId);
        orderMap.put("amount", amount);
        orderMap.put("promoId", promoId);
        orderMap.put("itemStockLogId", itemStockLogId);

        TransactionSendResult transactionSendResult;
        try {
            transactionSendResult = this.transactionMQProducer.sendMessageInTransaction(message, orderMap);
        } catch (MQClientException e) {
            e.printStackTrace();
            return false;
        }
        if (transactionSendResult.getLocalTransactionState() == LocalTransactionState.COMMIT_MESSAGE) {
            return true;
        }
        return false;
    }
}
