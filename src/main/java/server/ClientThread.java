package server;

import common.InputMessage;
import domain.sms.ReservationInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.Member.Login;
import service.Reservation.ReservationService;
import service.sms.HttpResponseCode;
import service.sms.Reservation;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class ClientThread extends Thread {

    private final Socket socket;
    private static final Logger logger = LogManager.getLogger(ClientThread.class);

    ReservationInfo reservationInfo;
    InetAddress inetAddress;

    public ClientThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            PrintWriter pw = makeOutboundStream(socket);
            BufferedReader br = makeInBoundStream(socket);
            inetAddress = socket.getInetAddress();


            Login login = new Login(pw,br);
            login.LoadingLoginFile();

            pw.println("로그인은 1번, 회원가입은 2번");
            pw.flush();
            InputMessage.input(pw);
            String str = login.selectLoginOrJoin(br.readLine());


                while (true) {
                    if (login.serviceLogin(str)) {
                        break;
                    }
                }//로그인 기능 종료

            pw.flush();

                while (true) {
                    pw.println("[원하는 식당 번호 입력]");
                    pw.println("[1] 한돈애");
                    pw.println("[2] 오늘통닭");
                    pw.println("[3] 초선과여포");
                    pw.println("[4] 하나우동");
                    pw.println("[5] 숯부래");
                    pw.flush();
                    InputMessage.input(pw);
                    String select = br.readLine();
                    logger.info("[Client Send] {}번 가게 선택", select);

                    // 1. 식당 선택후 시간 및 인원수 조회
                    ReservationService service = new ReservationService();
                    String scheduleAll = service.scheduleAll(select);

                    //입력 오류 체크
                    boolean errorCheck = service.getErrorCheck();
                    if (errorCheck == false) {
                        pw.println(scheduleAll);
                        pw.flush();
                        continue;
                    }

                    pw.println(scheduleAll);
                    pw.flush();

                    //예약 시간 입력 및 예외처리
                    pw.println("예약 [시간] 입력(숫자만 입력)");
                    pw.flush();
                    InputMessage.input(pw);
                    String selectTime = br.readLine();
                    logger.info("[Client Send] {}시 예약 선택", selectTime);
                    String errorTime = service.reservationTime(selectTime);
                    errorCheck = service.getErrorCheck();
                    if (errorCheck == false) {
                        pw.println(errorTime);
                        pw.flush();
                        continue;
                    }

                    //예약 인원 입력 및 예외처리
                    pw.println("예약 [인원] 입력(숫자만 입력)");
                    pw.flush();
                    InputMessage.input(pw);
                    String selectPeople = br.readLine();
                    logger.info("[Client Send] 예약인원 {}명 선택", selectPeople);
                    String errorPeople = service.reservationPeople(selectTime, selectPeople);
                    errorCheck = service.getErrorCheck();
                    if (errorCheck == false) {
                        pw.println(errorPeople);
                        pw.flush();
                        continue;
                    }

                    //예약 인원 및 인원 등록
                    String reservationResult = service.reservation(selectTime, selectPeople);
                    errorCheck = service.getErrorCheck();
                    if (errorCheck == false) {
                        pw.println(reservationResult);
                        pw.flush();
                        continue;
                    }

                    String storeName = service.getStoreName();
                    String reservationDate = service.getReservationDate();
                    int numberPeople = service.getNumberPeople();

                    reservationInfo = new ReservationInfo(storeName, reservationDate, numberPeople);

                    Reservation reservation = new Reservation();
                    logger.info("ReservationInfo Object Data: {}", reservationInfo.toString());
                    int response = reservation.ApiCall(reservationInfo, login.getMember());

                    if (response != HttpResponseCode.HTTP_ACCEPTED) {
                        logger.error("문자 API Error... ");
                        pw.println("예약이 완료되지 않았습니다. 관리자에게 문의바랍니다.");
                        pw.flush();
                    } else {
                        logger.info("[Server Send] {}",reservationResult);
                        pw.println(reservationResult);
                        pw.println(login.getMember().getName() + "님 [" + login.getMember().getMobileNumber() + "] 번호로 문자 전송이 완료되었습니다.");
                        pw.flush();
                    }

                    InputMessage.disconnect(pw);

                    br.close();
                    pw.close();

                    break;
                } //예약로직 종료

        } catch (IOException e) {
            logger.error(e);
        } finally {
            logger.warn("{} Client Disconnect", inetAddress);
        }

    }

    public PrintWriter makeOutboundStream(Socket socket) throws IOException {
        String str = "가나다";
        OutputStream out = socket.getOutputStream();
        OutputStreamWriter outW = new OutputStreamWriter(out);
        return new PrintWriter(outW);
    }

    public BufferedReader makeInBoundStream(Socket socket) throws IOException {
        InputStream in = socket.getInputStream();
        InputStreamReader inR = new InputStreamReader(in);
        return new BufferedReader(inR);
    }
}
