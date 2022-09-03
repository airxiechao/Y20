package com.airxiechao.y20.common.core.pubsub;

import com.airxiechao.axcboot.communication.pubsub.ISubscriber;
import com.airxiechao.axcboot.communication.pubsub.PubSubManager;
import com.airxiechao.axcboot.communication.pubsub.IPubSub;
import com.airxiechao.axcboot.communication.pubsub.annotation.Sink;
import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.axcboot.util.ClsUtil;
import com.airxiechao.axcboot.util.ModelUtil;
import com.airxiechao.y20.common.pojo.config.RabbitmqConfig;
import com.airxiechao.y20.common.pubsub.event.Event;
import com.airxiechao.y20.common.pojo.config.CommonConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

public class EventBus {

    private static final Logger logger = LoggerFactory.getLogger(EventBus.class);

    private static EventBus instance = new EventBus();
    public static EventBus getInstance() {
        return instance;
    }

    private IPubSub pubsub;

    private EventBus() {
        RabbitmqConfig config = ConfigFactory.get(CommonConfig.class).getRabbitmq();

        this.pubsub = PubSubManager.getInstance().createRabbitmq(
                config.getHost(),
                config.getPort(),
                config.getUsername(),
                config.getPassword(),
                config.getVirtualHost());
    }

    public void registerSinks(String pkg){
        Set<Class<?>> subs = ClsUtil.getTypesAnnotatedWith(pkg, Sink.class);
        for(Class<?> sub : subs) {
            logger.info("register sink: {}", sub);
            if(ISubscriber.class.isAssignableFrom(sub)){
                this.pubsub.registerSink((Class<? extends ISubscriber>)sub);
            }
        }
    }

    public void publish(Event event){
        Map<String, Object> param = ModelUtil.toMap(event);
        this.pubsub.publish(event.getType(), param);
    }

    public IPubSub getPubSub() {
        return pubsub;
    }
}
