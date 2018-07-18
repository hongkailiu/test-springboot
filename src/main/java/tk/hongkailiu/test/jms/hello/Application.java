package tk.hongkailiu.test.jms.hello;

import javax.jms.ConnectionFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

/**
 * Created by hongkliu on 7/17/18.
 */
@SpringBootApplication
@EnableJms
public class Application {

    @Bean
    public JmsListenerContainerFactory<?> myFactory(ConnectionFactory connectionFactory,
                                                    DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        // This provides all boot's default to this factory, including the message converter
        configurer.configure(factory, connectionFactory);
        //http://javasampleapproach.com/java-integration/activemq-work-spring-jms-activemq-topic-publisher-subcribers-pattern-using-springboot
        //https://stackoverflow.com/questions/40144561/how-to-listen-to-topic-using-spring-boot-jms
        //hk: the order of set is important
        //factory.setPubSubDomain(true);
        // You could still override some of Boot's default if necessary.
        return factory;
    }

    @Bean // Serialize message content to json using TextMessage
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }

    //http://javasampleapproach.com/java-integration/activemq-work-spring-jms-activemq-topic-publisher-subcribers-pattern-using-springboot
    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory,
                                   MessageConverter messageConverter){
        JmsTemplate template = new JmsTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        //template.setPubSubDomain(true);
        return template;
    }

    public static void main(String[] args) {
        // Launch the application
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);

        JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);

        // Send a message with a POJO - the template reuse the message converter
        System.out.println("Sending an email message.");
        jmsTemplate.convertAndSend("mailbox", new Email("info@example.com", "Hello"));
    }
}
