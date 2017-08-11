/*
 *    Copyright (c) Sematext International
 *    All Rights Reserved
 *
 *    THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF Sematext International
 *    The copyright notice above does not evidence any
 *    actual or intended publication of such source code.
 */
package com.sematext.opentracing.opentracinginjector.api;

import com.sematext.opentracing.SpanOperations;
import io.opentracing.ActiveSpan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class InjectorController {

    @Autowired
    private SpanOperations spanOperations;

    @Value("${extractor.endpoint}")
    private String extractorEndpoint;

    private RestTemplate restTemplate = new RestTemplate();

    @RequestMapping(value = "/inject", method = RequestMethod.POST)
    public void inject() {
        try (ActiveSpan span = spanOperations.startActive("inject")) {
            span.setTag("http.method", "POST");
            span.setTag("http.url", "http://localhost:8080/inject");
            // baggage items propagate along the trace
            // across Span boundaries
            // TODO: figure out why the baggage isn't propagating
            span.setBaggageItem("local.peer", "127.0.0.1");
            // beside tags, span can also have associated events
            span.log("injection started");
            HttpHeaders headers = new HttpHeaders();
            // inject the SpanContext into HTTP headers
            spanOperations.inject(span.context(), headers);
            HttpEntity<String> entity = new HttpEntity<>("params", headers);

            restTemplate.exchange(extractorEndpoint,
                                  HttpMethod.POST,
                                  entity,
                                  String.class);

        }
    }
}
