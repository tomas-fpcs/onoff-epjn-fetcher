package se.fpcs.epjn_fetcher;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;


public record EPJNPriceEntry(
        @JsonProperty("SEK_per_kWh") double sekPerKWh,
        @JsonProperty("EUR_per_kWh") double eurPerKWh,
        @JsonProperty("EXR") double exchangeRate,
        @JsonProperty("time_start") ZonedDateTime timeStart,
        @JsonProperty("time_end") ZonedDateTime timeEnd,
        @JsonProperty("region") String region,
        @JsonProperty("date") String date,
        @JsonProperty("hour") int hour
) {
}
