package jp.co.soramitsu.load.toolbox;

import io.prometheus.client.Histogram;
import io.prometheus.client.exporter.PushGateway;
import jp.co.soramitsu.load.Iroha2SetUp;
import jp.co.soramitsu.load.objects.CustomHistogram;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlueElectricalTape extends TailerListenerAdapter {
    private File logFile = new File("logs/logFile.log");
    private final String transactionRegExp = "\\w{64}";
    private final String wsRegExp = "REQUEST:\\sws";
    private final String timeStampTransactionRegExp = "\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}.\\d{3}";
    private final String creatingRegExp = "Creating";
    private final String sendingRegExp = "Sending";
    private final String validatingRegExp = "validating";
    private final String committedRegExp = "committed";
    private final  String rejectedRegExp = "rejected";
    private final  String unauthorizedRegExp = "[DEBUG] i.g.c.v.package$ - j.c.s.i.IrohaClientException";
    private final String closeEventsRegExp = "WebSocket opened";
    private long create;
    private long send;
    private long validate;
    private long commit;
    private long reject;
    private long unauthorized;
    private long events;
    private long closeEvents;
    private Tailer tailer;
    public Thread tailerListener;
    private Pattern timeStampEventPattern;
    private Pattern timeStampCloseEventPattern;
    private Matcher timeStampEventMatcher;
    private Matcher timeStampCloseEventMatcher;
    private PushGateway pushGateway;
    public BlueElectricalTape()  {
        pushGateway = new PushGateway("127.0.0.1:9091");
        new CustomHistogram();

        /*tailer = Tailer.create(logFile, listener);
        tailerListener = new Thread(tailer);*/
    }

 /*   private TailerListenerAdapter listener = new TailerListenerAdapter() {
        @Override
        public void handle(String line) {
            try {
                if (patternExist(line, transactionRegExp)) {

                    Pattern timeStampPattern = getPattern(timeStampTransactionRegExp);
                    Matcher timeStampMatcher = getMatch(timeStampPattern, line);

                    Pattern creatingPattern = getPattern(creatingRegExp);
                    Pattern sendingPattern = getPattern(sendingRegExp);
                    Pattern validatingPattern = getPattern(validatingRegExp);
                    Pattern committedPattern = getPattern(committedRegExp);
                    Pattern rejectedPattern = getPattern(rejectedRegExp);

                    Matcher creatingMatcher = getMatch(creatingPattern, line);
                    Matcher sendingMatcher = getMatch(sendingPattern, line);
                    Matcher validatingMatcher = getMatch(validatingPattern, line);
                    Matcher committedMatcher = getMatch(committedPattern, line);
                    Matcher rejectedMatcher = getMatch(rejectedPattern, line);

                    if (timeStampMatcher.find()) {
                        if (sendingMatcher.find()) {
                            send = transferToUnixTime(timeStampMatcher);
                            double sendResponseTime = send - create;

                            CustomHistogram.sendTransactionResponseTime.labels("gatling"
                                    , System.getProperty("user.dir").substring(System.getProperty("user.dir").lastIndexOf('/') + 1)
                                    , Iroha2SetUp.class.getSimpleName()).observe(sendResponseTime);
                            //sendMetricsToPrometheus(CustomHistogram.sendTransactionResponseTime, "histogram");
                        } else if (validatingMatcher.find()) {
                            validate = transferToUnixTime(timeStampMatcher);
                            double validateResponseTime = validate - send;

                            CustomHistogram.validatingTransactionResponseTime.labels("gatling"
                                    , System.getProperty("user.dir").substring(System.getProperty("user.dir").lastIndexOf('/') + 1)
                                    , Iroha2SetUp.class.getSimpleName()).observe(validateResponseTime);
                            //sendMetricsToPrometheus(CustomHistogram.validatingTransactionResponseTime, "histogram");
                        } else if (committedMatcher.find()) {
                            commit = transferToUnixTime(timeStampMatcher);
                            double commitResponseTime = commit - validate;

                            CustomHistogram.commitTransactionResponseTime.labels("gatling"
                                    , System.getProperty("user.dir").substring(System.getProperty("user.dir").lastIndexOf('/') + 1)
                                    , Iroha2SetUp.class.getSimpleName()).observe(commitResponseTime);
                            //sendMetricsToPrometheus(CustomHistogram.commitTransactionResponseTime, "histogram");
                        } else if (rejectedMatcher.find()) {
                            reject = transferToUnixTime(timeStampMatcher);
                            double rejectResponseTime = reject - send;

                            CustomHistogram.rejectTransactionResponseTime.labels("gatling"
                                    , System.getProperty("user.dir").substring(System.getProperty("user.dir").lastIndexOf('/') + 1)
                                    , Iroha2SetUp.class.getSimpleName()).observe(rejectResponseTime);
                            //sendMetricsToPrometheus(CustomHistogram.rejectTransactionResponseTime, "histogram");
                        } else if (creatingMatcher.find()) {
                            create = transferToUnixTime(timeStampMatcher);

                            CustomHistogram.createSubscriptionTransactionResponseTime.labels("gatling"
                                            , System.getProperty("user.dir").substring(System.getProperty("user.dir").lastIndexOf('/') + 1)
                                            , Iroha2SetUp.class.getSimpleName())
                                    .observe(create);
                            //sendMetricsToPrometheus(CustomHistogram.createSubscriptionTransactionResponseTime, "histogram");
                        }
                    }
                } else if (patternExist(line, unauthorizedRegExp)) {
                    Pattern timeStampPattern = getPattern(timeStampTransactionRegExp);
                    Matcher timeStampMatcher = getMatch(timeStampPattern, line);
                    Pattern unauthorizedPattern = getPattern(unauthorizedRegExp);
                    Matcher unauthorizedMatcher = getMatch(unauthorizedPattern, line);
                    if (unauthorizedMatcher.find() && timeStampMatcher.find()) {
                        unauthorized = transferToUnixTime(timeStampMatcher);
                        CustomHistogram.unauthorizedResponseTime.labels("gatling"
                                        , System.getProperty("user.dir").substring(System.getProperty("user.dir").lastIndexOf('/') + 1)
                                        , Iroha2SetUp.class.getSimpleName())
                                .observe(create);
                        //sendMetricsToPrometheus(CustomHistogram.unauthorizedResponseTime, "histogram");
                    }
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };*/

    private void sendMetricsToPrometheus(Histogram histogram, String job){
        try{
            pushGateway.pushAdd(histogram, job);
        } catch (Exception ex){
            ex.getMessage();
        }
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

    private Long transferToUnixTime(Matcher timeStampPattern) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date date = sdf.parse(timeStampPattern.group());
        return date.getTime();
    }
}
