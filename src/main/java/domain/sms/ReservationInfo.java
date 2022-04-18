package domain.sms;

import java.util.Date;

public class ReservationInfo {

    String store;

    String reservationDate;

    int numberPeople;

    public ReservationInfo(String store, String reservationDate, int numberPeople) {
        this.store = store;
        this.reservationDate = reservationDate;
        this.numberPeople = numberPeople;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
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

    @Override
    public String toString() {
        return "ReservationInfo{" +
                "store='" + store + '\'' +
                ", reservationDate='" + reservationDate + '\'' +
                ", numberPeople=" + numberPeople +
                '}';
    }
}
