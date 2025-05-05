package org.example.internship.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    //application
    APL_400("APL-400"),
    APL_404("APL-404"),
    APL_500("APL-500"),
    //gitlab
    GLB_400("GLB-400"),
    GLB_403("GLB-403"),
    GLB_500("GLB-500"),
    //internship
    ITS_400("ITS-400"),
    ITS_404("ITS-404"),
    //lesson
    LSN_400("LSN-400"),
    LSN_404("LSN-404"),
    //solution
    SLN_400("SLN-400"),
    SLN_404("SLN-404"),
    //task
    TSK_400("TSK-400"),
    TSK_404("TSK-404"),
    //user
    USR_400("USR-400"),
    USR_403("USR-403"),
    USR_404("USR-404"),
    //status
    STS_404("STS-404"),
    //message
    MSG_403("MSG-403"),
    //token
    TKN_401("TKN-401"),
    ;

    private final String code;
}
