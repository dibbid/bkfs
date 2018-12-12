package com.dib.neo.bkfs.server;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.dib.neo.bkfs.daemons.PidFileLocker;
import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.apache.zookeeper.server.quorum.QuorumPeerMain;

/**
 * ZooKeeper服务器的简单包装器
 *
 */
public class ZooKeeperMainWrapper implements AutoCloseable {

  private final Properties configuration;
  private final PidFileLocker pidFileLocker;
  private ZooKeeperServerMain server;
  private QuorumPeerMain quorumPeerMain;

  private static ZooKeeperMainWrapper runningInstance;

  public ZooKeeperMainWrapper(Properties configuration) {
    this.configuration = configuration;
    this.pidFileLocker = new PidFileLocker(Paths.get(System.getProperty("user.dir", ".")).toAbsolutePath());
  }

  @Override
  public void close() {

  }

  public static void main(String... args) {
    try {
      String here = new File(System.getProperty("user.dir")).getAbsolutePath();
      LOG.severe("Starting ZookKeeper version from BKFS.");
      Properties configuration = new Properties();

      boolean configFileFromParameter = false;
      for (int i = 0; i < args.length; i++) {
        String arg = args[i];
        if (!arg.startsWith("-")) {
          File configFile = new File(args[i]).getAbsoluteFile();
          LOG.severe("Reading configuration from " + configFile);
          try (InputStreamReader reader =
                   new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8)) {
            configuration.load(reader);
          }
          configFileFromParameter = true;
        } else if (arg.equals("--use-env")) {
          System.getenv().forEach((key, value) -> {
            System.out.println("Considering env as system property " + key + " -> " + value);
            System.setProperty(key, value);
          });
        } else if (arg.startsWith("-D")) {
          int equals = arg.indexOf('=');
          if (equals > 0) {
            String key = arg.substring(2, equals);
            String value = arg.substring(equals + 1);
            System.setProperty(key, value);
          }
        }
      }
      if (!configFileFromParameter) {
        File configFile = new File("conf/zoo.cfg").getAbsoluteFile();
        System.out.println("Reading configuration from " + configFile);
        if (configFile.isFile()) {
          try (InputStreamReader reader = new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8)) {
            configuration.load(reader);
          }
        }
      }

      System.getProperties().forEach((k, v) -> {
        String key = k + "";
        if (!key.startsWith("java") && !key.startsWith("user")) {
          configuration.put(k, v);
        }
      });

      for (Object key : configuration.keySet()) {
        String value = configuration.getProperty(key.toString());
        String newvalue = value.replace("${user.dir}", here);
        configuration.put(key, newvalue);
      }
      String datadir = configuration.getProperty("dataDir", null);
      if (datadir != null) {
        File file = new File(datadir);
        if (!file.isDirectory()) {
          LOG.severe("Creating directory " + file.getAbsolutePath());
          boolean result = file.mkdirs();
          if (!result) {
            LOG.severe("Failed to create directory " + file.getAbsolutePath());
          }
        } else {
          LOG.severe("Using directory " + file.getAbsolutePath());
        }
      }

      LogManager.getLogManager().readConfiguration();

      Runtime.getRuntime().addShutdownHook(new Thread("ctrlc-hook") {

        @Override
        public void run() {
          System.out.println("Ctrl-C trapped. Shutting down");
          ZooKeeperMainWrapper _brokerMain = runningInstance;
          if (_brokerMain != null) {
            Runtime.getRuntime().halt(0);
          }
        }

      });
      runningInstance = new ZooKeeperMainWrapper(configuration);
      runningInstance.run();

    } catch (Throwable t) {
      t.printStackTrace();
      Runtime.getRuntime().halt(0);
    }
  }
  private static final Logger LOG = Logger.getLogger(ZooKeeperMainWrapper.class.getName());

  public void run() throws Exception {
    pidFileLocker.lock();


    QuorumPeerConfig qp = new QuorumPeerConfig();
    qp.parseProperties(configuration);
    if(qp.isDistributed()){
      quorumPeerMain = new QuorumPeerMain();
      quorumPeerMain.runFromConfig(qp);
    }else{
      server = new ZooKeeperServerMain();
      ServerConfig sc = new ServerConfig();
      sc.readFrom(qp);
      server.runFromConfig(sc);
    }


  }
}

