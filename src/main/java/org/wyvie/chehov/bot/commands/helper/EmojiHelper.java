package org.wyvie.chehov.bot.commands.helper;

import org.springframework.stereotype.Service;

@Service
public class EmojiHelper {

    private final String thumbsUp;
    private final String thumbsDown;

    public EmojiHelper() {
        thumbsUp = "\uD83D\uDC4D";
        thumbsDown = "\uD83D\uDC4E";
    }

    private boolean isThisEmoji(String emoji, String it) {
        return it != null && it.length() >= 2
                && emoji.equals(it.substring(0, 2));
    }

    public boolean isThumbsUp(String it) {
        return isThisEmoji(thumbsUp, it);
    }

    public boolean isThumbsDown(String it) {
        return isThisEmoji(thumbsDown, it);
    }
}
