package service.sms;



import domain.Member.Member;
import domain.sms.ReservationInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import server.Main;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Reservation {

    private static final Logger logger = LogManager.getLogger(Reservation.class);

    private String accessKey = "WF83EH5UJ88zim65ZKdE";                                            // 네이버 클라우드 플랫폼 회원에게 발급되는 개인 인증키
    private String secretKey = "yNhdFVl0Kd9NWU9McNDSTb4DR4x55KZuZGOswRHf";                                        // 2차 인증을 위해 서비스마다 할당되는 main.service secret
    final private String method = "POST";                                            // 요청 method
    final private String timestamp = Long.toString(System.currentTimeMillis());    // current timestamp (epoch)
    String requestUrl = "";

    String makeApiUrl(){
        String hostNameUrl = "https://sens.apigw.ntruss.com";            // 호스트 URL

        String requestUrlType = "/messages";                            // 요청 URL
        String serviceId = "ncp:sms:kr:259613567145:chatbot_schedule_message";                                            // 프로젝트에 할당된 SMS 서비스 ID
        requestUrl = "/sms/v2/services/";                        // 요청 URL
        requestUrl += serviceId + requestUrlType;

        return hostNameUrl + requestUrl;
    }

    public JSONObject makeHttpBodyData(ReservationInfo reservationInfo, Member member){

        JSONObject bodyJson = new JSONObject();
        JSONObject toJson = new JSONObject();
        JSONArray toArr = new JSONArray();

        String content = new StringBuilder()
                .append("\n예약완료")
                .append("\n[").append(reservationInfo.getStore()).append("]")
                .append("\n예약자명: ").append(member.getName())
                .append("\n예약시간: ").append(reservationInfo.getReservationDate())
                .append("\n예약인원: ").append(reservationInfo.getNumberPeople())
                .toString();

        logger.info("문자 길이: {}", content.length());

        toJson.put("subject", "");                // 메시지 제목 * LMS Type에서만 사용할 수 있습니다.
        toJson.put("content", content);                // 메시지 내용 * Type별로 최대 byte 제한이 다릅니다.* SMS: 80byte / LMS: 2000byte
//        toJson.put("to", reservationInfo.getMobileNumber());                    // 수신번호 목록  * 최대 50개까지 한번에 전송할 수 있습니다.
        toJson.put("to", member.getMobileNumber());
        toArr.add(toJson);

        bodyJson.put("type", "SMS");                // 메시지 Type (sms | lms)
        bodyJson.put("contentType", "COMM");            // 메시지 내용 Type (AD | COMM) * AD: 광고용, COMM: 일반용 (default: COMM) * 광고용 메시지 발송 시 불법 스팸 방지를 위한 정보통신망법 (제 50조)가 적용됩니다.
        bodyJson.put("countryCode", "82");        // 국가 전화번호
        bodyJson.put("from", "01065840220");                // 발신번호 * 사전에 인증/등록된 번호만 사용할 수 있습니다.
        bodyJson.put("subject", "");                // 메시지 제목 * LMS Type에서만 사용할 수 있습니다.
        bodyJson.put("content", "SENS");                // 메시지 내용 * Type별로 최대 byte 제한이 다릅니다.* SMS: 80byte / LMS: 2000byte
        bodyJson.put("messages", toArr);

        logger.info("HTTP request Body Data: {}", bodyJson.toString());
        return bodyJson;
    }

    public int ApiCall(ReservationInfo reservationInfo, Member member) {

        int responseCode = 0;
        try {
            URL url = new URL(makeApiUrl());
            logger.info("request url : {}", url);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestProperty("content-type", "application/json");
            httpURLConnection.setRequestProperty("x-ncp-apigw-timestamp", timestamp);
            httpURLConnection.setRequestProperty("x-ncp-iam-access-key", accessKey);
            httpURLConnection.setRequestProperty("x-ncp-apigw-signature-v2", makeSignature(requestUrl, timestamp, method, accessKey, secretKey));
            httpURLConnection.setRequestMethod(method);
            httpURLConnection.setDoOutput(true);
            DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());

            dataOutputStream.write(makeHttpBodyData(reservationInfo, member)
                    .toJSONString()
                    .getBytes());
            dataOutputStream.flush();
            dataOutputStream.close();

            responseCode = httpURLConnection.getResponseCode();
            logger.info("responseCode: {}", responseCode);

            BufferedReader br;
            if (responseCode == HttpResponseCode.HTTP_ACCEPTED) {
                br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream()));
            }

            String line = "";
            StringBuilder response = new StringBuilder();
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();
            logger.info("HTTP Response Data: {}", response.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return responseCode;
    }

    public static String makeSignature(String url, String timestamp, String method, String accessKey, String secretKey) throws NoSuchAlgorithmException, InvalidKeyException {
        String space = " ";                    // one space
        String newLine = "\n";                 // new line

        String message = new StringBuilder()
                .append(method)
                .append(space)
                .append(url)
                .append(newLine)
                .append(timestamp)
                .append(newLine)
                .append(accessKey)
                .toString();

        SecretKeySpec signingKey;
        String encodeBase64String;
        try {

            signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
            encodeBase64String = Base64.getEncoder().encodeToString(rawHmac);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            encodeBase64String = e.toString();
        }

        return encodeBase64String;
    }

}
