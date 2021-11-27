package com.example.springgumballv3;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
class GumballCommand {

    private String action ;
    private String message ;
    private String state ;
    private String timestamp ;
    private String hash ;
    
}

