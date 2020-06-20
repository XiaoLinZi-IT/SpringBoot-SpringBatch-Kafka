package com.javacodingskill.spring.batch.example1.config;

import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class BaseConfig {

	@Autowired
	private JobRepository jobRepository;

	/*
	 * @Autowired public BaseConfig(JobRepository jobRepository) {
	 * this.jobRepository = jobRepository; }
	 */

	@Bean
	public JobLauncher simpleJobLauncher() {
		SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
		jobLauncher.setJobRepository(jobRepository);
		return jobLauncher;
	}
}
