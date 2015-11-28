package ztbsuper.lousysterm.services;

import java.util.Map;

/**
 * Created by tbzhang on 11/28/15.
 */
public interface ConnectionListener {
    void onConnectionStateChange(int state, Map<String, Object> extraMessage);
}
