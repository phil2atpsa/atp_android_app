package za.co.atpsa.common;

import org.json.JSONObject;

public interface Api {
    public void execute_post(final String module, final JSONObject params);
}
