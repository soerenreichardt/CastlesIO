package io.castles.core.model.dto;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class EventMessageDTO<T> {
    String event;
    T payload;
}
