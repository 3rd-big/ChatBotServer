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
        //채팅으로 입력 들어옴
        errorCheck = true;
        String ment;
        //null값 들어오는경우 에러 방지
        if(selectStore.equals("")) {
            ment = "다시 입력해주세요 처음으로 돌아갑니다";
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
            ment = "다시 입력해주세요 처음으로 돌아갑니다";
            errorCheck = false;
            return ment;
        }
        reservationDAO = new ReservationDAO();
        mapScheduleAll = reservationDAO.readScheduleAll(storeName);

        ment = "<"+storeName+">" +" 예약 가능한 시간 및 인원은 " + mapScheduleAll.entrySet() + "입니다.";
        this.setStoreName(storeName);
        return ment;
    }

        //2.예약 내역 등록
        public String reservation(String selectTime, String selectPeople) throws IOException {

            String ment;
            errorCheck = true;
            //null값 들어오는경우 에러 방지
            if(selectTime.equals("") || selectPeople.equals("")) {
                ment = "예약 시간 및 인원을 다시 입력해주세요 처음으로 돌아갑니다";
                errorCheck = false;
                return ment;
             }

            //입력 잘못 들어오는 경우 방지
            String reSelectTime = selectTime.replaceAll("[^0-9]", "")+"시"; //시간이 아닌 문자가 들어오면 삭제
            String reSelectPeople = selectPeople.replaceAll("[^0-9]", ""); //인원수가 아닌 문자가 들어오면 삭제
            int intreSelectPeople = Integer.parseInt(reSelectPeople); //올바른 인원수인지 판단하기 위해 int 변환

            //시간을 잘못 입력했을 때
            if(mapScheduleAll.containsKey(reSelectTime) == false) {
                ment = "예약시간 입력 오류 처음으로 돌아갑니다";
                errorCheck = false;
                return ment;
            }

            //인원수 잘못 입력했을 때
            String rePviPeople = mapScheduleAll.get(reSelectTime).replaceAll("[^0-9]", "");
            int intPviPeople = Integer.parseInt(rePviPeople);
            int intNewPeople = intPviPeople - intreSelectPeople;
            if(intNewPeople<0) {
                ment = "예약 인원 입력 오류 처음으로 돌아갑니다";
                errorCheck = false;
                return ment;
            }

            //DAO로 예약 내역 처리
            String strNewPeople = Integer.toString(intNewPeople)+"명"; //map 에 넣기 위한 String 처리
            mapScheduleAll.replace(reSelectTime, strNewPeople);
            reservationDAO.writeSchedule(mapScheduleAll);

            ment = "<"+storeName+"> "+reSelectTime + " " +reSelectPeople + "명 예약이 완료되었습니다.";

            //예약 시간 String -> util.date 처리
            Date now = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy년MM월dd일");
            String formatedNow = formatter.format(now);
    //
            String reservationDateStr = formatedNow + reSelectTime; //yyyy년MM월dd일HH시
    //		formatter = new SimpleDateFormat("yyyy년MM월dd일HH시");
    //		Date reservationDate = formatter.parse(reservationDateStr);

            this.setReservationDate(reservationDateStr);

            //예약 인원수 String -> int 처리
            this.setNumberPeople(intreSelectPeople);



            return ment;

            //이름,전화번호 들어있는 예약정보 DTO에 식당이름, 예약시간, 인원수 SET
            //예약정보 DTO 객체 문자발송 로직으로 반환
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
