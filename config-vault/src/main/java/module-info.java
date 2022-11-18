module io.scalecube.config.vault {

  exports io.scalecube.config.vault;

  requires transitive vault.java.driver;
  requires transitive io.scalecube.config;
  requires org.slf4j;
}
