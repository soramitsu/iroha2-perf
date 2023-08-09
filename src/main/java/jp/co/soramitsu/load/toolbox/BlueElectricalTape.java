package jp.co.soramitsu.load.toolbox;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.co.soramitsu.load.objects.Transaction;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;
import java.io.File;


public class BlueElectricalTape extends TailerListenerAdapter {
    private File logFile = new File("logs/gs.log");
    public MetricRegistry metricRegistry;
    public GraphiteReporter reporter;
    private Graphite graphite = new Graphite("localhost", 2003);
    private final String transactionRegExp = "\\w{64}";
    private final String wsRegExp = "REQUEST:\\sws";
    private final String timeStampTransactionRegExp = "\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2},\\d{3}";
    private final String sendingRegExp = "Sending";
    private final String validatingRegExp = "validating";
    private final String committedRegExp = "committed";
    private final  String rejectedRegExp = "rejected";
    private final String closeEventsRegExp = "WebSocket opened";
    private long create;
    private long send;
    private long validate;
    private long commit;
    private long reject;
    private long events;
    private long closeEvents;
    private Tailer tailer;
    private Thread tailerListener;
    private Pattern timeStampEventPattern;
    private Pattern timeStampCloseEventPattern;
    private Matcher timeStampEventMatcher;
    private Matcher timeStampCloseEventMatcher;
    public Transaction transaction = new Transaction();

    public BlueElectricalTape(){
        metricRegistry = new MetricRegistry();
        reporter = GraphiteReporter.forRegistry(metricRegistry)
                .prefixedWith("gatling.testsimulation.users.allUsers")
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .filter(MetricFilter.ALL)
                .build(graphite);

        reporter.start(1, TimeUnit.SECONDS);
        tailer = Tailer.create(logFile, listener);
        tailerListener = new Thread(tailer);
    }

    public TailerListenerAdapter listener = new TailerListenerAdapter() {
        @Override
        public void handle(String line) {
            try {
                if (patternExist(line, transactionRegExp)) {

                    Pattern timeStampPattern = getPattern(timeStampTransactionRegExp);
                    Matcher timeStampMatcher = getMatch(timeStampPattern, line);

                    Pattern sendingPattern = getPattern(sendingRegExp);
                    Pattern validatingPattern = getPattern(validatingRegExp);
                    Pattern committedPattern = getPattern(committedRegExp);
                    Pattern rejectedPattern = getPattern(rejectedRegExp);

                    Matcher sendingMatcher = getMatch(sendingPattern, line);
                    Matcher validatingMatcher = getMatch(validatingPattern, line);
                    Matcher committedMatcher = getMatch(committedPattern, line);
                    Matcher rejectedMatcher = getMatch(rejectedPattern, line);

                    if (timeStampMatcher.find()) {
                        if (sendingMatcher.find()) {
                            send = transferToUnixTime(timeStampMatcher);
                            transaction.setSend(true);
                            long sendResponseTime = send - create;
                            metricRegistry.histogram("send_transaction_response_time").update(sendResponseTime);
                        } else if (validatingMatcher.find()) {
                            validate = transferToUnixTime(timeStampMatcher);
                            transaction.setValidate(true);
                            long validateResponseTime = validate - send;
                            metricRegistry.histogram("validate_transaction_response_time").update(validateResponseTime);
                        } else if (committedMatcher.find()) {
                            commit = transferToUnixTime(timeStampMatcher);
                            transaction.setCommit(true);
                            long commitResponseTime = commit - validate;
                            metricRegistry.histogram("commit_transaction_response_time").update(commitResponseTime);
                        } else if (rejectedMatcher.find()) {
                            reject = transferToUnixTime(timeStampMatcher);
                            transaction.setReject(true);
                            long rejectResponseTime = reject - send;
                            metricRegistry.histogram("reject_transaction_response_time").update(rejectResponseTime);
                        }
                    }
                } else if (patternExist(line, wsRegExp)){
                    timeStampEventPattern = getPattern(timeStampTransactionRegExp);
                    timeStampEventMatcher = getMatch(timeStampEventPattern, line);
                } else if (patternExist(line, closeEventsRegExp)){
                    timeStampCloseEventPattern = getPattern(timeStampTransactionRegExp);
                    timeStampCloseEventMatcher = getMatch(timeStampCloseEventPattern, line);
                    timeStampEventMatcher.find();
                    timeStampCloseEventMatcher.find();
                    events = transferToUnixTime(timeStampEventMatcher);
                    closeEvents = transferToUnixTime(timeStampCloseEventMatcher);
                    create = closeEvents;
                    //transaction.setCreate(true);
                    long creatSubscriptioneResponseTime = closeEvents - events;
                    metricRegistry.histogram("create_subscription_transaction_response_time").update(creatSubscriptioneResponseTime);
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    };
    public void run() {
        tailerListener.start();
    }
    public void stop() {
        tailerListener.stop();
    }
    private Boolean patternExist(String line, String pattern) {
        Matcher transactionMatcher = getMatch(Pattern.compile(pattern), line);
        if (transactionMatcher.find()) {
            return true;
        } else {
            return false;
        }
    }
    private Pattern getPattern(String pattern) {
        return Pattern.compile(pattern);
    }
    private Matcher getMatch(Pattern pattern, String line) {
        Matcher match = pattern.matcher(line);
        return match;
    }
    public void stopReporter() {
        reporter.stop();
    }
    private Long transferToUnixTime(Matcher timeStampPattern) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d H:m:s,S");
        Date date = sdf.parse(timeStampPattern.group());
        return date.getTime();
    }
}