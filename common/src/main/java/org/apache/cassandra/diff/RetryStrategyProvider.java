package org.apache.cassandra.diff;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Provides new RetryStrategy instances.
 * Use abstract class instead of interface in order to retain the referece to retryOptions;
 */
public abstract class RetryStrategyProvider {
    protected final Map<String,String> retryOptions;

    public RetryStrategyProvider(Map<String,String> retryOptions) {
        this.retryOptions = retryOptions;
    }

    /**
     * Create a new instance of RetryStrategy.
     */
    public abstract RetryStrategy get();


    public final static String IMPLEMENTATION_KEY = "impl";
    private final static Logger logger = LoggerFactory.getLogger(RetryStrategyProvider.class);

    /**
     * Create a RetryStrategyProvider based on {@param retryOptions}.
     */
    public static RetryStrategyProvider create(Map<String,String> retryOptions) {
        try {
            if (retryOptions == null || retryOptions.get(IMPLEMENTATION_KEY) == null) {
                logger.info("No retry options were specified, using the default NoRetry provider");
                return createDefaultRetryStrategyProvider(retryOptions);
            }
            String implClass = retryOptions.get(IMPLEMENTATION_KEY);
            return (RetryStrategyProvider) Class.forName(implClass)
                                                .getConstructor(Map.class)
                                                .newInstance(retryOptions);
        } catch (Exception ex) {
            logger.warn("Using the default NoRetry provider due to an exception trying to create the requested RetryStrategyProvider", ex);
            return createDefaultRetryStrategyProvider(retryOptions);
        }
    }

    private static RetryStrategyProvider createDefaultRetryStrategyProvider(Map<String,String> retryOptions) {
        return new RetryStrategyProvider(retryOptions) {
            @Override
            public RetryStrategy get() {
                return RetryStrategy.NoRetry.INSTANCE;
            }
        };
    }

}
