package com.sematext.opentracing.opentracingextractor;

import com.sematext.opentracing.SpanOperations;
import com.sematext.opentracing.TracerInitializer;
import com.sematext.opentracing.Tracers;
import com.sematext.opentracing.span.SpanTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class OpentracingExtractorApplication {

	@Value("${tracer.host}")
	private String tracerHost;
	@Value("${tracer.port}")
	private int tracerPort;
	@Value("${tracer.type}")
	private Tracers tracerType;

	public static void main(String[] args) {
		SpringApplication.run(OpentracingExtractorApplication.class, args);
	}

	@Bean
	public TracerInitializer tracerInitializer() {
		TracerInitializer tracerInitializer = new TracerInitializer(tracerType);
		tracerInitializer.setup(tracerHost, tracerPort, "opentracing-extractor");
		return tracerInitializer;
	}

	@Bean
	public SpanOperations spanOperations(TracerInitializer tracerInitializer) {
		return new SpanTemplate(tracerInitializer.getTracer());
	}
}
