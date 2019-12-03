package streams;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;

import io.streamthoughts.azkarra.api.annotations.Component;
import io.streamthoughts.azkarra.api.annotations.DefaultStreamsConfig;
import io.streamthoughts.azkarra.api.streams.TopologyProvider;
import io.streamthoughts.azkarra.streams.AzkarraApplication;
import io.streamthoughts.azkarra.streams.autoconfigure.annotations.AzkarraStreamsApplication;

@AzkarraStreamsApplication
@DefaultStreamsConfig(name = StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, value = "kafka:9092")
public class MapSampleAzkarra {
  /*
  This application reads data from a topic whose keys are integers and whose values are sentence strings.
  The input values are transformed to lower-case and output to a new topic.
  */
  public static void main(String[] args) {
    System.out.println("*** Starting Map Sample Application ***");
    AzkarraApplication.run(MapSampleAzkarra.class, args);
  }

  @Component
  public static class MapTopology implements TopologyProvider {
    @Override
    public Topology get() {
      final Serde<String> stringSerde = Serdes.String();
      StreamsBuilder builder = new StreamsBuilder();
      builder.stream("lines-topic", Consumed.with(stringSerde, stringSerde))
          .mapValues(value -> value.toLowerCase())
          .to("lines-lower-topic", Produced.with(stringSerde, stringSerde));
      return builder.build();
    }

    @Override
    public String version() {
      return "1.0";
    }
  }
}
