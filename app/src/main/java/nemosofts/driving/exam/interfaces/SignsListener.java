package nemosofts.driving.exam.interfaces;

import java.util.ArrayList;

import nemosofts.driving.exam.item.ItemSigns;


public interface SignsListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemSigns> signsArrayList);
}