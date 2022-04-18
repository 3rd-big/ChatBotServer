package service.Member;

import domain.Member.Member;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MemberReadFile {

        List<String> textInfo = new ArrayList<>();
        static final String FILE_PATH_NAME = "member";

        public final void ReadTextFile() {
        File txtFile = new File(FILE_PATH_NAME);
        String line = "";

        try (BufferedReader br = new BufferedReader(new FileReader(txtFile))) {

            while ((line = br.readLine()) != null) {

                textInfo.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

        public final List<Member> saveData() {
        String line = "";
        String id;
        String pw;
        String name;
        String mobilNumber;
        String[] var;

        Iterator iterator = textInfo.iterator();
        List<Member> memberList = new ArrayList<>();
        while (iterator.hasNext()) {
            line = (String) iterator.next();
            var = line.split(" ");
            id = var[0];
            pw = var[1];
            name = var[2];
            mobilNumber = var[3];
            memberList.add(Member.joinMember(id, pw, name, mobilNumber));
        }
        return memberList;
    }

}
