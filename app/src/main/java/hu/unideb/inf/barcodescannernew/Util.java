package hu.unideb.inf.barcodescannernew;

import android.os.Bundle;
import android.os.Message;

public class Util {

    public static final String MESSAGE_BODY = "MESSAGE_BODY";
    public static final int IS_CREATED = 1;
    public static final int IS_NOT_CREATED = 2;

    public static Message createMessage(int id, String dataString) {
        Bundle bundle = new Bundle();
        bundle.putString(Util.MESSAGE_BODY, dataString);
        Message message = new Message();
        message.what = id;
        message.setData(bundle);

        return message;
    }
}
