package nemosofts.driving.exam.interfaces;

import java.util.ArrayList;

import nemosofts.driving.exam.item.ItemQuiz;

public interface QuizListener {
    void onStart();
    void onEnd(String success, String message, ArrayList<ItemQuiz> arrayList_img, ArrayList<ItemQuiz> arrayList_no_img);
}
