package com.github.javakira.simbir.admin.transport;

import com.github.javakira.simbir.transport.SearchTransportType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class GetTransportsRequest {
    private int start;
    private int count;
    private SearchTransportType searchTransportType;
}
