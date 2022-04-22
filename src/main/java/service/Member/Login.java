package service.Member;

import common.InputMessage;
import domain.Member.Member;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Login {
    List<Member> memberList = new ArrayList<>();
    private Member member;
    BufferedReader br;
    PrintWriter pw;

    private static final Logger logger = LogManager.getLogger(Login.class);

    public Login(PrintWriter pw, BufferedReader br) {
        this.br = br;
        this.pw = pw;
    }

    public final boolean serviceLogin(String str) throws IOException {
        if (str.equals("1")) return enterLogin();
        if (str.equals("2")) return joinMember();
        return false;
    }

    private boolean joinMember() throws IOException {
        String id;
        String mobileNumber;
        String name;
        do {
            pw.println("회원가입할 아이디를 입력");
            pw.flush();
            InputMessage.input(pw);
            id = br.readLine();
            logger.info("[Client Send] Id: {}", id);
        } while (!checkId(id));
        pw.println("비밀번호 입력");
        pw.flush();
        InputMessage.input(pw);
        String password = br.readLine();
        logger.info("[Client Send] Password: {}", password);
        do {
            pw.println("이름 입력");
            pw.flush();
            InputMessage.input(pw);
            name = br.readLine();
            logger.info("[Client Send] Name: {}", name);

        } while (!checkNameValidation(name));
        do {
            pw.println("전화번호 입력");
            pw.flush();
            InputMessage.input(pw);
            mobileNumber = br.readLine();
            logger.info("[Client Send] MobileNumber: {}", mobileNumber);
        } while (!checkNumberValidation(mobileNumber));

        memberList.add(Member.joinMember(id, password, name, mobileNumber));
        member = memberList.get(memberList.size() - 1);
        pw.println("회원가입완료");
        pw.flush();
        InputMessage.input(pw);
        MemberWriteFile.memberAddFile(member);
        return true;
    }


    private boolean enterLogin() throws IOException {
        String id;
        String password;
        do {
            pw.println("아이디를 입력해주세요.");
            pw.flush();
            InputMessage.input(pw);
            id = br.readLine();
            logger.info("[Client Send] Id: {}", id);
            pw.println("비밀번호를 입력해주세요.");
            pw.flush();
            InputMessage.input(pw);
            password = br.readLine();
            logger.info("[Client Send] Password: {}", password);

        } while (!loginMember(id, password));
        return true;
    }

    public Member getMember() {
        return member;
    }

    private boolean checkId(String id) {
        if (!checkIdValidation(id)) {
            pw.println("숫자와 영어만 입력해주세요. ");
            pw.flush();
            return false;
        }
        for (Member member : memberList
        ) {
            if (!member.checkDuplication(id)) {
                pw.append(id).println(" 는 이미 존재하는 ID 입니다.  ");
                pw.flush();
                return false;
            }
        }
        pw.append(id).println(" 는 사용 가능한 ID 입니다.  ");
        pw.flush();
        return true;
    }

    private boolean checkIdValidation(String id) {
        return Pattern.matches("^[a-zA-z0-9]*$", id);
    }

    private boolean checkNumberValidation(String phoneNumber) {
        if (!Pattern.matches("^\\d{3}\\d{3,4}\\d{4}$", phoneNumber)) {
            pw.append("번호 형식 오류 ");
        }
        return Pattern.matches("^\\d{3}\\d{3,4}\\d{4}$", phoneNumber);
    }

    private boolean checkNameValidation(String name) {
        if (!(Pattern.matches("^[가-힣]*$", name) || Pattern.matches("^[a-zA-Z]*$", name))) {
            pw.append("이름 형식 오류 ");
        }
        return Pattern.matches("^[가-힣]*$", name) || Pattern.matches("^[a-zA-Z]*$", name);
    }

    private Boolean loginMember(String id, String password) {
        for (Member member : memberList
        ) {
            if (member.getId().equals(id)) {
                if (member.getPassword().equals(password)) {
                    this.member = member;
                    pw.println(id + " 로그인 성공");
                    pw.flush();
                    logger.info("{} 로그인 성공", id);
                    return true;
                }
                pw.println("비밀번호 잘못 입력 ");
                pw.flush();

                return false;
            }
        }
        pw.println("존재하지 않는 아이디 입니다. ");
        pw.flush();
        return false;
    }

    public final String selectLoginOrJoin(String str) throws IOException {
        while (true) {
            if (str.equals("1") || str.equals("2")) return str;
            pw.println("로그인은1번, 회원가입은 2번");
            pw.flush();
            InputMessage.input(pw);
            str = br.readLine();
        }
    }

    public final void LoadingLoginFile() {
        MemberReadFile file = new MemberReadFile();
        file.ReadTextFile();
        memberList = file.saveData();
    }
}