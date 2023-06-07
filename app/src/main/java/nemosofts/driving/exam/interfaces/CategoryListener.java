package nemosofts.driving.exam.interfaces;

import java.util.ArrayList;

import nemosofts.driving.exam.item.ItemCat;

public interface CategoryListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemCat> arrayListCat);
}