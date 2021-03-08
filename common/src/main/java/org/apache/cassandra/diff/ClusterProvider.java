/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.cassandra.diff;

import java.io.Serializable;
import java.util.Map;

import com.datastax.driver.core.Cluster;

public interface ClusterProvider extends Serializable {
    String PREFIX = "diff.cluster";
    String USERNAME_KEY = "cql_user";
    String PASSWORD_KEY = "cql_password";
    String CLUSTER_PROVIDER_CLASS = "impl";

    void initialize(Map<String, String> conf, String identifier);
    Cluster getCluster();
    String getClusterName();

    static ClusterProvider getProvider(Map<String, String> conf, String identifier) {
        String providerImpl = conf.get(CLUSTER_PROVIDER_CLASS);
        ClusterProvider provider;
        try {
            provider = (ClusterProvider)(Class.forName(providerImpl)).newInstance();
            provider.initialize(conf, identifier);
        } catch (Exception e) {
            throw new RuntimeException("Could not instantiate ClusterProvider class: " + providerImpl +" " + conf, e);
        }
        return provider;
    }

}
