package io.ganich.sms;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ganitzsh on 4/11/16.
 */
public class Messages {
    List<String> texts;
    Boolean you;

    public Messages(List<String> texts) {
        this.texts = texts;
    }

    public Messages() {
        this.texts = new ArrayList<>();
    }
}
