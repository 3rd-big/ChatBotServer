package service.Member;

import domain.Member.Member;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Login {
    List<Member> memberList = new ArrayList<>();
    private Member member;

    public final boolean loginService(PrintWriter pw, BufferedReader br, String str) throws IOException {

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
            return true;
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
            return true;
        }
        return false;
    }

    public Member getMember() {
        return member;
    }

    private boolean idCheck(PrintWriter pw, String id) {
        if(idValidationCheck(id)) {
            pw.append(id).append("숫자와 영어만 입력해주세요. ");
            return false;
        }
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
    public final String SelectLoginOrJoin(PrintWriter pw, BufferedReader br, String str) throws IOException {
        while (true) {
            if (str.equals("1") || str.equals("2")) return str;
            pw.println("로그인은1번, 회원가입은 2번");
            pw.flush();
            str = br.readLine();
        }
    }

    public final void LoadingLoginFile() {
        MemberReadFile file = new MemberReadFile();
        file.ReadTextFile();
        memberList = file.saveData();
    }
    private boolean idValidationCheck(String id) {
        return Pattern.matches("[ㄱ-ㅎ가-힣 !@#$%^&*(),.?\\\":{}|<>]", id);

    }
}
