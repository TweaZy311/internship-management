package org.example.internship.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    //application
    APL_500("APL-500"),
    APL_404("APL-404"),
    APL_400("APL-400"),
    //gitlab
    GLB_403("GLB-403"),
    GLB_500("GLB-500"),
    //internship
    ITS_400("ITS-400"),
    ITS_404("ITS-404"),
    //lesson
    LSN_404("LSN-404"),
    LSN_400("LSN-400"),
    //solution
    SLN_400("SLN-400"),
    SLN_404("SLN-404"),
    //task
    TSK_404("TSK-404"),
    TSK_400("TSK-400"),
    //user
    USR_404("USR-404"),
    USR_403("USR-403"),
    USR_400("USR-400"),
    //status
    STS_404("STS-404"),
    //message
    MSG_403("MSG-403"),
    ;

    private final String code;
}
