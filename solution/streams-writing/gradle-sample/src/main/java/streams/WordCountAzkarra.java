package streams;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;

import java.util.Arrays;
import java.util.Locale;

import io.streamthoughts.azkarra.api.annotations.Component;
import io.streamthoughts.azkarra.api.annotations.DefaultStreamsConfig;
import io.streamthoughts.azkarra.api.streams.TopologyProvider;
import io.streamthoughts.azkarra.streams.AzkarraApplication;
import io.streamthoughts.azkarra.streams.autoconfigure.annotations.AzkarraStreamsApplication;

@AzkarraStreamsApplication
@DefaultStreamsConfig(name = StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, value = "kafka:9092")
public class WordCountAzkarra {

  public static void main(String[] args) {
    System.out.println("*** Starting Word Count Application ***");
    AzkarraApplication.run(WordCountAzkarra.class, args);
  }

  @Component
  public static class WordCountTopology implements TopologyProvider {
    @Override
    public Topology get() {
      final Serde<String> stringSerde = Serdes.String();
      StreamsBuilder builder = new StreamsBuilder();
      builder.stream("lines-topic", Consumed.with(Serdes.String(), Serdes.String()))
          .flatMapValues(line -> Arrays.asList(line.toLowerCase(Locale.getDefault()).split(" ")))
          .groupBy((k, word) -> word)
          .count(Materialized.as("WordCounts"))
          .toStream()
          .to("word-count-topic", Produced.with(Serdes.String(), Serdes.Long()));

      return builder.build();
    }

    @Override
    public String version() {
      return "1.0";
    }
  }
}
