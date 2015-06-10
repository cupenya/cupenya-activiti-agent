package com.cupenya.agent.activiti;

import com.cupenya.agent.common.CommonAgentConfig;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * A Java representation of the configuration file.
 */
public final class CupenyaConfig {
    private static final Config config = ConfigFactory.load();
    private static final Config agentConfig = config.getConfig("com.cupenya.agent.activiti");

    public static final CommonAgentConfig commonConfig = CommonAgentConfig.fromConfig(agentConfig);
}
