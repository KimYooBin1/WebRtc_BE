package com.example.webrtc.common.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.webrtc.chating.entity.Chatroom;
import com.example.webrtc.chating.service.ChatroomService;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class DeleteChatRoomJobConfig {
	private final EntityManagerFactory entityManagerFactory;
	// TODO : select, delete 가 각각 따로 n개씩 query문 생성됨, chunk때문에 중간에 삭제되지 않는 data가 있음. 근야 여러번 돌릴지 index = 0 처리를 할지

	@Bean
	public JpaPagingItemReader<Chatroom> reader(){
		return new JpaPagingItemReaderBuilder<Chatroom>()
			.pageSize(10)
			.name("chatListReader")
			.entityManagerFactory(entityManagerFactory)
			.queryString("select c from Chatroom c where c.userCnt <= 0")
			.build();
	}

	@Bean
	public ItemWriter<Chatroom> writer(ChatroomService chatroomService){
		return items ->{
			for(Chatroom c: items){
				log.info("delete chatRoom Id = {}", c.getId());
				chatroomService.deleteChatRoom(c.getId());
			}
		};
	}

	@Bean
	public Step deleteChatRoomStep(JobRepository jobRepository, ItemReader<Chatroom> itemReader,
		ItemWriter<Chatroom> itemWriter, PlatformTransactionManager transactionManager) {
		return new StepBuilder("delete-chatroom-step", jobRepository)
			.<Chatroom, Chatroom>chunk(10, transactionManager)
			.reader(itemReader)
			.writer(itemWriter)
			.build();
	}

	@Bean
	public Job deleteChatRoomJob(Step deleteChatRoom, JobRepository jobRepository) {
		return new JobBuilder("delete-chatroom-job", jobRepository)
			.incrementer(new RunIdIncrementer())
			.start(deleteChatRoom)
			.build();
	}
}
