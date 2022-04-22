package service.Reservation;

import java.io.*;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

public class ReservationDAO {

        private Reader in;
        private BufferedReader bIn;
        private Writer out;
        private BufferedWriter bOut;
        private PrintWriter pOut;
        private LinkedHashMap<String, String> mapScheduleAll;
//        private String path = ReservationDAO.class.getResource("").getPath(); //현재클래스의 절대경로
        private String path = "/Users/seotaesu/projects/ChatBotServer/src/main/java/service/Reservation/";
//        private String path = "";
        private String storeName;


        //1.선택 식당의 스케줄 txt에서 전체 조회
        public LinkedHashMap<String, String> readScheduleAll(String storeName) throws IOException {
        //입력 스트림
        this.storeName = storeName;
        in = new FileReader(path+storeName+".txt");
        bIn = new BufferedReader(in);

        mapScheduleAll = new LinkedHashMap<>();
        while(true) {
            String str = bIn.readLine();
            if(str == null) {
                break;
            }
            String time = str.substring(0, 3); //시간 가져오기
            String people = str.substring(4, str.length()); //인원수 가져오기

            mapScheduleAll.put(time, people);
        }

        bIn.close();

        return mapScheduleAll;
    }


        //2.선택 시간 및 인원수 .txt에 등록
        public void writeSchedule(LinkedHashMap<String, String> mapScheduleAll) throws IOException {
        //출력 스트림
        out = new FileWriter(path+storeName+".txt");
        bOut = new BufferedWriter(out);
        pOut = new PrintWriter(bOut);

        Set<String> set = mapScheduleAll.keySet();
        Iterator<String> iter = set.iterator();
        while(iter.hasNext()) {
            String key = ((String)iter.next());
            String value = mapScheduleAll.get(key);
            pOut.println(key + ":" + value);
        }
        pOut.close();

    }


}
