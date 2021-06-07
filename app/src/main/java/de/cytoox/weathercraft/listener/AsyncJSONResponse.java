package de.cytoox.weathercraft.listener;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Interface to proceed results of async JSON requests with Unirest.
 *
 * @author Marcel Steffen
 */
public interface AsyncJSONResponse {
    /**
     * Called after async request to process the result.
     *
     * @param result the result
     * @param type   the request type
     * @throws JSONException the json exception
     */
    void processFinish(JSONObject result, char type) throws JSONException;
}
