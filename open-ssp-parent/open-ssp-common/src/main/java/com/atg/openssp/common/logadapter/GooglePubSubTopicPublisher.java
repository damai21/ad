package com.atg.openssp.common.logadapter;

import com.google.api.gax.batching.BatchingSettings;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PubsubMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.Duration;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class GooglePubSubTopicPublisher {
    private static final Logger log = LoggerFactory.getLogger(GooglePubSubTopicPublisher.class);
    private AtomicLong publishedMessages = new AtomicLong();
    private Executor listerExecutor = Executors.newSingleThreadExecutor();
    private final String project;
    private final String topic;
    private Publisher publisher;

    public GooglePubSubTopicPublisher(String project, String topic) {
        this.project = project;
        this.topic = topic;
    }

    public void init() {
        ProjectTopicName ptn = ProjectTopicName.of(project, topic);
        try {
            publisher = Publisher.newBuilder(ptn)
                    .setBatchingSettings(BatchingSettings
                            .newBuilder()
                            .setIsEnabled(true)
                            .setRequestByteThreshold(1_000_000L)
                            .setElementCountThreshold(2000L)
                            .setDelayThreshold(Duration.ofSeconds(1))
                            .build())
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void destroy() {
        if (publisher != null) {
            try {
                publisher.shutdown();
            } catch (Exception e) {
                log.warn("Exception while shuting down: " + e.getMessage());
            }
        }
    }

    public void send(ByteString message) {
        if (publisher == null) {
            init();
            if (publisher == null) {
                throw new IllegalStateException("Publisher is not defined.");
            }
        }
        PubsubMessage pubsubMessage = PubsubMessage
                .newBuilder()
                .setData(message)
                .build();

        publisher.publish(pubsubMessage)
                .addListener(() -> publishedMessages.incrementAndGet(), listerExecutor);

    }


}
