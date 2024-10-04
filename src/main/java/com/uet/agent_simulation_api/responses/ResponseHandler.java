package com.uet.agent_simulation_api.responses;

import com.uet.agent_simulation_api.constant.AppConst;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public class ResponseHandler {
    /**
     * This method is used to response success for this API.
     *
     * @param data Object
     *
     * @return ResponseEntity<SuccessResponse>
     */
    public ResponseEntity<SuccessResponse> respondSuccess(Object data) {
        return ResponseEntity.ok(new SuccessResponse(AppConst.SUCCESS, HttpStatus.OK.value(), null, data));
    }

    /**
     * This method is used to response success for this API.
     *
     * @param data Object
     *
     * @return ResponseEntity<SuccessResponse>
     */
    public ResponseEntity<SuccessResponse> respondSuccess(BigInteger total, Object data) {
        return ResponseEntity.ok(new SuccessResponse(AppConst.SUCCESS, HttpStatus.OK.value(), total, data));
    }

    /**
     * This method is used to response success for this API.
     *
     * @param httpStatus HttpStatus
     * @param data Object
     *
     * @return ResponseEntity<SuccessResponse>
     */
    public ResponseEntity<SuccessResponse> respondSuccess(HttpStatus httpStatus, Object data) {
        return ResponseEntity.ok(new SuccessResponse(AppConst.SUCCESS, httpStatus.value(), null, data));
    }

    /**
     * This method is used to response success for this API.
     *
     * @param httpStatus HttpStatus
     * @param total BigInteger
     * @param data Object
     *
     * @return ResponseEntity<SuccessResponse>
     */
    public ResponseEntity<SuccessResponse> respondSuccess(HttpStatus httpStatus, BigInteger total, Object data) {
        return ResponseEntity.status(httpStatus).body(new SuccessResponse(AppConst.SUCCESS, httpStatus.value(), total, data));
    }
}
