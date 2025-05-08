package com.example;

import com.google.cloud.bigquery.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class KafkaToBQ {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "your-kafka-bootstrap:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "bq-consumer-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList("your-topic"));

        BigQuery bigquery = BigQueryOptions.getDefaultInstance().getService();
        TableId tableId = TableId.of("your_dataset", "your_table");

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord<String, String> record : records) {
                InsertAllRequest insertRequest = InsertAllRequest.newBuilder(tableId)
                    .addRow("rowId-" + record.offset(), 
                        Collections.singletonMap("payload", record.value()))
                    .build();
                bigquery.insertAll(insertRequest);
                System.out.println("Inserted: " + record.value());
            }
        }
    }
}
