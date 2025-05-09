package se.fpcs.epjn_fetcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.tasks.v2.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskCreator {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final FetcherConfig config;

    public TaskCreator(FetcherConfig config) {
        this.config = config;
    }

    public void enqueue(List<EPJNPriceEntry> entries) {
        try (CloudTasksClient client = CloudTasksClient.create()) {
            QueueName queueName = getQueueName();

            entries.stream()
                    .map(this::safeCreateTask)
                    .filter(task -> task != null)
                    .forEach(task -> {
                        client.createTask(queueName, task);
                        logEnqueued(task);
                    });

        } catch (Exception e) {
            handleError(e);
        }
    }

    private Task safeCreateTask(EPJNPriceEntry entry) {
        try {
            return createHttpTask(entry);
        } catch (Exception e) {
            System.err.printf("Failed to create task for hour %d: %s%n", entry.hour(), e.getMessage());
            return null;
        }
    }


    private QueueName getQueueName() {
        return QueueName.of(
                config.getProjectId(),
                config.getLocationId(),
                config.getQueueId()
        );
    }

    private Task createHttpTask(EPJNPriceEntry entry) throws Exception {

        byte[] payloadBytes = objectMapper.writeValueAsBytes(entry);
        ByteString payload = ByteString.copyFrom(payloadBytes);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .setUrl(config.getTargetUrl())
                .setHttpMethod(HttpMethod.POST)
                .putHeaders("Content-Type", "application/json")
                .setBody(payload)
                .build();

        return Task.newBuilder()
                .setHttpRequest(httpRequest)
                .build();
    }


    private void logEnqueued(EPJNPriceEntry entry) {
        System.out.printf("Enqueued task for hour %d%n", entry.hour());
    }

    private void handleError(Exception e) {
        System.err.println("Failed to enqueue tasks: " + e.getMessage());
        e.printStackTrace();
    }
}
