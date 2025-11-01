package ru.practicum.aggregator.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

import java.util.Properties;

@Configuration
public class KafkaProducerFabric {

    @Value("${aggregator.kafka.producer.bootstrap.servers}")
    private String bootstrapServers;
    @Value("${aggregator.kafka.producer.key.serializer}")
    private String keySerializer;
    @Value("${aggregator.kafka.producer.value.serializer}")
    private String valueSerializer;

    @Bean
    public Producer<Long, EventSimilarityAvro> getProducer() {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);

        return new KafkaProducer<>(config);
    }
}
