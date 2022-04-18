package service.Member;

import domain.Member.Member;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MemberWriteFile {

        public static final void memberAddFile(Member member) {
        File file = new File(MemberReadFile.FILE_PATH_NAME);
        try {
            FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);

            bw.newLine();
            bw.write(member.getId() + " " + member.getPassword() + " " + member.getName() + " " + member.getMobileNumber());
            bw.flush();
            bw.close();
            System.out.println(member.getId() + " 파일 저장 완료");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
