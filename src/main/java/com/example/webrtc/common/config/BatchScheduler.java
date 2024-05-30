package com.example.webrtc.common.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class BatchScheduler {
	private final JobLauncher jobLauncher;
	private final Job notificationJob;

	@Scheduled(cron = "1 * * * * *")
	private void deleteChatRoom() throws
		JobInstanceAlreadyCompleteException,
		JobExecutionAlreadyRunningException,
		JobParametersInvalidException,
		JobRestartException {
		log.info("deleteChatRoom Job work");
		JobParametersBuilder jobParameter = new JobParametersBuilder();
		jobLauncher.run(notificationJob, jobParameter.addLong("time", System.currentTimeMillis()).toJobParameters());

	}
}
