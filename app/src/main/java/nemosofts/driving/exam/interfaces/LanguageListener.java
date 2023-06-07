package nemosofts.driving.exam.interfaces;

import java.util.ArrayList;

import nemosofts.driving.exam.item.ItemLanguage;

public interface LanguageListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemLanguage> languageArrayList);
}