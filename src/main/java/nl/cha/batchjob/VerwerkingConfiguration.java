package nl.cha.batchjob;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import nl.cha.domein.IdeaRetourOntvangst;

@Configuration
@EnableBatchProcessing
public class VerwerkingConfiguration {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	public DataSource dataSource;

	// tag::readerwriterprocessor[]
	@Bean
	public FlatFileItemReader<IdeaRetourOntvangst> reader() {
		FlatFileItemReader<IdeaRetourOntvangst> reader = new FlatFileItemReader<IdeaRetourOntvangst>();
		reader.setResource(new ClassPathResource("mailsample.csv"));
     	reader.setLineMapper(new DefaultLineMapper<IdeaRetourOntvangst>() {
			{
				setLineTokenizer(new DelimitedLineTokenizer() {
					{
						setNames(new String[] { "nrdeclaratie", "uzovi", "factuurnr" });
					}
				});
				setFieldSetMapper(new BeanWrapperFieldSetMapper<IdeaRetourOntvangst>() {
					{
						setTargetType(IdeaRetourOntvangst.class);
					}
				});
			}
		});
		return reader;
	}

	@Bean
	public IdearetourOntvangstItemProcessor processor() {
		return new IdearetourOntvangstItemProcessor();
	}

	@Bean
	public JdbcBatchItemWriter<IdeaRetourOntvangst> writer() {
		JdbcBatchItemWriter<IdeaRetourOntvangst> writer = new JdbcBatchItemWriter<IdeaRetourOntvangst>();
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<IdeaRetourOntvangst>());
		writer.setSql("INSERT INTO idearetourontvangst (first_name, last_name) VALUES (:firstName, :lastName)");
		writer.setDataSource(dataSource);
		return writer;
	}
	// end::readerwriterprocessor[]

	// tag::jobstep[]
	@Bean
	public Job importUserJob(JobCompletionNotificationListener listener) {
		return jobBuilderFactory.get("importUserJob").incrementer(new RunIdIncrementer()).listener(listener)
				.flow(step2()).next(step3()).next(step1()).end().build();
   }

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1").<IdeaRetourOntvangst, IdeaRetourOntvangst>chunk(10).reader(reader())
				.processor(processor()).writer(writer()).build();
	}
	
	@Bean
	public Step step2() {
		return stepBuilderFactory.get("step2").tasklet(new Tasklet() {
			@Override
			public RepeatStatus execute(StepContribution contribution,
					ChunkContext chunkContext) throws Exception {
				System.out.println(" Step 2");
				return RepeatStatus.FINISHED;
			}
		}).build();
	}
	
	@Bean
	public Step step3() {
		return stepBuilderFactory.get("step3").tasklet(new Tasklet() {
			@Override
			public RepeatStatus execute(StepContribution contribution,
					ChunkContext chunkContext) throws Exception {
				System.out.println(" Step 3");
				return RepeatStatus.FINISHED;
			}
		}).build();
	}
	
	// end::jobstep[]

}
