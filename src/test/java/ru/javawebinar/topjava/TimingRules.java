package ru.javawebinar.topjava;

import org.junit.rules.ExternalResource;
import org.junit.rules.Stopwatch;
import org.junit.runner.Description;
import org.slf4j.Logger;

import java.util.concurrent.TimeUnit;

import static org.slf4j.LoggerFactory.getLogger;

public class TimingRules {
    private static final Logger log = getLogger("result");

    private static StringBuilder results = new StringBuilder();

    // http://stackoverflow.com/questions/14892125/what-is-the-best-practice-to-determine-the-execution-time-of-the-bussiness-relev
    public static Stopwatch STOPWATCH = new Stopwatch() {
        @Override
        protected void finished(long nanos, Description description) {
            String result = String.format("\n%-75s %7d", description.getDisplayName(), TimeUnit.NANOSECONDS.toMillis(nanos));
            results.append(result);
            log.info(result + " ms\n");
        }
    };

    public static final ExternalResource SUMMARY = new ExternalResource() {
        @Override
        protected void before() throws Throwable {
            results.setLength(0);
        }

        @Override
        protected void after() {
            log.info("\n-----------------------------------------------------------------------------------" +
                    "\nTest                                                                   Duration, ms" +
                    "\n-----------------------------------------------------------------------------------" +
                    results +
                    "\n-----------------------------------------------------------------------------------");
        }
    };
}
