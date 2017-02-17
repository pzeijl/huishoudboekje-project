package nl.cha.batchjob;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import nl.cha.domein.IdeaRetourOntvangst;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

	private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
			log.info("!!! JOB FINISHED! Time to verify the results");

			List<IdeaRetourOntvangst> results = jdbcTemplate.query("SELECT * FROM idearetourontvangst where emailadres=''", new RowMapper<IdeaRetourOntvangst>() {
				@Override
				public IdeaRetourOntvangst mapRow(ResultSet rs, int row) throws SQLException {
					return new IdeaRetourOntvangst();
				}
			});

			for (IdeaRetourOntvangst r : results) {
				log.info("Apotheek <" + r + "> heeft geen emailadres.");
			}

		}
	}
}

