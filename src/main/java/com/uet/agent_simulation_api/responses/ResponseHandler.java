package com.uet.agent_simulation_api.responses;

import com.uet.agent_simulation_api.constant.Const;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ResponseHandler {
    /**
     * This method is used to response success for this API.
     *
     * @param data - Object
     * @return ResponseEntity<SuccessResponse>
     */
    public ResponseEntity<SuccessResponse> respondSuccess(Object data) {
        return ResponseEntity.ok(new SuccessResponse(Const.SUCCESS, data));
    }
}
