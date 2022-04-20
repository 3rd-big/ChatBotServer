package service.Reservation;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;

public class ReservationService {

        private ReservationDAO reservationDAO;
        private LinkedHashMap<String, String> mapScheduleAll;
        private String storeName;
        private String reservationDate;
        private int numberPeople;

        private boolean errorCheck;

        //채팅으로 예약가능 시간 및 인원 목록 반환
        public String scheduleAll(String selectStore) throws IOException {
        errorCheck = true;
        String ment;

        //null값 들어오는경우 에러 방지
        if(selectStore.equals("")) {
            ment = "입력되지 않았습니다.";
            errorCheck = false;
            return ment;
        }

        //식당 선택 입력 들어옴
        char charInput = selectStore.charAt(0);
        if(charInput == '1') {
            storeName = "한돈애";
        }else if(charInput == '2') {
            storeName = "오늘통닭";
        }else if(charInput == '3') {
            storeName = "초선과 여포";
        }else if(charInput == '4') {
            storeName = "하나우동";
        }else if(charInput == '5') {
            storeName = "숯부래";
        }else {
            ment = "다시 입력해주세요.";
            errorCheck = false;
            return ment;
        }
        reservationDAO = new ReservationDAO();
        mapScheduleAll = reservationDAO.readScheduleAll(storeName);

        ment = "<"+storeName+">" +" 예약 가능한 시간 및 인원은 " + mapScheduleAll.entrySet() + "입니다.";
        this.setStoreName(storeName);

        return ment;
    }

    //2.예약시간 오류 처리
    public String reservationTime(String selectTime) {
        errorCheck = true;
        String ment;
        //널포인트 들어오는 오류
        if(selectTime.equals("")) {
            ment = "입력되지 않았습니다.";
            errorCheck = false;
            return ment;
        }

        //숫자만 입력되지 않은경우
        String reSelectTime = selectTime.replaceAll("[^0-9]", "")+"시"; //시간이 아닌 문자가 들어오면 삭제
        String strError = selectTime.replaceAll("[^0-9]", "");
        if(strError.equals(selectTime) == false) {
            ment = "문자입력 오류가 발생했습니다.";
            errorCheck = false;
            return ment;
        }

        //없는 시간 들어오는 오류
        if(mapScheduleAll.containsKey(reSelectTime) == false) {
            errorCheck = false;
            ment = "예약가능 한 시간이 아닙니다.";
            return ment;
        }

        return null;
    }

    //3.예약인원 오류 처리
    public String reservationPeople(String selectTime, String selectPeople) {
        errorCheck = true;
        String ment;

        //널포인트 또는 0명 입력 들어오는 오류
        if(selectPeople.equals("") || selectPeople.equals("0")) {
            errorCheck = false;
        }

        //입력 잘못 들어오는 경우 방지
        String reSelectTime = selectTime.replaceAll("[^0-9]", "")+"시"; //시간이 아닌 문자가 들어오면 삭제
        String reSelectPeople = selectPeople.replaceAll("[^0-9]", ""); //인원수가 아닌 문자가 들어오면 삭제
        int intreSelectPeople = Integer.parseInt(reSelectPeople); //올바른 인원수인지 판단하기 위해 int 변환
        String rePviPeople = mapScheduleAll.get(reSelectTime).replaceAll("[^0-9]", "");
        int intPviPeople = Integer.parseInt(rePviPeople);
        int intNewPeople = intPviPeople - intreSelectPeople;

        //숫자만 입력되지 않은경우
        if(reSelectPeople.equals(selectPeople) == false) {
            ment = "문자입력 오류가 발생했습니다.";
            errorCheck = false;
            return ment;
        }
        //예약가능 인원 초과 시
        if(intNewPeople<0) {
            ment = "예약가능 인원 범위 초과 오류가 발생했습니다.";
            errorCheck = false;
            return ment;
        }

        return null;
    }



    //4.예약 내역 등록
    public synchronized String reservation(String selectTime, String selectPeople) throws IOException {

        String ment;

        //동시접속에 따른 오류 해결
        mapScheduleAll = reservationDAO.readScheduleAll(storeName);
        String errorTime = this.reservationTime(selectTime);
        if(errorCheck == false) return errorTime;
        String errorPeople = this.reservationPeople(selectTime, selectPeople);
        if(errorCheck == false) return errorPeople;

        //DAO로 예약 등록을 위한 데이터 전처리
        String reSelectTime = selectTime.replaceAll("[^0-9]", "")+"시"; //시간이 아닌 문자가 들어오면 삭제
        String reSelectPeople = selectPeople.replaceAll("[^0-9]", ""); //인원수가 아닌 문자가 들어오면 삭제
        int intreSelectPeople = Integer.parseInt(reSelectPeople); //올바른 인원수인지 판단하기 위해 int 변환

        String rePviPeople = mapScheduleAll.get(reSelectTime).replaceAll("[^0-9]", "");//예약 전 인원수 String
        int intPviPeople = Integer.parseInt(rePviPeople);//예약 전 인원수 integer
        int intNewPeople = intPviPeople - intreSelectPeople;//예약 후 인원수 integer

        //DAO로 예약 내역 처리
        String strNewPeople = Integer.toString(intNewPeople)+"명"; //map 에 넣기 위한 String 처리
        mapScheduleAll.replace(reSelectTime, strNewPeople);
        reservationDAO.writeSchedule(mapScheduleAll);

        ment = "<"+storeName+"> "+reSelectTime + " " +reSelectPeople + "명 예약이 완료되었습니다.";

        //오늘 날짜 + 예약 시간 생성 로직
        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy년MM월dd일");
        String formatedNow = formatter.format(now);
        String reservationDateStr = formatedNow + reSelectTime; //yyyy년MM월dd일HH시

        //예약정보 문자발송을 위한 정보 set
        this.setReservationDate(reservationDateStr);
        this.setNumberPeople(intreSelectPeople);

        return ment;

    }

    public boolean getErrorCheck() {
        return errorCheck;
    }

    public String getStoreName() {
    return storeName;
}

    public void setStoreName(String storeName) {
    this.storeName = storeName;
}

    public String getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(String reservationDate) {
        this.reservationDate = reservationDate;
    }

    public int getNumberPeople() {
        return numberPeople;
    }

    public void setNumberPeople(int numberPeople) {
        this.numberPeople = numberPeople;
    }

}
