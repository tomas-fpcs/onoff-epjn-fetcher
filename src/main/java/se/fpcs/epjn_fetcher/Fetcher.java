package se.fpcs.epjn_fetcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

@Component
public class Fetcher {

    private final RestTemplate restTemplate = new RestTemplate();

    private final ApplicationContext context;

    public Fetcher(ApplicationContext context) {
        this.context = context;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void fetch() {
        LocalDate date = LocalDate.now(); // or use a fixed date for testing
        String year = String.format("%04d", date.getYear());
        String month = String.format("%02d", date.getMonthValue());
        String day = String.format("%02d", date.getDayOfMonth());

        String url = String.format(
                "https://www.elprisetjustnu.se/api/v1/prices/%s/%s-%s_SE3.json",
                year, month, day
        );

        try {
            ResponseEntity<EPJNPriceEntry[]> response = restTemplate.getForEntity(url, EPJNPriceEntry[].class);
            EPJNPriceEntry[] prices = response.getBody();

            if (prices != null) {
                for (EPJNPriceEntry EPJNPriceEntry : prices) {
                    System.out.println("priceEntry=" + EPJNPriceEntry);
                }
            } else {
                System.out.println("No data received.");
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch price data: " + e.getMessage());
        }

        int exitCode = SpringApplication.exit(context, () -> 0);
        System.exit(exitCode);

    }

}
