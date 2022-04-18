package server;

import domain.Member.Member;
import domain.sms.ReservationInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.Member.MemberReadFile;
import service.Member.MemberWriteFile;
import service.Reservation.ReservationService;
import service.sms.Reservation;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientThread extends Thread {

    private final Socket socket;
    private static final Logger logger = LogManager.getLogger(ClientThread.class);


    List<Member> memberList = new ArrayList<>();
    Member member;
    ReservationInfo reservationInfo;

    public ClientThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            PrintWriter pw = makeOutboundStream(socket);
            BufferedReader br = makeInBoundStream(socket);
            InetAddress inetAddress = socket.getInetAddress();
            String line = "";


            LoadingLoginFile();

            pw.println("로그인은1번, 회원가입은 2번");
            pw.flush();
            String str = SelectLoginOrJoin(pw, br, br.readLine());

            while (true) {
                //// start

                while (true) {
                    String id;
                    if (str.equals("1")) {
                        String password;
                        do {
                            pw.println("아이디를 입력해주세요.");
                            pw.flush();
                            id = br.readLine();
                            pw.println("비밀번호를 입력해주세요.");
                            pw.flush();
                            password = br.readLine();

                        } while (!login(pw, id, password));
                        break;
                    }
                    if (str.equals("2")) {
                        do {
                            pw.println("회원가입할 아이디를 입력");
                            pw.flush();
                            id = br.readLine();
                        } while (!idCheck(pw, id));
                        pw.println("비밀번호 입력");
                        pw.flush();
                        String password = br.readLine();
                        pw.println("이름 입력");
                        pw.flush();
                        String name = br.readLine();
                        pw.println("전화번호 입력");
                        pw.flush();
                        String mobileNumber = br.readLine();
                        memberList.add(Member.joinMember(id, password, name, mobileNumber));
                        member = memberList.get(memberList.size() - 1);
                        pw.println("회원가입완료");
                        pw.flush();
                        MemberWriteFile.memberAddFile(member);
                        break;
                    }

                }//로그인 기능 끝

                while (true) {
                    br.readLine();
                    pw.println("원하는 식당 번호 입력 >> (1.한돈애 2.오늘통닭 3.초선과여포 4.하나우동 5.숯부래)");
                    pw.flush();
                    String select = br.readLine();
                    System.out.println("1");
                    // 1. 식당 선택후 시간 및 인원수 조회 로직
                    ReservationService service = new ReservationService();
                    System.out.println("2");
                    String scheduleAll = service.scheduleAll(select); //
                    System.out.println("3");
                    boolean errorCheck = service.getErrorCheck();
                    System.out.println("4");
                    // 잘못 입력되면 식당목록으로 돌아감
                    if (errorCheck == false) {
                        pw.println(scheduleAll);
                        pw.flush();
                        continue;
                    }
                    pw.println(scheduleAll + "  : 예약진행 >> enter");
                    pw.flush();
                    String enter = br.readLine();

                    // 2. 예약 가능 시간 및 인원 예약 로직
                    pw.println("예약 시간 입력 >>");
                    pw.flush();
                    String selectTime = br.readLine();

                    pw.println("예약 인원 입력 >>");
                    pw.flush();
                    String selectPeople = br.readLine();

                    String reservartion = service.reservation(selectTime, selectPeople); //
                    pw.println(reservartion + "  : 종료 >> enter");
                    pw.flush();

                    // 잘못 입력되면 식당목록으로 돌아감
                    errorCheck = service.getErrorCheck();
                    if(errorCheck == false) {
                        continue;
                    }

                    String storeName = service.getStoreName();
                    String reservationDate = service.getReservationDate();
                    int numberPeople = service.getNumberPeople();

                    reservationInfo = new ReservationInfo(storeName, reservationDate, numberPeople);


                    Reservation reservation = new Reservation();
                    logger.info("ReservationInfo Object Data: {}", reservationInfo.toString());
                    int response = reservation.ApiCall(reservationInfo, member);


                    break;
                } // end while


                /// end

                if ((line = br.readLine()) == null) {
                    logger.warn("{} Client Disconnect", inetAddress.getHostAddress());
                    break;
                }

                logger.info("[Server Received] {}", line);
                /// TODO Server -> Client 메시지 로거로 찍어놓기
                pw.println(line);
                pw.flush();


//                ReservationInfo reservationInfo = new ReservationInfo("한돈애", "1123123", 3);
//                Reservation reservation = new Reservation();
//                logger.info("ReservationInfo Object Data: {}", reservationInfo.toString());
//                int response = reservation.ApiCall(reservationInfo);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public PrintWriter makeOutboundStream(Socket socket) throws IOException {
        OutputStream out = socket.getOutputStream();
        OutputStreamWriter outW = new OutputStreamWriter(out);
        return new PrintWriter(outW);
    }

    public BufferedReader makeInBoundStream(Socket socket) throws IOException {
        InputStream in = socket.getInputStream();
        InputStreamReader inR = new InputStreamReader(in);
        return new BufferedReader(inR);
    }


    private void LoadingLoginFile() {
        MemberReadFile file = new MemberReadFile();
        file.ReadTextFile();
        memberList = file.saveData();
    }

    private String SelectLoginOrJoin(PrintWriter pw, BufferedReader br, String str) throws IOException {
        while (true) {
            if (str.equals("1") || str.equals("2")) return str;
            pw.println("로그인은1번, 회원가입은 2번");
            pw.flush();
            str = br.readLine();
        }
    }

    private Boolean login(PrintWriter pw, String id, String password) {
        for (Member member : memberList
        ) {
            if (member.getId().equals(id)) {
                if (member.getPassword().equals(password)) {
                    this.member = member;
                    pw.println(id + " 로그인 성공 >> enter");
                    pw.flush();
                    return true;
                }
                pw.append("비밀번호 잘못 입력 ");

                return false;
            }
        }
        pw.append("존재하지 않는 아이디 입니다. ");
        pw.flush();
        return false;
    }

    private boolean idCheck(PrintWriter pw, String id) {
        for (Member member : memberList
        ) {
            if (!member.duplicationCheck(id)) {
                pw.append(id).append(" 는 이미 존재하는 ID 입니다.  ");
                return false;
            }
        }
        pw.append(id).append(" 는 사용 가능한 ID 입니다.  ");
        return true;
    }

}
