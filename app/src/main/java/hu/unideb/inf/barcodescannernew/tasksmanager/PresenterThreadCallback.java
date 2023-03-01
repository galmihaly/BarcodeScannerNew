package hu.unideb.inf.barcodescannernew.tasksmanager;

import android.os.Message;

public interface PresenterThreadCallback {
    void sendResultToPresenter(Message message);
}
