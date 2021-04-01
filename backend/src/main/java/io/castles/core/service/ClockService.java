package io.castles.core.service;

import org.springframework.stereotype.Service;

import java.time.Clock;

@Service
public class ClockService {

    public Clock instance() {
        return Clock.systemUTC();
    }
}
