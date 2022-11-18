module io.scalecube.config {

  exports io.scalecube.config;
  exports io.scalecube.config.audit;
  exports io.scalecube.config.jmx;
  exports io.scalecube.config.keyvalue;
  exports io.scalecube.config.source;
  exports io.scalecube.config.utils;

  requires java.management;
  requires org.slf4j;
}
