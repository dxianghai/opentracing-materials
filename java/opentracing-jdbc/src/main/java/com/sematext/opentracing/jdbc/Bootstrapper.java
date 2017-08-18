/*
 *    Copyright (c) Sematext International
 *    All Rights Reserved
 *
 *    THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF Sematext International
 *    The copyright notice above does not evidence any
 *    actual or intended publication of such source code.
 */
package com.sematext.opentracing.jdbc;

import com.sematext.opentracing.SpanOperations;
import com.sematext.opentracing.TracerInitializer;
import com.sematext.opentracing.Tracers;
import com.sematext.opentracing.span.SpanTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class Bootstrapper implements CommandLineRunner {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Value("${tracer.host}")
	private String tracerHost;
	@Value("${tracer.port}")
	private int tracerPort;
	@Value("${tracer.type}")
	private Tracers tracerType;

	public static void main(String[] args) {
		SpringApplication.run(Bootstrapper.class, args);
	}

	@Override
	public void run(String... strings) throws Exception {
		jdbcTemplate.execute("DROP TABLE apps IF EXISTS");
		jdbcTemplate.execute("CREATE TABLE apps(name VARCHAR(255))");
	}

	@Bean
	public TracerInitializer tracerInitializer() {
		TracerInitializer tracerInitializer = new TracerInitializer(tracerType);
		tracerInitializer.setup(tracerHost, tracerPort, "opentracing-jdbc");
		return tracerInitializer;
	}

	@Bean
	public SpanOperations spanOps(TracerInitializer tracerInitializer) {
		return new SpanTemplate(tracerInitializer.getTracer());
	}
}
