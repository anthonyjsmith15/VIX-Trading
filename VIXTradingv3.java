package com.ultramixer.igmarkets.api;

import com.google.gson.*;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import com.ultramixer.igmarkets.api.model.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.sql.*;
import java.util.*;

import static java.lang.String.format;

public class VIXTradingv3 {
    private static final String url = "jdbc:postgresql://localhost/emotionless";
    private static final String user = "postgres";
    private static final String passwd = "postgres";
    public static String epiccheck = "";
    public static String dealId = "";
    public static String dealreference = "";
    public static Integer yesterdayDay = 0;

    public static String vixdate = "";
    public static Double vixhigh;
    public static Double vixlow;
    public static Double volume = 0.0;
    public static Double high = 0.0;
    public static Double low = 0.0;
    public static String signal = "";
    public static Long secprevious = 0L;
    public static String sellsignal2 = "";
    public static String buysignal2 = "";
    public static String epicVix = "CC.D.VIX.USS.IP"; // VIX Daily
    public static String epicVixFuture = "IN.D.VIX.MONTH3.IP"; // VIX Dec-22
    public static String epicSandP = "IX.D.SPTRD.DAILY.IP"; //S&P 500 Daily
    public static String expirydate = "DEC-22";
    public static String proceed = "";
    public static final String API_URL_REAL = "https://api.ig.com/gateway/deal";
    public static final String API_URL_DEMO = "https://demo-api.ig.com/gateway/deal";
    private static String apiURL = API_URL_DEMO;
    public static String apiUsername = "toondemo";
    public static String apiPassword = "Daniamh!70649";
    public static String apiKey = "a92f8af6a9787de11564ecadb8e1a71ca368a4b4";
    public static String tradestatus = "not placed or updated";
    public static String pgupdate = "";
    public static String buytradeaction = "";
    public static String selltradeaction = "";
    public static String[] tradeaction = new String[2];
    public static String dealref = "not placed";
    public static String buysignalcurrent = "";
    public static String sellsignalcurrent = "";
    public static String dealupdate = "not updated";
    public static Long millis = 0L;
    public static Integer minutes = 0;
    public static Long seconds = 0L;
    public static Integer hours = 0;
    public static Integer day = 0;
    public static Integer month = 0;
    public static Integer year = 0;
    public static String tradingDay = "";
    public static String tradingDayYesterday = "";
    public static Integer sellsignal = 0;
    public static Integer buysignal = 0;
    public static String sellsignalprevious = "";
    public static String buysignalprevious = "";
    public static Integer sellsignalprev = 0;
    public static Integer buysignalprev = 0;
    public static Double volcurrent = 0.0;
    public static Double volprevious = 0.0;
    public static Double highscurrent = 0.0;
    public static Double highsprevious = 0.0;
    public static Double lowscurrent = 0.0;
    public static Double lowsprevious = 0.0;
    public static Double variancehigh = 0.0;
    public static Double previousvixhighdaily = 0.0;
    public static Double previoussandphighdaily = 0.0;
    public static Double variancelow = 0.0;
    public static Double previousvixlowdaily = 0.0;
    public static Double previoussandplowdaily = 0.0;
    public static Double thirtyvixlowtemp = 0.0;
    public static Double thirtyvixlow = 0.0;
    public static Double thirtymintohigh = 0.0;
    public static Double thirtypctofmin = 0.0;
    public static Double thirtyvixhightemp = 0.0;
    public static Double thirtyvixhigh = 0.0;
    public static Double thirtymaxtolow = 0.0;
    public static Double thirtypctofmax = 0.0;
    public static Double vix50dayavg = 0.0;
    public static Double vixhighto50avg = 0.0;
    public static Double sum = 0.0;
    public static Double standardDeviation = 0.0;
    public static Double mean = 0.0;
    public static Double res = 0.0;
    public static Double sq = 0.0;
    public static String crisisevent = "";
    private static boolean demo;
    private static Gson gson = new Gson();
    public static String securityToken;
    private static String cst;
    public VIXTradingv3() throws SQLException, LoginException {
        this.gson = new Gson();
    }
    public static void run(String[] tradesignal) throws LoginException, InterruptedException, APIException {
        while ((tradestatus == "not placed or updated")) {
            if (tradesignal[0] == "BUY") {
                signal = "buy";
                System.out.println("Placing trade");
                dealreference = placeTrade(signal, apiUsername, apiPassword, apiKey, epicSandP);
                tradestatus = "not updated";
            } else if (tradesignal[1] == "SELL") {
                signal = "sell";
                System.out.println("Placing trade");
                dealreference = placeTrade(signal, apiUsername, apiPassword, apiKey, epicSandP);
                tradestatus = "not updated";
            }
            tradestatus = "not updated";
        }
        if (tradestatus == "not updated" && (!(tradesignal[0] == "TIGHTEN BUY STOP")) && (!(tradesignal[1] == "TIGHTEN SELL STOP"))
                && (!(tradesignal[1] == "HOLD")) && (!(tradesignal[0] == "HOLD"))) {
            while (dealupdate == "not updated") {
                if (dealupdate == "Trade updated")
                    break;
                System.out.println("Updating trade");
                dealId = checkDealId();
                updateTrade(apiUsername, apiPassword, apiKey, epicVixFuture);
                System.out.println("Trade updated");
                dealupdate = "Trade updated";
            }
        }
        System.out.println("Awaiting market close");
    }
//            if (minutes == 30 && hours == 9) {
//                if (seconds == 59) {
//                    if (dealupdate == "Trade updated") {
//                        try {
//                            System.out.println("Closing trade");
//                            connect(apiUsername, apiPassword, apiKey, true);
//                            OpenPositions positions = getOpenPositions();
//                            List<Positions> pos1 = positions.getPositions();
//                            if (pos1.size() > 0) {
//                                Position position = pos1.get(0).getPosition();
//                                String dealid = position.getDealId();
//                                Double possize = position.getSize();
//                                ClosePosition.Direction closedirectionbuy = (ClosePosition.Direction.BUY);
//                                ClosePosition.Direction closedirectionsell = (ClosePosition.Direction.SELL);
//                                ClosePosition.Direction direction = ClosePosition.Direction.valueOf(position.getDirection());
//                                ClosePosition closePosition = new ClosePosition();
//
//                                if (direction == closedirectionbuy)
//                                    closePosition.setDirection(closedirectionsell);
//                                else if (direction == closedirectionsell)
//                                    closePosition.setDirection(closedirectionbuy);
//                                closePosition.setExpiry("DFB");
//                                closePosition.setOrderType("MARKET");
//                                closePosition.setDealId(dealid);
//                                closePosition.setSize(possize);
//                                closePosition.setEpic(null);
//                                closePosition.setExpiry(null);
//                                closePosition.setLevel(null);
//                                closePosition.setQuoteId(null);
//                                System.out.println("Closing first trade");
//                                closeOpenPosition(closePosition);
//                                checkclosed = "closed";
//                                System.out.println("Trade closed");
//                            } else {
//                                break;
//                            }
//                        } catch (IllegalArgumentException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//                }
//            }
//        }
//            else if (TradingDays.TradingHours.valueOf(tradingDay).id == "SHORT DAY") {
//            if (minutes == 59 && hours == 12) {
//                if (seconds == 40) {
//                    signal = "buy";
//                    try {
//                        while (tradestatus == "not placed or updated") {
//                            System.out.println("Placing first trade");
//                            dealref = placeTrade(signal, apiUsername, apiPassword, apiKey);
//                            tradestatus = "not updated";
//                        }
//                        if (tradestatus == "not updated") {
//                            while (dealupdate == "not updated") {
//                                if (dealupdate == "Trade updated")
//                                    break;
//                                System.out.println("Updating first trade");
//                                updateTrade(apiUsername, apiPassword, apiKey);
//                                System.out.println("Trade updated");
//                                dealupdate = "Trade updated";
//                            }
//                        }
//                        Thread.sleep(1000);
//                    } catch (APIException e) {
//                        throw new RuntimeException(e);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//            }
//            if (minutes == 30 && hours == 9) {
//                if (seconds == 59) {
//                    if (dealupdate == "Trade updated") {
//                        try {
//                            System.out.println("Closing trade");
//                            connect(apiUsername, apiPassword, apiKey, true);
//                            OpenPositions positions = getOpenPositions();
//                            List<Positions> pos1 = positions.getPositions();
//                            if (pos1.size() > 0) {
//                                Position position = pos1.get(0).getPosition();
//                                String dealid = position.getDealId();
//                                Double possize = position.getSize();
//                                ClosePosition.Direction closedirectionbuy = (ClosePosition.Direction.BUY);
//                                ClosePosition.Direction closedirectionsell = (ClosePosition.Direction.SELL);
//                                ClosePosition.Direction direction = ClosePosition.Direction.valueOf(position.getDirection());
//                                ClosePosition closePosition = new ClosePosition();
//
//                                if (direction == closedirectionbuy)
//                                    closePosition.setDirection(closedirectionsell);
//                                else if (direction == closedirectionsell)
//                                    closePosition.setDirection(closedirectionbuy);
//                                closePosition.setExpiry("DFB");
//                                closePosition.setOrderType("MARKET");
//                                closePosition.setDealId(dealid);
//                                closePosition.setSize(possize);
//                                closePosition.setEpic(null);
//                                closePosition.setExpiry(null);
//                                closePosition.setLevel(null);
//                                closePosition.setQuoteId(null);
//                                System.out.println("Closing first trade");
//                                closeOpenPosition(closePosition);
//                                checkclosed = "closed";
//                                System.out.println("Trade closed");
//                                disconnect();
//                            } else {
//                                break;
//                            }
//                        } catch (IllegalArgumentException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//                }
//            }
//        } else if (TradingDays.TradingHours.valueOf(tradingDay).id == "CLOSED") {
//            System.out.println("Awaiting market close");
//            Thread.sleep(1000);
//        }
//        if (seconds != secprevious)
//            System.out.println("seconds: " + (seconds));
//        secprevious = Long.valueOf(seconds);
//    }
    public static void main(String[] args) throws SQLException, LoginException, APIException, InterruptedException, IOException, URISyntaxException {

        crisisevent = "no";
        millis = (long) Calendar.getInstance(TimeZone.getTimeZone("America/New_York"), Locale.US).get(Calendar.MILLISECOND);
        minutes = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"), Locale.US).get(Calendar.MINUTE);
        seconds = (long) Calendar.getInstance(TimeZone.getTimeZone("America/New_York"), Locale.US).get(Calendar.SECOND);
        hours = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"), Locale.US).get(Calendar.HOUR_OF_DAY);
        day = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"), Locale.US).get(Calendar.DAY_OF_MONTH);
        month = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"), Locale.US).get(Calendar.MONTH) + 1;
        year = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"), Locale.US).get(Calendar.YEAR);
        int dayofyear = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"), Locale.US).get(Calendar.DAY_OF_YEAR);
        tradingDay = "DAY_" + dayofyear;
        int dayofyearyesterday = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"), Locale.US).get(Calendar.DAY_OF_YEAR) - 1;
        tradingDayYesterday = "DAY_" + dayofyearyesterday;
        vixdate = day + ", " + month + ", " + year;
        System.out.println("Checking for previous updates and market times...");
        proceed = checkPostgresUpdateDone();
        String currenttimedate = vixdate + " " + hours + ":" + minutes + ":" + seconds;
        System.out.println(currenttimedate);
        if (proceed != "Update done previously") {
            if ((TradingDays.TradingHours.valueOf(tradingDay).id == "FULL DAY" && minutes >= 0 && hours >= 16) ||
                    (TradingDays.TradingHours.valueOf(tradingDayYesterday).id == "FULL DAY" && hours <= 9)) {
                        System.out.println("Updating Postgres");
                        updatePostgresVIXSandPHighsLows();
                        pgupdate = "Postgres updated";
                        System.out.println(pgupdate);
                        if (pgupdate == "Postgres updated") {
                            String[] tradesignal = calculateBuySellSignals(pgupdate);
                            System.out.println("Buy Signal: " + tradesignal[0]);
                            System.out.println("Sell Signal: " + tradesignal[1]);
                            run(tradesignal);
                            updatePostgresSetBuysSells(tradesignal);
                }
            }
            if ((TradingDays.TradingHours.valueOf(tradingDay).id == "SHORT DAY" && minutes >= 0 && hours >= 13) ||
                    (TradingDays.TradingHours.valueOf(tradingDayYesterday).id == "SHORT DAY" && hours <= 9)) {
                        System.out.println("Updating Postgres");
                        updatePostgresVIXSandPHighsLows();
                        pgupdate = "Postgres updated";
                        System.out.println(pgupdate);
                        if (pgupdate == "Postgres updated") {
                            String[] tradesignal = calculateBuySellSignals(pgupdate);
                            System.out.println("Buy Signal: " + tradesignal[0]);
                            System.out.println("Sell Signal: " + tradesignal[1]);
                            run(tradesignal);
                            updatePostgresSetBuysSells(tradesignal);
                }
            }
        }
    }
    public static Connection connectpostgres() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, passwd);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
    public static void updatePostgresSetBuysSells(String[] tradesignal) throws SQLException {
        String buysupdate = "";
        String sellsupdate = "";
        if (tradesignal[0] == "BUY") {
            buysupdate = "BUY";
        }
        else if (tradesignal[0] == "TIGHTEN BUY STOP") {
            buysupdate = "TIGHTEN STOP";
        } else {
            buysupdate = "HOLD";
        }
        if (tradesignal[1] == "SELL") {
            sellsupdate = "SELL";
        } else if (tradesignal[0] == "TIGHTEN SELL STOP") {
            sellsupdate = "TIGHTEN STOP";
        }
        else {
            sellsupdate = "HOLD";
        }
        String vixSQL3 = format("UPDATE vix SET buys = '%s' WHERE vixdate = '%s' AND hours = '%s'",buysupdate,vixdate, hours);
        Connection conn = connectpostgres();
        PreparedStatement pstmt2 = conn.prepareStatement(vixSQL3);
        {
            pstmt2.executeUpdate();
        }
        String vixSQL4 = format("UPDATE vix SET sells = '%s' WHERE vixdate = '%s' AND hours = '%s'",sellsupdate,vixdate, hours);
        Connection conn4 = connectpostgres();
        PreparedStatement pstmt4 = conn4.prepareStatement(vixSQL4);
        {
            pstmt4.executeUpdate();
        }
    }
    public static String checkPostgresUpdateDone() throws SQLException {

        String lastupdate = "";
        String lastupdatehours = "";
        yesterdayDay = day - 1;
        String yesterday = yesterdayDay + ", " + month + ", " + year;
        String vixSQL5 = format("SELECT id, vixdate, hours from vix ORDER BY id DESC LIMIT 1");
        Connection conn = connectpostgres();
        PreparedStatement pstmt5 = conn.prepareStatement(vixSQL5);
        ResultSet rsupdatedone = pstmt5.executeQuery();
        while (rsupdatedone.next()) {
            lastupdate = rsupdatedone.getString("vixdate");
            lastupdatehours = rsupdatedone.getString("hours");
            if (Objects.equals(lastupdate, vixdate) && ((Integer.parseInt(Objects.toString(lastupdatehours))) >= 16))
//                    || ((Integer.parseInt(Objects.toString(lastupdatehours))) < 9))
            {
                proceed = "Update done previously";
                System.out.println("Update done today already");
                return proceed;
            }
            if (Objects.equals(lastupdate, yesterday) && (Integer.parseInt(Objects.toString(lastupdatehours))) < 9) {
                proceed = "Update done previously";
                System.out.println("Update done today already");
                return proceed;
            }
        }
        return proceed;
    }
    public static void updatePostgresVIXSandPHighsLows() throws APIException, LoginException, SQLException, IOException {
        vixdate = day + ", " + month + ", " + year;
        String vixSQL = "INSERT INTO vix (vixdate, vixhigh, vixlow, volume, high, low, highs, lows, buysignal, sellsignal, " +
                "hours, thirtymin, thirtymintohigh, thirtypctofmin, thirtymax, thirtymaxtolow, thirtypctofmax," +
                "vixhighto50avg, vix50dayavg, crisisevent) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//        vixhigh = getVixHigh(apiUsername, apiPassword, apiKey);
//        vixlow = getVixLow(apiUsername, apiPassword, apiKey);
//        high = getSandPHigh(apiUsername, apiPassword, apiKey);
//        low = getSandPLow(apiUsername, apiPassword, apiKey);
        String vixurl = "https://financialmodelingprep.com/api/v3/quote/%5EVIX?apikey=94292fcd12435d943f18b61042be7e8b";
        URL urlvix = new URL(vixurl);
        URLConnection requestvix = urlvix.openConnection();
        requestvix.connect();
        JsonParser jpvix = new JsonParser();
        JsonElement rootvix = jpvix.parse(new InputStreamReader((InputStream) requestvix.getContent()));
        JsonArray jsonobjectvix = rootvix.getAsJsonArray();
        JsonElement jsonelementvix = jsonobjectvix.get(0);
        vixhigh = Double.valueOf(String.valueOf(jsonelementvix.getAsJsonObject().get("dayHigh")));
        vixlow = Double.valueOf(String.valueOf(jsonelementvix.getAsJsonObject().get("dayLow")));
        vix50dayavg = Double.valueOf(String.valueOf(jsonelementvix.getAsJsonObject().get("priceAvg50")));
        class calculateSD2 {
            double SD()
        {
            Double[] arr = { vixhigh, vix50dayavg };
            int n = arr.length;
            for (int i = 0; i < n; i++) {
                sum = sum + arr[i];
            }
            mean = sum / (n);
            for (int i = 0; i < n; i++) {
                standardDeviation = standardDeviation + Math.pow((arr[i] - mean), 2);
            }
            sq = standardDeviation / n;
            res = Math.sqrt(sq);
            return res;
            }
        }
        calculateSD2 calsd = new calculateSD2();
        double res = calsd.SD();
        vixhighto50avg = res;

        String sandpurl = "https://financialmodelingprep.com/api/v3/quote/%5EGSPC?apikey=94292fcd12435d943f18b61042be7e8b";
        URL url = new URL(sandpurl);
        URLConnection request = url.openConnection();
        request.connect();
        JsonParser jp = new JsonParser();
        JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
        JsonArray jsonobject = root.getAsJsonArray();
        JsonElement jsonelement = jsonobject.get(0);
        high = Double.valueOf(String.valueOf(jsonelement.getAsJsonObject().get("dayHigh")));
        low = Double.valueOf(String.valueOf(jsonelement.getAsJsonObject().get("dayLow")));
        Long longvolume = jsonelement.getAsJsonObject().get("volume").getAsLong();

        String SQL6 = "SELECT id, vixlow, vixhigh FROM vix ORDER BY id desc LIMIT 30;";
        Connection conn16 = connectpostgres();
        PreparedStatement pstmt6 = conn16.prepareStatement(SQL6);
        ResultSet rs6 = pstmt6.executeQuery();
        while (rs6.next()) {
            if (rs6.isFirst() == true) {
                thirtyvixlowtemp = rs6.getDouble("vixlow");
                thirtyvixlow = thirtyvixlowtemp;
                thirtyvixhightemp = rs6.getDouble("vixhigh");
                thirtyvixhigh = thirtyvixhightemp;
            }
            else if (!rs6.isFirst() == true) {
                thirtyvixlowtemp = rs6.getDouble("vixlow");
                if (thirtyvixlowtemp < thirtyvixlow) {
                    thirtyvixlow = thirtyvixlowtemp;
                }
                thirtyvixhightemp = rs6.getDouble("vixhigh");
                if (thirtyvixhightemp > thirtyvixhigh) {
                    thirtyvixhigh = thirtyvixhightemp;
                }
            }
            rs6.next();
        }

        thirtymintohigh = vixhigh - thirtyvixlow;
        thirtypctofmin = thirtymintohigh / thirtyvixlow;

        thirtymaxtolow = thirtyvixhigh - vixlow;
        thirtypctofmax = thirtymaxtolow / thirtyvixhigh;

        String SQL5 = "SELECT id, vixhigh, vixlow, high, low FROM vix ORDER BY id desc LIMIT 1;";
        Connection conn15 = connectpostgres();
        PreparedStatement pstmt5 = conn15.prepareStatement(SQL5);
        ResultSet rs5 = pstmt5.executeQuery();
        while (rs5.next()) {
            if (rs5.isFirst() == true) {
                previousvixhighdaily = rs5.getDouble("vixhigh");
                previousvixlowdaily = rs5.getDouble("vixlow");
                previoussandphighdaily = rs5.getDouble("high");
                previoussandplowdaily = rs5.getDouble("low");
            }
        }
        
        volume = longvolume.doubleValue();
        Double variancehighpg = vixhigh / high;
        Double variancelowpg = vixlow / low;
        Double lows = 0.01 - variancelowpg;
        Double highs = 0.01 - variancehighpg;
        variancehigh = vixhigh / high;
        highscurrent = 0.01 - variancehigh;
        highsprevious = 0.01 - (previousvixhighdaily / previoussandphighdaily);
        if (Math.abs(highscurrent) < Math.abs(highsprevious)) {
            sellsignal = 0;
        } else {
            sellsignal = 1;
        }
        variancelow = vixlow / low;
        lowscurrent = 0.01 - variancelow;
        lowsprevious = 0.01 - (previousvixlowdaily / previoussandplowdaily);
        if (Math.abs(lowscurrent) < Math.abs(lowsprevious)) {
            buysignal = 0;
        } else {
            buysignal = 1;
        }
        if (sellsignal == 0)
            sellsignal2 = "NARROWING";
        if (sellsignal == 1)
            sellsignal2 = "EXPANDING";
        if (buysignal == 0)
            buysignal2 = "NARROWING";
        if (buysignal == 1)
            buysignal2 = "EXPANDING";

        Connection conn = connectpostgres();
        PreparedStatement pstmt2 = conn.prepareStatement(vixSQL);
        {
            pstmt2.setString(1, vixdate);
            pstmt2.setDouble(2, vixhigh);
            pstmt2.setDouble(3, vixlow);
            pstmt2.setLong(4, longvolume);
            pstmt2.setDouble(5, high);
            pstmt2.setDouble(6, low);
            pstmt2.setDouble(7, highs);
            pstmt2.setDouble(8, lows);
            pstmt2.setString(9, buysignal2);
            pstmt2.setString(10, sellsignal2);
            pstmt2.setInt(11, hours);
            pstmt2.setDouble(12, thirtyvixlow);
            pstmt2.setDouble(13, thirtymintohigh);
            pstmt2.setDouble(14, thirtypctofmin);
            pstmt2.setDouble(15, thirtyvixhigh);
            pstmt2.setDouble(16, thirtymaxtolow);
            pstmt2.setDouble(17, thirtypctofmax);
            pstmt2.setDouble(18, vixhighto50avg);
            pstmt2.setDouble(19, vix50dayavg);
            pstmt2.setString(20, crisisevent);
            pstmt2.executeUpdate();
        }
    }
        public static String[] calculateBuySellSignals (String pgupdate) {
            String SQL = "SELECT id, vixhigh, vixlow, high, low, highs, lows, volume, sellsignal, buysignal FROM vix ORDER BY id desc LIMIT 2;";
            if (pgupdate == "Postgres updated") {
                try (Connection conn = connectpostgres();
                     PreparedStatement pstmt = conn.prepareStatement(SQL)) {
                    ResultSet rs = pstmt.executeQuery();
                    while (rs.next()) {
                        if (rs.isFirst() == true) {
                            vixhigh = rs.getDouble("vixhigh");
                            vixlow = rs.getDouble("vixlow");
                            high = rs.getDouble("high");
                            low = rs.getDouble("low");
                            highscurrent = rs.getDouble("highs");
                            lowscurrent = rs.getDouble("lows");
                            volcurrent = rs.getDouble("volume");
                            buysignalcurrent = rs.getString("buysignal");
                            sellsignalcurrent = rs.getString("sellsignal");
                        } else if (rs.isLast() == true) {
                            previousvixhighdaily = rs.getDouble("vixhigh");
                            previoussandphighdaily = rs.getDouble("high");
                            previousvixlowdaily = rs.getDouble("vixlow");
                            previoussandplowdaily = rs.getDouble("low");
                            sellsignalprevious = rs.getString("sellsignal");
                            buysignalprevious = rs.getString("buysignal");
                            volprevious = rs.getDouble("volume");
                            lowsprevious = rs.getDouble("lows");
                            highsprevious = rs.getDouble("highs");
                        }
                    }
                    if (Objects.equals(sellsignalprevious, "NARROWING")) {
                        sellsignalprev = 0;
                    } else if (Objects.equals(sellsignalprevious, "EXPANDING")) {
                        sellsignalprev = 1;
                    }
                    if (Objects.equals(buysignalprevious, "NARROWING")) {
                        buysignalprev = 0;
                    } else if (Objects.equals(buysignalprevious, "EXPANDING")) {
                        buysignalprev = 1;
                    }

                    if (Objects.equals(sellsignalcurrent, "NARROWING")) {
                        sellsignal = 0;
                    } else if (Objects.equals(sellsignalcurrent, "EXPANDING")) {
                        sellsignal = 1;
                    }
                    if (Objects.equals(buysignalcurrent, "NARROWING")) {
                        buysignal = 0;
                    } else if (Objects.equals(buysignalcurrent, "EXPANDING")) {
                        buysignal = 1;
                    }

                    if ((sellsignal == 1) && (buysignal == 1) && (sellsignalprev == 1) && (buysignalprev == 1) && (volcurrent > volprevious) && (Math.abs(lowscurrent) > Math.abs(lowsprevious)) && thirtypctofmin < 0.25) {
                        buytradeaction = "BUY";
                    } else if (thirtypctofmin > 0.45) {
                        buytradeaction = "TIGHTEN BUY STOP";
                    } else {
                        buytradeaction = "HOLD";
                    }
                    if (crisisevent == "yes" && vixhigh < 50) {
                        selltradeaction = "HOLD";
                    } else if ((sellsignal == 1) && (sellsignalprev == 0) && (buysignalprev == 0) && (thirtypctofmax < 0.35) && (Math.abs(lowscurrent) < 0.0035)) {
                        selltradeaction = "SELL";
                    } else if ((sellsignal == 0) && (sellsignalprev == 0) && (buysignalprev == 0) && (thirtypctofmax < 0.35) && (Math.abs(lowscurrent) < 0.0035)) {
                        selltradeaction = "SELL";
                    } else if ((Math.abs(highscurrent) < 0.0025) && vixhighto50avg > 7.5 && vixhigh > vix50dayavg && vixhigh > 30) {
                        selltradeaction = "SELL";
                    } else if ((Math.abs(lowscurrent) > 0.02) && vixhigh > 30) {
                        selltradeaction = "SELL";
                    } else if (thirtypctofmax > 0.45) {
                        selltradeaction = "TIGHTEN SELL STOP";
                    } else {
                        selltradeaction = "HOLD";
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            tradeaction[0] = buytradeaction;
            tradeaction[1] = selltradeaction;
            return tradeaction;
        }
        public static String placeTrade (String signal, String apiUsername, String apiPassword, String apiKey, String epic) throws
        APIException {
            try {
                connect(apiUsername, apiPassword, apiKey, true);
                OpenPositions positions = getOpenPositions();
                List<Positions> pos1 = positions.getPositions();
                OpenPosition.Direction openDirectionBuy = OpenPosition.Direction.valueOf("BUY");
                OpenPosition.Direction openDirectionSell = OpenPosition.Direction.valueOf("SELL");
                String epic1 = epicVixFuture;
                String expiry = expirydate;
                Double openBid = 0.0;
                Double openOffer = 0.0;
                Double buyLevel = 0.0;
                Double sellLevel = 0.0;
                Double available = 0.0;
                Accounts accounts = getAccounts();
                assert accounts != null;
                for (Account account : accounts.getAccounts()) {
                    Account acc = account;
                    available = acc.getBalance().getAvailable();
                }
                Double openSize = 0.0;
                Double sizeFinal = 0.0;
                Double stopDistance = 0.0;
                String dealReference = null;
                Double limitLevel = 0.0;
                Double limitLevelFinal = Double.valueOf(format("%.2f", limitLevel));
                Double limitDistance = 0.0;
                Double limitDistanceFinal = Double.valueOf(format("%.2f", limitDistance));
                Double trailingStep = 0.5;
                boolean trailingStop = true;
                String currencyCode = "GBP";
                String orderType = "MARKET";
                String quoteId = null;
                if (signal == "buy") {
                    openOffer = getMarketDetails(epic1).getSnapshot().getHigh();
                    buyLevel = openOffer;
                    stopDistance = buyLevel * 0.2;
                    openSize = available * 0.175 / stopDistance;
                    sizeFinal = Double.valueOf(format("%.2f", openSize));
                    OpenPosition openPosition = new OpenPosition();
                    openPosition.setEpic(epic1);
                    openPosition.setCurrencyCode(currencyCode);
                    openPosition.setDirection(openDirectionBuy);
                    openPosition.setOrderType(orderType);
                    openPosition.setExpiry(expiry);
                    openPosition.setForceOpen(true);
                    openPosition.setGuaranteedStop(false);
                    openPosition.setOrderType(orderType);
                    openPosition.setSize(sizeFinal);
                    openPosition.setStopDistance(stopDistance);
                    openPosition.setDealReference(dealReference);
                    //openPosition.setLimitDistance(limitDistanceFinal);
                    openPosition.setQuoteId(quoteId);
                    dealreference = OpenNewPosition(openPosition);
                    return dealreference;
                } else if (signal == "sell") {
                    OpenPosition openPosition = new OpenPosition();
                    openBid = getMarketDetails(epic1).getSnapshot().getHigh();
                    sellLevel = openBid;
                    stopDistance = sellLevel * 0.2;
                    openSize = available * 0.1 / stopDistance;
                    sizeFinal = Double.valueOf(format("%.2f", openSize));
                    openPosition.setEpic(epic1);
                    openPosition.setCurrencyCode(currencyCode);
                    openPosition.setDirection(openDirectionSell);
                    openPosition.setOrderType(orderType);
                    openPosition.setExpiry(expiry);
                    openPosition.setForceOpen(true);
                    openPosition.setGuaranteedStop(false);
                    openPosition.setOrderType(orderType);
                    openPosition.setSize(sizeFinal);
                    openPosition.setStopDistance(stopDistance);
                    openPosition.setDealReference(dealReference);
                    //openPosition.setLimitDistance(limitDistanceFinal);
                    openPosition.setQuoteId(quoteId);
                    dealreference = OpenNewPosition(openPosition);
                    return dealreference;
                    }
                } catch (LoginException e) {
                throw new RuntimeException(e);
            }
            return signal;
        }
        public static String checkDealId() {
            OpenPositions newpositions = getOpenPositions();
            List<Positions> newpos = newpositions.getPositions();
            ListIterator<Positions> newposlist = newpos.listIterator();
            while (newposlist.hasNext()) {
                for (ListIterator<Positions> it = newposlist; it.hasNext(); ) {
                    Positions s = it.next();
                    String newdealref = s.getPosition().getDealReference();
                    if (Objects.equals(newdealref, dealreference)) {
                        dealId = s.getPosition().getDealId();
                        return dealId;
                    }
                }
            }
            return null;
        }
        private static void updateTrade(String apiUsername, String apiPassword, String apiKey, String epic) throws
        LoginException, APIException {
            connect(apiUsername, apiPassword, apiKey, true);
            OpenPositions positions = getOpenPositions();
            String epic1 = epic;
            Double openBid = getMarketDetails(epic1).getSnapshot().getLow();
            Double openOffer = getMarketDetails(epic1).getSnapshot().getHigh();
            Double stopLevelBuy = openBid - 5.0;
            Double stopLevelSell = openOffer + 5.0;
            OpenPositions positions1 = getOpenPositions();
            List<Positions> pos2 = positions1.getPositions();
            ListIterator<Positions> newposlist = pos2.listIterator();
            if (pos2.size() > 0) {
                while (newposlist.hasNext()) {
                    for (ListIterator<Positions> it = newposlist; it.hasNext(); ) {
                        Positions s2 = it.next();
                        UpdatePosition updatePosition = new UpdatePosition();
                        String dealid = s2.getPosition().getDealId();
                        Double trailingStop = s2.getPosition().getTrailingStep();
                        String direction = s2.getPosition().getDirection();
                        if (trailingStop == null && Objects.equals(dealid, dealId)) {
                            if (direction == "BUY")
                                updatePosition.setGuaranteedStop(false);
                            updatePosition.setTrailingStop(true);
                            updatePosition.setTrailingStopDistance(5.0);
                            updatePosition.setTrailingStopIncrement(0.5);
                            updatePosition.setStopLevel(stopLevelBuy);
                            String dealref = UpdateOpenPosition(updatePosition, dealId);
                            if (direction == "SELL")
                                updatePosition.setGuaranteedStop(false);
                            updatePosition.setTrailingStop(true);
                            updatePosition.setTrailingStopDistance(5.0);
                            updatePosition.setTrailingStopIncrement(0.5);
                            updatePosition.setStopLevel(stopLevelSell);
                            String dealref1 = UpdateOpenPosition(updatePosition, dealId);
                        }
                    }
                }
            }
        }
    private static Double getVixHigh(String apiUsername, String apiPassword, String apiKey) throws
        LoginException, APIException {
            connect(apiUsername, apiPassword, apiKey, true);
            Double vixhigh = getMarketDetails(epicVix).getSnapshot().getHigh();
            return vixhigh;
        }
    private static Double getVixLow (String apiUsername, String apiPassword, String apiKey) throws
        LoginException, APIException {
            connect(apiUsername, apiPassword, apiKey, true);
            Double vixlow = getMarketDetails(epicVix).getSnapshot().getLow();
            return vixlow;
        }
    private static Double getSandPHigh (String apiUsername, String apiPassword, String apiKey) throws
        LoginException, APIException {
            connect(apiUsername, apiPassword, apiKey, true);
            Double high = getMarketDetails(epicSandP).getSnapshot().getHigh();
            return high;
        }
    private static Double getSandPLow (String apiUsername, String apiPassword, String apiKey) throws
        LoginException, APIException {

            connect(apiUsername, apiPassword, apiKey, true);
            Double low = getMarketDetails(epicSandP).getSnapshot().getLow();

            return low;
        }
    public static OpenPositions checkPositions () throws APIException, LoginException {
            try {
                connect(apiUsername, apiPassword, apiKey, true);
                OpenPositions positions = getOpenPositions();
                List<Positions> pos1 = positions.getPositions();
                if (pos1.size() > 0) {
                    Market posmarket = pos1.get(0).getMarket();
                    Position position = pos1.get(0).getPosition();
                    epiccheck = posmarket.getEpic();
                    //dealId = position.getDealId();
                }
                return new OpenPositions(epiccheck);
            } catch (LoginException e) {
                throw new RuntimeException(e);
            }
        }
    public static void closeOpenPosition (ClosePosition closePosition){
            try {
                String json = gson.toJson(closePosition);
                HttpResponse<JsonNode> response = createPostHttpRequest("positions/otc").header("version", "1").header("_method", "DELETE").body(json).asJson();

                if (response.getStatus() == 200) {
                    response.getBody().getObject().getString("dealReference");
                } else {
                    System.out.println("closeOpenPosition: response = " + response);
                    System.out.println("closeOpenPosition: response.getBody().toString() = " + response.getBody().toString());
                }
            } catch (UnirestException e) {
                e.printStackTrace();
            }
        }
        public static String OpenNewPosition (OpenPosition openPosition){
            try {
                String json = gson.toJson(openPosition);
                HttpResponse<JsonNode> response = createPostHttpRequest("positions/otc").header("version", "2").body(json).asJson();

                if (response.getStatus() == 200) {
                    return response.getBody().getObject().getString("dealReference");
                } else {
                    System.out.println("OpenPosition: response = " + response);
                    System.out.println("OpenPosition: response.getBody().toString() = " + response.getBody().toString());
                }
            } catch (UnirestException e) {
                e.printStackTrace();
            }
            return null;
        }
        public static OpenPositions getOpenPositions () {
            try {
                HttpResponse<JsonNode> response = createGetHttpRequest("positions").header("version", "2").asJson();
                if (response.getStatus() == 200) {
                    return gson.fromJson(response.getBody().toString(), OpenPositions.class);
                } else {
                    System.out.println("response = " + response);
                    System.out.println("response.getStatusText() = " + response.getStatusText());
                    System.out.println("response.getBody() = " + response.getBody());
                }
            } catch (UnirestException e) {
                e.printStackTrace();
            }

            return null;
        }
        public static String UpdateOpenPosition (UpdatePosition request, String dealId){
            try {
                String json = gson.toJson(request);
                HttpResponse<JsonNode> response = createPutHttpRequest("positions/otc/" + dealId).header("version", "2").body(json).asJson();

                if (response.getStatus() == 200) {
                    return response.getBody().getObject().getString("dealReference");
                } else {
                    System.out.println("UpdatePosition: response = " + response);
                    System.out.println("UpdatePosition: response.getBody().toString() = " + response.getBody().toString());
                }
            } catch (UnirestException e) {
                e.printStackTrace();
            }
            return null;
        }
        public static Accounts getAccounts() {
            //
            try {
                HttpResponse<String> response = createGetHttpRequest("accounts").header("version", "1").asString();
                if (response.getStatus() == 200) {
                    return gson.fromJson(response.getBody(), Accounts.class);
                } else {
                    System.out.println("response = " + response);
                    System.out.println("response.getStatusText() = " + response.getStatusText());
                    System.out.println("response.getBody() = " + response.getBody());
                }
            } catch (UnirestException e) {
                e.printStackTrace();
            }
            return null;
        }
        public static GetRequest createGetHttpRequest (String url){
            GetRequest request = Unirest.get(apiURL + "/" + url);
            System.out.println("request.getUrl() = " + request.getUrl());
            setHeaders(request);
            return request;
        }
        public static MarketDetails getMarketDetails (String epic) throws APIException {
            //
            try {
                HttpResponse<JsonNode> response = createGetHttpRequest("markets/" + epic).header("version", "2").asJson();
                if (response.getStatus() == 200) {
                    return gson.fromJson(response.getBody().toString(), MarketDetails.class);
                } else {
                    System.out.println("response = " + response);
                    //throw  new APIException(response.getStatus(),response.getBody().getObject().getString("errorCode"))
                    System.out.println("response = " + response);
                    System.out.println("response.getStatusText() = " + response.getStatusText());
                    System.out.println("response.getBody() = " + response.getBody());
                }
            } catch (UnirestException e) {
                e.printStackTrace();
            }
            return null;
        }
        public String createWorkingOrderV2 (WorkingOrderV2Request workingOrderV2Request){
            try {
                String json = gson.toJson(workingOrderV2Request);
                HttpResponse<JsonNode> response = createPostHttpRequest("workingorders/otc").header("version", "2").body(json).asJson();

                if (response.getStatus() == 200) {
                    return response.getBody().getObject().getString("dealReference");
                } else {
                    System.out.println("createWorkingOrderV2: response = " + response);
                    System.out.println("createWorkingOrderV2: response.getBody().toString() = " + response.getBody().toString());
                }
            } catch (UnirestException e) {
                e.printStackTrace();
            }
            return null;
        }
        public static LoginResponse connect (String username, String password, String apiKey,boolean demo) throws
        LoginException {
            apiKey = apiKey;
            demo = demo;
            if (!demo) {
                apiURL = API_URL_REAL;
            } else {
                apiURL = API_URL_DEMO;
            }
            try {
                JsonNode json = new JsonNode("");
                json.getObject().put("identifier", username);
                json.getObject().put("password", password);

                HttpResponse<JsonNode> response = createPostHttpRequest("session")
                        .body(json)
                        .asJson();
                if (response.getStatus() == 200) {
                    securityToken = response.getHeaders().getFirst("X-SECURITY-TOKEN");
                    cst = response.getHeaders().getFirst("CST");
                    //loginResponse = gson.fromJson(response.getBody().toString(), LoginResponse.class);
                    return null; // loginResponse;


                } else {
                    System.out.println("response = " + response);
                    throw new LoginException(response.getStatus(), response.getStatusText(), response.getBody().getObject().getString("errorCode"));
                }

            } catch (UnirestException e) {
                throw new LoginException(e);
            }
        }
        private static void disconnect () throws LogoutException {

            try {
                HttpResponse<JsonNode> response = createDeleteHttpRequest("session")
                        .asJson();
                if (response.getStatus() == 200) {
                    securityToken = response.getHeaders().getFirst("X-SECURITY-TOKEN");
                    cst = response.getHeaders().getFirst("CST");
                } else {
                    throw new LogoutException(response.getStatus(), response.getStatusText(), response.getBody().getObject().getString("errorCode"));
                }

            } catch (UnirestException e) {
                throw new LogoutException(e);
            }
        }
        public static HttpRequestWithBody createPostHttpRequest (String url){
            HttpRequestWithBody request = Unirest.post(apiURL + "/" + url);
            setHeaders(request);
            return request;
        }
        public static HttpRequestWithBody createDeleteHttpRequest (String url){
            HttpRequestWithBody request = Unirest.delete(apiURL + "/" + url);
            setHeaders(request);
            return request;
        }
        public static HttpRequestWithBody createPutHttpRequest (String url){
            HttpRequestWithBody request = Unirest.put(apiURL + "/" + url);
            System.out.println("request.getUrl() = " + request.getUrl());
            setHeaders(request);
            return request;
        }
        private static HttpRequest setHeaders (HttpRequest request){
            request.header("Content-Type", "application/json; charset=UTF-8")
                    .header("Accept", "application/json; charset=UTF-8")
                    .header("X-IG-API-KEY", apiKey);
            System.out.println("this.securityToken = " + securityToken);
            if (securityToken != null) {
                request = request.header("X-SECURITY-TOKEN", securityToken);
            }
            if (cst != null) {
                request = request.header("CST", cst);
            }
            return request;
        }
    }