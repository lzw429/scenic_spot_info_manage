package Model;

import java.util.Date;

public class Car {
    private int number; // 编号
    private Date ar_time; // 进入停车场的时间，便道不计费

    Car(int number) {
        this.number = number;
    }

    Car(int number, Date ar_time) {
        this.number = number;
        this.ar_time = ar_time;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Date getAr_time() {
        return ar_time;
    }

    public void setAr_time(Date ar_time) {
        this.ar_time = ar_time;
    }
}
