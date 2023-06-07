package nemosofts.driving.exam.interfaces;

import java.util.ArrayList;

import nemosofts.driving.exam.item.ItemNotification;

public interface NotificationListener {
    void onStart();
    void onEnd(String success, ArrayList<ItemNotification> notificationArrayList);
}