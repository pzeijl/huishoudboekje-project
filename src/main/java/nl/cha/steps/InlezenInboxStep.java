package nl.cha.steps;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import nl.cha.service.MailService;

@Configuration
public class InlezenInboxStep {

	static final String STEP_NAME = StepsEnum.inlezenInbox.name();
		
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private MailService mailService;

	@Bean
	public Step inlezenInbox() {
		return stepBuilderFactory.get(STEP_NAME).tasklet(new Tasklet() {
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println("Inlezen inbox");
				mailService.inlezenInbox();
				return RepeatStatus.FINISHED;
			}
		}).build();
	}

}
