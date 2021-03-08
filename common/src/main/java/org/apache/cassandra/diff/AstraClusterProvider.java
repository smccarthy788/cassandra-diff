package org.apache.cassandra.diff;

import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.datastax.driver.core.Cluster;

public class AstraClusterProvider implements ClusterProvider {
    private static final String SECURE_CONNECT_BUNDLE_KEY = "secure_connect_bundle";
    private static final String CLUSTER_NAME_KEY = "cluster_name";

    private String secureConnectBundle;
    private String username;
    private String password;
    private String clusterName;

    private static String throwIfMissing(String key, Function<String, String> supplier) {
        return Optional.ofNullable(supplier.apply(key))
            .orElseThrow(() -> new RuntimeException(String.format("Missing required property: %s", key)));
    }

    @Override
    public void initialize(Map<String, String> conf, String identifier) {
        secureConnectBundle = throwIfMissing(SECURE_CONNECT_BUNDLE_KEY, conf::get);
        clusterName = throwIfMissing(CLUSTER_NAME_KEY, conf::get);
        username = throwIfMissing(USERNAME_KEY, conf::get);
        password = throwIfMissing(PASSWORD_KEY, conf::get);
    }

    @Override
    public Cluster getCluster() {
        return Cluster.builder()
            .withCloudSecureConnectBundle(new File(secureConnectBundle))
            .withCredentials(username, password)
            .build();
    }

    @Override
    public String getClusterName() {
        return clusterName;
    }
}
